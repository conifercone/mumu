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

import baby.mumu.authentication.domain.permission.Permission;
import baby.mumu.authentication.domain.role.Role;
import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.constants.CommonConstants;
import baby.mumu.basis.domain.BasisDomainModel;
import baby.mumu.basis.enums.LanguageEnum;
import baby.mumu.basis.enums.SexEnum;
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
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 账户领域模型
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Metamodel
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
  @Builder.Default
  private Boolean enabled = true;

  /**
   * 凭证未过期
   */
  @Builder.Default
  private Boolean credentialsNonExpired = true;

  /**
   * 帐户未锁定
   */
  @Builder.Default
  private Boolean accountNonLocked = true;

  /**
   * 帐号未过期
   */
  @Builder.Default
  private Boolean accountNonExpired = true;

  /**
   * 账户角色
   */
  private List<Role> roles;

  /**
   * 账户角色后代
   */
  @Builder.Default
  private transient List<Role> descendantRoles = new ArrayList<>();

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

  /**
   * 系统设置
   */
  private List<AccountSystemSettings> systemSettings;

  @Override
  @JsonIgnore
  public Collection<Permission> getAuthorities() {
    return Stream.concat(
        Optional.ofNullable(this.roles).orElse(Collections.emptyList()).stream(),
        this.descendantRoles.stream()
      )
      .collect(Collectors.toMap(Role::getCode, role -> role, (v1, v2) -> v1))
      .values()
      .stream()
      .flatMap(role -> {
        if (CollectionUtils.isEmpty(role.getPermissions())) {
          return Stream.empty();
        }
        // 将角色权限去重
        Set<Permission> authorities = new HashSet<>(
          CollectionUtils.union(role.getPermissions(), role.getDescendantPermissions()).stream()
            .collect(Collectors.toMap(Permission::getCode, authority -> authority, (v1, v2) -> v1))
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

  public int getAge() {
    return Optional.ofNullable(this.birthday)
      .map(accountBirthday -> Period.between(accountBirthday, LocalDate.now()).getYears())
      .orElse(0);
  }
}
