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
package baby.mumu.file.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 文件存储配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Data
@ConfigurationProperties("mumu.file")
public class FileProperties {

  @NestedConfigurationProperty
  private Minio minio = new Minio();

  @Data
  public static class Minio {

    /**
     * 地址
     */
    private String endpoint;
    /**
     * 访问密钥
     */
    private String accessKey;
    /**
     * 密钥
     */
    private String secretKey;
  }
}
