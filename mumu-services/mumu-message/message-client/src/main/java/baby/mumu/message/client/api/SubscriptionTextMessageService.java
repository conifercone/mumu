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
package baby.mumu.message.client.api;

import baby.mumu.message.client.cmds.SubscriptionTextMessageFindAllWithSomeOneCmd;
import baby.mumu.message.client.cmds.SubscriptionTextMessageFindAllYouSendCmd;
import baby.mumu.message.client.cmds.SubscriptionTextMessageForwardCmd;
import baby.mumu.message.client.dto.SubscriptionTextMessageFindAllWithSomeOneDTO;
import baby.mumu.message.client.dto.SubscriptionTextMessageFindAllYouSendDTO;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.data.domain.Page;

/**
 * 文本订阅消息service
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
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
   * @param id 文本订阅消息ID
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void readMsgById(Long id);

  /**
   * 根据ID未读消息
   *
   * @param id 文本订阅消息ID
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void unreadMsgById(Long id);

  /**
   * 根据ID删除消息
   *
   * @param id 文本订阅消息ID
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void deleteMsgById(Long id);

  /**
   * 查询所有当前用户发送消息
   *
   * @param subscriptionTextMessageFindAllYouSendCmd 文本订阅消息查询所有当前用户发送消息指令
   */
  @API(status = Status.STABLE, since = "1.0.3")
  Page<SubscriptionTextMessageFindAllYouSendDTO> findAllYouSend(
    SubscriptionTextMessageFindAllYouSendCmd subscriptionTextMessageFindAllYouSendCmd);

  /**
   * 根据ID归档消息
   *
   * @param id 文本订阅消息ID
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void archiveMsgById(Long id);

  /**
   * 根据ID从存档中恢复消息指令
   *
   * @param id 文本订阅消息ID
   */
  @API(status = Status.STABLE, since = "1.0.4")
  void recoverMsgFromArchiveById(
    Long id);

  /**
   * 查询所有和某人的消息记录
   *
   * @param subscriptionTextMessageFindAllWithSomeOneCmd 文本订阅消息查询所有和某人的消息记录指令
   * @return 查询结果
   */
  @API(status = Status.STABLE, since = "1.0.3")
  Page<SubscriptionTextMessageFindAllWithSomeOneDTO> findAllMessageRecordWithSomeone(
    SubscriptionTextMessageFindAllWithSomeOneCmd subscriptionTextMessageFindAllWithSomeOneCmd);
}
