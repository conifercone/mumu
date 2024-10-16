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
import baby.mumu.authentication.client.dto.co.AccountAddSystemSettingsCo;
import baby.mumu.authentication.client.dto.co.AccountBasicInfoCo;
import baby.mumu.authentication.client.dto.co.AccountCurrentLoginCo;
import baby.mumu.authentication.client.dto.co.AccountFindAllCo;
import baby.mumu.authentication.client.dto.co.AccountFindAllQueryCo;
import baby.mumu.authentication.client.dto.co.AccountFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.AccountFindAllSliceQueryCo;
import baby.mumu.authentication.client.dto.co.AccountModifySystemSettingsBySettingsIdCo;
import baby.mumu.authentication.client.dto.co.AccountRegisterCo;
import baby.mumu.authentication.client.dto.co.AccountUpdateByIdCo;
import baby.mumu.authentication.client.dto.co.AccountUpdateRoleCo;
import baby.mumu.authentication.domain.account.Account;
import baby.mumu.authentication.domain.account.AccountAddress;
import baby.mumu.authentication.domain.account.AccountSystemSettings;
import baby.mumu.authentication.domain.role.Role;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.AccountAddressRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.AccountArchivedRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.AccountRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountAddressDo;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountArchivedDo;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountDo;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.mongodb.AccountSystemSettingsMongodbRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.mongodb.dataobject.AccountSystemSettingsMongodbDo;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.redis.dataobject.AccountRedisDo;
import baby.mumu.authentication.infrastructure.relations.database.AccountRoleDo;
import baby.mumu.authentication.infrastructure.relations.database.AccountRoleDoId;
import baby.mumu.authentication.infrastructure.relations.database.AccountRoleRepository;
import baby.mumu.authentication.infrastructure.role.convertor.RoleConvertor;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.RoleRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.redis.RoleRedisRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.redis.dataobject.RoleRedisDo;
import baby.mumu.basis.constants.AccountSystemSettingsDefaultValueConstants;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResultCode;
import baby.mumu.unique.client.api.PrimaryKeyGrpcService;
import java.util.ArrayList;
import java.util.Collections;
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
  private final RoleRedisRepository roleRedisRepository;
  private final AccountSystemSettingsMongodbRepository accountSystemSettingsMongodbRepository;

  @Autowired
  public AccountConvertor(RoleConvertor roleConvertor, AccountRepository accountRepository,
      RoleRepository roleRepository, PrimaryKeyGrpcService primaryKeyGrpcService,
      AccountArchivedRepository accountArchivedRepository,
      AccountAddressRepository accountAddressRepository,
      AccountRoleRepository accountRoleRepository,
      RoleRedisRepository roleRedisRepository,
      AccountSystemSettingsMongodbRepository accountSystemSettingsMongodbRepository) {
    this.roleConvertor = roleConvertor;
    this.accountRepository = accountRepository;
    this.roleRepository = roleRepository;
    this.primaryKeyGrpcService = primaryKeyGrpcService;
    this.accountArchivedRepository = accountArchivedRepository;
    this.accountAddressRepository = accountAddressRepository;
    this.accountRoleRepository = accountRoleRepository;
    this.roleRedisRepository = roleRedisRepository;
    this.accountSystemSettingsMongodbRepository = accountSystemSettingsMongodbRepository;
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> toEntity(AccountDo accountDo) {
    return Optional.ofNullable(accountDo).map(accountDataObject -> {
      Account account = AccountMapper.INSTANCE.toEntity(accountDataObject);
      List<Long> roleIds = accountRoleRepository.findByAccountId(accountDataObject.getId()).stream()
          .map(AccountRoleDo::getId).map(AccountRoleDoId::getRoleId).collect(Collectors.toList());
      setRolesWithIds(account, roleIds);
      account.setAddresses(
          accountAddressRepository.findByUserId(accountDataObject.getId()).stream().map(
              AccountMapper.INSTANCE::toAccountAddress).collect(Collectors.toList()));
      account.setSystemSettings(
          accountSystemSettingsMongodbRepository.findByUserId(accountDataObject.getId()).stream()
              .flatMap(accountSystemSettingsMongodbDo -> this.toAccountSystemSettings(
                  accountSystemSettingsMongodbDo).stream())
              .collect(Collectors.toList()));
      return account;
    });
  }

  private void setRolesWithIds(Account account, List<Long> roleIds) {
    // 查询缓存中存在的数据
    List<RoleRedisDo> roleRedisDos = roleRedisRepository.findAllById(
        roleIds);
    // 缓存中存在的角色ID
    List<Long> cachedCollectionOfRoleIDs = roleRedisDos.stream()
        .map(RoleRedisDo::getId)
        .collect(Collectors.toList());
    // 已缓存的角色
    List<Role> cachedCollectionOfRole = roleRedisDos.stream()
        .flatMap(roleRedisDo -> roleConvertor.toEntity(roleRedisDo).stream())
        .collect(
            Collectors.toList());
    // 未缓存的角色
    List<Role> uncachedCollectionOfRole = Optional.of(
            CollectionUtils.subtract(roleIds, cachedCollectionOfRoleIDs))
        .filter(CollectionUtils::isNotEmpty).map(
            uncachedCollectionOfRoleId -> roleRepository.findAllById(
                    uncachedCollectionOfRoleId)
                .stream()
                .flatMap(roleDo -> roleConvertor.toEntity(roleDo).stream())
                .collect(
                    Collectors.toList())).orElse(new ArrayList<>());
    // 未缓存的角色放入缓存
    if (CollectionUtils.isNotEmpty(uncachedCollectionOfRole)) {
      roleRedisRepository.saveAll(uncachedCollectionOfRole.stream()
          .flatMap(authority -> roleConvertor.toRoleRedisDo(authority).stream())
          .collect(
              Collectors.toList()));
    }
    // 合并已缓存和未缓存的角色
    account.setRoles(new ArrayList<>(
        CollectionUtils.union(cachedCollectionOfRole, uncachedCollectionOfRole)));
  }

  private void setRolesWithCodes(Account account, List<String> codes) {
    // 查询缓存中存在的数据
    List<RoleRedisDo> roleRedisDos = roleRedisRepository.findByCodeIn(
        codes);
    // 缓存中存在的角色编码
    List<String> cachedCollectionOfRoleCodes = roleRedisDos.stream()
        .map(RoleRedisDo::getCode)
        .collect(Collectors.toList());
    // 已缓存的角色
    List<Role> cachedCollectionOfRole = roleRedisDos.stream()
        .flatMap(roleRedisDo -> roleConvertor.toEntity(roleRedisDo).stream())
        .collect(
            Collectors.toList());
    // 未缓存的角色
    List<Role> uncachedCollectionOfRole = Optional.of(
            CollectionUtils.subtract(codes, cachedCollectionOfRoleCodes))
        .filter(CollectionUtils::isNotEmpty).map(
            uncachedCollectionOfRoleId -> roleRepository.findByCodeIn(uncachedCollectionOfRoleId)
                .stream()
                .flatMap(roleDo -> roleConvertor.toEntity(roleDo).stream())
                .collect(
                    Collectors.toList())).orElse(new ArrayList<>());
    // 未缓存的角色放入缓存
    if (CollectionUtils.isNotEmpty(uncachedCollectionOfRole)) {
      roleRedisRepository.saveAll(uncachedCollectionOfRole.stream()
          .flatMap(authority -> roleConvertor.toRoleRedisDo(authority).stream())
          .collect(
              Collectors.toList()));
    }
    // 合并已缓存和未缓存的角色
    account.setRoles(new ArrayList<>(
        CollectionUtils.union(cachedCollectionOfRole, uncachedCollectionOfRole)));
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Account> toBasicInfoEntity(AccountDo accountDo) {
    return Optional.ofNullable(accountDo).map(accountDataObject -> {
      Account account = AccountMapper.INSTANCE.toEntity(accountDataObject);
      account.setAddresses(
          accountAddressRepository.findByUserId(accountDataObject.getId()).stream().map(
              AccountMapper.INSTANCE::toAccountAddress).collect(Collectors.toList()));
      account.setSystemSettings(
          accountSystemSettingsMongodbRepository.findByUserId(accountDataObject.getId()).stream()
              .flatMap(accountSystemSettingsMongodbDo -> this.toAccountSystemSettings(
                  accountSystemSettingsMongodbDo).stream())
              .collect(Collectors.toList()));
      return account;
    });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Account> toEntity(AccountRedisDo accountRedisDo) {
    return Optional.ofNullable(accountRedisDo).map(AccountMapper.INSTANCE::toEntity);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<AccountDo> toDataObject(Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toDataObject);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountRedisDo> toAccountRedisDo(Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toAccountRedisDo);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> toEntity(AccountRegisterCo accountRegisterCo) {
    return Optional.ofNullable(accountRegisterCo).map(accountRegisterClientObject -> {
      Account account = AccountMapper.INSTANCE.toEntity(accountRegisterClientObject);
      Optional.ofNullable(account.getId()).ifPresentOrElse(id -> {
        if (id == 0) {
          throw new MuMuException(ResultCode.ACCOUNT_ID_IS_NOT_ALLOWED_TO_BE_0);
        }
      }, () -> account.setId(primaryKeyGrpcService.snowflake()));
      setRolesWithCodes(account, Optional.ofNullable(accountRegisterClientObject.getRoleCodes())
          .orElse(new ArrayList<>()));
      Optional.ofNullable(account.getAddresses())
          .filter(CollectionUtils::isNotEmpty)
          .ifPresent(accountAddresses -> accountAddresses.forEach(accountAddress -> {
            accountAddress.setUserId(account.getId());
            if (accountAddress.getId() == null) {
              accountAddress.setId(primaryKeyGrpcService.snowflake());
            }
          }));
      Optional.ofNullable(account.getSystemSettings())
          .ifPresentOrElse(
              accountSystemSettings -> accountSystemSettings.forEach(accountSystemSettingsItem -> {
                accountSystemSettingsItem.setUserId(account.getId());
                if (accountSystemSettingsItem.getId() == null) {
                  accountSystemSettingsItem.setId(
                      String.valueOf(primaryKeyGrpcService.snowflake()));
                }
              }), () -> account.setSystemSettings(Collections.singletonList(
                  AccountSystemSettings.builder()
                      .id(String.valueOf(primaryKeyGrpcService.snowflake()))
                      .userId(
                          account.getId()).enabled(true).profile(
                          AccountSystemSettingsDefaultValueConstants.DEFAULT_ACCOUNT_SYSTEM_SETTINGS_PROFILE_VALUE)
                      .name(
                          AccountSystemSettingsDefaultValueConstants.DEFAULT_ACCOUNT_SYSTEM_SETTINGS_NAME_VALUE)
                      .enabled(true)
                      .build()))
          );
      accountRegisterClientObject.setId(account.getId());
      return account;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> toEntity(AccountUpdateByIdCo accountUpdateByIdCo) {
    return Optional.ofNullable(accountUpdateByIdCo).flatMap(accountUpdateByIdClientObject -> {
      Optional.ofNullable(accountUpdateByIdClientObject.getId())
          .orElseThrow(() -> new MuMuException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
      return accountRepository.findById(accountUpdateByIdClientObject.getId())
          .flatMap(this::toEntity).flatMap(account -> {
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
            return Optional.of(account);
          });
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> toEntity(AccountUpdateRoleCo accountUpdateRoleCo) {
    return Optional.ofNullable(accountUpdateRoleCo).flatMap(accountUpdateRoleClientObject -> {
      Optional.ofNullable(accountUpdateRoleClientObject.getId())
          .orElseThrow(() -> new MuMuException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
      Optional<AccountDo> accountDoOptional = accountRepository.findById(
          accountUpdateRoleClientObject.getId());
      AccountDo accountDo = accountDoOptional.orElseThrow(
          () -> new MuMuException(ResultCode.ACCOUNT_DOES_NOT_EXIST));
      return toEntity(accountDo).map(account -> {
        Optional.ofNullable(accountUpdateRoleClientObject.getRoleCodes())
            .ifPresent(roleCodes -> setRolesWithCodes(account, roleCodes));
        return account;
      });
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<AccountCurrentLoginCo> toCurrentLoginQueryCo(
      Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toCurrentLoginQueryCo);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountBasicInfoCo> toBasicInfoCo(
      Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toBasicInfoCo);
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
  public Optional<AccountAddressDo> toAccountAddressDo(
      AccountAddress accountAddress) {
    return Optional.ofNullable(accountAddress).map(AccountMapper.INSTANCE::toAccountAddressDo);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountSystemSettingsMongodbDo> toAccountSystemSettingMongodbDo(
      AccountSystemSettings accountSystemSettings) {
    return Optional.ofNullable(accountSystemSettings)
        .map(AccountMapper.INSTANCE::toAccountSystemSettingMongodbDo);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountSystemSettings> toAccountSystemSettings(
      AccountSystemSettingsMongodbDo accountSystemSettingsMongodbDo) {
    return Optional.ofNullable(accountSystemSettingsMongodbDo)
        .map(AccountMapper.INSTANCE::toAccountSystemSettings);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountSystemSettings> toAccountSystemSettings(
      AccountAddSystemSettingsCo accountAddSystemSettingsCo) {
    return Optional.ofNullable(accountAddSystemSettingsCo)
        .map(accountAddSystemSettingsCoNotNull -> {
          AccountSystemSettings accountSystemSettings = AccountMapper.INSTANCE.toAccountSystemSettings(
              accountAddSystemSettingsCoNotNull);
          if (StringUtils.isBlank(accountSystemSettings.getId())) {
            accountSystemSettings.setId(String.valueOf(primaryKeyGrpcService.snowflake()));
          }
          return accountSystemSettings;
        });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountSystemSettings> toAccountSystemSettings(
      AccountModifySystemSettingsBySettingsIdCo accountModifySystemSettingsBySettingsIdCo) {
    return Optional.ofNullable(accountModifySystemSettingsBySettingsIdCo)
        .flatMap(accountModifySystemSettingsBySettingsIdCoNotNull -> {
          Optional.ofNullable(accountModifySystemSettingsBySettingsIdCoNotNull.getId())
              .orElseThrow(() -> new MuMuException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
          return accountSystemSettingsMongodbRepository.findById(
                  accountModifySystemSettingsBySettingsIdCoNotNull.getId())
              .flatMap(this::toAccountSystemSettings).flatMap(accountSystemSettings -> {
                AccountMapper.INSTANCE.toAccountSystemSettings(
                    accountModifySystemSettingsBySettingsIdCoNotNull,
                    accountSystemSettings);
                return Optional.of(accountSystemSettings);
              });
        });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountSystemSettingsMongodbDo> resetAccountSystemSettingMongodbDo(
      AccountSystemSettingsMongodbDo accountSystemSettingsMongodbDo) {
    return Optional.ofNullable(accountSystemSettingsMongodbDo)
        .map(accountSystemSettingsMongodbDoTarget -> {
          // id，userId,profile,name,enabled,version属性不重置
          AccountMapper.INSTANCE.toAccountSystemSettingMongodbDo(
              new AccountSystemSettingsMongodbDo(accountSystemSettingsMongodbDoTarget.getId(),
                  accountSystemSettingsMongodbDoTarget.getUserId(),
                  accountSystemSettingsMongodbDoTarget.getProfile(),
                  accountSystemSettingsMongodbDoTarget.getName(),
                  accountSystemSettingsMongodbDoTarget.getEnabled(),
                  accountSystemSettingsMongodbDoTarget.getVersion()),
              accountSystemSettingsMongodbDoTarget);
          return accountSystemSettingsMongodbDoTarget;
        });
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

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountFindAllCo> toFindAllCo(
      Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toFindAllCo);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountFindAllSliceCo> toFindAllSliceCo(
      Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toFindAllSliceCo);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Account> toEntity(
      AccountFindAllQueryCo accountFindAllQueryCo) {
    return Optional.ofNullable(accountFindAllQueryCo).map(AccountMapper.INSTANCE::toEntity)
        .map(account -> {
          setRolesWithIds(account, accountFindAllQueryCo.getRoleIds());
          return account;
        });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Account> toEntity(
      AccountFindAllSliceQueryCo accountFindAllSliceQueryCo) {
    return Optional.ofNullable(accountFindAllSliceQueryCo).map(AccountMapper.INSTANCE::toEntity)
        .map(account -> {
          setRolesWithIds(account, accountFindAllSliceQueryCo.getRoleIds());
          return account;
        });
  }
}
