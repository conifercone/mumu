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

package baby.mumu.message.infra.subscription.convertor;

import baby.mumu.basis.kotlin.tools.SecurityContextUtils;
import baby.mumu.extension.translation.SimpleTextTranslation;
import baby.mumu.message.client.cmds.SubscriptionTextMessageFindAllYouSendCmd;
import baby.mumu.message.client.cmds.SubscriptionTextMessageForwardCmd;
import baby.mumu.message.client.dto.SubscriptionTextMessageFindAllWithSomeOneDTO;
import baby.mumu.message.client.dto.SubscriptionTextMessageFindAllYouSendDTO;
import baby.mumu.message.domain.subscription.SubscriptionTextMessage;
import baby.mumu.message.infra.subscription.gatewayimpl.database.po.SubscriptionTextMessageArchivedPO;
import baby.mumu.message.infra.subscription.gatewayimpl.database.po.SubscriptionTextMessagePO;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 文本订阅消息转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.2
 */
@Component
public class SubscriptionTextMessageConvertor {

  private final SimpleTextTranslation simpleTextTranslation;

  @Autowired
  public SubscriptionTextMessageConvertor(
    ObjectProvider<SimpleTextTranslation> simpleTextTranslations) {
    this.simpleTextTranslation = simpleTextTranslations.getIfAvailable();
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.2")
  public Optional<SubscriptionTextMessage> toEntity(
    SubscriptionTextMessageForwardCmd subscriptionTextMessageForwardCmd) {
    return Optional.ofNullable(subscriptionTextMessageForwardCmd)
      .flatMap(res -> SecurityContextUtils.getLoginAccountId().map(senderAccountId -> {
        SubscriptionTextMessage entity = SubscriptionTextMessageMapper.INSTANCE.toEntity(res);
        entity.setSenderId(senderAccountId);
        return entity;
      }));
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.2")
  public Optional<SubscriptionTextMessagePO> toSubscriptionTextMessagePO(
    SubscriptionTextMessage subscriptionTextMessage) {
    return Optional.ofNullable(subscriptionTextMessage)
      .map(SubscriptionTextMessageMapper.INSTANCE::toSubscriptionTextMessagePO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.3")
  public Optional<SubscriptionTextMessage> toEntity(
    SubscriptionTextMessagePO subscriptionTextMessagePO) {
    return Optional.ofNullable(subscriptionTextMessagePO)
      .map(SubscriptionTextMessageMapper.INSTANCE::toEntity);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.3")
  public Optional<SubscriptionTextMessage> toEntity(
    SubscriptionTextMessageFindAllYouSendCmd subscriptionTextMessageFindAllYouSendCmd) {
    return Optional.ofNullable(subscriptionTextMessageFindAllYouSendCmd)
      .map(SubscriptionTextMessageMapper.INSTANCE::toEntity);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.3")
  public Optional<SubscriptionTextMessageFindAllYouSendDTO> toSubscriptionTextMessageFindAllYouSendDTO(
    SubscriptionTextMessage subscriptionTextMessage) {
    return Optional.ofNullable(subscriptionTextMessage)
      .map(SubscriptionTextMessageMapper.INSTANCE::toSubscriptionTextMessageFindAllYouSendDTO)
      .map(subscriptionTextMessageFindAllYouSendCo -> {
        Optional.ofNullable(simpleTextTranslation).flatMap(
            simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
              subscriptionTextMessageFindAllYouSendCo.getMessage()))
          .ifPresent(subscriptionTextMessageFindAllYouSendCo::setMessage);
        return subscriptionTextMessageFindAllYouSendCo;
      });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.3")
  public Optional<SubscriptionTextMessageFindAllWithSomeOneDTO> toSubscriptionTextMessageFindAllWithSomeOneDTO(
    SubscriptionTextMessage subscriptionTextMessage) {
    return Optional.ofNullable(subscriptionTextMessage)
      .map(SubscriptionTextMessageMapper.INSTANCE::toSubscriptionTextMessageFindAllWithSomeOneDTO)
      .map(subscriptionTextMessageFindAllWithSomeOneCo -> {
        Optional.ofNullable(simpleTextTranslation).flatMap(
            simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
              subscriptionTextMessageFindAllWithSomeOneCo.getMessage()))
          .ifPresent(subscriptionTextMessageFindAllWithSomeOneCo::setMessage);
        return subscriptionTextMessageFindAllWithSomeOneCo;
      });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<SubscriptionTextMessageArchivedPO> toSubscriptionTextMessageArchivedPO(
    SubscriptionTextMessagePO subscriptionTextMessagePO) {
    return Optional.ofNullable(subscriptionTextMessagePO)
      .map(SubscriptionTextMessageMapper.INSTANCE::toSubscriptionTextMessageArchivedPO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<SubscriptionTextMessagePO> toSubscriptionTextMessagePO(
    SubscriptionTextMessageArchivedPO subscriptionTextMessageArchivedPO) {
    return Optional.ofNullable(subscriptionTextMessageArchivedPO)
      .map(SubscriptionTextMessageMapper.INSTANCE::toSubscriptionTextMessagePO);
  }
}
