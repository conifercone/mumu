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
package com.sky.centaur.authentication.domain.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sky.centaur.authentication.domain.authority.Authority;
import com.sky.centaur.authentication.domain.role.Role;
import com.sky.centaur.basis.domain.BasisDomainModel;
import com.sky.centaur.basis.enums.LanguageEnum;
import com.sky.centaur.basis.enums.SexEnum;
import java.io.Serial;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 账户领域模型
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@JsonDeserialize
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account extends BasisDomainModel implements UserDetails {

  @Serial
  private static final long serialVersionUID = 7134070079639713190L;
  @Getter
  private Long id;

  @Setter
  private String username;

  private String password;

  private Boolean enabled = true;

  private Boolean credentialsNonExpired = true;

  private Boolean accountNonLocked = true;

  private Boolean accountNonExpired = true;

  @Getter
  @Setter
  private Role role;

  @Getter
  @Setter
  private String avatarUrl;

  @Getter
  @Setter
  private String phone;

  @Getter
  @Setter
  private SexEnum sex;

  @Getter
  @Setter
  private String email;

  @Getter
  @Setter
  private String timezone;

  @Getter
  @Setter
  private LanguageEnum language;

  @Getter
  @Setter
  private LocalDate birthday;

  private final int age = 0;

  public Account(Long id, String username, String password, Role role) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.role = role;
  }

  public Account(Long id, String username, String password, Boolean enabled,
      Boolean accountNonExpired,
      Boolean credentialsNonExpired, Boolean accountNonLocked, Role role) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.enabled = enabled;
    this.accountNonExpired = accountNonExpired;
    this.credentialsNonExpired = credentialsNonExpired;
    this.role = role;
    this.accountNonLocked = accountNonLocked;
  }

  @Override
  public Collection<Authority> getAuthorities() {
    return Optional.ofNullable(this.role)
        .flatMap((accountRole) -> Optional.ofNullable(accountRole.getAuthorities()))
        .stream().peek(authorities -> authorities.add(
            Authority.builder().code("ROLE_".concat(this.role.getCode())).build())).findAny()
        .orElse(Collections.emptyList());
  }

  public void setAuthorities(Collection<Authority> authorities) {
    this.role.setAuthorities(new ArrayList<>(authorities));
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
