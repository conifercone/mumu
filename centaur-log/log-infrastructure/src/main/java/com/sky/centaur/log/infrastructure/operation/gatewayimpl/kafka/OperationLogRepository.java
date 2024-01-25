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
package com.sky.centaur.log.infrastructure.operation.gatewayimpl.kafka;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.stereotype.Component;

/**
 * 操作日志kafka操作实例
 *
 * @author 单开宇
 * @since 2024-01-25
 */
@Component
public class OperationLogRepository extends KafkaTemplate<Object, Object> {

  public OperationLogRepository(@Autowired ProducerFactory<Object, Object> kafkaProducerFactory,
      @Autowired ProducerListener<Object, Object> kafkaProducerListener,
      @Autowired @NotNull ObjectProvider<RecordMessageConverter> messageConverter,
      @Autowired @NotNull KafkaProperties properties) {
    super(kafkaProducerFactory);
    PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
    messageConverter.ifUnique(this::setMessageConverter);
    map.from(kafkaProducerListener).to(this::setProducerListener);
    map.from(properties.getTemplate().getDefaultTopic()).to(this::setDefaultTopic);
    map.from(properties.getTemplate().getTransactionIdPrefix()).to(this::setTransactionIdPrefix);
    map.from(properties.getTemplate().isObservationEnabled()).to(this::setObservationEnabled);
  }
}
