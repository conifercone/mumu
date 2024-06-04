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

import com.expediagroup.beans.BeanUtils;
import com.expediagroup.beans.transformer.BeanTransformer;
import com.sky.centaur.authentication.client.dto.co.AccountCurrentLoginQueryCo;
import com.sky.centaur.authentication.client.dto.co.AccountRegisterCo;
import com.sky.centaur.authentication.client.dto.co.AccountUpdateByIdCo;
import com.sky.centaur.authentication.client.dto.co.AccountUpdateRoleCo;
import com.sky.centaur.authentication.domain.account.Account;
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.AccountRepository;
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountDo;
import com.sky.centaur.authentication.infrastructure.role.convertor.RoleConvertor;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.RoleRepository;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.kotlin.tools.SpringContextUtil;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.unique.client.api.PrimaryKeyGrpcService;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 账户信息转换器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public final class AccountConvertor {

  private static final BeanTransformer BEAN_TRANSFORMER = new BeanUtils().getTransformer()
      .setDefaultValueForMissingField(true)
      .setDefaultValueForMissingPrimitiveField(false);

  private AccountConvertor() {
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
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
    account.setAvatarUrl(accountDo.getAvatarUrl());
    account.setPhone(accountDo.getPhone());
    account.setSex(accountDo.getSex());
    account.setEmail(accountDo.getEmail());
    return account;
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public static @NotNull AccountDo toDataObject(@NotNull Account account) {
    AccountDo accountDo = new AccountDo();
    accountDo.setId(account.getId());
    accountDo.setUsername(account.getUsername());
    accountDo.setPassword(account.getPassword());
    accountDo.setEnabled(account.isEnabled());
    accountDo.setCredentialsNonExpired(account.isCredentialsNonExpired());
    accountDo.setAccountNonLocked(account.isAccountNonLocked());
    accountDo.setAccountNonExpired(account.isAccountNonExpired());
    accountDo.setAvatarUrl(account.getAvatarUrl());
    accountDo.setPhone(account.getPhone());
    accountDo.setSex(account.getSex());
    accountDo.setEmail(account.getEmail());
    Optional.ofNullable(account.getRole())
        .ifPresent(role -> accountDo.setRole(RoleConvertor.toDataObject(account.getRole())));
    return accountDo;
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static @NotNull Account toEntity(@NotNull AccountRegisterCo accountRegisterCo) {
    RoleRepository roleRepository = SpringContextUtil.getBean(RoleRepository.class);
    Account account = new Account(
        accountRegisterCo.getId() == null ?
            SpringContextUtil.getBean(PrimaryKeyGrpcService.class).snowflake()
            : accountRegisterCo.getId(), accountRegisterCo.getUsername(),
        accountRegisterCo.getPassword(),
        roleRepository.findByCode(accountRegisterCo.getRoleCode())
            .map(RoleConvertor::toEntity).orElse(null));
    accountRegisterCo.setId(account.getId());
    account.setAvatarUrl(accountRegisterCo.getAvatarUrl());
    account.setPhone(accountRegisterCo.getPhone());
    account.setSex(accountRegisterCo.getSex());
    account.setEmail(accountRegisterCo.getEmail());
    return account;
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static @NotNull Account toEntity(@NotNull AccountUpdateByIdCo accountUpdateByIdCo) {
    Optional.ofNullable(accountUpdateByIdCo.getId())
        .orElseThrow(() -> new CentaurException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
    AccountRepository accountRepository = SpringContextUtil.getBean(AccountRepository.class);
    Optional<AccountDo> accountDoOptional = accountRepository.findById(accountUpdateByIdCo.getId());
    if (accountDoOptional.isPresent()) {
      Account account = toEntity(accountDoOptional.get());
      Optional.ofNullable(accountUpdateByIdCo.getAvatarUrl()).ifPresent(account::setAvatarUrl);
      Optional.ofNullable(accountUpdateByIdCo.getPhone()).ifPresent(account::setPhone);
      Optional.ofNullable(accountUpdateByIdCo.getSex()).ifPresent(account::setSex);
      Optional.ofNullable(accountUpdateByIdCo.getEmail()).ifPresent(account::setEmail);
      return account;
    } else {
      throw new CentaurException(ResultCode.ACCOUNT_DOES_NOT_EXIST);
    }

  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static @NotNull Account toEntity(@NotNull AccountUpdateRoleCo accountUpdateRoleCo) {
    Optional.ofNullable(accountUpdateRoleCo.getId())
        .orElseThrow(() -> new CentaurException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
    AccountRepository accountRepository = SpringContextUtil.getBean(AccountRepository.class);
    Optional<AccountDo> accountDoOptional = accountRepository.findById(accountUpdateRoleCo.getId());
    AccountDo accountDo = accountDoOptional.orElseThrow(
        () -> new CentaurException(ResultCode.ACCOUNT_DOES_NOT_EXIST));
    Account account = toEntity(accountDo);
    RoleRepository roleRepository = SpringContextUtil.getBean(RoleRepository.class);
    roleRepository.findByCode(accountUpdateRoleCo.getRoleCode())
        .ifPresentOrElse(roleDo -> account.setRole(RoleConvertor.toEntity(roleDo)), () -> {
          throw new CentaurException(ResultCode.ROLE_DOES_NOT_EXIST);
        });
    return account;
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static @NotNull AccountCurrentLoginQueryCo toCurrentLoginQueryCo(
      @NotNull Account account) {
    return BEAN_TRANSFORMER.transform(account, AccountCurrentLoginQueryCo.class);
  }
}
