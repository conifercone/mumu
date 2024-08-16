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
package com.sky.centaur.message.client.api;

import com.sky.centaur.message.client.dto.SubscriptionTextMessageArchiveByIdCmd;
import com.sky.centaur.message.client.dto.SubscriptionTextMessageDeleteByIdCmd;
import com.sky.centaur.message.client.dto.SubscriptionTextMessageFindAllWithSomeOneCmd;
import com.sky.centaur.message.client.dto.SubscriptionTextMessageFindAllYouSendCmd;
import com.sky.centaur.message.client.dto.SubscriptionTextMessageForwardCmd;
import com.sky.centaur.message.client.dto.SubscriptionTextMessageReadByIdCmd;
import com.sky.centaur.message.client.dto.SubscriptionTextMessageRecoverMsgFromArchiveByIdCmd;
import com.sky.centaur.message.client.dto.SubscriptionTextMessageUnreadByIdCmd;
import com.sky.centaur.message.client.dto.co.SubscriptionTextMessageFindAllWithSomeOneCo;
import com.sky.centaur.message.client.dto.co.SubscriptionTextMessageFindAllYouSendCo;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.data.domain.Page;

/**
 * 文本订阅消息service
 *
 * @author kaiyu.shan
 * @since 1.0.2
 */
public interface SubscriptionTextMessageService {

  /**
   * 消息转发
   *
   * @param subscriptionTextMessageForwardCmd 文本订阅消息转发指令
   * @since 1.0.2
   */
  @API(status = Status.STABLE, since = "1.0.2")
  void forwardMsg(SubscriptionTextMessageForwardCmd subscriptionTextMessageForwardCmd);

  /**
   * 根据ID已读消息
   *
   * @param subscriptionTextMessageReadByIdCmd 文本订阅消息根据ID已读指令
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void readMsgById(SubscriptionTextMessageReadByIdCmd subscriptionTextMessageReadByIdCmd);

  /**
   * 根据ID未读消息
   *
   * @param subscriptionTextMessageUnreadByIdCmd 文本订阅消息根据ID未读指令
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void unreadMsgById(SubscriptionTextMessageUnreadByIdCmd subscriptionTextMessageUnreadByIdCmd);

  /**
   * 根据ID删除消息
   *
   * @param subscriptionTextMessageDeleteByIdCmd 文本订阅消息根据ID删除指令
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void deleteMsgById(SubscriptionTextMessageDeleteByIdCmd subscriptionTextMessageDeleteByIdCmd);

  /**
   * 查询所有当前用户发送消息
   *
   * @param subscriptionTextMessageFindAllYouSendCmd 文本订阅消息查询所有当前用户发送消息指令
   */
  @API(status = Status.STABLE, since = "1.0.3")
  Page<SubscriptionTextMessageFindAllYouSendCo> findAllYouSend(
      SubscriptionTextMessageFindAllYouSendCmd subscriptionTextMessageFindAllYouSendCmd);

  /**
   * 根据ID归档消息
   *
   * @param subscriptionTextMessageArchiveByIdCmd 文本订阅消息根据ID归档指令
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void archiveMsgById(SubscriptionTextMessageArchiveByIdCmd subscriptionTextMessageArchiveByIdCmd);

  /**
   * 根据ID从存档中恢复消息指令
   *
   * @param subscriptionTextMessageRecoverMsgFromArchiveByIdCmd 文本订阅消息根据ID从存档中恢复消息指令
   */
  @API(status = Status.STABLE, since = "1.0.4")
  void recoverMsgFromArchiveById(
      SubscriptionTextMessageRecoverMsgFromArchiveByIdCmd subscriptionTextMessageRecoverMsgFromArchiveByIdCmd);

  /**
   * 查询所有和某人的消息记录
   *
   * @param subscriptionTextMessageFindAllWithSomeOneCmd 文本订阅消息查询所有和某人的消息记录指令
   * @return 查询结果
   */
  @API(status = Status.STABLE, since = "1.0.3")
  Page<SubscriptionTextMessageFindAllWithSomeOneCo> findAllMessageRecordWithSomeone(
      SubscriptionTextMessageFindAllWithSomeOneCmd subscriptionTextMessageFindAllWithSomeOneCmd);
}
