/*
 * Copyright (c) 2024-2024, the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package baby.mumu.extension.aspects;

import static java.time.Duration.ofDays;
import static java.time.Duration.ofHours;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.annotations.RateLimiters;
import baby.mumu.basis.exception.RateLimiterException;
import baby.mumu.basis.provider.RateLimitingCustomGenerateProvider;
import baby.mumu.basis.provider.RateLimitingCustomGenerateProvider.RateLimitingCustomGenerate;
import baby.mumu.basis.provider.RateLimitingKeyProvider;
import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.rl.RateLimiterStringByteArrayCodec;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConfigurationBuilder;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;

/**
 * 限流注解切面
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.1.0
 */
@Aspect
public class RateLimitingAspect extends AbstractAspect implements DisposableBean {

  private final ApplicationContext applicationContext;
  private final RedisClient redisClient;
  private final StatefulRedisConnection<String, byte[]> connection;
  private final LettuceBasedProxyManager<String> proxyManager;

  public RateLimitingAspect(ApplicationContext applicationContext,
      @NotNull ExtensionProperties extensionProperties) {
    this.applicationContext = applicationContext;
    this.redisClient = RedisClient.create(extensionProperties.getRl().getRedis().getUri());
    this.connection = redisClient.connect(new RateLimiterStringByteArrayCodec());
    this.proxyManager = Bucket4jLettuce.casBasedBuilder(
            connection.async())
        .expirationAfterWrite(
            ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(ofSeconds(10)))
        .build();
  }

  @Around("@annotation(baby.mumu.basis.annotations.RateLimiter) || @annotation(baby.mumu.basis.annotations.RateLimiters)")
  public Object rounding(ProceedingJoinPoint joinPoint) throws Throwable {
    List<RateLimiter> annotations = new ArrayList<>();
    Optional.ofNullable(getMethodAnnotation(joinPoint, RateLimiter.class))
        .ifPresent(annotations::add);
    Optional.ofNullable(getMethodAnnotation(joinPoint, RateLimiters.class))
        .map(rateLimiters -> Arrays.asList(rateLimiters.value())).ifPresent(annotations::addAll);

    if (!annotations.isEmpty()) {
      String defaultSignature = DigestUtils.md5Hex(joinPoint.getSignature().toString());
      annotations.stream().distinct().collect(
          Collectors.groupingBy(
              x -> RateLimitingKey.builder().keyProvider(x.keyProvider()).prefix(x.prefix())
                  .build())).forEach((key, val) -> {
        if (StringUtils.isBlank(key.getPrefix())) {
          key.setPrefix(defaultSignature);
        }
        rateLimiting(key, val);
      });
    }

    Object[] args = joinPoint.getArgs();
    return joinPoint.proceed(args);
  }

  private void rateLimiting(@NotNull RateLimitingKey rateLimitingKey,
      @NotNull List<RateLimiter> list) {
    Bucket bucket = getBucket(rateLimitingKey, list);
    ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
    if (!probe.isConsumed()) {
      long waitForRefillNanos = probe.getNanosToWaitForRefill();
      long waitForRefillMillis = Duration.ofNanos(waitForRefillNanos).toSeconds();
      throw new RateLimiterException(waitForRefillMillis);
    }
  }

  private Bucket getBucket(@NotNull RateLimitingKey rateLimitingKey,
      @NotNull List<RateLimiter> list) {
    RateLimitingKeyProvider rateLimitingKeyProvider = applicationContext.getBean(
        rateLimitingKey.getKeyProvider());
    String uniqKey = rateLimitingKey.getPrefix() + ":" + rateLimitingKeyProvider.generateUniqKey();
    ConfigurationBuilder configurationBuilder = BucketConfiguration.builder();

    list.forEach(x -> {
      BasicInformation basicInformation = getBasicInformation(x);
      int capacity = basicInformation.capacity();
      long period = basicInformation.period();
      TimeUnit timeUnit = basicInformation.timeUnit();
      // 每 period 单位时间内最高调用 capacity 次数
      switch (timeUnit) {
        case SECONDS:
          configurationBuilder
              .addLimit(limit -> limit.capacity(capacity)
                  .refillIntervally(capacity, ofSeconds(period)));
          break;
        case MINUTES:
          configurationBuilder
              .addLimit(limit -> limit.capacity(capacity)
                  .refillIntervally(capacity, ofMinutes(period)));
          break;
        case HOURS:
          configurationBuilder
              .addLimit(limit -> limit.capacity(capacity)
                  .refillIntervally(capacity, ofHours(period)));
          break;
        case DAYS:
          configurationBuilder
              .addLimit(limit -> limit.capacity(capacity)
                  .refillIntervally(capacity, ofDays(period)));
          break;
        default:
          throw new IllegalStateException("Unexpected value: " + x.timeUnit());
      }
    });
    return proxyManager.getProxy(uniqKey, configurationBuilder::build);
  }

  @Contract("_ -> new")
  private @NotNull BasicInformation getBasicInformation(@NotNull RateLimiter rateLimiter) {
    if (rateLimiter.customGeneration()) {
      RateLimitingCustomGenerateProvider rateLimitingCustomGenerateProvider = applicationContext.getBean(
          rateLimiter.customGenerationProvider());
      RateLimitingCustomGenerate generate = rateLimitingCustomGenerateProvider.generate();
      assert generate != null;
      return new BasicInformation(generate.capacity(),
          generate.period(),
          generate.timeUnit());
    } else {
      return new BasicInformation(rateLimiter.capacity(), rateLimiter.period(),
          rateLimiter.timeUnit());
    }
  }

  private record BasicInformation(int capacity, long period, TimeUnit timeUnit) {

  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class RateLimitingKey {

    /**
     * 唯一标识，需要实现 RateLimitingKeyProvider 接口
     */
    private Class<? extends RateLimitingKeyProvider> keyProvider;

    /**
     * 缓存 key 的前缀 （1）默认使用方法的签名的 MD5，作为缓存 key 的前缀，用于区分是为了给哪个方法设置限流；
     * （2）如果需要要对全局进行限流，需设置固定值，例如想实现全局IP的限流，可以采用固定 prefix + （IP）keyProvider 的模式
     */
    private String prefix;
  }


  @Override
  public void destroy() {
    Optional.ofNullable(connection).ifPresent(StatefulRedisConnection::close);
    Optional.ofNullable(redisClient).ifPresent(RedisClient::close);
  }
}
