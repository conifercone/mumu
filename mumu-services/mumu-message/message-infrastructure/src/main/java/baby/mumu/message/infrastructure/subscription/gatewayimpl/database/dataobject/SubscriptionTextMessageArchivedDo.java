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
package baby.mumu.message.infrastructure.subscription.gatewayimpl.database.dataobject;

import baby.mumu.basis.dataobject.jpa.JpaBasisArchivableDataObject;
import baby.mumu.basis.enums.MessageStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

/**
 * 订阅文本消息归档数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.3
 */
@Getter
@Setter
@Entity
@Table(name = "subscription_text_message_archived")
@RequiredArgsConstructor
@DynamicInsert
public class SubscriptionTextMessageArchivedDo extends JpaBasisArchivableDataObject {

  @Serial
  private static final long serialVersionUID = 3607564772412326961L;

  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @ColumnDefault("0")
  @Column(name = "sender_id", nullable = false)
  private Long senderId;

  @NotNull
  @ColumnDefault("0")
  @Column(name = "receiver_id", nullable = false)
  private Long receiverId;

  @Size(max = 500)
  @NotNull
  @ColumnDefault("''")
  @Column(name = "message", nullable = false, length = 500)
  private String message;

  @Column(name = "message_status", nullable = false)
  @Enumerated(EnumType.STRING)
  @ColumnDefault("'UNREAD'")
  private MessageStatusEnum messageStatus;
}
