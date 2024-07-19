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
package com.sky.centaur.message.infrastructure.broadcast.gatewayimpl;

import com.sky.centaur.message.domain.broadcast.BroadcastTextMessage;
import com.sky.centaur.message.domain.broadcast.gateway.BroadcastTextMessageGateway;
import com.sky.centaur.message.infrastructure.broadcast.convertor.BroadcastTextMessageConvertor;
import com.sky.centaur.message.infrastructure.broadcast.gatewayimpl.database.BroadcastTextMessageRepository;
import com.sky.centaur.message.infrastructure.config.MessageProperties;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 广播文本消息领域网关实现
 *
 * @author kaiyu.shan
 * @since 1.0.2
 */
@Component
public class BroadcastTextMessageGatewayImpl implements BroadcastTextMessageGateway {


  private final BroadcastTextMessageConvertor broadcastTextMessageConvertor;
  private final MessageProperties messageProperties;
  private final BroadcastTextMessageRepository broadcastTextMessageRepository;

  public BroadcastTextMessageGatewayImpl(
      BroadcastTextMessageConvertor broadcastTextMessageConvertor,
      MessageProperties messageProperties,
      BroadcastTextMessageRepository broadcastTextMessageRepository) {
    this.broadcastTextMessageConvertor = broadcastTextMessageConvertor;
    this.messageProperties = messageProperties;
    this.broadcastTextMessageRepository = broadcastTextMessageRepository;
  }

  @Override
  @Transactional
  public void forwardMsg(BroadcastTextMessage msg) {
    Optional.ofNullable(msg).ifPresent(broadcastTextMessage -> Optional.ofNullable(
            messageProperties.getWebSocket().getAccountChannelMap())
        .ifPresent(allOnlineAccountChannels -> broadcastTextMessageConvertor.toDataObject(
                broadcastTextMessage)
            .ifPresent(broadcastTextMessageDo -> Optional.ofNullable(
                    broadcastTextMessageDo.getReceiverIds())
                .ifPresent(receiverIds -> {
                  broadcastTextMessageRepository.persist(broadcastTextMessageDo);
                  receiverIds.forEach(
                      receiverId -> Optional.ofNullable(allOnlineAccountChannels.get(receiverId))
                          .ifPresent(accountChannel -> accountChannel.writeAndFlush(
                              new TextWebSocketFrame(broadcastTextMessage.getMessage()))));
                }))));
  }
}
