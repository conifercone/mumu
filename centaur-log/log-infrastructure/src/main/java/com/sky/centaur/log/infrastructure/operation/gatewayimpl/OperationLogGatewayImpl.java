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
package com.sky.centaur.log.infrastructure.operation.gatewayimpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.centaur.log.domain.operation.OperationLog;
import com.sky.centaur.log.domain.operation.gateway.OperationLogGateway;
import com.sky.centaur.log.infrastructure.operation.gatewayimpl.kafka.OperationLogRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 操作日志领域网关实现
 *
 * @author 单开宇
 * @since 2024-01-25
 */
@Component
public class OperationLogGatewayImpl implements OperationLogGateway {

  @Resource
  private OperationLogRepository operationLogRepository;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void submit(OperationLog operationLog) {
    try {
      operationLogRepository.send("operation-log", objectMapper.writeValueAsString(operationLog));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
