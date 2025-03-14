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
package baby.mumu.extension.translation.deepl;

import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.translation.SimpleTextTranslation;
import com.deepl.api.DeepLClient;
import com.deepl.api.Translator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * deepl配置类
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 1.0.3
 */
@Configuration
@ConditionalOnClass(Translator.class)
@ConditionalOnProperty(prefix = "mumu.extension.translation.deepl", value = "enabled", havingValue = "true")
@EnableConfigurationProperties(ExtensionProperties.class)
public class DeeplTranslationConfiguration {

  @Bean
  @ConditionalOnMissingBean(Translator.class)
  public Translator deeplTranslator(ExtensionProperties extensionProperties) {
    return new DeepLClient(extensionProperties.getTranslation().getDeepl().getAccessKey());
  }

  @Bean
  @ConditionalOnMissingBean(SimpleTextTranslation.class)
  public SimpleTextTranslation simpleTextTranslation(Translator deeplTranslator) {
    return new DeeplSimpleTextTranslation(deeplTranslator);
  }
}
