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

import static com.sky.centaur.basis.constants.CommonConstants.LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE;

import com.sky.centaur.basis.enums.MessageStatusEnum;
import com.sky.centaur.basis.kotlin.tools.SecurityContextUtil;
import com.sky.centaur.message.domain.subscription.SubscriptionTextMessage;
import com.sky.centaur.message.domain.subscription.gateway.SubscriptionTextMessageGateway;
import com.sky.centaur.message.infrastructure.config.MessageProperties;
import com.sky.centaur.message.infrastructure.subscription.convertor.SubscriptionTextMessageConvertor;
import com.sky.centaur.message.infrastructure.subscription.gatewayimpl.database.SubscriptionTextMessageArchivedRepository;
import com.sky.centaur.message.infrastructure.subscription.gatewayimpl.database.SubscriptionTextMessageRepository;
import com.sky.centaur.message.infrastructure.subscription.gatewayimpl.database.dataobject.SubscriptionTextMessageDo;
import com.sky.centaur.message.infrastructure.subscription.gatewayimpl.database.dataobject.SubscriptionTextMessageDo_;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 文本订阅消息领域网关实现类
 *
 * @author kaiyu.shan
 * @since 1.0.2
 */
@Component
public class SubscriptionTextMessageGatewayImpl implements SubscriptionTextMessageGateway {

  private final MessageProperties messageProperties;
  private final SubscriptionTextMessageRepository subscriptionTextMessageRepository;
  private final SubscriptionTextMessageArchivedRepository subscriptionTextMessageArchivedRepository;
  private final SubscriptionTextMessageConvertor subscriptionTextMessageConvertor;

  @Autowired
  public SubscriptionTextMessageGatewayImpl(MessageProperties messageProperties,
      SubscriptionTextMessageRepository subscriptionTextMessageRepository,
      SubscriptionTextMessageConvertor subscriptionTextMessageConvertor,
      SubscriptionTextMessageArchivedRepository subscriptionTextMessageArchivedRepository) {
    this.messageProperties = messageProperties;
    this.subscriptionTextMessageRepository = subscriptionTextMessageRepository;
    this.subscriptionTextMessageConvertor = subscriptionTextMessageConvertor;
    this.subscriptionTextMessageArchivedRepository = subscriptionTextMessageArchivedRepository;
  }

  @Override
  @Transactional
  public void forwardMsg(SubscriptionTextMessage msg) {
    Optional.ofNullable(msg)
        .flatMap(subscriptionTextMessageConvertor::toDataObject)
        .ifPresent(subscriptionMessageDo -> Optional.ofNullable(
                messageProperties.getWebSocket().getAccountSubscriptionChannelMap()
                    .get(subscriptionMessageDo.getReceiverId()))
            .flatMap(res -> Optional.ofNullable(res.get(subscriptionMessageDo.getSenderId())))
            .ifPresentOrElse(channel -> {
              subscriptionTextMessageRepository.persist(subscriptionMessageDo);
              channel.writeAndFlush(new TextWebSocketFrame(subscriptionMessageDo.getMessage()));
            }, () -> subscriptionTextMessageRepository.persist(subscriptionMessageDo)));
  }

  @Override
  @Transactional
  public void readMsgById(Long id) {
    Optional.ofNullable(id).flatMap(msgId -> SecurityContextUtil.getLoginAccountId().flatMap(
            accountId -> subscriptionTextMessageRepository.findByIdAndReceiverId(msgId, accountId)))
        .filter(subscriptionTextMessageDo -> MessageStatusEnum.UNREAD.equals(
            subscriptionTextMessageDo.getMessageStatus())).ifPresent(subscriptionTextMessageDo -> {
          subscriptionTextMessageDo.setMessageStatus(MessageStatusEnum.READ);
          subscriptionTextMessageRepository.merge(subscriptionTextMessageDo);
        });
  }

  @Override
  @Transactional
  public void unreadMsgById(Long id) {
    Optional.ofNullable(id).flatMap(msgId -> SecurityContextUtil.getLoginAccountId().flatMap(
            accountId -> subscriptionTextMessageRepository.findByIdAndReceiverId(msgId, accountId)))
        .filter(subscriptionTextMessageDo -> MessageStatusEnum.READ.equals(
            subscriptionTextMessageDo.getMessageStatus())).ifPresent(subscriptionTextMessageDo -> {
          subscriptionTextMessageDo.setMessageStatus(MessageStatusEnum.UNREAD);
          subscriptionTextMessageRepository.merge(subscriptionTextMessageDo);
        });
  }

  @Override
  @Transactional
  public void deleteMsgById(Long id) {
    Optional.ofNullable(id).flatMap(msgId -> SecurityContextUtil.getLoginAccountId())
        .ifPresent(
            accountId -> {
              subscriptionTextMessageRepository.deleteByIdAndSenderId(id, accountId);
              subscriptionTextMessageArchivedRepository.deleteByIdAndSenderId(id, accountId);
            });
  }

