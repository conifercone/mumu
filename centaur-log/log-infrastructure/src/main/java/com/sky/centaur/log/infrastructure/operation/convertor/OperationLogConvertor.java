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
package com.sky.centaur.log.infrastructure.operation.convertor;

import com.sky.centaur.basis.kotlin.tools.SpringContextUtil;
import com.sky.centaur.log.client.dto.co.OperationLogFindAllCo;
import com.sky.centaur.log.client.dto.co.OperationLogSaveCo;
import com.sky.centaur.log.client.dto.co.OperationLogSubmitCo;
import com.sky.centaur.log.domain.operation.OperationLog;
import com.sky.centaur.log.infrastructure.operation.gatewayimpl.elasticsearch.dataobject.OperationLogEsDo;
import com.sky.centaur.log.infrastructure.operation.gatewayimpl.kafka.dataobject.OperationLogKafkaDo;
import com.sky.centaur.unique.client.api.PrimaryKeyGrpcService;
import io.micrometer.tracing.Tracer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;

/**
 * 操作日志转换器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public final class OperationLogConvertor {

  private OperationLogConvertor() {
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<OperationLogKafkaDo> toKafkaDataObject(OperationLog operationLog) {
    return Optional.ofNullable(operationLog).map(OperationLogMapper.INSTANCE::toKafkaDataObject);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<OperationLogEsDo> toEsDataObject(OperationLog operationLog) {
    return Optional.ofNullable(operationLog).map(OperationLogMapper.INSTANCE::toEsDataObject);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<OperationLog> toEntity(OperationLogSubmitCo operationLogSubmitCo) {
    return Optional.ofNullable(operationLogSubmitCo).map(res -> {
      OperationLog operationLog = OperationLogMapper.INSTANCE.toEntity(res);
      operationLog.setId(
          Optional.ofNullable(SpringContextUtil.getBean(Tracer.class).currentSpan())
              .map(span -> span.context().traceId()).orElseGet(() ->
                  String.valueOf(SpringContextUtil.getBean(PrimaryKeyGrpcService.class).snowflake()))
      );
      operationLog.setOperatingTime(LocalDateTime.now(ZoneId.of("UTC")));
      return operationLog;
    });

  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<OperationLog> toEntity(OperationLogSaveCo operationLogSaveCo) {
    return Optional.ofNullable(operationLogSaveCo).map(OperationLogMapper.INSTANCE::toEntity);
  }


  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<OperationLog> toEntity(OperationLogEsDo operationLogEsDo) {
    return Optional.ofNullable(operationLogEsDo).map(OperationLogMapper.INSTANCE::toEntity);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<OperationLog> toEntity(
      OperationLogFindAllCo operationLogFindAllCo) {
    return Optional.ofNullable(operationLogFindAllCo).map(OperationLogMapper.INSTANCE::toEntity);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<OperationLogFindAllCo> toFindAllCo(OperationLog operationLog) {
    return Optional.ofNullable(operationLog).map(OperationLogMapper.INSTANCE::toFindAllCo);
  }

}
