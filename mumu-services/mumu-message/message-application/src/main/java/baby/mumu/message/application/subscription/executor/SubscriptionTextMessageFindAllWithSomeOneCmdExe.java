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
package baby.mumu.message.application.subscription.executor;

import baby.mumu.message.client.cmds.SubscriptionTextMessageFindAllWithSomeOneCmd;
import baby.mumu.message.client.dto.SubscriptionTextMessageFindAllWithSomeOneDTO;
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
 * 文本订阅消息查询所有和某人的消息记录指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.3
 */
@Component
public class SubscriptionTextMessageFindAllWithSomeOneCmdExe {

  private final SubscriptionTextMessageGateway subscriptionTextMessageGateway;
  private final SubscriptionTextMessageConvertor subscriptionTextMessageConvertor;

  @Autowired
  public SubscriptionTextMessageFindAllWithSomeOneCmdExe(
    SubscriptionTextMessageGateway subscriptionTextMessageGateway,
    SubscriptionTextMessageConvertor subscriptionTextMessageConvertor) {
    this.subscriptionTextMessageGateway = subscriptionTextMessageGateway;
    this.subscriptionTextMessageConvertor = subscriptionTextMessageConvertor;
  }

  public Page<SubscriptionTextMessageFindAllWithSomeOneDTO> execute(
    @NotNull SubscriptionTextMessageFindAllWithSomeOneCmd subscriptionTextMessageFindAllWithSomeOneCmd) {
    Assert.notNull(subscriptionTextMessageFindAllWithSomeOneCmd,
      "SubscriptionTextMessageFindAllWithSomeOneCmd cannot null");
    Page<SubscriptionTextMessage> allMessageRecordWithSomeone = subscriptionTextMessageGateway.findAllMessageRecordWithSomeone(
      subscriptionTextMessageFindAllWithSomeOneCmd.getCurrent(),
      subscriptionTextMessageFindAllWithSomeOneCmd.getPageSize(),
      subscriptionTextMessageFindAllWithSomeOneCmd.getReceiverId());
    List<SubscriptionTextMessageFindAllWithSomeOneDTO> subscriptionTextMessageFindAllWithSomeOneDTOS = allMessageRecordWithSomeone.getContent()
      .stream()
      .map(subscriptionTextMessageConvertor::toFindAllWithSomeOne)
      .filter(Optional::isPresent).map(Optional::get).toList();
    return new PageImpl<>(subscriptionTextMessageFindAllWithSomeOneDTOS,
      allMessageRecordWithSomeone.getPageable(),
      allMessageRecordWithSomeone.getTotalElements());
  }
}
