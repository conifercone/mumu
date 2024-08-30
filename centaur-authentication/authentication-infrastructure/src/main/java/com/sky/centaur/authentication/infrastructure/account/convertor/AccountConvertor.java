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

import com.sky.centaur.authentication.client.dto.co.AccountAddAddressCo;
import com.sky.centaur.authentication.client.dto.co.AccountCurrentLoginQueryCo;
import com.sky.centaur.authentication.client.dto.co.AccountRegisterCo;
import com.sky.centaur.authentication.client.dto.co.AccountUpdateByIdCo;
import com.sky.centaur.authentication.client.dto.co.AccountUpdateRoleCo;
import com.sky.centaur.authentication.domain.account.Account;
import com.sky.centaur.authentication.domain.account.AccountAddress;
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.AccountAddressRepository;
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.AccountArchivedRepository;
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.AccountRepository;
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountAddressDo;
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountArchivedDo;
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountDo;
import com.sky.centaur.authentication.infrastructure.role.convertor.RoleConvertor;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.RoleRepository;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.unique.client.api.PrimaryKeyGrpcService;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 账户信息转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
public class AccountConvertor {

  private final RoleConvertor roleConvertor;
  private final AccountRepository accountRepository;
  private final RoleRepository roleRepository;
  private final PrimaryKeyGrpcService primaryKeyGrpcService;
  private final AccountArchivedRepository accountArchivedRepository;
  private final AccountAddressRepository accountAddressRepository;

