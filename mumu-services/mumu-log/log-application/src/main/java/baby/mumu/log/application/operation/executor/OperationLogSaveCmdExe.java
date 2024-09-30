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
package baby.mumu.log.application.operation.executor;

import baby.mumu.log.client.dto.OperationLogSaveCmd;
import baby.mumu.log.domain.operation.gateway.OperationLogGateway;
import baby.mumu.log.infrastructure.operation.convertor.OperationLogConvertor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 操作日志保存指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
public class OperationLogSaveCmdExe {

  private final OperationLogGateway operationLogGateway;
  private final OperationLogConvertor operationLogConvertor;

  @Autowired
  public OperationLogSaveCmdExe(OperationLogGateway operationLogGateway,
      OperationLogConvertor operationLogConvertor) {
    this.operationLogGateway = operationLogGateway;
    this.operationLogConvertor = operationLogConvertor;
  }

  public void execute(@NotNull OperationLogSaveCmd operationLogSaveCmd) {
    Assert.notNull(operationLogSaveCmd, "OperationLogSaveCmd cannot be null");
    operationLogConvertor.toEntity(operationLogSaveCmd.getOperationLogSaveCo())
        .ifPresent(operationLogGateway::save);
  }
}
