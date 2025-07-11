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

package baby.mumu.iam.infra.relations.database;

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
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

/**
 * 权限路径表主键实体
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.3.0
 */
@Getter
@Setter
@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PermissionPathPOId implements Serializable {

  @Serial
  private static final long serialVersionUID = -550546129487660285L;

  @NotNull
  @Column(name = "ancestor_id", nullable = false)
  private Long ancestorId;

  @NotNull
  @Column(name = "descendant_id", nullable = false)
  private Long descendantId;

  @NotNull
  @Column(name = "depth", nullable = false)
  private Long depth;

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
    PermissionPathPOId that = (PermissionPathPOId) o;
    return getAncestorId() != null && Objects.equals(getAncestorId(), that.getAncestorId())
      && getDescendantId() != null && Objects.equals(getDescendantId(),
      that.getDescendantId())
      && getDepth() != null && Objects.equals(getDepth(), that.getDepth());
  }

  @Override
  public final int hashCode() {
    return Objects.hash(ancestorId, descendantId, depth);
  }
}
