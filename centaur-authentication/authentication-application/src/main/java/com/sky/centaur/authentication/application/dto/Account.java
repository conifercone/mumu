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
package com.sky.centaur.authentication.application.dto;

import com.sky.centaur.authentication.domain.account.Users;
import java.util.Collection;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * 账户信息
 *
 * @author 单开宇
 * @since 2024-01-12
 */
public class Account extends User {

  private Long id;

  private String username;

  private String password;

  private boolean enable = true;

  private boolean credentialsNonExpired = true;

  private boolean accountNonLocked = true;

  private boolean accountNonExpired = true;

  private Collection<? extends GrantedAuthority> authorities;

  public Account(@NotNull Users users) {
    super(users.getUsername(), users.getPassword(), users.getEnabled(),
        users.getAccountNonExpired(), users.getCredentialsNonExpired(), users.getAccountNonLocked(),
        Collections.emptyList());
    this.id = users.getId();
    this.username = users.getUsername();
    this.password = users.getPassword();
    this.enable = users.getEnabled();
    this.credentialsNonExpired = users.getCredentialsNonExpired();
    this.accountNonExpired = users.getAccountNonExpired();
    this.accountNonLocked = users.getAccountNonLocked();
  }

  public Account(Long id, String username, String password,
      Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
    this.id = id;
    this.username = username;
    this.password = password;
    this.authorities = authorities;
  }

  public Account(Long id, String username, String password, boolean enabled,
      boolean accountNonExpired,
      boolean credentialsNonExpired, boolean accountNonLocked,
      Collection<? extends GrantedAuthority> authorities) {
    super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
        authorities);
    this.id = id;
    this.username = username;
    this.password = password;
    this.authorities = authorities;
    this.enable = enabled;
    this.accountNonExpired = accountNonExpired;
    this.credentialsNonExpired = credentialsNonExpired;
    this.accountNonLocked = accountNonLocked;
  }

  @Override
  public Collection<GrantedAuthority> getAuthorities() {
    return super.getAuthorities();
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
    return this.enable;
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
}
