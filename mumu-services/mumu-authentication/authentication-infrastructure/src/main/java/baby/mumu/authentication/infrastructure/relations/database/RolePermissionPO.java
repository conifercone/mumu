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

import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.po.PermissionPO;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.po.RolePO;
import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.po.jpa.JpaBasisDefaultPersistentObject;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.io.Serial;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

/**
 * 角色权限关系数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.1.0
 */
@Getter
@Setter
@Entity
@Table(name = "mumu_role_permissions")
@DynamicInsert
@Metamodel
public class RolePermissionPO extends JpaBasisDefaultPersistentObject {

  @Serial
  private static final long serialVersionUID = 4305030711096513693L;

  @EmbeddedId
  private RolePermissionPOId id;

  @MapsId("roleId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "role_id", nullable = false)
  private RolePO role;

  @MapsId("permissionId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "permission_id", nullable = false)
  private PermissionPO permission;
}
