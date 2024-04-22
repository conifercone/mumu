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
package com.sky.centaur.log.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * 日志服务全局配置信息
 *
 * @author kaiyu.shan
 * @since 2024-01-19
 */
@Data
@Component
@ConfigurationProperties("centaur.log")
public class LogProperties {

  @NestedConfigurationProperty
  private Elasticsearch elasticsearch = new Elasticsearch();

  @NestedConfigurationProperty
  private Kafka kafka = new Kafka();


  /**
   * 是否开启服务日志
   */
  private boolean enableLog = false;

  @Data
  public static class Elasticsearch {

    /**
     * 地址
     */
    private String[] hostAndPorts;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * CA证书指纹
     */
    private String caFingerprint;

    /**
     * 是否启用
     */
    private boolean enabled;
  }

  @Data
  public static class Kafka {

    /**
     * 是否启用
     */
    private boolean enabled;
  }
}
