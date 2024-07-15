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
package com.sky.centaur.message.infrastructure.subscription.convertor;

import com.sky.centaur.message.client.dto.co.SubscriptionTextMessageForwardCo;
import com.sky.centaur.message.domain.subscription.SubscriptionTextMessage;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.stereotype.Component;

/**
 * 文本订阅消息转换器转换器
 *
 * @author kaiyu.shan
 * @since 1.0.2
 */
@Component
public class SubscriptionTextMessageConvertor {

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.2")
  public Optional<SubscriptionTextMessage> toEntity(
      SubscriptionTextMessageForwardCo subscriptionTextMessageForwardCo) {
    return Optional.ofNullable(subscriptionTextMessageForwardCo).map(
        SubscriptionTextMessageMapper.INSTANCE::toEntity);
  }
}
