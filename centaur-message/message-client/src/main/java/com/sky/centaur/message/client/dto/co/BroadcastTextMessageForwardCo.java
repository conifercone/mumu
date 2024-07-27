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
import jakarta.validation.constraints.NotBlank;
import java.util.Collection;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文本广播消息转发客户端对象
 *
 * @author kaiyu.shan
 * @since 1.0.2
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BroadcastTextMessageForwardCo extends BaseClientObject {

  private Long id;

  private Collection<Long> receiverIds;

  @NotBlank(message = "{text.message.validation.not.blank}")
  private String message;

  private MessageStatusEnum messageStatus;
}
