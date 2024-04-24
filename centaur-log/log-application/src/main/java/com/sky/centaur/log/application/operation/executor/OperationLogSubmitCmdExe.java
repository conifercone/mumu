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

import com.sky.centaur.log.client.dto.OperationLogSubmitCmd;
import com.sky.centaur.log.client.dto.co.OperationLogSubmitCo;
import com.sky.centaur.log.domain.operation.gateway.OperationLogGateway;
import com.sky.centaur.log.infrastructure.operation.convertor.OperationLogConvertor;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * 操作日志提交指令执行器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
public class OperationLogSubmitCmdExe {

  @Resource
  private OperationLogGateway operationLogGateway;

  public void execute(@NotNull OperationLogSubmitCmd operationLogSubmitCmd) {
    OperationLogSubmitCo operationLogSubmitCo = operationLogSubmitCmd.getOperationLogSubmitCo();
    operationLogGateway.submit(OperationLogConvertor.toEntity(operationLogSubmitCo));
  }
}
