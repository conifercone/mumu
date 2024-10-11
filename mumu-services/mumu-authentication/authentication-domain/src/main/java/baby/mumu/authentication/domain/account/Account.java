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
package baby.mumu.authentication.domain.account;

import baby.mumu.authentication.domain.authority.Authority;
import baby.mumu.authentication.domain.role.Role;
import baby.mumu.basis.annotations.GenerateDescription;
import baby.mumu.basis.constants.CommonConstants;
import baby.mumu.basis.domain.BasisDomainModel;
import baby.mumu.basis.enums.LanguageEnum;
import baby.mumu.basis.enums.SexEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serial;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 账户领域模型
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@JsonDeserialize
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@GenerateDescription
@Data
@SuperBuilder(toBuilder = true)
public class Account extends BasisDomainModel implements UserDetails {

  @Serial
  private static final long serialVersionUID = 7134070079639713190L;

  /**
   * 账户id
   */
  private Long id;

  /**
   * 账户名
   */
  private String username;

  /**
   * 账户密码
   */
  private String password;

  /**
   * 是否启用
   */
  private Boolean enabled = true;

  /**
   * 凭证未过期
   */
  private Boolean credentialsNonExpired = true;

  /**
   * 帐户未锁定
   */
  private Boolean accountNonLocked = true;

  /**
   * 帐号未过期
   */
  private Boolean accountNonExpired = true;

  /**
   * 账户角色
   */
  private List<Role> roles;

  /**
   * 头像地址
   */
  private String avatarUrl;

  /**
   * 电话
   */
  private String phone;

  /**
   * 性别
   */
  private SexEnum sex;

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
   * 地址
   */
  private List<AccountAddress> addresses;

  private Collection<Authority> authorities;

  public Account(Long id, String username, String password, List<Role> roles) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.roles = roles;
  }

  public Account(Long id, String username, String password, Boolean enabled,
      Boolean accountNonExpired,
      Boolean credentialsNonExpired, Boolean accountNonLocked, List<Role> roles) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.enabled = enabled;
    this.accountNonExpired = accountNonExpired;
    this.credentialsNonExpired = credentialsNonExpired;
    this.roles = roles;
    this.accountNonLocked = accountNonLocked;
  }

  @Override
  public Collection<Authority> getAuthorities() {
    return Optional.ofNullable(this.roles)
        .orElse(Collections.emptyList())
        .stream()
        .collect(Collectors.toMap(Role::getCode, role -> role, (v1, v2) -> v1))
        .values()
        .stream()
        .flatMap(role -> {
          if (CollectionUtils.isEmpty(role.getAuthorities())) {
            return Stream.empty();
          }
          // 将角色权限去重
          Set<Authority> authorities = new HashSet<>(role.getAuthorities().stream()
              .collect(Collectors.toMap(Authority::getCode, authority -> authority, (v1, v2) -> v1))
              .values());
          // 添加角色本身的权限
          authorities.add(Authority.builder()
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

  public int getAge() {
    return Optional.ofNullable(this.birthday)
        .map(accountBirthday -> Period.between(accountBirthday, LocalDate.now()).getYears())
        .orElse(0);
  }
}
