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
package baby.mumu.authentication.infrastructure.role.gatewayimpl.database.dataobject;

import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.dataobject.jpa.JpaBasisArchivableDataObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

/**
 * 角色基本信息归档数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.4
 */
@Getter
@Setter
@Entity
@Table(name = "roles_archived")
@DynamicInsert
@Metamodel
public class RoleArchivedDO extends JpaBasisArchivableDataObject {

  @Serial
  private static final long serialVersionUID = 8174728781452519483L;

  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  @Size(max = 200)
  @Column(name = "name", nullable = false, length = 200)
  private String name;

  @Size(max = 100)
  @NotNull
  @Column(name = "code", nullable = false, length = 100)
  private String code;

  /**
   * 角色描述
   */
  @Size(max = 500)
  @ColumnDefault("''")
  @Column(name = "description", nullable = false, length = 500)
  private String description;
}
