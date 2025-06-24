/*
 * Copyright (c) 2024-2025, the original author or authors.
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

package baby.mumu.extension.idempotent.request.id.redis;

import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.idempotent.request.id.RequestIdIdempotentProcessor;
import baby.mumu.extension.idempotent.request.id.RequestIdIdempotentProperties.RedisRequestIdIdempotentProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

/**
 * redis request id 幂等处理器实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.3.0
 */
public class RedisRequestIdIdempotentProcessor implements RequestIdIdempotentProcessor {

  private final String REQUEST_ID_PREFIX = "mumu:request:id:";
  private final ExtensionProperties extensionProperties;
  private final RedisTemplate<String, String> redisTemplate;

  public RedisRequestIdIdempotentProcessor(RedisConnectionFactory redisConnectionFactory,
    ExtensionProperties extensionProperties) {
    Assert.notNull(redisConnectionFactory, "RedisConnectionFactory must not be null");
    this.extensionProperties = extensionProperties;
    redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    redisTemplate.setValueSerializer(new StringRedisSerializer());
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.afterPropertiesSet();
  }

  @Override
  public boolean processed(String requestId) {
    if (StringUtils.isBlank(requestId)) {
      return false;
    }
    return redisTemplate.hasKey(REQUEST_ID_PREFIX.concat(requestId));
  }

  @Override
  public void process(String requestId) {
    if (StringUtils.isNotBlank(requestId)) {
      RedisRequestIdIdempotentProperties redis = extensionProperties.getIdempotent().getRequestId()
        .getRedis();
      redisTemplate.opsForValue()
        .set(REQUEST_ID_PREFIX.concat(requestId), requestId, redis.getTimeout(), redis.getUnit());
    }
  }
}
