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
package baby.mumu.message.infrastructure.broadcast.gatewayimpl;

import static baby.mumu.basis.constants.CommonConstants.LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE;

import baby.mumu.basis.annotations.DangerousOperation;
import baby.mumu.basis.enums.MessageStatusEnum;
import baby.mumu.basis.kotlin.tools.SecurityContextUtil;
import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.GlobalProperties;
import baby.mumu.message.domain.broadcast.BroadcastTextMessage;
import baby.mumu.message.domain.broadcast.gateway.BroadcastTextMessageGateway;
import baby.mumu.message.infrastructure.broadcast.convertor.BroadcastTextMessageConvertor;
import baby.mumu.message.infrastructure.broadcast.gatewayimpl.database.BroadcastTextMessageArchivedRepository;
import baby.mumu.message.infrastructure.broadcast.gatewayimpl.database.BroadcastTextMessageRepository;
import baby.mumu.message.infrastructure.broadcast.gatewayimpl.database.dataobject.BroadcastTextMessageDo;
import baby.mumu.message.infrastructure.broadcast.gatewayimpl.database.dataobject.BroadcastTextMessageDo_;
import baby.mumu.message.infrastructure.config.MessageProperties;
import baby.mumu.message.infrastructure.relations.database.BroadcastTextMessageReceiverDo;
import baby.mumu.message.infrastructure.relations.database.BroadcastTextMessageReceiverDoId;
import baby.mumu.message.infrastructure.relations.database.BroadcastTextMessageReceiverRepository;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 广播文本消息领域网关实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.2
 */
@Component
public class BroadcastTextMessageGatewayImpl implements BroadcastTextMessageGateway {


  private final BroadcastTextMessageConvertor broadcastTextMessageConvertor;
  private final MessageProperties messageProperties;
  private final BroadcastTextMessageRepository broadcastTextMessageRepository;
  private final BroadcastTextMessageArchivedRepository broadcastTextMessageArchivedRepository;
  private final JobScheduler jobScheduler;
  private final ExtensionProperties extensionProperties;
  private final BroadcastTextMessageReceiverRepository broadcastTextMessageReceiverRepository;

