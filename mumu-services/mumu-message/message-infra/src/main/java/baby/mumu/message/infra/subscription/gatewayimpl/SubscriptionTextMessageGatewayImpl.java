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

package baby.mumu.message.infra.subscription.gatewayimpl;

import static baby.mumu.basis.constants.CommonConstants.LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE;

import baby.mumu.basis.annotations.DangerousOperation;
import baby.mumu.basis.enums.MessageStatusEnum;
import baby.mumu.basis.kotlin.tools.SecurityContextUtils;
import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.GlobalProperties;
import baby.mumu.message.domain.subscription.SubscriptionTextMessage;
import baby.mumu.message.domain.subscription.gateway.SubscriptionTextMessageGateway;
import baby.mumu.message.infra.config.MessageProperties;
import baby.mumu.message.infra.subscription.convertor.SubscriptionTextMessageConvertor;
import baby.mumu.message.infra.subscription.gatewayimpl.database.SubscriptionTextMessageArchivedRepository;
import baby.mumu.message.infra.subscription.gatewayimpl.database.SubscriptionTextMessageRepository;
import baby.mumu.message.infra.subscription.gatewayimpl.database.po.SubscriptionTextMessagePO;
import baby.mumu.message.infra.subscription.gatewayimpl.database.po.SubscriptionTextMessagePO_;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
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
 * 文本订阅消息领域网关实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.2
 */
@Component
public class SubscriptionTextMessageGatewayImpl implements SubscriptionTextMessageGateway {

  private final MessageProperties messageProperties;
  private final SubscriptionTextMessageRepository subscriptionTextMessageRepository;
  private final SubscriptionTextMessageArchivedRepository subscriptionTextMessageArchivedRepository;
  private final SubscriptionTextMessageConvertor subscriptionTextMessageConvertor;
  private final JobScheduler jobScheduler;
  private final ExtensionProperties extensionProperties;

