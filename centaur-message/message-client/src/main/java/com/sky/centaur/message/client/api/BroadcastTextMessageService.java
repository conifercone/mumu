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

import com.sky.centaur.message.client.dto.BroadcastTextMessageDeleteByIdCmd;
import com.sky.centaur.message.client.dto.BroadcastTextMessageForwardCmd;
import com.sky.centaur.message.client.dto.BroadcastTextMessageReadByIdCmd;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * 文本广播消息service
 *
 * @author kaiyu.shan
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
   * @param broadcastTextMessageReadByIdCmd 文本广播消息根据ID已读指令
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void readMsgById(BroadcastTextMessageReadByIdCmd broadcastTextMessageReadByIdCmd);

  /**
   * 根据ID删除消息
   *
   * @param broadcastTextMessageDeleteByIdCmd 文本广播消息根据ID删除指令
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void deleteMsgById(BroadcastTextMessageDeleteByIdCmd broadcastTextMessageDeleteByIdCmd);
}
