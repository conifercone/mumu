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

import baby.mumu.file.infrastructure.config.FileProperties;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * minio配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Configuration
@EnableConfigurationProperties(FileProperties.class)
public class MinioConfiguration {

  private final FileProperties fileProperties;

  @Autowired
  public MinioConfiguration(FileProperties fileProperties) {
    this.fileProperties = fileProperties;
  }

  @Bean
  public MinioClient minioClient() {
    return MinioClient.builder()
      .endpoint(fileProperties.getMinio().getEndpoint())
      .credentials(fileProperties.getMinio().getAccessKey(),
        fileProperties.getMinio().getSecretKey())
      .build();
  }
}