  @Autowired
  public BroadcastTextMessageGatewayImpl(
    BroadcastTextMessageConvertor broadcastTextMessageConvertor,
    MessageProperties messageProperties,
    BroadcastTextMessageRepository broadcastTextMessageRepository,
    BroadcastTextMessageArchivedRepository broadcastTextMessageArchivedRepository,
    JobScheduler jobScheduler, ExtensionProperties extensionProperties,
    BroadcastTextMessageReceiverRepository broadcastTextMessageReceiverRepository) {
    this.broadcastTextMessageConvertor = broadcastTextMessageConvertor;
    this.messageProperties = messageProperties;
    this.broadcastTextMessageRepository = broadcastTextMessageRepository;
    this.broadcastTextMessageArchivedRepository = broadcastTextMessageArchivedRepository;
    this.jobScheduler = jobScheduler;
    this.extensionProperties = extensionProperties;
    this.broadcastTextMessageReceiverRepository = broadcastTextMessageReceiverRepository;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void forwardMsg(BroadcastTextMessage msg) {
    Optional.ofNullable(msg).ifPresent(broadcastTextMessage -> Optional.ofNullable(
        messageProperties.getWebSocket().getAccountBroadcastChannelMap())
      .ifPresent(allOnlineAccountChannels -> broadcastTextMessageConvertor.toDataObject(
          broadcastTextMessage)
        .ifPresent(broadcastTextMessageDo -> {
          if (CollectionUtils.isNotEmpty(broadcastTextMessage.getReceiverIds())) {
            broadcastTextMessageRepository.persist(broadcastTextMessageDo);
            broadcastTextMessageReceiverRepository.mergeAll(
              broadcastTextMessageConvertor.toBroadcastTextMessageSenderReceiverDos(
                broadcastTextMessage));
            broadcastTextMessage.getReceiverIds().forEach(
              receiverId -> Optional.ofNullable(allOnlineAccountChannels.get(receiverId))
                .ifPresent(accountChannel -> accountChannel.writeAndFlush(
                  new TextWebSocketFrame(broadcastTextMessage.getMessage()))));
          } else {
            broadcastTextMessageRepository.persist(broadcastTextMessageDo);
          }
        })));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void readMsgById(Long id) {
    Optional.ofNullable(id).ifPresent(msgId -> SecurityContextUtil.getLoginAccountId()
      .flatMap(accountId -> broadcastTextMessageReceiverRepository.findById(
        BroadcastTextMessageReceiverDoId.builder().messageId(msgId).receiverId(accountId)
          .build())).ifPresent(broadcastTextMessageReceiverDo -> {
        broadcastTextMessageReceiverDo.setMessageStatus(
          MessageStatusEnum.READ);
        broadcastTextMessageReceiverRepository.merge(broadcastTextMessageReceiverDo);
        List<BroadcastTextMessageReceiverDo> messageSenderReceiverDos = broadcastTextMessageReceiverRepository.findByBroadcastTextMessageId(
            msgId).stream().filter(
            messageReceiverDo -> MessageStatusEnum.UNREAD.equals(
              messageReceiverDo.getMessageStatus()))
          .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(messageSenderReceiverDos)) {
          broadcastTextMessageRepository.findById(msgId).ifPresent(messageSenderReceiverDo -> {
            messageSenderReceiverDo.setMessageStatus(MessageStatusEnum.READ);
            broadcastTextMessageRepository.merge(messageSenderReceiverDo);
          });
        }
      }));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteMsgById(Long id) {
    Optional.ofNullable(id)
      .flatMap(msgId -> SecurityContextUtil.getLoginAccountId()).flatMap(accountId ->
        broadcastTextMessageRepository.findByIdAndSenderId(id, accountId)
      ).ifPresent(broadcastTextMessageDo -> {
        broadcastTextMessageRepository.deleteById(id);
        broadcastTextMessageReceiverRepository.deleteByBroadcastTextMessageId(id);
        broadcastTextMessageArchivedRepository.deleteById(id);
      });
  }

  @Override
  public Page<BroadcastTextMessage> findAllYouSend(BroadcastTextMessage broadcastTextMessage,
    int current,
    int pageSize) {
    return SecurityContextUtil.getLoginAccountId().map(accountId -> {
      Specification<BroadcastTextMessageDo> broadcastTextMessageDoSpecification = (root, query, cb) -> {
        List<Predicate> predicateList = new ArrayList<>();
        Optional.ofNullable(broadcastTextMessage).ifPresent(broadcastTextMessageEntity -> {
          Optional.ofNullable(broadcastTextMessageEntity.getMessage())
            .filter(StringUtils::isNotBlank)
            .ifPresent(
              message -> predicateList.add(cb.like(root.get(BroadcastTextMessageDo_.message),
                String.format(LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE, message))));
          Optional.ofNullable(broadcastTextMessageEntity.getMessageStatus()).ifPresent(
            messageStatusEnum -> predicateList.add(
              cb.equal(root.get(BroadcastTextMessageDo_.messageStatus), messageStatusEnum)));
        });
        predicateList.add(cb.equal(root.get(BroadcastTextMessageDo_.senderId), accountId));
        assert query != null;
        return query.orderBy(cb.desc(root.get(BroadcastTextMessageDo_.creationTime)))
          .where(predicateList.toArray(new Predicate[0]))
          .getRestriction();
      };
      PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
      Page<BroadcastTextMessageDo> repositoryAll = broadcastTextMessageRepository.findAll(
        broadcastTextMessageDoSpecification,
        pageRequest);
      List<BroadcastTextMessage> broadcastTextMessages = repositoryAll.getContent().stream()
        .map(broadcastTextMessageConvertor::toEntity)
        .filter(Optional::isPresent).map(Optional::get)
        .toList();
      return new PageImpl<>(broadcastTextMessages, pageRequest, repositoryAll.getTotalElements());
    }).orElse(new PageImpl<>(Collections.emptyList()));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void archiveMsgById(Long id) {
    //noinspection DuplicatedCode
    Optional.ofNullable(id).flatMap(msgId -> SecurityContextUtil.getLoginAccountId().flatMap(
        accountId -> broadcastTextMessageRepository.findByIdAndSenderId(msgId,
          accountId)))
      .ifPresent(broadcastTextMessageDo -> broadcastTextMessageConvertor.toArchiveDo(
        broadcastTextMessageDo).ifPresent(broadcastTextMessageArchivedDo -> {
        broadcastTextMessageArchivedDo.setArchived(true);
        broadcastTextMessageRepository.delete(broadcastTextMessageDo);
        broadcastTextMessageArchivedRepository.persist(broadcastTextMessageArchivedDo);
        GlobalProperties global = extensionProperties.getGlobal();
        jobScheduler.schedule(Instant.now()
            .plus(global.getArchiveDeletionPeriod(), global.getArchiveDeletionPeriodUnit()),
          () -> deleteArchivedDataJob(broadcastTextMessageArchivedDo.getId()));
      }));
  }

  @Job(name = "删除ID为：%0 的广播消息归档数据")
  @DangerousOperation("根据ID删除ID为%0的广播消息归档数据定时任务")
  public void deleteArchivedDataJob(Long id) {
    Optional.ofNullable(id)
      .ifPresent(messageId -> {
        broadcastTextMessageArchivedRepository.deleteById(messageId);
        broadcastTextMessageReceiverRepository.deleteByBroadcastTextMessageId(messageId);
      });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverMsgFromArchiveById(Long id) {
    Optional.ofNullable(id).flatMap(msgId -> SecurityContextUtil.getLoginAccountId().flatMap(
        accountId -> broadcastTextMessageArchivedRepository.findByIdAndSenderId(msgId,
          accountId)))
      .flatMap(
        broadcastTextMessageConvertor::toDataObject)
      .ifPresent(broadcastTextMessageDo -> {
        broadcastTextMessageDo.setArchived(false);
        broadcastTextMessageArchivedRepository.deleteById(broadcastTextMessageDo.getId());
        broadcastTextMessageRepository.persist(broadcastTextMessageDo);
      });
  }
}
