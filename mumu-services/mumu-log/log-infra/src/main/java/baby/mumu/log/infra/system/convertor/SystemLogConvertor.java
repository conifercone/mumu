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

package baby.mumu.log.infra.system.convertor;

import baby.mumu.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import baby.mumu.log.client.cmds.SystemLogFindAllCmd;
import baby.mumu.log.client.cmds.SystemLogSaveCmd;
import baby.mumu.log.client.cmds.SystemLogSubmitCmd;
import baby.mumu.log.client.dto.SystemLogFindAllDTO;
import baby.mumu.log.domain.system.SystemLog;
import baby.mumu.log.infra.system.gatewayimpl.elasticsearch.po.SystemLogEsPO;
import baby.mumu.log.infra.system.gatewayimpl.kafka.po.SystemLogKafkaPO;
import baby.mumu.unique.client.api.PrimaryKeyGrpcService;
import io.micrometer.tracing.Tracer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 系统日志转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
public class SystemLogConvertor {


  private final PrimaryKeyGrpcService primaryKeyGrpcService;
  private final Tracer tracer;

  @Autowired
  public SystemLogConvertor(PrimaryKeyGrpcService primaryKeyGrpcService, Tracer tracer) {
    this.primaryKeyGrpcService = primaryKeyGrpcService;
    this.tracer = tracer;
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<SystemLogKafkaPO> toSystemLogKafkaPO(SystemLog systemLog) {
    return Optional.ofNullable(systemLog)
      .map(SystemLogMapper.INSTANCE::toSystemLogKafkaPO);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<SystemLogEsPO> toSystemLogEsPO(SystemLog systemLog) {
    return Optional.ofNullable(systemLog)
      .map(SystemLogMapper.INSTANCE::toSystemLogEsPO);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<SystemLog> toEntity(SystemLogSubmitCmd systemLogSubmitCmd) {
    return Optional.ofNullable(systemLogSubmitCmd).map(res -> {
      SystemLog systemLog = SystemLogMapper.INSTANCE.toEntity(res);
      systemLog.setId(
        Optional.ofNullable(tracer.currentSpan())
          .map(span -> span.context().traceId()).orElseGet(() ->
            String.valueOf(primaryKeyGrpcService.snowflake())));
      systemLog.setRecordTime(LocalDateTime.now(ZoneId.of("UTC")));
      return systemLog;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<SystemLog> toEntity(SystemLogSaveCmd systemLogSaveCmd) {
    return Optional.ofNullable(systemLogSaveCmd)
      .map(SystemLogMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<SystemLog> toEntity(SystemLogEsPO systemLogEsPO) {
    return Optional.ofNullable(systemLogEsPO)
      .map(SystemLogMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<SystemLog> toEntity(
    SystemLogFindAllCmd systemLogFindAllCmd) {
    return Optional.ofNullable(systemLogFindAllCmd)
      .map(SystemLogMapper.INSTANCE::toEntity).map(systemLog -> {
        Optional.ofNullable(systemLogFindAllCmd.getRecordStartTime())
          .ifPresent(systemLog::setRecordStartTime);
        Optional.ofNullable(systemLogFindAllCmd.getRecordEndTime())
          .ifPresent(systemLog::setRecordEndTime);
        return systemLog;
      });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<SystemLogFindAllDTO> toSystemLogFindAllDTO(SystemLog systemLog) {
    return Optional.ofNullable(systemLog)
      .map(SystemLogMapper.INSTANCE::toSystemLogFindAllDTO);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<SystemLogSubmitCmd> toSystemLogSubmitCmd(
    SystemLogSubmitGrpcCmd systemLogSubmitGrpcCmd) {
    return Optional.ofNullable(systemLogSubmitGrpcCmd)
      .map(SystemLogMapper.INSTANCE::toSystemLogSubmitCmd);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<SystemLogSaveCmd> toSystemLogSaveCmd(
    SystemLogKafkaPO systemLogKafkaPO) {
    return Optional.ofNullable(systemLogKafkaPO)
      .map(SystemLogMapper.INSTANCE::toSystemLogSaveCmd);
  }
}
