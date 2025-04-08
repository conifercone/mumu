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
package baby.mumu.message.application.subscription.executor;

import baby.mumu.message.client.cmds.SubscriptionTextMessageFindAllYouSendCmd;
import baby.mumu.message.client.dto.SubscriptionTextMessageFindAllYouSendDTO;
import baby.mumu.message.domain.subscription.SubscriptionTextMessage;
import baby.mumu.message.domain.subscription.gateway.SubscriptionTextMessageGateway;
import baby.mumu.message.infrastructure.subscription.convertor.SubscriptionTextMessageConvertor;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 文本订阅消息查询所有当前用户发送消息指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.3
 */
@Component
public class SubscriptionTextMessageFindAllYouSendCmdExe {

  private final SubscriptionTextMessageGateway subscriptionTextMessageGateway;
  private final SubscriptionTextMessageConvertor subscriptionTextMessageConvertor;

  @Autowired
  public SubscriptionTextMessageFindAllYouSendCmdExe(
    SubscriptionTextMessageGateway subscriptionTextMessageGateway,
    SubscriptionTextMessageConvertor subscriptionTextMessageConvertor) {
    this.subscriptionTextMessageGateway = subscriptionTextMessageGateway;
    this.subscriptionTextMessageConvertor = subscriptionTextMessageConvertor;
  }

  public Page<SubscriptionTextMessageFindAllYouSendDTO> execute(
    @NotNull SubscriptionTextMessageFindAllYouSendCmd subscriptionTextMessageFindAllYouSendCmd) {
    Assert.notNull(subscriptionTextMessageFindAllYouSendCmd,
      "SubscriptionTextMessageFindAllYouSendCmd cannot null");
    Page<SubscriptionTextMessage> allYouSend = subscriptionTextMessageGateway.findAllYouSend(
      subscriptionTextMessageConvertor.toEntity(subscriptionTextMessageFindAllYouSendCmd)
        .orElse(null),
      subscriptionTextMessageFindAllYouSendCmd.getCurrent(),
      subscriptionTextMessageFindAllYouSendCmd.getPageSize());
    List<SubscriptionTextMessageFindAllYouSendDTO> subscriptionTextMessageFindAllYouSendDTOS = allYouSend.getContent()
      .stream()
      .map(subscriptionTextMessageConvertor::toFindAllYouSendDTO)
      .filter(Optional::isPresent).map(Optional::get).toList();
    return new PageImpl<>(subscriptionTextMessageFindAllYouSendDTOS, allYouSend.getPageable(),
      allYouSend.getTotalElements());
  }
}
