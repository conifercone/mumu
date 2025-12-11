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

package baby.mumu.storage.configuration;

import baby.mumu.storage.client.config.StorageProperties;
import baby.mumu.storage.client.config.StorageProperties.S3;
import java.net.URI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

/**
 * S3配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.16.0
 */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
@ConditionalOnProperty(prefix = "mumu.storage", value = "storage-media-type", havingValue = "S3", matchIfMissing = true)
public class S3Configuration {

  private final StorageProperties storageProperties;

  @Autowired
  public S3Configuration(StorageProperties storageProperties) {
    this.storageProperties = storageProperties;
  }

  @Bean
  public S3Client s3Client() {
    S3 s3 = storageProperties.getS3();
    AwsCredentialsProvider credentialsProvider;

    if (StringUtils.isNotBlank(s3.getAccessKeyId())) {
      credentialsProvider = StaticCredentialsProvider.create(
        AwsBasicCredentials.create(s3.getAccessKeyId(), s3.getSecretAccessKey())
      );
    } else {
      // 默认从环境变量 / 配置文件 / IAM Role 自动获取
      credentialsProvider = DefaultCredentialsProvider.builder().build();
    }

    S3ClientBuilder s3ClientBuilder = S3Client.builder()
      .region(Region.of(s3.getRegion()))
      .credentialsProvider(credentialsProvider)
      .serviceConfiguration(software.amazon.awssdk.services.s3.S3Configuration.builder()
        .pathStyleAccessEnabled(s3.isPathStyleAccessEnabled())
        .build());

    if (StringUtils.isNotBlank(s3.getEndpoint())) {
      s3ClientBuilder.endpointOverride(URI.create(s3.getEndpoint()));
    }

    return s3ClientBuilder.build();
  }

}
