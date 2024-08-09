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
package com.sky.centaur.log.application.operation.executor;

import com.sky.centaur.log.client.dto.OperationLogFindAllCmd;
import com.sky.centaur.log.client.dto.co.OperationLogFindAllCo;
import com.sky.centaur.log.domain.operation.OperationLog;
import com.sky.centaur.log.domain.operation.gateway.OperationLogGateway;
import com.sky.centaur.log.infrastructure.operation.convertor.OperationLogConvertor;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 操作日志查询所有指令执行器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
public class OperationLogFindAllCmdExe {

  private final OperationLogGateway operationLogGateway;
  private final OperationLogConvertor operationLogConvertor;

  @Autowired
  public OperationLogFindAllCmdExe(OperationLogGateway operationLogGateway,
      OperationLogConvertor operationLogConvertor) {
    this.operationLogGateway = operationLogGateway;
    this.operationLogConvertor = operationLogConvertor;
  }

  public Page<OperationLogFindAllCo> execute(
      @NotNull OperationLogFindAllCmd operationLogFindAllCmd) {
    Assert.notNull(operationLogFindAllCmd, "operationLogFindAllCmd cannot be null");
    OperationLog operationLog = operationLogConvertor.toEntity(
            operationLogFindAllCmd.getOperationLogFindAllCo())
        .orElseGet(OperationLog::new);
    Optional.ofNullable(operationLogFindAllCmd.getOperatingStartTime())
        .ifPresent(operationLog::setOperatingStartTime);
    Optional.ofNullable(operationLogFindAllCmd.getOperatingEndTime())
        .ifPresent(operationLog::setOperatingEndTime);
    Page<OperationLog> operationLogs = operationLogGateway.findAll(
        operationLog,
        operationLogFindAllCmd.getPageNo(),
        operationLogFindAllCmd.getPageSize());
    List<OperationLogFindAllCo> operationLogFindAllCos = operationLogs.getContent().stream()
        .map(operationLogConvertor::toFindAllCo).filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
    return new PageImpl<>(operationLogFindAllCos, operationLogs.getPageable(),
        operationLogs.getTotalElements());
  }

}
