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
package com.sky.centaur.message.client.dto.co;

import com.sky.centaur.basis.client.dto.co.BaseClientObject;
import com.sky.centaur.basis.enums.MessageStatusEnum;
import java.io.Serial;
import java.util.Collection;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文本广播消息查询所有当前用户发送消息客户端对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BroadcastTextMessageFindAllYouSendCo extends BaseClientObject {

  @Serial
  private static final long serialVersionUID = 2003211486094796833L;

  private Long id;

  private Long senderId;

  private Collection<Long> receiverIds;

  private Long readQuantity;

  private Long unreadQuantity;

  private String message;

  private MessageStatusEnum messageStatus;

  private Collection<Long> readReceiverIds;

  private Collection<Long> unreadReceiverIds;
}
