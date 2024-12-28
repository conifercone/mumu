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
package baby.mumu.log.infrastructure.operation.convertor;

import baby.mumu.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import baby.mumu.log.client.cmds.OperationLogFindAllCmd;
import baby.mumu.log.client.cmds.OperationLogSaveCmd;
import baby.mumu.log.client.cmds.OperationLogSubmitCmd;
import baby.mumu.log.client.dto.OperationLogFindAllDTO;
import baby.mumu.log.client.dto.OperationLogQryDTO;
import baby.mumu.log.domain.operation.OperationLog;
import baby.mumu.log.infrastructure.operation.gatewayimpl.elasticsearch.po.OperationLogEsPO;
import baby.mumu.log.infrastructure.operation.gatewayimpl.kafka.po.OperationLogKafkaPO;
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
 * 操作日志转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
public class OperationLogConvertor {


  private final PrimaryKeyGrpcService primaryKeyGrpcService;
  private final Tracer tracer;

  @Autowired
  public OperationLogConvertor(PrimaryKeyGrpcService primaryKeyGrpcService, Tracer tracer) {
    this.primaryKeyGrpcService = primaryKeyGrpcService;
    this.tracer = tracer;
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<OperationLogKafkaPO> toKafkaPO(OperationLog operationLog) {
    return Optional.ofNullable(operationLog).map(OperationLogMapper.INSTANCE::toKafkaPO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<OperationLogEsPO> toEsPO(OperationLog operationLog) {
    return Optional.ofNullable(operationLog).map(OperationLogMapper.INSTANCE::toEsPO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<OperationLog> toEntity(OperationLogSubmitCmd operationLogSubmitCmd) {
    return Optional.ofNullable(operationLogSubmitCmd).map(res -> {
      OperationLog operationLog = OperationLogMapper.INSTANCE.toEntity(res);
      operationLog.setId(
        Optional.ofNullable(tracer.currentSpan())
          .map(span -> span.context().traceId()).orElseGet(() ->
            String.valueOf(primaryKeyGrpcService.snowflake()))
      );
      operationLog.setOperatingTime(LocalDateTime.now(ZoneId.of("UTC")));
      return operationLog;
    });

  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<OperationLog> toEntity(OperationLogSaveCmd operationLogSaveCmd) {
    return Optional.ofNullable(operationLogSaveCmd).map(OperationLogMapper.INSTANCE::toEntity);
  }


  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<OperationLog> toEntity(OperationLogEsPO operationLogEsPO) {
    return Optional.ofNullable(operationLogEsPO).map(OperationLogMapper.INSTANCE::toEntity);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<OperationLog> toEntity(
    OperationLogFindAllCmd operationLogFindAllCmd) {
    return Optional.ofNullable(operationLogFindAllCmd)
      .map(OperationLogMapper.INSTANCE::toEntity).map(operationLog -> {
        Optional.ofNullable(operationLogFindAllCmd.getOperatingStartTime())
          .ifPresent(operationLog::setOperatingStartTime);
        Optional.ofNullable(operationLogFindAllCmd.getOperatingEndTime())
          .ifPresent(operationLog::setOperatingEndTime);
        return operationLog;
      });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<OperationLogFindAllDTO> toFindAllDTO(OperationLog operationLog) {
    return Optional.ofNullable(operationLog).map(OperationLogMapper.INSTANCE::toFindAllDTO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<OperationLogSubmitCmd> toOperationLogSubmitCmd(
    OperationLogSubmitGrpcCmd operationLogSubmitGrpcCmd) {
    return Optional.ofNullable(operationLogSubmitGrpcCmd)
      .map(OperationLogMapper.INSTANCE::toOperationLogSubmitCmd);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<OperationLogSaveCmd> toOperationLogSaveCmd(
    OperationLogKafkaPO operationLogKafkaPO) {
    return Optional.ofNullable(operationLogKafkaPO)
      .map(OperationLogMapper.INSTANCE::toOperationLogSaveCmd);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<OperationLogQryDTO> toQryDTO(
    OperationLog operationLog) {
    return Optional.ofNullable(operationLog)
      .map(OperationLogMapper.INSTANCE::toQryDTO);
  }
}
