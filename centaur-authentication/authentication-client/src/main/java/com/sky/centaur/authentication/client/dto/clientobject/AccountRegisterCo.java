package com.sky.centaur.authentication.client.dto.clientobject;

import java.util.Collection;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

/**
 * 账户信息注册客户端对象
 *
 * @author 单开宇
 * @since 2024-01-16
 */
@Data
public class AccountRegisterCo {

  private Long id;

  private String username;

  private String password;

  private Collection<GrantedAuthority> authorities;
}
