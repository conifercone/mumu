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
package baby.mumu.extension;

import baby.mumu.extension.authentication.AuthenticationProperties;
import baby.mumu.extension.distributed.DistributedProperties;
import baby.mumu.extension.fd.FaceDetectionProperties;
import baby.mumu.extension.ocr.OcrProperties;
import baby.mumu.extension.sql.SqlProperties;
import baby.mumu.extension.translation.TranslationProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 拓展属性
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
@ConfigurationProperties("mumu.extension")
public class ExtensionProperties {

  /**
   * 全局配置
   */
  @NestedConfigurationProperty
  private GlobalProperties global = new GlobalProperties();

  /**
   * 分布式相关配置
   */
  @NestedConfigurationProperty
  private DistributedProperties distributed = new DistributedProperties();

  /**
   * sql相关配置
   */
  @NestedConfigurationProperty
  private SqlProperties sql = new SqlProperties();

  /**
   * 认证相关配置
   */
  @NestedConfigurationProperty
  private AuthenticationProperties authentication = new AuthenticationProperties();

  /**
   * 机器翻译相关配置
   */
  @NestedConfigurationProperty
  private TranslationProperties translation = new TranslationProperties();

  /**
   * ocr
   */
  @NestedConfigurationProperty
  private OcrProperties ocr = new OcrProperties();

  /**
   * 人脸检测
   */
  @NestedConfigurationProperty
  private FaceDetectionProperties fd = new FaceDetectionProperties();
}
