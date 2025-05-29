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
package baby.mumu.extension.ocr.aliyun;

import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.ocr.OcrProcessor;
import com.aliyun.ocr_api20210707.Client;
import com.aliyun.teaopenapi.models.Config;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云ocr配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.0.0
 */
@Configuration
@EnableConfigurationProperties(ExtensionProperties.class)
@ConditionalOnProperty(prefix = "mumu.extension.ocr.aliyun", value = "enabled", havingValue = "true")
@ConditionalOnClass(Client.class)
public class AliyunOcrConfiguration {


  @Bean
  @ConditionalOnMissingBean(Client.class)
  public Client client(ExtensionProperties extensionProperties) throws Exception {
    AliyunOcrProperties aliyun = extensionProperties.getOcr().getAliyun();
    Config config = new Config();
    config.setAccessKeyId(
      StringUtils.isBlank(aliyun.getAccessKeyId()) ? System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID")
        : aliyun.getAccessKeyId());
    config.setAccessKeySecret(StringUtils.isBlank(aliyun.getAccessKeySecret()) ? System.getenv(
      "ALIBABA_CLOUD_ACCESS_KEY_SECRET") : aliyun.getAccessKeySecret());
    config.setEndpoint(aliyun.getEndpoint());
    return new Client(config);
  }

  @Bean
  @ConditionalOnMissingBean(OcrProcessor.class)
  public OcrProcessor ocrProcessor(Client client) {
    return new AliyunOcrProcessor(client);
  }
}
