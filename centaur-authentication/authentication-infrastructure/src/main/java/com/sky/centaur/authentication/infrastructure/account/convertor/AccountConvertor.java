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
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountNodeDo;
import com.sky.centaur.authentication.infrastructure.role.convertor.RoleConvertor;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.RoleRepository;
import com.sky.centaur.basis.tools.SpringContextUtil;
import com.sky.centaur.unique.client.api.PrimaryKeyGrpcService;
import java.util.Optional;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 账户信息转换器
 *
 * @author 单开宇
 * @since 2024-01-16
 */
public class AccountConvertor {

  @Contract("_ -> new")
  public static @NotNull Account toEntity(@NotNull AccountDo accountDo) {
    Account account = new Account(accountDo.getId(), accountDo.getUsername(),
        accountDo.getPassword(),
        accountDo.getEnabled(), accountDo.getAccountNonExpired(),
        accountDo.getCredentialsNonExpired(),
        accountDo.getAccountNonLocked(), RoleConvertor.toEntity(accountDo.getRole()));
    account.setFounder(accountDo.getFounder());
    account.setModifier(accountDo.getModifier());
    account.setCreationTime(accountDo.getCreationTime());
    account.setModificationTime(accountDo.getModificationTime());
    return account;
  }

  @Contract("_ -> new")
  public static @NotNull AccountDo toDataObject(@NotNull Account account) {
    AccountDo accountDo = new AccountDo();
    accountDo.setId(account.getId());
    accountDo.setUsername(account.getUsername());
    accountDo.setPassword(account.getPassword());
    accountDo.setEnabled(account.isEnabled());
    accountDo.setCredentialsNonExpired(account.isCredentialsNonExpired());
    accountDo.setAccountNonLocked(account.isAccountNonLocked());
    accountDo.setAccountNonExpired(account.isAccountNonExpired());
    Optional.ofNullable(account.getRole())
        .ifPresent(role -> accountDo.setRole(RoleConvertor.toDataObject(account.getRole())));
    return accountDo;
  }

  @Contract("_ -> new")
  public static @NotNull AccountNodeDo toNodeDataObject(@NotNull Account account) {
    AccountNodeDo accountNodeDo = new AccountNodeDo();
    accountNodeDo.setId(account.getId());
    accountNodeDo.setUsername(account.getUsername());
    Optional.ofNullable(account.getRole())
        .ifPresent(role -> accountNodeDo.setRole(RoleConvertor.toNodeDataObject(role)));
    return accountNodeDo;
  }

  public static @NotNull Account toEntity(@NotNull AccountRegisterCo accountRegisterCo) {
    RoleRepository roleRepository = SpringContextUtil.getBean(RoleRepository.class);
    return new Account(
        accountRegisterCo.getId() == null ?
            SpringContextUtil.getBean(PrimaryKeyGrpcService.class).snowflake()
            : accountRegisterCo.getId(), accountRegisterCo.getUsername(),
        accountRegisterCo.getPassword(),
        Optional.ofNullable(roleRepository.findByCode(accountRegisterCo.getRoleCode()))
            .map(RoleConvertor::toEntity).orElse(null));
  }
}
