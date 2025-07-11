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

package baby.mumu.iam.domain.account;

import baby.mumu.basis.constants.CommonConstants;
import baby.mumu.basis.domain.BasisDomainModel;
import baby.mumu.basis.enums.DigitalPreferenceEnum;
import baby.mumu.basis.enums.GenderEnum;
import baby.mumu.basis.enums.LanguageEnum;
import baby.mumu.iam.domain.permission.Permission;
import baby.mumu.iam.domain.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serial;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.javamoney.moneta.Money;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 账号领域模型
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@SuperBuilder(toBuilder = true)
public class Account extends BasisDomainModel implements UserDetails {

  @Serial
  private static final long serialVersionUID = 7134070079639713190L;

  /**
   * 账号id
   */
  private Long id;

  /**
   * 账号名
   */
  private String username;

  /**
   * 账号密码
   */
  @ToString.Exclude
  private String password;

  /**
   * 是否启用
   */
  @Builder.Default
  private boolean enabled = true;

  /**
   * 凭证未过期
   */
  @Builder.Default
  private boolean credentialsNonExpired = true;

  /**
   * 帐户未锁定
   */
  @Builder.Default
  private boolean accountNonLocked = true;

  /**
   * 帐号未过期
   */
  @Builder.Default
  private boolean accountNonExpired = true;

  /**
   * 账号角色
   */
  private List<Role> roles;

  /**
   * 账号角色后代
   */
  @Builder.Default
  private transient List<Role> descendantRoles = new ArrayList<>();

  /**
   * 头像
   */
  private AccountAvatar avatar;

  /**
   * 国际电话区号
   */
  private String phoneCountryCode;

  /**
   * 手机号
   */
  private String phone;

  /**
   * 性别
   */
  private GenderEnum gender;

  /**
   * 电子邮件
   */
  private String email;

  /**
   * 时区
   */
  private String timezone;

  /**
   * 语言偏好
   */
  private LanguageEnum language;

  /**
   * 生日
   */
  private LocalDate birthday;

  /**
   * 个性签名
   */
  private String bio;

  /**
   * 昵称
   */
  private String nickName;

  /**
   * 余额
   */
  private Money balance;

  /**
   * 数字偏好
   */
  private DigitalPreferenceEnum digitalPreference;

  /**
   * 地址
   */
  private List<AccountAddress> addresses;

  /**
   * 系统设置
   */
  private List<AccountSystemSettings> systemSettings;

  /**
   * 手机号已验证
   */
  private boolean phoneVerified;

  /**
   * 电子邮件已验证
   */
  private boolean emailVerified;

  @Override
  @JsonIgnore
  public Collection<Permission> getAuthorities() {
    return Stream.concat(
        Optional.ofNullable(this.roles).orElse(Collections.emptyList()).stream(),
        this.descendantRoles.stream()
      )
      .collect(Collectors.toMap(Role::getCode, role -> role, (v1, _) -> v1))
      .values()
      .stream()
      .flatMap(role -> {
        if (CollectionUtils.isEmpty(role.getPermissions())) {
          return Stream.empty();
        }
        // 将角色权限去重
        Set<Permission> authorities = new HashSet<>(
          CollectionUtils.union(role.getPermissions(), role.getDescendantPermissions()).stream()
            .collect(Collectors.toMap(Permission::getCode, authority -> authority, (v1, _) -> v1))
            .values());
        // 添加角色本身的权限
        authorities.add(Permission.builder()
          .code(CommonConstants.ROLE_PREFIX.concat(role.getCode()))
          .build());
        return authorities.stream();
      })
      .collect(Collectors.toSet());
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  @Override
  public boolean isAccountNonExpired() {
    return this.accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return this.credentialsNonExpired;
  }

  /**
   * 获取年龄
   *
   * @return 年龄
   */
  public int getAge() {
    return Optional.ofNullable(this.birthday)
      .map(accountBirthday -> Period.between(accountBirthday, LocalDate.now()).getYears())
      .orElse(0);
  }
}
