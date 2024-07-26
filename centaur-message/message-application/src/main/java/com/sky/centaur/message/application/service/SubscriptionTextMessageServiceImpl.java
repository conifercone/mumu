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
package com.sky.centaur.message.application.service;

import com.sky.centaur.message.application.subscription.executor.SubscriptionTextMessageDeleteByIdCmdExe;
import com.sky.centaur.message.application.subscription.executor.SubscriptionTextMessageFindAllYouSendCmdExe;
import com.sky.centaur.message.application.subscription.executor.SubscriptionTextMessageForwardCmdExe;
import com.sky.centaur.message.application.subscription.executor.SubscriptionTextMessageReadByIdCmdExe;
import com.sky.centaur.message.application.subscription.executor.SubscriptionTextMessageUnreadByIdCmdExe;
import com.sky.centaur.message.client.api.SubscriptionTextMessageService;
import com.sky.centaur.message.client.dto.SubscriptionTextMessageDeleteByIdCmd;
import com.sky.centaur.message.client.dto.SubscriptionTextMessageFindAllYouSendCmd;
import com.sky.centaur.message.client.dto.SubscriptionTextMessageForwardCmd;
import com.sky.centaur.message.client.dto.SubscriptionTextMessageReadByIdCmd;
import com.sky.centaur.message.client.dto.SubscriptionTextMessageUnreadByIdCmd;
import com.sky.centaur.message.client.dto.co.SubscriptionTextMessageFindAllYouSendCo;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文本订阅消息service实现类
 *
 * @author kaiyu.shan
 * @since 1.0.2
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class})
@Observed(name = "SubscriptionTextMessageServiceImpl")
public class SubscriptionTextMessageServiceImpl implements SubscriptionTextMessageService {

  private final SubscriptionTextMessageForwardCmdExe subscriptionTextMessageForwardCmdExe;
  private final SubscriptionTextMessageReadByIdCmdExe subscriptionTextMessageReadByIdCmdExe;
  private final SubscriptionTextMessageDeleteByIdCmdExe subscriptionTextMessageDeleteByIdCmdExe;
  private final SubscriptionTextMessageFindAllYouSendCmdExe subscriptionTextMessageFindAllYouSendCmdExe;
  private final SubscriptionTextMessageUnreadByIdCmdExe subscriptionTextMessageUnreadByIdCmdExe;

  @Autowired
  public SubscriptionTextMessageServiceImpl(
      SubscriptionTextMessageForwardCmdExe subscriptionTextMessageForwardCmdExe,
      SubscriptionTextMessageReadByIdCmdExe subscriptionTextMessageReadByIdCmdExe,
      SubscriptionTextMessageDeleteByIdCmdExe subscriptionTextMessageDeleteByIdCmdExe,
      SubscriptionTextMessageFindAllYouSendCmdExe subscriptionTextMessageFindAllYouSendCmdExe,
      SubscriptionTextMessageUnreadByIdCmdExe subscriptionTextMessageUnreadByIdCmdExe) {
    this.subscriptionTextMessageForwardCmdExe = subscriptionTextMessageForwardCmdExe;
    this.subscriptionTextMessageReadByIdCmdExe = subscriptionTextMessageReadByIdCmdExe;
    this.subscriptionTextMessageDeleteByIdCmdExe = subscriptionTextMessageDeleteByIdCmdExe;
    this.subscriptionTextMessageFindAllYouSendCmdExe = subscriptionTextMessageFindAllYouSendCmdExe;
    this.subscriptionTextMessageUnreadByIdCmdExe = subscriptionTextMessageUnreadByIdCmdExe;
  }

  @Override
  @Transactional
  public void forwardMsg(SubscriptionTextMessageForwardCmd subscriptionTextMessageForwardCmd) {
    subscriptionTextMessageForwardCmdExe.execute(subscriptionTextMessageForwardCmd);
  }

  @Override
  @Transactional
  public void readMsgById(SubscriptionTextMessageReadByIdCmd subscriptionTextMessageReadByIdCmd) {
    subscriptionTextMessageReadByIdCmdExe.execute(subscriptionTextMessageReadByIdCmd);
  }

  @Override
  @Transactional
  public void unreadMsgById(
      SubscriptionTextMessageUnreadByIdCmd subscriptionTextMessageUnreadByIdCmd) {
    subscriptionTextMessageUnreadByIdCmdExe.execute(subscriptionTextMessageUnreadByIdCmd);
  }

  @Override
  @Transactional
  public void deleteMsgById(
      SubscriptionTextMessageDeleteByIdCmd subscriptionTextMessageDeleteByIdCmd) {
    subscriptionTextMessageDeleteByIdCmdExe.execute(subscriptionTextMessageDeleteByIdCmd);
  }

  @Override
  public Page<SubscriptionTextMessageFindAllYouSendCo> findAllYouSend(
      SubscriptionTextMessageFindAllYouSendCmd subscriptionTextMessageFindAllYouSendCmd) {
    return subscriptionTextMessageFindAllYouSendCmdExe.execute(
        subscriptionTextMessageFindAllYouSendCmd);
  }
}
