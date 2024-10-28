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
package baby.mumu.log.configuration;

import baby.mumu.log.infrastructure.config.LogProperties;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * kafka配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(prefix = "mumu.log.kafka", name = "enabled", havingValue = "true")
public class KafkaConfiguration {

  /**
   * 操作日志主题
   *
   * @return topic
   */
  @Bean
  public NewTopic operationLog() {
    return TopicBuilder.name(LogProperties.OPERATION_LOG_KAFKA_TOPIC_NAME)
      .partitions(10)
      .replicas(1)
      .build();
  }

  /**
   * 系统日志主题
   *
   * @return topic
   */
  @Bean
  public NewTopic systemLog() {
    return TopicBuilder.name(LogProperties.SYSTEM_LOG_KAFKA_TOPIC_NAME)
      .partitions(10)
      .replicas(1)
      .build();
  }
}
