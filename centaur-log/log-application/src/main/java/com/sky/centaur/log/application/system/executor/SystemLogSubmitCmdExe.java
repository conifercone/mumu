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
package com.sky.centaur.log.application.system.executor;

import com.sky.centaur.log.client.dto.SystemLogSubmitCmd;
import com.sky.centaur.log.client.dto.co.SystemLogSubmitCo;
import com.sky.centaur.log.domain.system.gateway.SystemLogGateway;
import com.sky.centaur.log.infrastructure.system.convertor.SystemLogConvertor;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * 系统日志提交指令执行器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
public class SystemLogSubmitCmdExe {

  @Resource
  private SystemLogGateway systemLogGateway;

  public void execute(@NotNull SystemLogSubmitCmd systemLogSubmitCmd) {
    SystemLogSubmitCo systemLogSubmitCo = systemLogSubmitCmd.getSystemLogSubmitCo();
    systemLogGateway.submit(SystemLogConvertor.toEntity(systemLogSubmitCo));
  }
}
