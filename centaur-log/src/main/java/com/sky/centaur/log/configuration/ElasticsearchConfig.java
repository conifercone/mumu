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
package com.sky.centaur.log.configuration;

import com.sky.centaur.log.infrastructure.config.LogProperties;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

/**
 * es配置类
 *
 * @author 单开宇
 * @since 2024-01-23
 */
@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

  @Resource
  private LogProperties logProperties;

  @Override
  public @NotNull ClientConfiguration clientConfiguration() {
    // 使用构建器来提供集群地址
    return ClientConfiguration.builder()
        // 设置连接地址
        .connectedTo(logProperties.getElasticSearch().getHostAndPorts())
        // 启用ssl并配置CA指纹
        .usingSsl(logProperties.getElasticSearch().getCaFingerprint())
        // 设置用户名密码
        .withBasicAuth(logProperties.getElasticSearch().getUsername(),
            logProperties.getElasticSearch().getPassword())
        // 创建连接信息
        .build();
  }
}
