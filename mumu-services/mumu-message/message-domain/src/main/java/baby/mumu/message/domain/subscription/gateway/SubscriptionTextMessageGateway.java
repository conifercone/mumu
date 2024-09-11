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
package baby.mumu.message.domain.subscription.gateway;

import baby.mumu.message.domain.subscription.SubscriptionTextMessage;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.data.domain.Page;

/**
 * 文本订阅消息领域网关
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.2
 */
public interface SubscriptionTextMessageGateway {

  /**
   * 消息转发
   *
   * @param msg 文本订阅消息
   * @since 1.0.2
   */
  @API(status = Status.STABLE, since = "1.0.2")
  void forwardMsg(SubscriptionTextMessage msg);

  /**
   * 根据ID已读消息
   *
   * @param id 消息ID
   * @since 1.0.3
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void readMsgById(Long id);

  /**
   * 根据ID未读消息
   *
   * @param id 消息ID
   * @since 1.0.3
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void unreadMsgById(Long id);

  /**
   * 根据ID删除消息
   *
   * @param id 消息ID
   * @since 1.0.3
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void deleteMsgById(Long id);

  /**
   * 查询所有你发送的消息
   *
   * @param subscriptionTextMessage 文本订阅消息
   * @param pageNo                  当前页码
   * @param pageSize                当前页数量
   * @return 查询结果
   */
  @API(status = Status.STABLE, since = "1.0.3")
  Page<SubscriptionTextMessage> findAllYouSend(SubscriptionTextMessage subscriptionTextMessage,
      int pageNo, int pageSize);

  /**
   * 根据ID归档消息
   *
   * @param id 消息ID
   * @since 1.0.3
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void archiveMsgById(Long id);

  /**
   * 根据ID从归档中恢复消息
   *
   * @param id 消息ID
   * @since 1.0.4
   */
  @API(status = Status.STABLE, since = "1.0.4")
  void recoverMsgFromArchiveById(Long id);

  /**
   * 查询所有和某人的消息记录
   *
   * @param pageNo     当前页码
   * @param pageSize   当前页数量
   * @param receiverId 接收者ID
   * @return 查询结果
   */
  @API(status = Status.STABLE, since = "1.0.3")
  Page<SubscriptionTextMessage> findAllMessageRecordWithSomeone(int pageNo, int pageSize,
      Long receiverId);
}
