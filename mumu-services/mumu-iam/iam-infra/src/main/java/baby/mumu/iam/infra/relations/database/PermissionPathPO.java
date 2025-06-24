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

import baby.mumu.basis.po.jpa.JpaBasisDefaultPersistentObject;
import baby.mumu.iam.infra.permission.gatewayimpl.database.po.PermissionPO;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.io.Serial;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.proxy.HibernateProxy;

/**
 * 权限路径
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.3.0
 */
@Getter
@Setter
@Entity
@Table(name = "mumu_permission_paths")
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
public class PermissionPathPO extends JpaBasisDefaultPersistentObject {

  @Serial
  private static final long serialVersionUID = 5664371470283158730L;

  @EmbeddedId
  private PermissionPathPOId id;

  @MapsId("ancestorId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ancestor_id", nullable = false)
  private PermissionPO ancestor;

  @MapsId("descendantId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "descendant_id", nullable = false)
  private PermissionPO descendant;

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
    PermissionPathPO that = (PermissionPathPO) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return Objects.hash(id);
  }
}
