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

import baby.mumu.basis.provider.RateLimitingAccountIdKeyProviderImpl;
import baby.mumu.basis.provider.RateLimitingCustomGenerateDefaultProviderImpl;
import baby.mumu.basis.provider.RateLimitingCustomGenerateProvider;
import baby.mumu.basis.provider.RateLimitingHttpIpKeyProviderImpl;
import baby.mumu.basis.provider.RateLimitingKeyProvider;
import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import baby.mumu.log.client.api.SystemLogGrpcService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 切面配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
@Configuration
@EnableAspectJAutoProxy
public class AspectConfiguration {

  @Bean
  public DangerousOperationAspect dangerousOperationAspect(
      SystemLogGrpcService systemLogGrpcService) {
    return new DangerousOperationAspect(systemLogGrpcService);
  }

  @Bean
  @ConditionalOnProperty(prefix = "mumu.extension.rl", value = "enabled", havingValue = "true")
  public RateLimitingAspect rateLimitingAspect(ApplicationContext applicationContext,
      ExtensionProperties extensionProperties) {
    return new RateLimitingAspect(applicationContext, extensionProperties);
  }

  @Bean
  @ConditionalOnProperty(prefix = "mumu.extension.rl", value = "enabled", havingValue = "true")
  public RateLimitingKeyProvider rateLimitingHttpIpKeyProviderImpl(
      HttpServletRequest httpServletRequest) {
    return new RateLimitingHttpIpKeyProviderImpl(httpServletRequest);
  }

  @Bean
  @ConditionalOnProperty(prefix = "mumu.extension.rl", value = "enabled", havingValue = "true")
  public RateLimitingKeyProvider rateLimitingAccountIdKeyProviderImpl() {
    return new RateLimitingAccountIdKeyProviderImpl();
  }

  @Bean
  @ConditionalOnProperty(prefix = "mumu.extension.rl", value = "enabled", havingValue = "true")
  public RateLimitingKeyProvider rateLimitingGrpcIpKeyProviderImpl() {
    return new RateLimitingGrpcIpKeyProviderImpl();
  }

  @Bean
  @ConditionalOnProperty(prefix = "mumu.extension.rl", value = "enabled", havingValue = "true")
  public RateLimitingCustomGenerateProvider rateLimitingCustomGenerateDefaultProviderImpl() {
    return new RateLimitingCustomGenerateDefaultProviderImpl();
  }
}
