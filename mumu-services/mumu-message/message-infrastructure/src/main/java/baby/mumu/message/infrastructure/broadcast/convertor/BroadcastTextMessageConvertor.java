/*
 * Copyright (c) 2024-2024, the original author or authors.
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
package baby.mumu.message.infrastructure.broadcast.convertor;

import baby.mumu.basis.enums.MessageStatusEnum;
import baby.mumu.basis.kotlin.tools.SecurityContextUtil;
import baby.mumu.extension.translation.SimpleTextTranslation;
import baby.mumu.message.client.dto.BroadcastTextMessageFindAllYouSendCmd;
import baby.mumu.message.client.dto.co.BroadcastTextMessageFindAllYouSendCo;
import baby.mumu.message.client.dto.co.BroadcastTextMessageForwardCo;
import baby.mumu.message.domain.broadcast.BroadcastTextMessage;
import baby.mumu.message.infrastructure.broadcast.gatewayimpl.database.BroadcastTextMessageRepository;
import baby.mumu.message.infrastructure.broadcast.gatewayimpl.database.dataobject.BroadcastTextMessageArchivedDo;
import baby.mumu.message.infrastructure.broadcast.gatewayimpl.database.dataobject.BroadcastTextMessageDo;
import baby.mumu.message.infrastructure.config.MessageProperties;
import baby.mumu.message.infrastructure.relations.database.BroadcastTextMessageReceiverDo;
import baby.mumu.message.infrastructure.relations.database.BroadcastTextMessageReceiverDoId;
import baby.mumu.message.infrastructure.relations.database.BroadcastTextMessageReceiverRepository;
import baby.mumu.unique.client.api.PrimaryKeyGrpcService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 文本广播消息转换器转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.2
 */
@Component
public class BroadcastTextMessageConvertor {


  private final PrimaryKeyGrpcService primaryKeyGrpcService;
  private final MessageProperties messageProperties;
  private final SimpleTextTranslation simpleTextTranslation;
  private final BroadcastTextMessageReceiverRepository broadcastTextMessageReceiverRepository;
  private final BroadcastTextMessageRepository broadcastTextMessageRepository;

  @Autowired
  public BroadcastTextMessageConvertor(PrimaryKeyGrpcService primaryKeyGrpcService,
      MessageProperties messageProperties,
      ObjectProvider<SimpleTextTranslation> simpleTextTranslations,
      BroadcastTextMessageReceiverRepository broadcastTextMessageReceiverRepository,
      BroadcastTextMessageRepository broadcastTextMessageRepository) {
    this.primaryKeyGrpcService = primaryKeyGrpcService;
    this.messageProperties = messageProperties;
    this.simpleTextTranslation = simpleTextTranslations.getIfAvailable();
    this.broadcastTextMessageReceiverRepository = broadcastTextMessageReceiverRepository;
    this.broadcastTextMessageRepository = broadcastTextMessageRepository;
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
  @API(status = Status.STABLE, since = "2.2.0")
  public List<BroadcastTextMessageReceiverDo> toBroadcastTextMessageSenderReceiverDos(
      BroadcastTextMessage broadcastTextMessage) {
    return Optional.ofNullable(broadcastTextMessage)
        .flatMap(broadcastTextMessageNotNull ->
            Optional.ofNullable(broadcastTextMessageNotNull.getReceiverIds())
                .map(receiverIds -> receiverIds.stream().map(receiverId -> {
                  BroadcastTextMessageReceiverDo broadcastTextMessageReceiverDo = new BroadcastTextMessageReceiverDo();
                  broadcastTextMessageReceiverDo.setMessageStatus(
                      broadcastTextMessageNotNull.getMessageStatus());
                  broadcastTextMessageReceiverDo.setId(
                      BroadcastTextMessageReceiverDoId.builder()
                          .messageId(broadcastTextMessageNotNull.getId())
                          .receiverId(receiverId)
                          .build());
                  broadcastTextMessageRepository.findById(broadcastTextMessage.getId())
                      .ifPresent(broadcastTextMessageReceiverDo::setBroadcastTextMessage);
                  return broadcastTextMessageReceiverDo;
                }).collect(Collectors.toList()))
        ).orElse(new ArrayList<>());
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.3")
  public Optional<BroadcastTextMessage> toEntity(
      BroadcastTextMessageDo broadcastTextMessageDo) {
    return Optional.ofNullable(broadcastTextMessageDo)
        .map(BroadcastTextMessageMapper.INSTANCE::toEntity).map(broadcastTextMessage -> {
          broadcastTextMessage.setReceiverIds(
              broadcastTextMessageReceiverRepository.findReceiverIdsByMessageId(
                  broadcastTextMessage.getId()));
          broadcastTextMessage.setReadQuantity(
              broadcastTextMessageReceiverRepository.countByMessageIdAndMessageStatus(
                  broadcastTextMessage.getId(), MessageStatusEnum.READ));
          broadcastTextMessage.setUnreadQuantity(
              broadcastTextMessageReceiverRepository.countByMessageIdAndMessageStatus(
                  broadcastTextMessage.getId(), MessageStatusEnum.UNREAD));
          broadcastTextMessage.setReadReceiverIds(
              broadcastTextMessageReceiverRepository.findReceiverIdsByMessageIdAndMessageStatus(
                  broadcastTextMessage.getId(), MessageStatusEnum.READ));
          broadcastTextMessage.setUnreadReceiverIds(
              broadcastTextMessageReceiverRepository.findReceiverIdsByMessageIdAndMessageStatus(
                  broadcastTextMessage.getId(), MessageStatusEnum.UNREAD));
          return broadcastTextMessage;
        });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.3")
  public Optional<BroadcastTextMessage> toEntity(
      BroadcastTextMessageFindAllYouSendCmd broadcastTextMessageFindAllYouSendCmd) {
    return Optional.ofNullable(broadcastTextMessageFindAllYouSendCmd)
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

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<BroadcastTextMessageArchivedDo> toArchiveDo(
      BroadcastTextMessageDo broadcastTextMessageDo) {
    return Optional.ofNullable(broadcastTextMessageDo)
        .map(BroadcastTextMessageMapper.INSTANCE::toArchiveDo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<BroadcastTextMessageDo> toDataObject(
      BroadcastTextMessageArchivedDo broadcastTextMessageArchivedDo) {
    return Optional.ofNullable(broadcastTextMessageArchivedDo)
        .map(BroadcastTextMessageMapper.INSTANCE::toDataObject);
  }
}
