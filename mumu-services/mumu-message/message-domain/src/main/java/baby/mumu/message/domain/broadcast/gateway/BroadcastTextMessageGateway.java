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

package baby.mumu.message.domain.broadcast.gateway;

import baby.mumu.message.domain.broadcast.BroadcastTextMessage;
import jakarta.annotation.Nullable;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.data.domain.Page;

/**
 * 广播文本消息领域网关
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.2
 */
public interface BroadcastTextMessageGateway {

  /**
   * 消息转发
   *
   * @param msg 文本广播消息
   * @since 1.0.2
   */
  @API(status = Status.STABLE, since = "1.0.2")
  void forwardMsg(BroadcastTextMessage msg);

  /**
   * 根据ID已读消息
   *
   * @param id 消息ID
   * @since 1.0.3
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void readMsgById(Long id);

  /**
   * 根据ID删除消息
   *
   * @param id 消息ID
   * @since 1.0.3
   */
  @API(status = Status.STABLE, since = "1.0.3")
  void deleteMsgById(Long id);

  /**
   * 分页查询所有当前登录用户转发的文本广播消息
   *
   * @param broadcastTextMessage 查询条件
   * @param current              页码
   * @param pageSize             每页数量
   * @return 查询结果
   */
  @API(status = Status.STABLE, since = "1.0.3")
  Page<BroadcastTextMessage> findAllYouSend(@Nullable BroadcastTextMessage broadcastTextMessage,
    int current,
    int pageSize);

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
}
