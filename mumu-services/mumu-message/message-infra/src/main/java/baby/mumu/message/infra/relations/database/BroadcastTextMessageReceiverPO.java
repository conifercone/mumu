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

package baby.mumu.message.infra.relations.database;

import baby.mumu.basis.enums.MessageStatusEnum;
import baby.mumu.basis.po.jpa.JpaBasisDefaultPersistentObject;
import baby.mumu.message.infra.broadcast.gatewayimpl.database.po.BroadcastTextMessagePO;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.io.Serial;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.proxy.HibernateProxy;

/**
 * 文本广播消息接收者关系数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
@Getter
@Setter
@Entity
@Table(name = "mumu_broadcast_text_message_receivers")
@DynamicInsert
@ToString
public class BroadcastTextMessageReceiverPO extends JpaBasisDefaultPersistentObject {

  @Serial
  private static final long serialVersionUID = -3752303933612720910L;

  @EmbeddedId
  private BroadcastTextMessageReceiverPOId id;

  @Column(name = "message_status", nullable = false)
  @Enumerated(EnumType.STRING)
  @ColumnDefault("'UNREAD'")
  private MessageStatusEnum messageStatus;

  @MapsId("messageId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "message_id", nullable = false)
  @Exclude
  private BroadcastTextMessagePO broadcastTextMessage;

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
    BroadcastTextMessageReceiverPO that = (BroadcastTextMessageReceiverPO) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return Objects.hash(id);
  }
}
