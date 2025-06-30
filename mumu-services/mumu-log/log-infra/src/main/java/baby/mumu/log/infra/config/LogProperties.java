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

package baby.mumu.log.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 日志服务全局配置信息
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Data
@ConfigurationProperties("mumu.log")
public class LogProperties {

  //  操作日志索引名称
  public static final String OPERATION_LOG_ES_INDEX_NAME = "mumu-operation-log";
  //  系统日志索引名称
  public static final String SYSTEM_LOG_ES_INDEX_NAME = "mumu-system-log";
  //  操作日志topic名称
  public static final String OPERATION_LOG_KAFKA_TOPIC_NAME = "mumu-operation-log";
  //  系统日志topic名称
  public static final String SYSTEM_LOG_KAFKA_TOPIC_NAME = "mumu-system-log";

  @NestedConfigurationProperty
  private Elasticsearch elasticsearch = new Elasticsearch();

  @NestedConfigurationProperty
  private Kafka kafka = new Kafka();

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
    private boolean enabled = true;
  }

  @Data
  public static class Kafka {

    /**
     * 是否启用
     */
    private boolean enabled = true;
  }
}
