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
package com.sky.centaur.log.application.consumer.system;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.centaur.log.client.api.SystemLogService;
import com.sky.centaur.log.client.dto.SystemLogSaveCmd;
import com.sky.centaur.log.client.dto.co.SystemLogSaveCo;
import com.sky.centaur.log.infrastructure.system.gatewayimpl.kafka.dataobject.SystemLogKafkaDo;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 系统日志消费者
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
@Observed(name = "SystemLogConsumer")
public class SystemLogConsumer {

  @Resource
  private ObjectMapper objectMapper;

  @Resource
  private SystemLogService systemLogService;

  @KafkaListener(topics = {"system-log"})
  public void handle(String systemLog) throws JsonProcessingException {
    SystemLogKafkaDo systemLogKafkaDo = objectMapper.readValue(systemLog,
        SystemLogKafkaDo.class);
    SystemLogSaveCmd systemLogSaveCmd = new SystemLogSaveCmd();
    SystemLogSaveCo systemLogSaveCo = new SystemLogSaveCo();
    BeanUtils.copyProperties(systemLogKafkaDo, systemLogSaveCo);
    systemLogSaveCmd.setSystemLogSaveCo(systemLogSaveCo);
    systemLogService.save(systemLogSaveCmd);
  }
}
