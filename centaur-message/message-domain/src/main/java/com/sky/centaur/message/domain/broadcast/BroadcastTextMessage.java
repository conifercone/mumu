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
package com.sky.centaur.message.domain.broadcast;

import com.sky.centaur.basis.annotations.GenerateDescription;
import com.sky.centaur.basis.domain.BasisDomainModel;
import com.sky.centaur.basis.enums.MessageStatusEnum;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 广播文本消息
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.2
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@GenerateDescription
public class BroadcastTextMessage extends BasisDomainModel {

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
