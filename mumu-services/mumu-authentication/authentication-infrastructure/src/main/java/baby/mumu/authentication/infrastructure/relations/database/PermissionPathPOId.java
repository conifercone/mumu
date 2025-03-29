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
package baby.mumu.authentication.infrastructure.relations.database;

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
 * 权限路径表主键实体
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.3.0
 */
@Getter
@Setter
@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    PermissionPathPOId entity = (PermissionPathPOId) o;
    return Objects.equals(this.ancestorId, entity.ancestorId) &&
      Objects.equals(this.descendantId, entity.descendantId) &&
      Objects.equals(this.depth, entity.depth);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ancestorId, descendantId, depth);
  }
}
