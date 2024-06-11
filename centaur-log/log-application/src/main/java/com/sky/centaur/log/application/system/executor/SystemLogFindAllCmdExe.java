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

import com.sky.centaur.log.client.dto.SystemLogFindAllCmd;
import com.sky.centaur.log.client.dto.co.SystemLogFindAllCo;
import com.sky.centaur.log.domain.system.SystemLog;
import com.sky.centaur.log.domain.system.gateway.SystemLogGateway;
import com.sky.centaur.log.infrastructure.system.convertor.SystemLogConvertor;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 系统日志查询所有指令执行器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
public class SystemLogFindAllCmdExe {

  private final SystemLogGateway systemLogGateway;

  @Autowired
  public SystemLogFindAllCmdExe(SystemLogGateway systemLogGateway) {
    this.systemLogGateway = systemLogGateway;
  }

  public Page<SystemLogFindAllCo> execute(
      @NotNull SystemLogFindAllCmd systemLogFindAllCmd) {
    Assert.notNull(systemLogFindAllCmd, "SystemLogFindAllCmd cannot be null");
    SystemLog systemLog = SystemLogConvertor.toEntity(
            systemLogFindAllCmd.getSystemLogFindAllCo())
        .orElseGet(SystemLog::new);
    Optional.ofNullable(systemLogFindAllCmd.getRecordStartTime())
        .ifPresent(systemLog::setRecordStartTime);
    Optional.ofNullable(systemLogFindAllCmd.getRecordEndTime())
        .ifPresent(systemLog::setRecordEndTime);
    Page<SystemLog> systemLogs = systemLogGateway.findAll(
        systemLog,
        systemLogFindAllCmd.getPageNo(),
        systemLogFindAllCmd.getPageSize());
    List<SystemLogFindAllCo> systemLogFindAllCos = systemLogs.getContent().stream()
        .map(res -> SystemLogConvertor.toFindAllCo(res).orElse(null)).filter(Objects::nonNull)
        .toList();
    return new PageImpl<>(systemLogFindAllCos, systemLogs.getPageable(),
        systemLogs.getTotalElements());
  }

}
