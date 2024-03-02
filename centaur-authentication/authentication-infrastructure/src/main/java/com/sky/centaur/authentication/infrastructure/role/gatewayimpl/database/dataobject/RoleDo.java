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

package com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.dataobject;

import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountDo;
import com.sky.centaur.basis.dataobject.jpa.JpaBasisDataObject;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

/**
 * 角色基本信息数据对象
 *
 * @author 单开宇
 * @since 2024-02-23
 */
@Getter
@Setter
@Entity
@Table(name = "roles")
public class RoleDo extends JpaBasisDataObject {

  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  @Size(max = 200)
  @Column(name = "name", length = 200)
  private String name;

  @Size(max = 100)
  @NotNull
  @Column(name = "code", nullable = false, length = 100)
  private String code;

  @OneToMany(mappedBy = "role")
  private Set<AccountDo> users = new LinkedHashSet<>();

  @Column(name = "authorities", columnDefinition = "bigint[]")
  @Type(ListArrayType.class)
  private List<Long> authorities;

}
