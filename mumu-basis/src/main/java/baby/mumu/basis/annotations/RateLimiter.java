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
package baby.mumu.basis.annotations;

import baby.mumu.basis.provider.RateLimitingCustomGenerateDefaultProviderImpl;
import baby.mumu.basis.provider.RateLimitingCustomGenerateProvider;
import baby.mumu.basis.provider.RateLimitingHttpIpKeyProviderImpl;
import baby.mumu.basis.provider.RateLimitingKeyProvider;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.1.0
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {

  /**
   * 生成的令牌数量
   */
  int capacity() default 5;

  /**
   * 每单位时间，生成的令牌数量
   * <p>默认：1 秒内生成 5 个令牌</p>
   */
  long period() default 1;

  /**
   * 时间单位，默认：秒
   */
  TimeUnit timeUnit() default TimeUnit.SECONDS;

  /**
   * 唯一标识，需要实现 RateLimitingKeyProvider 接口
   */
  Class<? extends RateLimitingKeyProvider> keyProvider() default RateLimitingHttpIpKeyProviderImpl.class;

  /**
   * 是否自定义令牌生成逻辑
   */
  boolean customGeneration() default false;

  /**
   * 自定义令牌生成接口
   */
  Class<? extends RateLimitingCustomGenerateProvider> customGenerationProvider() default RateLimitingCustomGenerateDefaultProviderImpl.class;

  /**
   * 缓存 key 的前缀 （1）默认使用方法的签名的 MD5，作为缓存 key 的前缀，用于区分是为了给哪个方法设置限流；
   * （2）如果需要要对全局进行限流，需设置固定值，例如想实现全局IP的限流，可以采用固定 prefix + （IP）keyProvider 的模式，相当于提供某一组接口共用一个限流配置
   */
  String prefix() default "";
}
