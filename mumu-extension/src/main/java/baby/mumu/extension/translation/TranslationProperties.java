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
package baby.mumu.extension.translation;

import baby.mumu.extension.translation.aliyun.AliyunTranslationProperties;
import baby.mumu.extension.translation.deepl.DeeplTranslationProperties;
import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 机器翻译属性
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 1.0.3
 */
@Data
public class TranslationProperties {

  private boolean enabled;

  @NestedConfigurationProperty
  private AliyunTranslationProperties aliyun = new AliyunTranslationProperties();

  @NestedConfigurationProperty
  private DeeplTranslationProperties deepl = new DeeplTranslationProperties();
}
