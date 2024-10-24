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

import baby.mumu.message.client.dto.BroadcastTextMessageFindAllYouSendCmd;
import baby.mumu.message.client.dto.BroadcastTextMessageForwardCmd;
import baby.mumu.message.client.dto.co.BroadcastTextMessageFindAllYouSendCo;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.data.domain.Page;

/**
 * 文本广播消息service
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.2
 */
public interface BroadcastTextMessageService {

  /**
   * 消息转发
   *
   * @param broadcastTextMessageForwardCmd 文本广播消息转发指令
   * @since 1.0.2
   */
  @API(status = Status.STABLE, since = "1.0.2")
  void forwardMsg(BroadcastTextMessageForwardCmd broadcastTextMessageForwardCmd);

  /**
   * 根据ID已读消息
   *
   * @param id 文本广播消息ID
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void readMsgById(Long id);

  /**
   * 根据ID删除消息
   *
   * @param id 文本广播消息ID
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void deleteMsgById(Long id);

  /**
   * 查询所有当前用户发送消息
   *
   * @param broadcastTextMessageFindAllYouSendCmd 文本广播消息查询所有当前用户发送消息指令
   */
  @API(status = Status.STABLE, since = "1.0.3")
  Page<BroadcastTextMessageFindAllYouSendCo> findAllYouSend(
      BroadcastTextMessageFindAllYouSendCmd broadcastTextMessageFindAllYouSendCmd);

  /**
   * 根据ID归档消息
   *
   * @param id 文本广播消息ID
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void archiveMsgById(Long id);

  /**
   * 根据ID从存档中恢复消息指令
   *
   * @param id 文本广播消息ID
   */
  @API(status = Status.STABLE, since = "1.0.4")
  void recoverMsgFromArchiveById(Long id);
}
