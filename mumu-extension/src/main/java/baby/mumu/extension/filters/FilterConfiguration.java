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

package baby.mumu.extension.filters;

import baby.mumu.extension.ExtensionProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 过滤器配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.3.0
 */
@Configuration
@EnableConfigurationProperties(ExtensionProperties.class)
public class FilterConfiguration {

  @Bean
  @ConditionalOnMissingBean(SignatureFilter.class)
  @ConditionalOnProperty(prefix = "mumu.extension.global.digital-signature", value = "enabled", havingValue = "true")
  public SignatureFilter mumuSignatureFilter(ExtensionProperties extensionProperties) {
    return new SignatureFilter(extensionProperties);
  }
}
