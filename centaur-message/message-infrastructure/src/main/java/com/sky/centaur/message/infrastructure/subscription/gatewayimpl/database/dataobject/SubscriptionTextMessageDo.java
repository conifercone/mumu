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
package com.sky.centaur.message.infrastructure.subscription.gatewayimpl.database.dataobject;

import com.sky.centaur.basis.dataobject.jpa.JpaBasisDataObject;
import com.sky.centaur.basis.enums.MessageStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

/**
 * 订阅文本消息数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.2
 */
@Getter
@Setter
@Entity
@Table(name = "subscription_text_message")
@RequiredArgsConstructor
@DynamicInsert
public class SubscriptionTextMessageDo extends JpaBasisDataObject {

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

  @Column(name = "message_status", columnDefinition = "message_status(0, 0)", nullable = false)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Enumerated(EnumType.STRING)
  @ColumnDefault("'UNREAD'::message_status")
  private MessageStatusEnum messageStatus;
}
