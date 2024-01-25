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
package com.sky.centaur.log.application.consumer.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 操作日志消费者
 *
 * @author 单开宇
 * @since 2024-01-25
 */
@Component
public class OperationLogConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(OperationLogConsumer.class);

  @KafkaListener(topics = {"operation-log"})
  public void handle(String operationLog) {
    LOGGER.info("接收到消息: {}", operationLog);
  }
}