  @Autowired
  public AccountConvertor(RoleConvertor roleConvertor, AccountRepository accountRepository,
      RoleRepository roleRepository, PrimaryKeyGrpcService primaryKeyGrpcService,
      AccountArchivedRepository accountArchivedRepository,
      AccountAddressRepository accountAddressRepository) {
    this.roleConvertor = roleConvertor;
    this.accountRepository = accountRepository;
    this.roleRepository = roleRepository;
    this.primaryKeyGrpcService = primaryKeyGrpcService;
    this.accountArchivedRepository = accountArchivedRepository;
    this.accountAddressRepository = accountAddressRepository;
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> toEntity(AccountDo accountDo) {
    return Optional.ofNullable(accountDo).map(accountDataObject -> {
      Account account = new Account(accountDataObject.getId(), accountDataObject.getUsername(),
          accountDataObject.getPassword(),
          accountDataObject.getEnabled(), accountDataObject.getAccountNonExpired(),
          accountDataObject.getCredentialsNonExpired(),
          accountDataObject.getAccountNonLocked(),
          roleConvertor.toEntity(accountDataObject.getRole()).orElse(null));
      AccountMapper.INSTANCE.toEntity(accountDataObject, account);
      Optional.ofNullable(accountDataObject.getAddressId())
          .flatMap(accountAddressRepository::findById).ifPresent(
              accountAddressDo -> account.setAddress(
                  AccountMapper.INSTANCE.toEntity(accountAddressDo)));
      return account;
    });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<AccountDo> toDataObject(Account account) {
    return Optional.ofNullable(account).map(accountDomain -> {
      AccountDo accountDo = AccountMapper.INSTANCE.toDataObject(accountDomain);
      Optional.ofNullable(accountDomain.getAddress())
          .flatMap(address -> Optional.ofNullable(address.getId()))
          .ifPresent(accountDo::setAddressId);
      Optional.ofNullable(accountDomain.getRole())
          .ifPresent(
              role -> accountDo.setRole(
                  roleConvertor.toDataObject(accountDomain.getRole()).orElse(null)));
      return accountDo;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> toEntity(AccountRegisterCo accountRegisterCo) {
    return Optional.ofNullable(accountRegisterCo).map(accountRegisterClientObject -> {
      Account account = new Account(
          accountRegisterClientObject.getId() == null ?
              primaryKeyGrpcService.snowflake()
              : accountRegisterClientObject.getId(), accountRegisterClientObject.getUsername(),
          accountRegisterClientObject.getPassword(),
          roleRepository.findByCode(accountRegisterClientObject.getRoleCode())
              .flatMap(roleConvertor::toEntity).orElse(null));
      AccountMapper.INSTANCE.toEntity(accountRegisterClientObject, account);
      Optional.ofNullable(account.getAddress())
          .ifPresent(accountAddress -> {
            accountAddress.setUserId(account.getId());
            if (accountAddress.getId() == null) {
              accountAddress.setId(primaryKeyGrpcService.snowflake());
              accountRegisterClientObject.getAddress().setId(accountAddress.getId());
            }
          });
      accountRegisterClientObject.setId(account.getId());
      return account;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> toEntity(AccountUpdateByIdCo accountUpdateByIdCo) {
    return Optional.ofNullable(accountUpdateByIdCo).map(accountUpdateByIdClientObject -> {
      Optional.ofNullable(accountUpdateByIdClientObject.getId())
          .orElseThrow(() -> new CentaurException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
      return accountRepository.findById(accountUpdateByIdClientObject.getId())
          .flatMap(this::toEntity).map(account -> {
            String emailBeforeUpdated = account.getEmail();
            String usernameBeforeUpdated = account.getUsername();
            AccountMapper.INSTANCE.toEntity(accountUpdateByIdClientObject, account);
            Optional.ofNullable(account.getAddress())
                .ifPresent(accountAddress -> accountAddress.setUserId(account.getId()));
            String emailAfterUpdated = account.getEmail();
            String usernameAfterUpdated = account.getUsername();
            if (StringUtils.hasText(emailAfterUpdated) && !emailAfterUpdated.equals(
                emailBeforeUpdated
            ) && (accountRepository.existsByEmail(emailAfterUpdated)
                || accountArchivedRepository.existsByEmail(emailAfterUpdated))) {
              throw new CentaurException(ResultCode.ACCOUNT_EMAIL_ALREADY_EXISTS);
            }
            if (StringUtils.hasText(usernameAfterUpdated) && !usernameAfterUpdated.equals(
                usernameBeforeUpdated
            ) && (accountRepository.existsByUsername(usernameAfterUpdated)
                || accountArchivedRepository.existsByUsername(usernameAfterUpdated))) {
              throw new CentaurException(ResultCode.ACCOUNT_NAME_ALREADY_EXISTS);
            }
            return account;
          }).orElse(null);
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> toEntity(AccountUpdateRoleCo accountUpdateRoleCo) {
    return Optional.ofNullable(accountUpdateRoleCo).map(accountUpdateRoleClientObject -> {
      Optional.ofNullable(accountUpdateRoleClientObject.getId())
          .orElseThrow(() -> new CentaurException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
      Optional<AccountDo> accountDoOptional = accountRepository.findById(
          accountUpdateRoleClientObject.getId());
      AccountDo accountDo = accountDoOptional.orElseThrow(
          () -> new CentaurException(ResultCode.ACCOUNT_DOES_NOT_EXIST));
      return toEntity(accountDo).map(account -> {
        roleRepository.findByCode(accountUpdateRoleClientObject.getRoleCode())
            .ifPresentOrElse(roleDo -> account.setRole(roleConvertor.toEntity(roleDo).orElse(null)),
                () -> {
                  throw new CentaurException(ResultCode.ROLE_DOES_NOT_EXIST);
                });
        return account;
      }).orElse(null);
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<AccountCurrentLoginQueryCo> toCurrentLoginQueryCo(
      Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toCurrentLoginQueryCo);
  }

  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<AccountArchivedDo> toArchivedDo(
      AccountDo accountDo) {
    return Optional.ofNullable(accountDo).map(accountDataObject -> {
      AccountArchivedDo archivedDo = AccountMapper.INSTANCE.toArchivedDo(accountDataObject);
      Optional.ofNullable(accountDataObject.getRole())
          .ifPresent(roleDo -> archivedDo.setRoleId(roleDo.getId()));
      return archivedDo;
    });
  }

  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<AccountDo> toDataObject(
      AccountArchivedDo accountArchivedDo) {
    return Optional.ofNullable(accountArchivedDo).map(accountArchivedDataObject -> {
      AccountDo accountDo = AccountMapper.INSTANCE.toDataObject(accountArchivedDataObject);
      Optional.ofNullable(accountArchivedDataObject.getRoleId()).flatMap(roleRepository::findById)
          .ifPresent(accountDo::setRole);
      return accountDo;
    });
  }

  @API(status = Status.STABLE, since = "1.0.5")
  public Optional<AccountAddressDo> toDataObject(
      AccountAddress accountAddress) {
    return Optional.ofNullable(accountAddress).map(AccountMapper.INSTANCE::toDataObject);
  }

  @API(status = Status.STABLE, since = "1.0.5")
  public Optional<AccountAddress> toEntity(
      AccountAddAddressCo accountAddAddressCo) {
    return Optional.ofNullable(accountAddAddressCo).map(accountAddAddressCoNonNull -> {
      AccountAddress instanceEntity = AccountMapper.INSTANCE.toEntity(accountAddAddressCo);
      if (instanceEntity.getId() == null) {
        instanceEntity.setId(primaryKeyGrpcService.snowflake());
        accountAddAddressCoNonNull.setId(instanceEntity.getId());
      }
      return instanceEntity;
    });
  }
}
