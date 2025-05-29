/*
 * Copyright (c) 2024-2025, the original author or authors.
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
import baby.mumu.basis.kotlin.tools.SecurityContextUtils;
import baby.mumu.extension.translation.SimpleTextTranslation;
import baby.mumu.message.client.cmds.BroadcastTextMessageFindAllYouSendCmd;
import baby.mumu.message.client.cmds.BroadcastTextMessageForwardCmd;
import baby.mumu.message.client.dto.BroadcastTextMessageFindAllYouSendDTO;
import baby.mumu.message.domain.broadcast.BroadcastTextMessage;
import baby.mumu.message.infrastructure.broadcast.gatewayimpl.database.BroadcastTextMessageRepository;
import baby.mumu.message.infrastructure.broadcast.gatewayimpl.database.po.BroadcastTextMessageArchivedPO;
import baby.mumu.message.infrastructure.broadcast.gatewayimpl.database.po.BroadcastTextMessagePO;
import baby.mumu.message.infrastructure.config.MessageProperties;
import baby.mumu.message.infrastructure.relations.database.BroadcastTextMessageReceiverPO;
import baby.mumu.message.infrastructure.relations.database.BroadcastTextMessageReceiverPOId;
import baby.mumu.message.infrastructure.relations.database.BroadcastTextMessageReceiverRepository;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.2
 */
@Component
public class BroadcastTextMessageConvertor {


  private final MessageProperties messageProperties;
  private final SimpleTextTranslation simpleTextTranslation;
  private final BroadcastTextMessageReceiverRepository broadcastTextMessageReceiverRepository;
  private final BroadcastTextMessageRepository broadcastTextMessageRepository;

  @Autowired
  public BroadcastTextMessageConvertor(MessageProperties messageProperties,
    ObjectProvider<SimpleTextTranslation> simpleTextTranslations,
    BroadcastTextMessageReceiverRepository broadcastTextMessageReceiverRepository,
    BroadcastTextMessageRepository broadcastTextMessageRepository) {
    this.messageProperties = messageProperties;
    this.simpleTextTranslation = simpleTextTranslations.getIfAvailable();
    this.broadcastTextMessageReceiverRepository = broadcastTextMessageReceiverRepository;
    this.broadcastTextMessageRepository = broadcastTextMessageRepository;
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.2")
  public Optional<BroadcastTextMessage> toEntity(
    BroadcastTextMessageForwardCmd broadcastTextMessageForwardCmd) {
    return Optional.ofNullable(broadcastTextMessageForwardCmd)
      .flatMap(res -> SecurityContextUtils.getLoginAccountId().map(senderAccountId -> {
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
        return entity;
      }));
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.2")
  public Optional<BroadcastTextMessagePO> toPO(
    BroadcastTextMessage broadcastTextMessage) {
    return Optional.ofNullable(broadcastTextMessage)
      .map(BroadcastTextMessageMapper.INSTANCE::toPO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public List<BroadcastTextMessageReceiverPO> toBroadcastTextMessageSenderReceiverDos(
    BroadcastTextMessage broadcastTextMessage) {
    return Optional.ofNullable(broadcastTextMessage)
      .flatMap(broadcastTextMessageNotNull ->
        Optional.ofNullable(broadcastTextMessageNotNull.getReceiverIds())
          .map(receiverIds -> receiverIds.stream().map(receiverId -> {
            BroadcastTextMessageReceiverPO broadcastTextMessageReceiverPO = new BroadcastTextMessageReceiverPO();
            broadcastTextMessageReceiverPO.setMessageStatus(
              broadcastTextMessageNotNull.getMessageStatus());
            broadcastTextMessageReceiverPO.setId(
              BroadcastTextMessageReceiverPOId.builder()
                .messageId(broadcastTextMessageNotNull.getId())
                .receiverId(receiverId)
                .build());
            broadcastTextMessageRepository.findById(broadcastTextMessage.getId())
              .ifPresent(broadcastTextMessageReceiverPO::setBroadcastTextMessage);
            return broadcastTextMessageReceiverPO;
          }).collect(Collectors.toList()))
      ).orElse(new ArrayList<>());
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.3")
  public Optional<BroadcastTextMessage> toEntity(
    BroadcastTextMessagePO broadcastTextMessagePO) {
    return Optional.ofNullable(broadcastTextMessagePO)
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
  public Optional<BroadcastTextMessageFindAllYouSendDTO> toFindAllYouSendDTO(
    BroadcastTextMessage broadcastTextMessage) {
    return Optional.ofNullable(broadcastTextMessage)
      .map(BroadcastTextMessageMapper.INSTANCE::toFindAllYouSendDTO)
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
  public Optional<BroadcastTextMessageArchivedPO> toArchivePO(
    BroadcastTextMessagePO broadcastTextMessagePO) {
    return Optional.ofNullable(broadcastTextMessagePO)
      .map(BroadcastTextMessageMapper.INSTANCE::toArchivePO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<BroadcastTextMessagePO> toPO(
    BroadcastTextMessageArchivedPO broadcastTextMessageArchivedPO) {
    return Optional.ofNullable(broadcastTextMessageArchivedPO)
      .map(BroadcastTextMessageMapper.INSTANCE::toPO);
  }
}