  @Autowired
  public SubscriptionTextMessageGatewayImpl(MessageProperties messageProperties,
    SubscriptionTextMessageRepository subscriptionTextMessageRepository,
    SubscriptionTextMessageConvertor subscriptionTextMessageConvertor,
    SubscriptionTextMessageArchivedRepository subscriptionTextMessageArchivedRepository,
    JobScheduler jobScheduler, ExtensionProperties extensionProperties) {
    this.messageProperties = messageProperties;
    this.subscriptionTextMessageRepository = subscriptionTextMessageRepository;
    this.subscriptionTextMessageConvertor = subscriptionTextMessageConvertor;
    this.subscriptionTextMessageArchivedRepository = subscriptionTextMessageArchivedRepository;
    this.jobScheduler = jobScheduler;
    this.extensionProperties = extensionProperties;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void forwardMsg(SubscriptionTextMessage msg) {
    Optional.ofNullable(msg)
      .flatMap(subscriptionTextMessageConvertor::toSubscriptionTextMessagePO)
      .ifPresent(subscriptionTextMessagePO -> Optional.ofNullable(
          messageProperties.getWebSocket().getAccountSubscriptionChannelMap()
            .get(subscriptionTextMessagePO.getReceiverId()))
        .flatMap(res -> Optional.ofNullable(res.get(subscriptionTextMessagePO.getSenderId())))
        .ifPresentOrElse(channel -> {
          subscriptionTextMessageRepository.persist(subscriptionTextMessagePO);
          channel.writeAndFlush(new TextWebSocketFrame(subscriptionTextMessagePO.getMessage()));
        }, () -> subscriptionTextMessageRepository.persist(subscriptionTextMessagePO)));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void readMsgById(Long id) {
    Optional.ofNullable(id).flatMap(msgId -> SecurityContextUtils.getLoginAccountId().flatMap(
        accountId -> subscriptionTextMessageRepository.findByIdAndReceiverId(msgId, accountId)))
      .filter(subscriptionTextMessagePO -> MessageStatusEnum.UNREAD.equals(
        subscriptionTextMessagePO.getMessageStatus())).ifPresent(subscriptionTextMessageDo -> {
        subscriptionTextMessageDo.setMessageStatus(MessageStatusEnum.READ);
        subscriptionTextMessageRepository.merge(subscriptionTextMessageDo);
      });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void unreadMsgById(Long id) {
    Optional.ofNullable(id).flatMap(msgId -> SecurityContextUtils.getLoginAccountId().flatMap(
        accountId -> subscriptionTextMessageRepository.findByIdAndReceiverId(msgId, accountId)))
      .filter(subscriptionTextMessagePO -> MessageStatusEnum.READ.equals(
        subscriptionTextMessagePO.getMessageStatus())).ifPresent(subscriptionTextMessageDo -> {
        subscriptionTextMessageDo.setMessageStatus(MessageStatusEnum.UNREAD);
        subscriptionTextMessageRepository.merge(subscriptionTextMessageDo);
      });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteMsgById(Long id) {
    Optional.ofNullable(id).flatMap(_ -> SecurityContextUtils.getLoginAccountId())
      .ifPresent(
        accountId -> {
          subscriptionTextMessageRepository.deleteByIdAndSenderId(id, accountId);
          subscriptionTextMessageArchivedRepository.deleteByIdAndSenderId(id, accountId);
        });
  }

  @Override
  public Page<SubscriptionTextMessage> findAllYouSend(
    SubscriptionTextMessage subscriptionTextMessage, int current, int pageSize) {
    return SecurityContextUtils.getLoginAccountId().map(accountId -> {
      Specification<SubscriptionTextMessagePO> subscriptionTextMessageDoSpecification = (root, query, cb) -> {
        List<Predicate> predicateList = new ArrayList<>();
        Optional.ofNullable(subscriptionTextMessage).ifPresent(subscriptionTextMessageEntity -> {
          Optional.ofNullable(subscriptionTextMessageEntity.getMessage())
            .filter(StringUtils::isNotBlank)
            .ifPresent(
              message -> predicateList.add(cb.like(root.get(SubscriptionTextMessagePO_.message),
                String.format(LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE, message))));
          Optional.ofNullable(subscriptionTextMessageEntity.getMessageStatus()).ifPresent(
            messageStatusEnum -> predicateList.add(
              cb.equal(root.get(SubscriptionTextMessagePO_.messageStatus), messageStatusEnum)));
        });
        predicateList.add(cb.equal(root.get(SubscriptionTextMessagePO_.senderId), accountId));
        assert query != null;
        return query.orderBy(cb.desc(root.get(SubscriptionTextMessagePO_.creationTime)))
          .where(predicateList.toArray(new Predicate[0]))
          .getRestriction();
      };
      return getSubscriptionTextMessages(current, pageSize, subscriptionTextMessageDoSpecification);
    }).orElse(new PageImpl<>(Collections.emptyList()));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void archiveMsgById(Long id) {
    // noinspection DuplicatedCode
    Optional.ofNullable(id).flatMap(msgId -> SecurityContextUtils.getLoginAccountId().flatMap(
        accountId -> subscriptionTextMessageRepository.findByIdAndSenderId(msgId, accountId)))
      .ifPresent(
        subscriptionTextMessageDo -> subscriptionTextMessageConvertor.toSubscriptionTextMessageArchivedPO(
          subscriptionTextMessageDo).ifPresent(subscriptionTextMessageArchivedPO -> {
          subscriptionTextMessageArchivedPO.setArchived(true);
          subscriptionTextMessageRepository.delete(subscriptionTextMessageDo);
          subscriptionTextMessageArchivedRepository.persist(subscriptionTextMessageArchivedPO);
          GlobalProperties global = extensionProperties.getGlobal();
          jobScheduler.schedule(Instant.now()
              .plus(global.getArchiveDeletionPeriod(), global.getArchiveDeletionPeriodUnit()),
            () -> deleteArchivedDataJob(subscriptionTextMessageArchivedPO.getId()));
        }));
  }

  @Job(name = "删除ID为：%0 的订阅消息归档数据")
  @DangerousOperation("根据ID删除ID为%0的订阅消息归档数据定时任务")
  @Transactional(rollbackFor = Exception.class)
  public void deleteArchivedDataJob(Long id) {
    Optional.ofNullable(id)
      .ifPresent(subscriptionTextMessageArchivedRepository::deleteById);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverMsgFromArchiveById(Long id) {
    // noinspection DuplicatedCode
    Optional.ofNullable(id).flatMap(msgId -> SecurityContextUtils.getLoginAccountId().flatMap(
        accountId -> subscriptionTextMessageArchivedRepository.findByIdAndSenderId(msgId,
          accountId)))
      .ifPresent(
        subscriptionTextMessageArchivedPO -> subscriptionTextMessageConvertor.toSubscriptionTextMessagePO(
            subscriptionTextMessageArchivedPO)
          .ifPresent(subscriptionTextMessageDo -> {
            subscriptionTextMessageDo.setArchived(false);
            subscriptionTextMessageArchivedRepository.delete(
              subscriptionTextMessageArchivedPO);
            subscriptionTextMessageRepository.persist(subscriptionTextMessageDo);
          }));
  }

  @Override
  public Page<SubscriptionTextMessage> findAllMessageRecordWithSomeone(
    int current, int pageSize, Long receiverId) {
    return SecurityContextUtils.getLoginAccountId().map(accountId -> {
      Specification<SubscriptionTextMessagePO> subscriptionTextMessageDoSpecification = (root, query, cb) -> {
        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(
          cb.or(cb.and(cb.equal(root.get(SubscriptionTextMessagePO_.senderId), accountId),
            cb.equal(root.get(SubscriptionTextMessagePO_.receiverId), receiverId))));
        predicateList.add(
          cb.or(cb.and(cb.equal(root.get(SubscriptionTextMessagePO_.senderId), receiverId),
            cb.equal(root.get(SubscriptionTextMessagePO_.receiverId), accountId))));
        assert query != null;
        return query.orderBy(cb.desc(root.get(SubscriptionTextMessagePO_.creationTime)))
          .where(predicateList.toArray(new Predicate[0]))
          .getRestriction();
      };
      return getSubscriptionTextMessages(current, pageSize, subscriptionTextMessageDoSpecification);
    }).orElse(new PageImpl<>(Collections.emptyList()));
  }

  @NotNull
  private PageImpl<SubscriptionTextMessage> getSubscriptionTextMessages(int current, int pageSize,
    Specification<SubscriptionTextMessagePO> subscriptionTextMessageDoSpecification) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<SubscriptionTextMessagePO> repositoryAll = subscriptionTextMessageRepository.findAll(
      subscriptionTextMessageDoSpecification,
      pageRequest);
    List<SubscriptionTextMessage> subscriptionTextMessages = repositoryAll.getContent().stream()
      .map(subscriptionTextMessageConvertor::toEntity)
      .filter(Optional::isPresent).map(Optional::get)
      .toList();
    return new PageImpl<>(subscriptionTextMessages, pageRequest,
      repositoryAll.getTotalElements());
  }
}
