/*
 * Copyright (c) 2024-2024, the original author or authors.
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
package baby.mumu.authentication.infrastructure.account.convertor;

import baby.mumu.authentication.client.dto.co.AccountAddAddressCo;
import baby.mumu.authentication.client.dto.co.AccountCurrentLoginQueryCo;
import baby.mumu.authentication.client.dto.co.AccountRegisterCo;
import baby.mumu.authentication.client.dto.co.AccountUpdateByIdCo;
import baby.mumu.authentication.client.dto.co.AccountUpdateRoleCo;
import baby.mumu.authentication.domain.account.Account;
import baby.mumu.authentication.domain.account.AccountAddress;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.AccountAddressRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.AccountArchivedRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.AccountRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountAddressDo;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountArchivedDo;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountDo;
import baby.mumu.authentication.infrastructure.relations.database.AccountRoleDo;
import baby.mumu.authentication.infrastructure.relations.database.AccountRoleDoId;
import baby.mumu.authentication.infrastructure.relations.database.AccountRoleRepository;
import baby.mumu.authentication.infrastructure.role.convertor.RoleConvertor;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.RoleRepository;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResultCode;
import baby.mumu.unique.client.api.PrimaryKeyGrpcService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
  private final AccountRoleRepository accountRoleRepository;

  @Autowired
  public AccountConvertor(RoleConvertor roleConvertor, AccountRepository accountRepository,
      RoleRepository roleRepository, PrimaryKeyGrpcService primaryKeyGrpcService,
      AccountArchivedRepository accountArchivedRepository,
      AccountAddressRepository accountAddressRepository,
      AccountRoleRepository accountRoleRepository) {
    this.roleConvertor = roleConvertor;
    this.accountRepository = accountRepository;
    this.roleRepository = roleRepository;
    this.primaryKeyGrpcService = primaryKeyGrpcService;
    this.accountArchivedRepository = accountArchivedRepository;
    this.accountAddressRepository = accountAddressRepository;
    this.accountRoleRepository = accountRoleRepository;
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
          accountRoleRepository.findByAccountId(accountDataObject.getId()).stream()
              .flatMap(accountRoleDo -> roleConvertor.toEntity(accountRoleDo.getRole()).stream())
              .collect(Collectors.toList()));
      AccountMapper.INSTANCE.toEntity(accountDataObject, account);
      account.setAddresses(
          accountAddressRepository.findByUserId(accountDataObject.getId()).stream().map(
              AccountMapper.INSTANCE::toAccountAddress).collect(Collectors.toList()));
      return account;
    });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<AccountDo> toDataObject(Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toDataObject);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> toEntity(AccountRegisterCo accountRegisterCo) {
    return Optional.ofNullable(accountRegisterCo).map(accountRegisterClientObject -> {
      Account account = new Account(
          accountRegisterClientObject.getId() == null ?
              primaryKeyGrpcService.snowflake()
              : accountRegisterClientObject.getId(), accountRegisterClientObject.getUsername(),
          accountRegisterClientObject.getPassword(),
          roleRepository.findByCodeIn(accountRegisterClientObject.getRoleCodes()).stream()
              .flatMap(roleDo -> roleConvertor.toEntity(roleDo).stream())
              .collect(Collectors.toList()));
      AccountMapper.INSTANCE.toEntity(accountRegisterClientObject, account);
      Optional.ofNullable(account.getAddresses())
          .filter(CollectionUtils::isNotEmpty)
          .ifPresent(accountAddresses -> accountAddresses.forEach(accountAddress -> {
            accountAddress.setUserId(account.getId());
            if (accountAddress.getId() == null) {
              accountAddress.setId(primaryKeyGrpcService.snowflake());
            }
          }));
      accountRegisterClientObject.setId(account.getId());
      return account;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> toEntity(AccountUpdateByIdCo accountUpdateByIdCo) {
    return Optional.ofNullable(accountUpdateByIdCo).map(accountUpdateByIdClientObject -> {
      Optional.ofNullable(accountUpdateByIdClientObject.getId())
          .orElseThrow(() -> new MuMuException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
      return accountRepository.findById(accountUpdateByIdClientObject.getId())
          .flatMap(this::toEntity).map(account -> {
            String emailBeforeUpdated = account.getEmail();
            String usernameBeforeUpdated = account.getUsername();
            AccountMapper.INSTANCE.toEntity(accountUpdateByIdClientObject, account);
            Optional.ofNullable(account.getAddresses()).filter(CollectionUtils::isNotEmpty)
                .ifPresent(accountAddresses -> accountAddresses.forEach(
                    accountAddress -> accountAddress.setUserId(account.getId())));
            String emailAfterUpdated = account.getEmail();
            String usernameAfterUpdated = account.getUsername();
            if (StringUtils.isNotBlank(emailAfterUpdated) && !emailAfterUpdated.equals(
                emailBeforeUpdated
            ) && (accountRepository.existsByEmail(emailAfterUpdated)
                || accountArchivedRepository.existsByEmail(emailAfterUpdated))) {
              throw new MuMuException(ResultCode.ACCOUNT_EMAIL_ALREADY_EXISTS);
            }
            if (StringUtils.isNotBlank(usernameAfterUpdated) && !usernameAfterUpdated.equals(
                usernameBeforeUpdated
            ) && (accountRepository.existsByUsername(usernameAfterUpdated)
                || accountArchivedRepository.existsByUsername(usernameAfterUpdated))) {
              throw new MuMuException(ResultCode.ACCOUNT_NAME_ALREADY_EXISTS);
            }
            return account;
          }).orElse(null);
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> toEntity(AccountUpdateRoleCo accountUpdateRoleCo) {
    return Optional.ofNullable(accountUpdateRoleCo).map(accountUpdateRoleClientObject -> {
      Optional.ofNullable(accountUpdateRoleClientObject.getId())
          .orElseThrow(() -> new MuMuException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
      Optional<AccountDo> accountDoOptional = accountRepository.findById(
          accountUpdateRoleClientObject.getId());
      AccountDo accountDo = accountDoOptional.orElseThrow(
          () -> new MuMuException(ResultCode.ACCOUNT_DOES_NOT_EXIST));
      return toEntity(accountDo).map(account -> {
        account.setRoles(
            roleRepository.findByCodeIn(accountUpdateRoleClientObject.getRoleCodes()).stream()
                .flatMap(roleDo -> roleConvertor.toEntity(roleDo).stream())
                .collect(Collectors.toList()));
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
    return Optional.ofNullable(accountDo).map(AccountMapper.INSTANCE::toArchivedDo);
  }

  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<AccountDo> toDataObject(
      AccountArchivedDo accountArchivedDo) {
    return Optional.ofNullable(accountArchivedDo).map(AccountMapper.INSTANCE::toDataObject);
  }

  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<AccountAddressDo> toDataObject(
      AccountAddress accountAddress) {
    return Optional.ofNullable(accountAddress).map(AccountMapper.INSTANCE::toDataObject);
  }

  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<AccountAddress> toEntity(
      AccountAddAddressCo accountAddAddressCo) {
    return Optional.ofNullable(accountAddAddressCo).map(accountAddAddressCoNonNull -> {
      AccountAddress instanceEntity = AccountMapper.INSTANCE.toAccountAddress(accountAddAddressCo);
      if (instanceEntity.getId() == null) {
        instanceEntity.setId(primaryKeyGrpcService.snowflake());
        accountAddAddressCoNonNull.setId(instanceEntity.getId());
      }
      return instanceEntity;
    });
  }

  @API(status = Status.STABLE, since = "2.1.0")
  public List<AccountRoleDo> toAccountRoleDos(Account account) {
    return Optional.ofNullable(account).flatMap(accountNotNull -> Optional.ofNullable(
            accountNotNull.getRoles())).filter(CollectionUtils::isNotEmpty)
        .map(roles -> roles.stream().map(role -> {
          AccountRoleDo accountRoleDo = new AccountRoleDo();
          accountRoleDo.setId(
              AccountRoleDoId.builder().roleId(role.getId()).userId(account.getId()).build());
          accountRoleDo.setAccount(accountRepository.findById(account.getId()).orElse(null));
          accountRoleDo.setRole(roleRepository.findById(role.getId()).orElse(null));
          return accountRoleDo;
        }).collect(Collectors.toList())).orElse(new ArrayList<>());
  }
}
