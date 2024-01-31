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
package com.sky.centaur.log.infrastructure.system.gatewayimpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.centaur.extension.exception.DataConversionException;
import com.sky.centaur.log.domain.system.SystemLog;
import com.sky.centaur.log.domain.system.gateway.SystemLogGateway;
import com.sky.centaur.log.infrastructure.system.convertor.SystemLogConvertor;
import com.sky.centaur.log.infrastructure.system.gatewayimpl.elasticsearch.SystemLogEsRepository;
import com.sky.centaur.log.infrastructure.system.gatewayimpl.kafka.SystemLogKafkaRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 系统日志领域网关实现
 *
 * @author 单开宇
 * @since 2024-01-31
 */
@Component
public class SystemLogGatewayImpl implements SystemLogGateway {

  @Resource
  private SystemLogKafkaRepository systemLogKafkaRepository;

  @Resource
  private SystemLogEsRepository systemLogEsRepository;


  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void submit(SystemLog systemLog) {
    try {
      systemLogKafkaRepository.send("system-log", objectMapper.writeValueAsString(
          SystemLogConvertor.toKafkaDataObject(systemLog)));
    } catch (JsonProcessingException e) {
      throw new DataConversionException();
    }
  }

  @Override
  public void save(SystemLog systemLog) {
    systemLogEsRepository.save(SystemLogConvertor.toEsDataObject(systemLog));
  }
}
