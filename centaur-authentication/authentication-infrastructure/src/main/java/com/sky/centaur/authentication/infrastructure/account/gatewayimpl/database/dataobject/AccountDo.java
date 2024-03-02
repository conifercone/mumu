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
package com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.dataobject;

import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleDo;
import com.sky.centaur.basis.dataobject.jpa.JpaBasisDataObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 用户基本信息数据对象
 *
 * @author 单开宇
 * @since 2024-01-12
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@RequiredArgsConstructor
public class AccountDo extends JpaBasisDataObject {

  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  @Size(max = 50)
  @NotNull
  @Column(name = "username", nullable = false, length = 50)
  private String username;

  @Size(max = 500)
  @NotNull
  @Column(name = "password", nullable = false, length = 500)
  private String password;

  @NotNull
  @Column(name = "enabled", nullable = false)
  private Boolean enabled = false;

  @Column(name = "credentials_non_expired")
  private Boolean credentialsNonExpired;

  @Column(name = "account_non_locked")
  private Boolean accountNonLocked;

  @Column(name = "account_non_expired")
  private Boolean accountNonExpired;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "role_id")
  private RoleDo role;
}