  @Override
  public Page<SubscriptionTextMessage> findAllYouSend(
      SubscriptionTextMessage subscriptionTextMessage, int pageNo, int pageSize) {
    return SecurityContextUtil.getLoginAccountId().map(accountId -> {
      Specification<SubscriptionTextMessageDo> subscriptionTextMessageDoSpecification = (root, query, cb) -> {
        List<Predicate> predicateList = new ArrayList<>();
        Optional.ofNullable(subscriptionTextMessage).ifPresent(subscriptionTextMessageEntity -> {
          Optional.ofNullable(subscriptionTextMessageEntity.getMessage())
              .filter(StringUtils::hasText)
              .ifPresent(
                  message -> predicateList.add(cb.like(root.get(SubscriptionTextMessageDo_.message),
                      String.format(LEFT_AND_RIGHT_FUZZY_QUERY_TEMPLATE, message))));
          Optional.ofNullable(subscriptionTextMessageEntity.getMessageStatus()).ifPresent(
              messageStatusEnum -> predicateList.add(
                  cb.equal(root.get(SubscriptionTextMessageDo_.messageStatus), messageStatusEnum)));
        });
        predicateList.add(cb.equal(root.get(SubscriptionTextMessageDo_.senderId), accountId));
        assert query != null;
        return query.orderBy(cb.desc(root.get(SubscriptionTextMessageDo_.creationTime)))
            .where(predicateList.toArray(new Predicate[0]))
            .getRestriction();
      };
      return getSubscriptionTextMessages(pageNo, pageSize, subscriptionTextMessageDoSpecification);
    }).orElse(new PageImpl<>(Collections.emptyList()));
  }

  @Override
  @Transactional
  public void archiveMsgById(Long id) {
    //noinspection DuplicatedCode
    Optional.ofNullable(id).flatMap(msgId -> SecurityContextUtil.getLoginAccountId().flatMap(
            accountId -> subscriptionTextMessageRepository.findByIdAndSenderId(msgId, accountId)))
        .ifPresent(subscriptionTextMessageDo -> subscriptionTextMessageConvertor.toArchiveDo(
            subscriptionTextMessageDo).ifPresent(subscriptionTextMessageArchivedDo -> {
          subscriptionTextMessageArchivedDo.setArchived(true);
          subscriptionTextMessageRepository.delete(subscriptionTextMessageDo);
          subscriptionTextMessageArchivedRepository.persist(subscriptionTextMessageArchivedDo);
        }));
  }

  @Override
  @Transactional
  public void recoverMsgFromArchiveById(Long id) {
    //noinspection DuplicatedCode
    Optional.ofNullable(id).flatMap(msgId -> SecurityContextUtil.getLoginAccountId().flatMap(
            accountId -> subscriptionTextMessageArchivedRepository.findByIdAndSenderId(msgId,
                accountId)))
        .ifPresent(
            subscriptionTextMessageArchivedDo -> subscriptionTextMessageConvertor.toDataObject(
                    subscriptionTextMessageArchivedDo)
                .ifPresent(subscriptionTextMessageDo -> {
                  subscriptionTextMessageDo.setArchived(false);
                  subscriptionTextMessageArchivedRepository.delete(
                      subscriptionTextMessageArchivedDo);
                  subscriptionTextMessageRepository.persist(subscriptionTextMessageDo);
                }));
  }

  @Override
  public Page<SubscriptionTextMessage> findAllMessageRecordWithSomeone(
      int pageNo, int pageSize, Long receiverId) {
    return SecurityContextUtil.getLoginAccountId().map(accountId -> {
      Specification<SubscriptionTextMessageDo> subscriptionTextMessageDoSpecification = (root, query, cb) -> {
        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(
            cb.or(cb.and(cb.equal(root.get(SubscriptionTextMessageDo_.senderId), accountId),
                cb.equal(root.get(SubscriptionTextMessageDo_.receiverId), receiverId))));
        predicateList.add(
            cb.or(cb.and(cb.equal(root.get(SubscriptionTextMessageDo_.senderId), receiverId),
                cb.equal(root.get(SubscriptionTextMessageDo_.receiverId), accountId))));
        assert query != null;
        return query.orderBy(cb.desc(root.get(SubscriptionTextMessageDo_.creationTime)))
            .where(predicateList.toArray(new Predicate[0]))
            .getRestriction();
      };
      return getSubscriptionTextMessages(pageNo, pageSize, subscriptionTextMessageDoSpecification);
    }).orElse(new PageImpl<>(Collections.emptyList()));
  }

  @NotNull
  private PageImpl<SubscriptionTextMessage> getSubscriptionTextMessages(int pageNo, int pageSize,
      Specification<SubscriptionTextMessageDo> subscriptionTextMessageDoSpecification) {
    PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
    Page<SubscriptionTextMessageDo> repositoryAll = subscriptionTextMessageRepository.findAll(
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
