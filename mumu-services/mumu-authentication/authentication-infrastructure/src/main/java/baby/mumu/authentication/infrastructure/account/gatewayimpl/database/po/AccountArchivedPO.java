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
package baby.mumu.authentication.infrastructure.account.gatewayimpl.database.po;

import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.enums.GenderEnum;
import baby.mumu.basis.enums.LanguageEnum;
import baby.mumu.basis.po.jpa.JpaBasisArchivablePersistentObject;
import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CompositeType;
import org.hibernate.annotations.DynamicInsert;
import org.javamoney.moneta.Money;

/**
 * 用户基本信息归档数据对象
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 1.0.4
 */
@Entity
@Table(name = "mumu_users_archived")
@Getter
@Setter
@RequiredArgsConstructor
@DynamicInsert
@Metamodel
public class AccountArchivedPO extends JpaBasisArchivablePersistentObject {

  @Serial
  private static final long serialVersionUID = -7785479212845125722L;

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
  private boolean enabled = true;

  @Column(name = "credentials_non_expired", nullable = false)
  private boolean credentialsNonExpired = true;

  @Column(name = "account_non_locked", nullable = false)
  private boolean accountNonLocked = true;

  @Column(name = "account_non_expired", nullable = false)
  private boolean accountNonExpired = true;

  @Size(max = 200)
  @Column(name = "avatar_url", length = 200, nullable = false)
  private String avatarUrl;

  @Size(max = 10)
  @Column(name = "phone_country_code", length = 10, nullable = false)
  private String phoneCountryCode;

  @Size(max = 200)
  @Column(name = "phone", length = 200, nullable = false)
  private String phone;

  @Column(name = "gender", nullable = false)
  @Enumerated(EnumType.STRING)
  private GenderEnum gender;

  @Size(max = 200)
  @Column(name = "email", length = 200, nullable = false)
  private String email;

  @Size(max = 200)
  @Column(name = "timezone", length = 200, nullable = false)
  private String timezone;

  @Column(name = "language", nullable = false)
  @Enumerated(EnumType.STRING)
  private LanguageEnum language;

  @ColumnDefault("'1970-01-01'::date")
  @Column(name = "birthday", nullable = false)
  private LocalDate birthday;

  /**
   * 个性签名
   */
  @Size(max = 500)
  @Column(name = "bio", length = 500, nullable = false)
  private String bio;

  /**
   * 昵称
   */
  @Size(max = 100)
  @Column(name = "nick_name", length = 100, nullable = false)
  private String nickName;

  /**
   * 余额
   */
  @AttributeOverride(
    name = "amount",
    column = @Column(name = "balance")
  )
  @AttributeOverride(
    name = "currency",
    column = @Column(name = "balance_currency")
  )
  @CompositeType(MonetaryAmountType.class)
  private Money balance;

  /**
   * 手机号已验证
   */
  @Column(name = "phone_verified", nullable = false)
  private boolean phoneVerified;

  /**
   * 邮箱已验证
   */
  @Column(name = "email_verified", nullable = false)
  private boolean emailVerified;
}
