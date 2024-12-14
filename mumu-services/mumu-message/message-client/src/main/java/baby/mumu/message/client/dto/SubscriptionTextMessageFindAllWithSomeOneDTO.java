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
package baby.mumu.message.client.dto;

import baby.mumu.basis.dto.BaseDataTransferObject;
import baby.mumu.basis.enums.MessageStatusEnum;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文本订阅消息查询所有和某人的消息记录数据传输对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SubscriptionTextMessageFindAllWithSomeOneDTO extends BaseDataTransferObject {

  @Serial
  private static final long serialVersionUID = 99754484797733666L;

  private Long id;

  private Long senderId;

  private Long receiverId;

  private String message;

  private MessageStatusEnum messageStatus;
}
