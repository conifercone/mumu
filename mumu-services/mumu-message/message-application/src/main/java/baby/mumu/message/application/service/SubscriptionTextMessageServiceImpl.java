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
package baby.mumu.message.application.service;

import baby.mumu.message.application.subscription.executor.SubscriptionTextMessageArchiveByIdCmdExe;
import baby.mumu.message.application.subscription.executor.SubscriptionTextMessageDeleteByIdCmdExe;
import baby.mumu.message.application.subscription.executor.SubscriptionTextMessageFindAllWithSomeOneCmdExe;
import baby.mumu.message.application.subscription.executor.SubscriptionTextMessageFindAllYouSendCmdExe;
import baby.mumu.message.application.subscription.executor.SubscriptionTextMessageForwardCmdExe;
import baby.mumu.message.application.subscription.executor.SubscriptionTextMessageReadByIdCmdExe;
import baby.mumu.message.application.subscription.executor.SubscriptionTextMessageRecoverMsgFromArchiveByIdCmdExe;
import baby.mumu.message.application.subscription.executor.SubscriptionTextMessageUnreadByIdCmdExe;
import baby.mumu.message.client.api.SubscriptionTextMessageService;
import baby.mumu.message.client.dto.SubscriptionTextMessageFindAllWithSomeOneCmd;
import baby.mumu.message.client.dto.SubscriptionTextMessageFindAllYouSendCmd;
import baby.mumu.message.client.dto.SubscriptionTextMessageForwardCmd;
import baby.mumu.message.client.dto.co.SubscriptionTextMessageFindAllWithSomeOneCo;
import baby.mumu.message.client.dto.co.SubscriptionTextMessageFindAllYouSendCo;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文本订阅消息service实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.2
 */
@Service
@Observed(name = "SubscriptionTextMessageServiceImpl")
public class SubscriptionTextMessageServiceImpl implements SubscriptionTextMessageService {

  private final SubscriptionTextMessageForwardCmdExe subscriptionTextMessageForwardCmdExe;
  private final SubscriptionTextMessageReadByIdCmdExe subscriptionTextMessageReadByIdCmdExe;
  private final SubscriptionTextMessageDeleteByIdCmdExe subscriptionTextMessageDeleteByIdCmdExe;
  private final SubscriptionTextMessageFindAllYouSendCmdExe subscriptionTextMessageFindAllYouSendCmdExe;
  private final SubscriptionTextMessageUnreadByIdCmdExe subscriptionTextMessageUnreadByIdCmdExe;
  private final SubscriptionTextMessageArchiveByIdCmdExe subscriptionTextMessageArchiveByIdCmdExe;
  private final SubscriptionTextMessageFindAllWithSomeOneCmdExe subscriptionTextMessageFindAllWithSomeOneCmdExe;
  private final SubscriptionTextMessageRecoverMsgFromArchiveByIdCmdExe subscriptionTextMessageRecoverMsgFromArchiveByIdCmdExe;

  @Autowired
  public SubscriptionTextMessageServiceImpl(
    SubscriptionTextMessageForwardCmdExe subscriptionTextMessageForwardCmdExe,
    SubscriptionTextMessageReadByIdCmdExe subscriptionTextMessageReadByIdCmdExe,
    SubscriptionTextMessageDeleteByIdCmdExe subscriptionTextMessageDeleteByIdCmdExe,
    SubscriptionTextMessageFindAllYouSendCmdExe subscriptionTextMessageFindAllYouSendCmdExe,
    SubscriptionTextMessageUnreadByIdCmdExe subscriptionTextMessageUnreadByIdCmdExe,
    SubscriptionTextMessageArchiveByIdCmdExe subscriptionTextMessageArchiveByIdCmdExe,
    SubscriptionTextMessageFindAllWithSomeOneCmdExe subscriptionTextMessageFindAllWithSomeOneCmdExe,
    SubscriptionTextMessageRecoverMsgFromArchiveByIdCmdExe subscriptionTextMessageRecoverMsgFromArchiveByIdCmdExe) {
    this.subscriptionTextMessageForwardCmdExe = subscriptionTextMessageForwardCmdExe;
    this.subscriptionTextMessageReadByIdCmdExe = subscriptionTextMessageReadByIdCmdExe;
    this.subscriptionTextMessageDeleteByIdCmdExe = subscriptionTextMessageDeleteByIdCmdExe;
    this.subscriptionTextMessageFindAllYouSendCmdExe = subscriptionTextMessageFindAllYouSendCmdExe;
    this.subscriptionTextMessageUnreadByIdCmdExe = subscriptionTextMessageUnreadByIdCmdExe;
    this.subscriptionTextMessageArchiveByIdCmdExe = subscriptionTextMessageArchiveByIdCmdExe;
    this.subscriptionTextMessageFindAllWithSomeOneCmdExe = subscriptionTextMessageFindAllWithSomeOneCmdExe;
    this.subscriptionTextMessageRecoverMsgFromArchiveByIdCmdExe = subscriptionTextMessageRecoverMsgFromArchiveByIdCmdExe;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void forwardMsg(SubscriptionTextMessageForwardCmd subscriptionTextMessageForwardCmd) {
    subscriptionTextMessageForwardCmdExe.execute(subscriptionTextMessageForwardCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void readMsgById(Long id) {
    subscriptionTextMessageReadByIdCmdExe.execute(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void unreadMsgById(
    Long id) {
    subscriptionTextMessageUnreadByIdCmdExe.execute(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteMsgById(
    Long id) {
    subscriptionTextMessageDeleteByIdCmdExe.execute(id);
  }

  @Override
  public Page<SubscriptionTextMessageFindAllYouSendCo> findAllYouSend(
    SubscriptionTextMessageFindAllYouSendCmd subscriptionTextMessageFindAllYouSendCmd) {
    return subscriptionTextMessageFindAllYouSendCmdExe.execute(
      subscriptionTextMessageFindAllYouSendCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void archiveMsgById(
    Long id) {
    subscriptionTextMessageArchiveByIdCmdExe.execute(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverMsgFromArchiveById(
    Long id) {
    subscriptionTextMessageRecoverMsgFromArchiveByIdCmdExe.execute(id);
  }

  @Override
  public Page<SubscriptionTextMessageFindAllWithSomeOneCo> findAllMessageRecordWithSomeone(
    SubscriptionTextMessageFindAllWithSomeOneCmd subscriptionTextMessageFindAllWithSomeOneCmd) {
    return subscriptionTextMessageFindAllWithSomeOneCmdExe.execute(
      subscriptionTextMessageFindAllWithSomeOneCmd);
  }
}
