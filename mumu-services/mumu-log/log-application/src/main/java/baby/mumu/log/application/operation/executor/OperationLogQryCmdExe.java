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

import baby.mumu.log.client.dto.OperationLogQryCmd;
import baby.mumu.log.client.dto.co.OperationLogQryCo;
import baby.mumu.log.domain.operation.gateway.OperationLogGateway;
import baby.mumu.log.infrastructure.operation.convertor.OperationLogMapper;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 操作日志查询指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
public class OperationLogQryCmdExe {

  private final OperationLogGateway operationLogGateway;

  @Autowired
  public OperationLogQryCmdExe(OperationLogGateway operationLogGateway) {
    this.operationLogGateway = operationLogGateway;
  }

  public OperationLogQryCo execute(@NotNull OperationLogQryCmd operationLogQryCmd) {
    AtomicReference<OperationLogQryCo> operationLogQryCo = new AtomicReference<>();
    operationLogGateway.findOperationLogById(
        operationLogQryCmd.getId()).ifPresent(operationLog -> {
      OperationLogQryCo operationLogQryCoTmp = OperationLogMapper.INSTANCE.toQryCo(operationLog);
      operationLogQryCo.set(operationLogQryCoTmp);
    });
    return operationLogQryCo.get();
  }

}
