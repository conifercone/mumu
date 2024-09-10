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
package baby.mumu.log.application.system.executor;

import baby.mumu.log.client.dto.SystemLogFindAllCmd;
import baby.mumu.log.client.dto.co.SystemLogFindAllCo;
import baby.mumu.log.domain.system.SystemLog;
import baby.mumu.log.domain.system.gateway.SystemLogGateway;
import baby.mumu.log.infrastructure.system.convertor.SystemLogConvertor;
import java.util.List;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
public class SystemLogFindAllCmdExe {

  private final SystemLogGateway systemLogGateway;
  private final SystemLogConvertor systemLogConvertor;

  @Autowired
  public SystemLogFindAllCmdExe(SystemLogGateway systemLogGateway,
      SystemLogConvertor systemLogConvertor) {
    this.systemLogGateway = systemLogGateway;
    this.systemLogConvertor = systemLogConvertor;
  }

  public Page<SystemLogFindAllCo> execute(
      @NotNull SystemLogFindAllCmd systemLogFindAllCmd) {
    Assert.notNull(systemLogFindAllCmd, "SystemLogFindAllCmd cannot be null");
    SystemLog systemLog = systemLogConvertor.toEntity(
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
        .map(systemLogConvertor::toFindAllCo).filter(Optional::isPresent).map(Optional::get)
        .toList();
    return new PageImpl<>(systemLogFindAllCos, systemLogs.getPageable(),
        systemLogs.getTotalElements());
  }

}
