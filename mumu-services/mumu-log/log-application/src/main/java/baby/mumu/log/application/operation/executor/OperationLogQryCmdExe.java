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

import baby.mumu.log.client.cmds.OperationLogQryCmd;
import baby.mumu.log.client.dto.OperationLogQryDTO;
import baby.mumu.log.domain.operation.gateway.OperationLogGateway;
import baby.mumu.log.infrastructure.operation.convertor.OperationLogConvertor;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 操作日志查询指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
public class OperationLogQryCmdExe {

  private final OperationLogGateway operationLogGateway;
  private final OperationLogConvertor operationLogConvertor;

  @Autowired
  public OperationLogQryCmdExe(OperationLogGateway operationLogGateway,
    OperationLogConvertor operationLogConvertor) {
    this.operationLogGateway = operationLogGateway;
    this.operationLogConvertor = operationLogConvertor;
  }

  public OperationLogQryDTO execute(OperationLogQryCmd operationLogQryCmd) {
    return Optional.ofNullable(operationLogQryCmd).map(OperationLogQryCmd::getId)
      .flatMap(operationLogGateway::findOperationLogById).flatMap(operationLogConvertor::toQryDTO)
      .orElse(null);
  }
}
