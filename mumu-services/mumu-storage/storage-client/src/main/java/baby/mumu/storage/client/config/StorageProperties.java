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

package baby.mumu.storage.client.config;

import baby.mumu.basis.enums.FileStorageMediaTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 存储配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Data
@ConfigurationProperties("mumu.storage")
public class StorageProperties {

  /**
   * minio配置属性
   */
  @NestedConfigurationProperty
  private Minio minio = new Minio();

  /**
   * S3配置属性
   */
  @NestedConfigurationProperty
  private S3 s3 = new S3();

  /**
   * 文件存储介质类型
   */
  @NestedConfigurationProperty
  private FileStorageMediaTypeEnum storageMediaType = FileStorageMediaTypeEnum.S3;

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
     * 认证密钥
     */
    private String secretKey;
  }

  @Data
  public static class S3 {

    /**
     * 地址
     */
    private String endpoint;

    /**
     * 区域
     */
    private String region;

    /**
     * 访问密钥 ID
     */
    private String accessKeyId;

    /**
     * 认证密钥
     */
    private String secretAccessKey;

    /**
     * 启用路径样式访问
     */
    private boolean pathStyleAccessEnabled;
  }
}
