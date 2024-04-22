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

import com.sky.centaur.basis.tools.SpringContextUtil;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;

/**
 * 系统日志转换器
 *
 * @author kaiyu.shan
 * @since 2024-01-31
 */
public class SystemLogConvertor {

  @Contract("_ -> new")
  public static @NotNull SystemLogKafkaDo toKafkaDataObject(@NotNull SystemLog systemLog) {
    SystemLogKafkaDo systemLogKafkaDo = new SystemLogKafkaDo();
    BeanUtils.copyProperties(systemLog, systemLogKafkaDo);
    return systemLogKafkaDo;
  }

  @Contract("_ -> new")
  public static @NotNull SystemLogEsDo toEsDataObject(@NotNull SystemLog systemLog) {
    SystemLogEsDo systemLogEsDo = new SystemLogEsDo();
    BeanUtils.copyProperties(systemLog, systemLogEsDo);
    return systemLogEsDo;
  }

  @Contract("_ -> new")
  public static @NotNull SystemLog toEntity(@NotNull SystemLogSubmitCo systemLogSubmitCo) {
    SystemLog systemLog = new SystemLog();
    BeanUtils.copyProperties(systemLogSubmitCo, systemLog);
    systemLog.setId(
        Optional.ofNullable(SpringContextUtil.getBean(Tracer.class).currentSpan())
            .map(span -> span.context().traceId()).orElseGet(() ->
                String.valueOf(SpringContextUtil.getBean(PrimaryKeyGrpcService.class).snowflake())));
    systemLog.setRecordTime(LocalDateTime.now(ZoneId.of("UTC")));
    return systemLog;
  }

  @Contract("_ -> new")
  public static @NotNull SystemLog toEntity(@NotNull SystemLogSaveCo systemLogSaveCo) {
    SystemLog systemLog = new SystemLog();
    BeanUtils.copyProperties(systemLogSaveCo, systemLog);
    return systemLog;
  }
}
