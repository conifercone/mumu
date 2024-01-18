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
package com.sky.centaur.authentication.infrastructure.account.convertor;

import com.sky.centaur.authentication.client.dto.co.AccountRegisterCo;
import com.sky.centaur.authentication.domain.account.Account;
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountDo;
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
