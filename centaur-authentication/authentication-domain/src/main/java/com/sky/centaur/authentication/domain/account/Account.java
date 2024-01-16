package com.sky.centaur.authentication.domain.account;

import java.util.Collection;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * 账户领域模型
 *
 * @author 单开宇
 * @since 2024-01-16
 */
@SuperBuilder
public class Account extends User {

  @Getter
  private Long id;

  private String username;

  private String password;

  private boolean enabled = true;

  private boolean credentialsNonExpired = true;

  private boolean accountNonLocked = true;

  private boolean accountNonExpired = true;

  private Collection<GrantedAuthority> authorities;

  public Account(Long id, String username, String password,
      Collection<GrantedAuthority> authorities) {
    super(username, password, authorities);
    this.id = id;
    this.username = username;
    this.password = password;
    this.authorities = authorities;
  }

  public Account(Long id, String username, String password, boolean enabled,
      boolean accountNonExpired,
      boolean credentialsNonExpired, boolean accountNonLocked,
      Collection<GrantedAuthority> authorities) {
    super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
        authorities);
    this.id = id;
    this.username = username;
    this.password = password;
    this.authorities = authorities;
    this.enabled = enabled;
    this.accountNonExpired = accountNonExpired;
    this.credentialsNonExpired = credentialsNonExpired;
    this.accountNonLocked = accountNonLocked;
  }

  @Override
  public Collection<GrantedAuthority> getAuthorities() {
    return this.authorities;
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
}
