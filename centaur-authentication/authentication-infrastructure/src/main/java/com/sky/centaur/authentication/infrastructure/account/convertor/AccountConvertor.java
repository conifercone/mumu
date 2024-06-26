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
  public static Optional<Account> toEntity(AccountDo accountDo) {
    return Optional.ofNullable(accountDo).map(accountDataObject -> {
      Account account = new Account(accountDataObject.getId(), accountDataObject.getUsername(),
          accountDataObject.getPassword(),
          accountDataObject.getEnabled(), accountDataObject.getAccountNonExpired(),
          accountDataObject.getCredentialsNonExpired(),
          accountDataObject.getAccountNonLocked(),
          RoleConvertor.toEntity(accountDataObject.getRole()).orElse(null));
      BEAN_TRANSFORMER.resetFieldsTransformationSkip();
      BEAN_TRANSFORMER.skipTransformationForField("id", "username", "password", "enabled",
              "accountNonExpired", "credentialsNonExpired", "role", "accountNonLocked")
          .transform(accountDataObject, account);
      return account;
    });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<AccountDo> toDataObject(Account account) {
    return Optional.ofNullable(account).map(accountDomain -> {
      BEAN_TRANSFORMER.resetFieldsTransformationSkip();
      AccountDo accountDo = BEAN_TRANSFORMER.skipTransformationForField("role")
          .transform(accountDomain, AccountDo.class);
      Optional.ofNullable(accountDomain.getRole())
          .ifPresent(
              role -> accountDo.setRole(
                  RoleConvertor.toDataObject(accountDomain.getRole()).orElse(null)));
      return accountDo;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<Account> toEntity(AccountRegisterCo accountRegisterCo) {
    return Optional.ofNullable(accountRegisterCo).map(accountRegisterClientObject -> {
      RoleRepository roleRepository = SpringContextUtil.getBean(RoleRepository.class);
      Account account = new Account(
          accountRegisterClientObject.getId() == null ?
              SpringContextUtil.getBean(PrimaryKeyGrpcService.class).snowflake()
              : accountRegisterClientObject.getId(), accountRegisterClientObject.getUsername(),
          accountRegisterClientObject.getPassword(),
          roleRepository.findByCode(accountRegisterClientObject.getRoleCode())
              .flatMap(RoleConvertor::toEntity).orElse(null));
      BEAN_TRANSFORMER.resetFieldsTransformationSkip();
      BEAN_TRANSFORMER.skipTransformationForField("id", "username", "password", "enabled",
              "accountNonExpired", "credentialsNonExpired", "role", "accountNonLocked")
          .transform(accountRegisterClientObject, account);
      accountRegisterClientObject.setId(account.getId());
      return account;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<Account> toEntity(AccountUpdateByIdCo accountUpdateByIdCo) {
    return Optional.ofNullable(accountUpdateByIdCo).map(accountUpdateByIdClientObject -> {
      Optional.ofNullable(accountUpdateByIdClientObject.getId())
          .orElseThrow(() -> new CentaurException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
      AccountRepository accountRepository = SpringContextUtil.getBean(AccountRepository.class);
      return accountRepository.findById(accountUpdateByIdClientObject.getId())
          .flatMap(AccountConvertor::toEntity).map(account -> {
            Optional.ofNullable(accountUpdateByIdClientObject.getAvatarUrl())
                .ifPresent(account::setAvatarUrl);
            Optional.ofNullable(accountUpdateByIdClientObject.getPhone())
                .ifPresent(account::setPhone);
            Optional.ofNullable(accountUpdateByIdClientObject.getSex()).ifPresent(account::setSex);
            Optional.ofNullable(accountUpdateByIdClientObject.getEmail())
                .ifPresent(account::setEmail);
            Optional.ofNullable(accountUpdateByIdClientObject.getTimezone())
                .ifPresent(account::setTimezone);
            Optional.ofNullable(accountUpdateByIdClientObject.getLanguage())
                .ifPresent(account::setLanguage);
            return account;
          }).orElse(null);
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<Account> toEntity(AccountUpdateRoleCo accountUpdateRoleCo) {
    return Optional.ofNullable(accountUpdateRoleCo).map(accountUpdateRoleClientObject -> {
      Optional.ofNullable(accountUpdateRoleClientObject.getId())
          .orElseThrow(() -> new CentaurException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
      AccountRepository accountRepository = SpringContextUtil.getBean(AccountRepository.class);
      Optional<AccountDo> accountDoOptional = accountRepository.findById(
          accountUpdateRoleClientObject.getId());
      AccountDo accountDo = accountDoOptional.orElseThrow(
          () -> new CentaurException(ResultCode.ACCOUNT_DOES_NOT_EXIST));
      return toEntity(accountDo).map(account -> {
        RoleRepository roleRepository = SpringContextUtil.getBean(RoleRepository.class);
        roleRepository.findByCode(accountUpdateRoleClientObject.getRoleCode())
            .ifPresentOrElse(roleDo -> account.setRole(RoleConvertor.toEntity(roleDo).orElse(null)),
                () -> {
                  throw new CentaurException(ResultCode.ROLE_DOES_NOT_EXIST);
                });
        return account;
      }).orElse(null);
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<AccountCurrentLoginQueryCo> toCurrentLoginQueryCo(
      Account account) {
    return Optional.ofNullable(account)
        .map(accountDomain -> {
          BEAN_TRANSFORMER.resetFieldsTransformationSkip();
          return BEAN_TRANSFORMER.transform(accountDomain,
              AccountCurrentLoginQueryCo.class);
        });
  }
}
