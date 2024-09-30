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
package baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.dataobject;

import baby.mumu.basis.dataobject.jpa.JpaBasisArchivableDataObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

/**
 * 权限基本信息数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Getter
@Setter
@Entity
@Table(name = "authorities")
@RequiredArgsConstructor
@DynamicInsert
public class AuthorityDo extends JpaBasisArchivableDataObject {

  @Serial
  private static final long serialVersionUID = 6607076146645504629L;

  /**
   * 权限id
   */
  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  /**
   * 权限编码
   */
  @Size(max = 50, message = "{authority.code.validation.size}")
  @NotNull
  @Column(name = "code", nullable = false, length = 50)
  private String code;

  /**
   * 权限名称
   */
  @Size(max = 200, message = "{authority.name.validation.size}")
  @Column(name = "name", nullable = false, length = 200)
  private String name;
}
