package com.sky.centaur.authentication.infrastructure.convertor;

import com.sky.centaur.authentication.client.dto.clientobject.AccountRegisterCo;
import com.sky.centaur.authentication.domain.account.Account;
import com.sky.centaur.authentication.infrastructure.gatewayimpl.database.dataobject.AccountDo;
import java.util.Collections;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;

/**
 * 账户信息转换器
 *
 * @author 单开宇
 * @since 2024-01-16
 */
public class AccountConvertor {

  @Contract("_ -> new")
  public static @NotNull Account toEntity(@NotNull AccountDo accountDo) {
    return new Account(accountDo.getId(), accountDo.getUsername(), accountDo.getPassword(),
        accountDo.isEnabled(), accountDo.isAccountNonExpired(), accountDo.isCredentialsNonExpired(),
        accountDo.isAccountNonLocked(), Collections.emptyList());
  }

  @Contract("_ -> new")
  public static @NotNull AccountDo toDataObject(@NotNull Account account) {
    AccountDo accountDo = new AccountDo();
    BeanUtils.copyProperties(account, accountDo);
    return accountDo;
  }

  @Contract("_ -> new")
  public static @NotNull Account toEntity(@NotNull AccountRegisterCo accountRegisterCo) {
    return new Account(
        accountRegisterCo.getId(), accountRegisterCo.getUsername(), accountRegisterCo.getPassword(),
        accountRegisterCo.getAuthorities());
  }
}
