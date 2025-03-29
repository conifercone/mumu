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
package baby.mumu.extension.idempotent.request.id;

import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.idempotent.request.id.redis.RedisRequestIdIdempotentConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Request ID 幂等配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.3.0
 */
@Configuration
@EnableConfigurationProperties(ExtensionProperties.class)
@ConditionalOnProperty(prefix = "mumu.extension.idempotent.request-id", value = "enabled", havingValue = "true")
@Import({RedisRequestIdIdempotentConfiguration.class})
public class RequestIdIdempotentConfiguration {

  /**
   * 请求ID过滤器，防止重放攻击
   *
   * @param requestIdIdempotentProcessor 请求 id 幂等处理器
   * @param extensionProperties          扩展属性
   * @return 请求ID过滤器
   */
  @Bean
  @ConditionalOnBean(RequestIdIdempotentProcessor.class)
  public RequestIdFilter mumuRequestIdFilter(
    RequestIdIdempotentProcessor requestIdIdempotentProcessor,
    ExtensionProperties extensionProperties) {
    return new RequestIdFilter(requestIdIdempotentProcessor, extensionProperties);
  }
}
