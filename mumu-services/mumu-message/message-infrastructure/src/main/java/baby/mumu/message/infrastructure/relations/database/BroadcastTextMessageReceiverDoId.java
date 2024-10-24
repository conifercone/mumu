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
package baby.mumu.message.infrastructure.relations.database;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

/**
 * 文本广播消息接收者关系联合主键
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Getter
@Setter
@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastTextMessageReceiverDoId implements Serializable {

  @Serial
  private static final long serialVersionUID = -6162743713483530309L;

  @NotNull
  @Column(name = "message_id", nullable = false)
  private Long messageId;

  @NotNull
  @Column(name = "receiver_id", nullable = false)
  private Long receiverId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    BroadcastTextMessageReceiverDoId entity = (BroadcastTextMessageReceiverDoId) o;
    return Objects.equals(this.receiverId, entity.receiverId) &&
        Objects.equals(this.messageId, entity.messageId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(receiverId, messageId);
  }

}
