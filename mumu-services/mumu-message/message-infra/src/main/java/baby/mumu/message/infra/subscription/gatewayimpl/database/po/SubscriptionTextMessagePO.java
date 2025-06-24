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

package baby.mumu.message.infra.subscription.gatewayimpl.database.po;

import baby.mumu.basis.enums.MessageStatusEnum;
import baby.mumu.basis.po.jpa.JpaBasisArchivablePersistentObject;
import baby.mumu.unique.client.config.SnowflakeIdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.proxy.HibernateProxy;

/**
 * 订阅文本消息数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.2
 */
@Getter
@Setter
@Entity
@Table(name = "mumu_subscription_text_message")
@RequiredArgsConstructor
@DynamicInsert
public class SubscriptionTextMessagePO extends JpaBasisArchivablePersistentObject {

  @Serial
  private static final long serialVersionUID = 4952753219449508235L;

  @Id
  @SnowflakeIdGenerator
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

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    Class<?> oEffectiveClass = o instanceof HibernateProxy
      ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
      : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy
      ? ((HibernateProxy) this).getHibernateLazyInitializer()
      .getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) {
      return false;
    }
    SubscriptionTextMessagePO that = (SubscriptionTextMessagePO) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
      .getPersistentClass().hashCode() : getClass().hashCode();
  }
}
