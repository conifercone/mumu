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

package baby.mumu.log.application.consumer.operation;

import baby.mumu.log.client.api.OperationLogService;
import baby.mumu.log.infra.config.LogProperties;
import baby.mumu.log.infra.operation.convertor.OperationLogConvertor;
import baby.mumu.log.infra.operation.gatewayimpl.kafka.po.OperationLogKafkaPO;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/**
 * 操作日志消费者
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "OperationLogKafkaConsumer")
@ConditionalOnProperty(prefix = "mumu.log.kafka", name = "enabled", havingValue = "true")
public class OperationLogKafkaConsumer {

  private final JsonMapper jsonMapper;
  private final OperationLogService operationLogService;
  private final OperationLogConvertor operationLogConvertor;

  @Autowired
  public OperationLogKafkaConsumer(JsonMapper jsonMapper,
    OperationLogService operationLogService,
    OperationLogConvertor operationLogConvertor) {
    this.jsonMapper = jsonMapper;
    this.operationLogService = operationLogService;
    this.operationLogConvertor = operationLogConvertor;
  }

  @KafkaListener(topics = {LogProperties.OPERATION_LOG_KAFKA_TOPIC_NAME})
  public void handle(String operationLog) {
    OperationLogKafkaPO operationLogKafkaPO = jsonMapper.readValue(operationLog,
      OperationLogKafkaPO.class);
    operationLogConvertor.toOperationLogSaveCmd(operationLogKafkaPO)
      .ifPresent(operationLogService::save);
  }
}
