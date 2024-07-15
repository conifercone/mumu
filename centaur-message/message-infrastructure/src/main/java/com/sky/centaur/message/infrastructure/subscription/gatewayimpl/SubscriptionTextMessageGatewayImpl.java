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
package com.sky.centaur.message.infrastructure.subscription.gatewayimpl;

import com.sky.centaur.message.domain.subscription.SubscriptionTextMessage;
import com.sky.centaur.message.domain.subscription.gateway.SubscriptionTextMessageGateway;
import com.sky.centaur.message.infrastructure.config.MessageProperties;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 文本订阅消息领域网关实现类
 *
 * @author kaiyu.shan
 * @since 1.0.2
 */
@Component
public class SubscriptionTextMessageGatewayImpl implements SubscriptionTextMessageGateway {

  private final MessageProperties messageProperties;

  @Autowired
  public SubscriptionTextMessageGatewayImpl(MessageProperties messageProperties) {
    this.messageProperties = messageProperties;
  }

  @Override
  public void forwardMsg(SubscriptionTextMessage msg) {
    Optional.ofNullable(msg).ifPresent(subscriptionMessage -> Optional.ofNullable(
            messageProperties.getWebSocket().getAccountChannelMap()
                .get(subscriptionMessage.getSubscriptionAccountId()))
        .ifPresent(channel -> channel.writeAndFlush(
            new TextWebSocketFrame(subscriptionMessage.getMessage()))));
  }
}
