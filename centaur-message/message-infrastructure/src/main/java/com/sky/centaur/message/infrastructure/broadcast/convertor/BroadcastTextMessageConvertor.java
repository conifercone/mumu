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
package com.sky.centaur.message.infrastructure.broadcast.convertor;

import com.sky.centaur.basis.kotlin.tools.SecurityContextUtil;
import com.sky.centaur.extension.translation.SimpleTextTranslation;
import com.sky.centaur.message.client.dto.co.BroadcastTextMessageFindAllYouSendCo;
import com.sky.centaur.message.client.dto.co.BroadcastTextMessageForwardCo;
import com.sky.centaur.message.domain.broadcast.BroadcastTextMessage;
import com.sky.centaur.message.infrastructure.broadcast.gatewayimpl.database.dataobject.BroadcastTextMessageDo;
import com.sky.centaur.message.infrastructure.config.MessageProperties;
import com.sky.centaur.unique.client.api.PrimaryKeyGrpcService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 文本广播消息转换器转换器
 *
 * @author kaiyu.shan
 * @since 1.0.2
 */
@Component
public class BroadcastTextMessageConvertor {


  private final PrimaryKeyGrpcService primaryKeyGrpcService;
  private final MessageProperties messageProperties;
  private final SimpleTextTranslation simpleTextTranslation;

  @Autowired
  public BroadcastTextMessageConvertor(PrimaryKeyGrpcService primaryKeyGrpcService,
      MessageProperties messageProperties,
      ObjectProvider<SimpleTextTranslation> simpleTextTranslations) {
    this.primaryKeyGrpcService = primaryKeyGrpcService;
    this.messageProperties = messageProperties;
    this.simpleTextTranslation = simpleTextTranslations.getIfAvailable();
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.2")
  public Optional<BroadcastTextMessage> toEntity(
      BroadcastTextMessageForwardCo broadcastTextMessageForwardCo) {
    return Optional.ofNullable(broadcastTextMessageForwardCo)
        .flatMap(res -> SecurityContextUtil.getLoginAccountId().map(senderAccountId -> {
          BroadcastTextMessage entity = BroadcastTextMessageMapper.INSTANCE.toEntity(res);
          entity.setSenderId(senderAccountId);
          entity.setReadReceiverIds(Collections.emptyList());
          Optional.ofNullable(entity.getReceiverIds())
              .ifPresentOrElse(receiverIds -> {
                    entity.setUnreadQuantity((long) receiverIds.size());
                    entity.setUnreadReceiverIds(receiverIds);
                  },
                  () -> {
                    ArrayList<Long> receiverIds = Collections.list(
                        messageProperties.getWebSocket().getAccountBroadcastChannelMap().keys());
                    entity.setReceiverIds(receiverIds);
                    entity.setUnreadReceiverIds(receiverIds);
                    entity.setUnreadQuantity((long) receiverIds.size());
                  });
          Optional.ofNullable(entity.getId()).ifPresentOrElse(id -> {
          }, () -> {
            Long id = primaryKeyGrpcService.snowflake();
            entity.setId(id);
            res.setId(id);
          });
          return entity;
        }));
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.2")
  public Optional<BroadcastTextMessageDo> toDataObject(
      BroadcastTextMessage broadcastTextMessage) {
    return Optional.ofNullable(broadcastTextMessage)
        .map(BroadcastTextMessageMapper.INSTANCE::toDataObject);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.3")
  public Optional<BroadcastTextMessage> toEntity(
      BroadcastTextMessageDo broadcastTextMessageDo) {
    return Optional.ofNullable(broadcastTextMessageDo)
        .map(BroadcastTextMessageMapper.INSTANCE::toEntity);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.3")
  public Optional<BroadcastTextMessage> toEntity(
      BroadcastTextMessageFindAllYouSendCo broadcastTextMessageFindAllYouSendCo) {
    return Optional.ofNullable(broadcastTextMessageFindAllYouSendCo)
        .map(BroadcastTextMessageMapper.INSTANCE::toEntity);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.3")
  public Optional<BroadcastTextMessageFindAllYouSendCo> toFindAllYouSendCo(
      BroadcastTextMessage broadcastTextMessage) {
    return Optional.ofNullable(broadcastTextMessage)
        .map(BroadcastTextMessageMapper.INSTANCE::toFindAllYouSendCo)
        .map(broadcastTextMessageFindAllYouSendCo -> {
          Optional.ofNullable(simpleTextTranslation).flatMap(
                  simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                      broadcastTextMessageFindAllYouSendCo.getMessage()))
              .ifPresent(broadcastTextMessageFindAllYouSendCo::setMessage);
          return broadcastTextMessageFindAllYouSendCo;
        });
  }
}
