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
package baby.mumu.log.application.consumer.system;

import baby.mumu.log.client.api.SystemLogService;
import baby.mumu.log.infrastructure.config.LogProperties;
import baby.mumu.log.infrastructure.system.convertor.SystemLogConvertor;
import baby.mumu.log.infrastructure.system.gatewayimpl.kafka.po.SystemLogKafkaPO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 系统日志消费者
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "SystemLogConsumer")
public class SystemLogConsumer {

  private final ObjectMapper objectMapper;
  private final SystemLogService systemLogService;
  private final SystemLogConvertor systemLogConvertor;


  @Autowired
  public SystemLogConsumer(ObjectMapper objectMapper, SystemLogService systemLogService,
    SystemLogConvertor systemLogConvertor) {
    this.objectMapper = objectMapper;
    this.systemLogService = systemLogService;
    this.systemLogConvertor = systemLogConvertor;
  }

  @KafkaListener(topics = {LogProperties.SYSTEM_LOG_KAFKA_TOPIC_NAME})
  public void handle(String systemLog) throws JsonProcessingException {
    SystemLogKafkaPO systemLogKafkaPO = objectMapper.readValue(systemLog,
      SystemLogKafkaPO.class);
    systemLogConvertor.toSystemLogSaveCmd(systemLogKafkaPO).ifPresent(systemLogService::save);
  }
}
