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
package com.sky.centaur.log.application.consumer.operation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.centaur.log.client.api.OperationLogService;
import com.sky.centaur.log.client.dto.OperationLogSaveCmd;
import com.sky.centaur.log.client.dto.co.OperationLogSaveCo;
import com.sky.centaur.log.infrastructure.operation.convertor.OperationLogMapper;
import com.sky.centaur.log.infrastructure.operation.gatewayimpl.kafka.dataobject.OperationLogKafkaDo;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 操作日志消费者
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
@Observed(name = "OperationLogConsumer")
public class OperationLogConsumer {

  private final ObjectMapper objectMapper;

  private final OperationLogService operationLogService;

  @Autowired
  public OperationLogConsumer(ObjectMapper objectMapper, OperationLogService operationLogService) {
    this.objectMapper = objectMapper;
    this.operationLogService = operationLogService;
  }

  @KafkaListener(topics = {"operation-log"})
  public void handle(String operationLog) throws JsonProcessingException {
    OperationLogKafkaDo operationLogKafkaDo = objectMapper.readValue(operationLog,
        OperationLogKafkaDo.class);
    OperationLogSaveCmd operationLogSaveCmd = new OperationLogSaveCmd();
    OperationLogSaveCo operationLogSaveCo = OperationLogMapper.INSTANCE.toSaveCo(
        operationLogKafkaDo);
    operationLogSaveCmd.setOperationLogSaveCo(operationLogSaveCo);
    operationLogService.save(operationLogSaveCmd);
  }
}
