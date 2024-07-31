/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
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
package com.sky.centaur.extension.translation.aliyun;

import com.aliyun.alimt20181012.Client;
import com.aliyun.teaopenapi.models.Config;
import com.sky.centaur.extension.ExtensionProperties;
import com.sky.centaur.extension.translation.SimpleTextTranslation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云机器翻译配置类
 *
 * @author kaiyu.shan
 * @since 1.0.3
 */
@Configuration
@ConditionalOnClass(Client.class)
@ConditionalOnProperty(prefix = "centaur.extension.translation.aliyun", value = "enabled", havingValue = "true")
@EnableConfigurationProperties(ExtensionProperties.class)
public class AliyunTranslationConfiguration {

  @Bean
  public Client alimtClient(ExtensionProperties extensionProperties) throws Exception {
    AliyunTranslationProperties aliyunTranslationProperties = extensionProperties.getTranslation()
        .getAliyun();
    Config config = new Config()
        .setAccessKeyId(aliyunTranslationProperties.getAccessKeyId())
        .setAccessKeySecret(aliyunTranslationProperties.getAccessKeySecret());
    config.endpoint = aliyunTranslationProperties.getEndpoint();
    return new Client(config);
  }

  @Bean
  public SimpleTextTranslation simpleTextTranslation(Client client) {
    return new AliyunSimpleTextTranslation(client);
  }
}
