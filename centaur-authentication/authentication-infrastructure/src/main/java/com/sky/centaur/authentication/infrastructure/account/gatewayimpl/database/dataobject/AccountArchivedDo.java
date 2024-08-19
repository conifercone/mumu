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

import com.sky.centaur.basis.annotations.GenerateDescription;
import com.sky.centaur.basis.dataobject.jpa.JpaBasisDataObject;
import com.sky.centaur.basis.enums.LanguageEnum;
import com.sky.centaur.basis.enums.SexEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

/**
 * 用户基本信息归档数据对象
 *
 * @author kaiyu.shan
 * @since 1.0.4
 */
@Entity
@Table(name = "users_archived")
@Getter
@Setter
@RequiredArgsConstructor
@DynamicInsert
@GenerateDescription
public class AccountArchivedDo extends JpaBasisDataObject {

  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  @Size(max = 50)
  @Column(name = "username", nullable = false, length = 50)
  private String username;

  @Size(max = 500)
  @Column(name = "password", nullable = false, length = 500)
  private String password;

  @Column(name = "enabled", nullable = false)
  private Boolean enabled = true;

  @Column(name = "credentials_non_expired", nullable = false)
  private Boolean credentialsNonExpired;

  @Column(name = "account_non_locked", nullable = false)
  private Boolean accountNonLocked;

  @Column(name = "account_non_expired", nullable = false)
  private Boolean accountNonExpired;

  @Size(max = 200)
  @Column(name = "avatar_url", length = 200, nullable = false)
  private String avatarUrl;

  @Size(max = 200)
  @Column(name = "phone", length = 200, nullable = false)
  private String phone;


  @Column(name = "sex", columnDefinition = "sex(0, 0)", nullable = false)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Enumerated(EnumType.STRING)
  private SexEnum sex;

  @Size(max = 200)
  @Column(name = "email", length = 200, nullable = false)
  private String email;

  @Size(max = 200)
  @Column(name = "timezone", length = 200, nullable = false)
  private String timezone;

  @Column(name = "language", columnDefinition = "language(0, 0)", nullable = false)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Enumerated(EnumType.STRING)
  private LanguageEnum language;

  @ColumnDefault("'1970-01-01'::date")
  @Column(name = "birthday", nullable = false)
  private LocalDate birthday;

  @ColumnDefault("0")
  @Column(name = "role_id", nullable = false)
  private Long roleId;

}
