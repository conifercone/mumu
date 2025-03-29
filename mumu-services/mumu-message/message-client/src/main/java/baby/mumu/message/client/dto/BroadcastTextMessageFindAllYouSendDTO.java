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
package baby.mumu.message.client.dto;

import baby.mumu.basis.dto.BaseDataTransferObject;
import baby.mumu.basis.enums.MessageStatusEnum;
import java.io.Serial;
import java.util.Collection;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文本广播消息查询所有当前用户发送消息数据传输对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BroadcastTextMessageFindAllYouSendDTO extends BaseDataTransferObject {

  @Serial
  private static final long serialVersionUID = -7258144172471088949L;

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
