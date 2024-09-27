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
package baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject;

import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleDo;
import baby.mumu.basis.annotations.GenerateDescription;
import baby.mumu.basis.dataobject.jpa.JpaBasisArchivableDataObject;
import baby.mumu.basis.enums.LanguageEnum;
import baby.mumu.basis.enums.SexEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

/**
 * 用户基本信息数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@RequiredArgsConstructor
@DynamicInsert
@GenerateDescription
public class AccountDo extends JpaBasisArchivableDataObject {

  @Serial
  private static final long serialVersionUID = 2503384819239906407L;

  /**
   * 账户id
   */
  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  /**
   * 账户名
   */
  @Size(max = 50)
  @NotNull
  @Column(name = "username", nullable = false, length = 50)
  private String username;

  /**
   * 账户密码
   */
  @Size(max = 500)
  @NotNull
  @Column(name = "password", nullable = false, length = 500)
  private String password;

  /**
   * 已启用
   */
  @NotNull
  @Column(name = "enabled", nullable = false)
  private Boolean enabled = true;

  /**
   * 凭证未过期
   */
  @Column(name = "credentials_non_expired", nullable = false)
  private Boolean credentialsNonExpired;

  /**
   * 帐户未锁定
   */
  @Column(name = "account_non_locked", nullable = false)
  private Boolean accountNonLocked;

  /**
   * 帐号未过期
   */
  @Column(name = "account_non_expired", nullable = false)
  private Boolean accountNonExpired;

  /**
   * 当前账户角色
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "role_id", nullable = false)
  private RoleDo role;

  /**
   * 头像地址
   */
  @Size(max = 200)
  @Column(name = "avatar_url", length = 200, nullable = false)
  private String avatarUrl;

  /**
   * 手机号
   */
  @Size(max = 200)
  @Column(name = "phone", length = 200, nullable = false)
  private String phone;

  /**
   * 性别
   */
  @Column(name = "sex", columnDefinition = "sex(0, 0)", nullable = false)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Enumerated(EnumType.STRING)
  private SexEnum sex;

  /**
   * 电子邮箱
   */
  @Size(max = 200)
  @Column(name = "email", length = 200, nullable = false)
  private String email;

  /**
   * 时区
   */
  @Size(max = 200)
  @Column(name = "timezone", length = 200, nullable = false)
  private String timezone;

  /**
   * 语言偏好
   */
  @Column(name = "language", columnDefinition = "language(0, 0)", nullable = false)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Enumerated(EnumType.STRING)
  private LanguageEnum language;

  /**
   * 出生日期
   */
  @NotNull
  @ColumnDefault("'1970-01-01'::date")
  @Column(name = "birthday", nullable = false)
  private LocalDate birthday;

  /**
   * 账户地址ID
   */
  @ColumnDefault("0")
  @Column(name = "address_id", nullable = false)
  private Long addressId;

}
