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

package baby.mumu.log.application.operation.executor;

import baby.mumu.log.client.cmds.OperationLogFindAllCmd;
import baby.mumu.log.client.dto.OperationLogFindAllDTO;
import baby.mumu.log.domain.operation.OperationLog;
import baby.mumu.log.domain.operation.gateway.OperationLogGateway;
import baby.mumu.log.infra.operation.convertor.OperationLogConvertor;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
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

  public Page<OperationLogFindAllDTO> execute(
    @NotNull OperationLogFindAllCmd operationLogFindAllCmd) {
    Assert.notNull(operationLogFindAllCmd, "operationLogFindAllCmd cannot be null");
    OperationLog operationLog = operationLogConvertor.toEntity(operationLogFindAllCmd)
      .orElseGet(OperationLog::new);
    Page<OperationLog> operationLogs = operationLogGateway.findAll(
      operationLog,
      operationLogFindAllCmd.getCurrent(),
      operationLogFindAllCmd.getPageSize());
    List<OperationLogFindAllDTO> operationLogFindAllDTOS = operationLogs.getContent().stream()
      .map(operationLogConvertor::toFindAllDTO).filter(Optional::isPresent)
      .map(Optional::get)
      .toList();
    return new PageImpl<>(operationLogFindAllDTOS, operationLogs.getPageable(),
      operationLogs.getTotalElements());
  }

}
