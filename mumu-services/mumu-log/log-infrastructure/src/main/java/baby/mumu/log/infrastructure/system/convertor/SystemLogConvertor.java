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
package baby.mumu.log.infrastructure.system.convertor;

import baby.mumu.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import baby.mumu.log.client.dto.SystemLogFindAllCmd;
import baby.mumu.log.client.dto.SystemLogSaveCmd;
import baby.mumu.log.client.dto.SystemLogSubmitCmd;
import baby.mumu.log.client.dto.co.SystemLogFindAllCo;
import baby.mumu.log.domain.system.SystemLog;
import baby.mumu.log.infrastructure.system.gatewayimpl.elasticsearch.dataobject.SystemLogEsDo;
import baby.mumu.log.infrastructure.system.gatewayimpl.kafka.dataobject.SystemLogKafkaDo;
import baby.mumu.unique.client.api.PrimaryKeyGrpcService;
import io.micrometer.tracing.Tracer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 系统日志转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
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

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<SystemLogKafkaDo> toKafkaDataObject(SystemLog systemLog) {
    return Optional.ofNullable(systemLog)
      .map(SystemLogMapper.INSTANCE::toKafkaDataObject);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<SystemLogEsDo> toEsDataObject(SystemLog systemLog) {
    return Optional.ofNullable(systemLog)
      .map(SystemLogMapper.INSTANCE::toEsDataObject);
  }

  @Contract("_ -> new")
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

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<SystemLog> toEntity(SystemLogSaveCmd systemLogSaveCmd) {
    return Optional.ofNullable(systemLogSaveCmd)
      .map(SystemLogMapper.INSTANCE::toEntity);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<SystemLog> toEntity(SystemLogEsDo systemLogEsDo) {
    return Optional.ofNullable(systemLogEsDo)
      .map(SystemLogMapper.INSTANCE::toEntity);
  }

  @Contract("_ -> new")
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

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<SystemLogFindAllCo> toFindAllCo(SystemLog systemLog) {
    return Optional.ofNullable(systemLog)
      .map(SystemLogMapper.INSTANCE::toFindAllCo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<SystemLogSubmitCmd> toSystemLogSubmitCmd(
    SystemLogSubmitGrpcCmd systemLogSubmitGrpcCmd) {
    return Optional.ofNullable(systemLogSubmitGrpcCmd)
      .map(SystemLogMapper.INSTANCE::toSystemLogSubmitCmd);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<SystemLogSaveCmd> toSystemLogSaveCmd(
    SystemLogKafkaDo systemLogKafkaDo) {
    return Optional.ofNullable(systemLogKafkaDo)
      .map(SystemLogMapper.INSTANCE::toSystemLogSaveCmd);
  }
}
