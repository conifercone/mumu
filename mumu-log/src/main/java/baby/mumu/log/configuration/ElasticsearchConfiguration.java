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
package baby.mumu.log.configuration;

import baby.mumu.log.infrastructure.config.LogProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;

/**
 * es配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(prefix = "mumu.log.elasticsearch", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(LogProperties.class)
public class ElasticsearchConfiguration extends
    org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration {

  private final LogProperties logProperties;

  @Autowired
  public ElasticsearchConfiguration(LogProperties logProperties) {
    this.logProperties = logProperties;
  }

  @Override
  public @NotNull ClientConfiguration clientConfiguration() {
    // 使用构建器来提供集群地址
    return ClientConfiguration.builder()
        // 设置连接地址
        .connectedTo(logProperties.getElasticsearch().getHostAndPorts())
        // 启用ssl并配置CA指纹
        .usingSsl(logProperties.getElasticsearch().getCaFingerprint())
        // 设置用户名密码
        .withBasicAuth(logProperties.getElasticsearch().getUsername(),
            logProperties.getElasticsearch().getPassword())
        .withSocketTimeout(60000)
        .withConnectTimeout(60000)
        // 创建连接信息
        .build();
  }
}
