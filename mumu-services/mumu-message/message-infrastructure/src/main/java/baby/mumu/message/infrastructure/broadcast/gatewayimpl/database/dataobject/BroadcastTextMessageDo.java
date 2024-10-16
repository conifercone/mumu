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
package baby.mumu.message.infrastructure.broadcast.gatewayimpl.database.dataobject;

import baby.mumu.basis.dataobject.jpa.JpaBasisArchivableDataObject;
import baby.mumu.basis.enums.MessageStatusEnum;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

/**
 * 广播文本消息数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.2
 */
@Getter
@Setter
@Entity
@Table(name = "broadcast_text_message")
@RequiredArgsConstructor
@DynamicInsert
public class BroadcastTextMessageDo extends JpaBasisArchivableDataObject {

  @Serial
  private static final long serialVersionUID = 8259321227756175071L;

  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @ColumnDefault("0")
  @Column(name = "sender_id", nullable = false)
  private Long senderId;

  @NotNull
  @ColumnDefault("'{}'::bigint[]")
  @Column(name = "receiver_ids", nullable = false, columnDefinition = "bigint[]")
  @Type(ListArrayType.class)
  private List<Long> receiverIds;

  @ColumnDefault("0")
  @Column(name = "read_quantity", nullable = false)
  private Long readQuantity;

  @NotNull
  @ColumnDefault("0")
  @Column(name = "unread_quantity", nullable = false)
  private Long unreadQuantity;

  @Size(max = 500)
  @NotNull
  @ColumnDefault("''")
  @Column(name = "message", nullable = false, length = 500)
  private String message;


  @Column(name = "message_status", columnDefinition = "message_status(0, 0)", nullable = false)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Enumerated(EnumType.STRING)
  @ColumnDefault("'UNREAD'::message_status")
  private MessageStatusEnum messageStatus;

  @NotNull
  @ColumnDefault("'{}'::bigint[]")
  @Column(name = "read_receiver_ids", nullable = false, columnDefinition = "bigint[]")
  @Type(ListArrayType.class)
  private List<Long> readReceiverIds;

  @NotNull
  @ColumnDefault("'{}'::bigint[]")
  @Column(name = "unread_receiver_ids", nullable = false, columnDefinition = "bigint[]")
  @Type(ListArrayType.class)
  private List<Long> unreadReceiverIds;
}
