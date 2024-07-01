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
package com.sky.centaur.log.infrastructure.system.convertor;

import com.sky.centaur.log.client.dto.co.SystemLogFindAllCo;
import com.sky.centaur.log.client.dto.co.SystemLogSaveCo;
import com.sky.centaur.log.client.dto.co.SystemLogSubmitCo;
import com.sky.centaur.log.domain.system.SystemLog;
import com.sky.centaur.log.infrastructure.system.gatewayimpl.elasticsearch.dataobject.SystemLogEsDo;
import com.sky.centaur.log.infrastructure.system.gatewayimpl.kafka.dataobject.SystemLogKafkaDo;
import com.sky.centaur.unique.client.api.PrimaryKeyGrpcService;
import io.micrometer.tracing.Tracer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.stereotype.Component;

/**
 * 系统日志转换器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
public class SystemLogConvertor {


  private final PrimaryKeyGrpcService primaryKeyGrpcService;
  private final Tracer tracer;

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
  public Optional<SystemLog> toEntity(SystemLogSubmitCo systemLogSubmitCo) {
    return Optional.ofNullable(systemLogSubmitCo).map(res -> {
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
  public Optional<SystemLog> toEntity(SystemLogSaveCo systemLogSaveCo) {
    return Optional.ofNullable(systemLogSaveCo)
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
      SystemLogFindAllCo systemLogFindAllCo) {
    return Optional.ofNullable(systemLogFindAllCo)
        .map(SystemLogMapper.INSTANCE::toEntity);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<SystemLogFindAllCo> toFindAllCo(SystemLog systemLog) {
    return Optional.ofNullable(systemLog)
        .map(SystemLogMapper.INSTANCE::toFindAllCo);
  }
}
