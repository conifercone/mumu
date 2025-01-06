/*
 * Copyright (c) 2024-2025, the original author or authors.
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

import baby.mumu.authentication.client.api.grpc.AccountCurrentLoginGrpcDTO;
import baby.mumu.authentication.client.api.grpc.AccountRoleCurrentLoginQueryGrpcDTO;
import baby.mumu.authentication.client.cmds.AccountAddAddressCmd;
import baby.mumu.authentication.client.cmds.AccountAddSystemSettingsCmd;
import baby.mumu.authentication.client.cmds.AccountFindAllCmd;
import baby.mumu.authentication.client.cmds.AccountFindAllSliceCmd;
import baby.mumu.authentication.client.cmds.AccountModifySystemSettingsBySettingsIdCmd;
import baby.mumu.authentication.client.cmds.AccountRegisterCmd;
import baby.mumu.authentication.client.cmds.AccountUpdateByIdCmd;
import baby.mumu.authentication.client.cmds.AccountUpdateRoleCmd;
import baby.mumu.authentication.client.dto.AccountBasicInfoDTO;
import baby.mumu.authentication.client.dto.AccountCurrentLoginDTO;
import baby.mumu.authentication.client.dto.AccountFindAllDTO;
import baby.mumu.authentication.client.dto.AccountFindAllSliceDTO;
import baby.mumu.authentication.client.dto.AccountNearbyDTO;
import baby.mumu.authentication.domain.account.Account;
import baby.mumu.authentication.domain.account.AccountAddress;
import baby.mumu.authentication.domain.account.AccountSystemSettings;
import baby.mumu.authentication.domain.role.Role;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.AccountArchivedRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.AccountRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.po.AccountArchivedPO;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.po.AccountPO;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.mongodb.AccountAddressMongodbRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.mongodb.AccountSystemSettingsMongodbRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.mongodb.po.AccountAddressMongodbPO;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.mongodb.po.AccountSystemSettingsMongodbPO;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.redis.po.AccountRedisPO;
import baby.mumu.authentication.infrastructure.account.units.AccountDigitalPreferenceUnit;
import baby.mumu.authentication.infrastructure.relations.database.AccountRolePO;
import baby.mumu.authentication.infrastructure.relations.database.AccountRolePOId;
import baby.mumu.authentication.infrastructure.relations.database.AccountRoleRepository;
import baby.mumu.authentication.infrastructure.relations.database.RolePathPO;
import baby.mumu.authentication.infrastructure.relations.database.RolePathPOId;
import baby.mumu.authentication.infrastructure.relations.database.RolePathRepository;
import baby.mumu.authentication.infrastructure.relations.redis.AccountRoleRedisPO;
import baby.mumu.authentication.infrastructure.relations.redis.AccountRoleRedisRepository;
import baby.mumu.authentication.infrastructure.role.convertor.RoleConvertor;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.RoleRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.redis.RoleRedisRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.redis.po.RoleRedisPO;
import baby.mumu.basis.constants.AccountSystemSettingsDefaultValueConstants;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
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
  private final AccountArchivedRepository accountArchivedRepository;
  private final AccountAddressMongodbRepository accountAddressMongodbRepository;
  private final AccountRoleRepository accountRoleRepository;
  private final RoleRedisRepository roleRedisRepository;
  private final AccountSystemSettingsMongodbRepository accountSystemSettingsMongodbRepository;
  private final AccountRoleRedisRepository accountRoleRedisRepository;
  private final RolePathRepository rolePathRepository;

  @Autowired
  public AccountConvertor(RoleConvertor roleConvertor, AccountRepository accountRepository,
    RoleRepository roleRepository,
    AccountArchivedRepository accountArchivedRepository,
    AccountAddressMongodbRepository accountAddressMongodbRepository,
    AccountRoleRepository accountRoleRepository,
    RoleRedisRepository roleRedisRepository,
    AccountSystemSettingsMongodbRepository accountSystemSettingsMongodbRepository,
    AccountRoleRedisRepository accountRoleRedisRepository,
    RolePathRepository rolePathRepository) {
    this.roleConvertor = roleConvertor;
    this.accountRepository = accountRepository;
    this.roleRepository = roleRepository;
    this.accountArchivedRepository = accountArchivedRepository;
    this.accountAddressMongodbRepository = accountAddressMongodbRepository;
    this.accountRoleRepository = accountRoleRepository;
    this.roleRedisRepository = roleRedisRepository;
    this.accountSystemSettingsMongodbRepository = accountSystemSettingsMongodbRepository;
    this.accountRoleRedisRepository = accountRoleRedisRepository;
    this.rolePathRepository = rolePathRepository;
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> toEntity(AccountPO accountPO) {
    return Optional.ofNullable(accountPO).flatMap(accountDataObject -> {
      Account account = AccountMapper.INSTANCE.toEntity(accountDataObject);
      setRolesWithIds(account, getRoleIds(accountDataObject.getId()));
      return getBasicInfoAccount(accountDataObject, account);
    });
  }

  private void setDigitalPreference(Account account) {
    AccountDigitalPreferenceUnit ruleUnit = new AccountDigitalPreferenceUnit();
    ruleUnit.getAccounts().add(account);
    // 加载规则单元并执行规则
    try (RuleUnitInstance<AccountDigitalPreferenceUnit> instance =
      RuleUnitProvider.get().createRuleUnitInstance(ruleUnit)) {
      instance.fire();
    }
  }

  private void setRolesWithIds(Account account, List<Long> roleIds) {
    Optional.ofNullable(account).ifPresent(accountNotNull -> {
      ArrayList<Role> roles = getRoles(
        Optional.ofNullable(roleIds).map(
            roleIdsNotNull -> roleIdsNotNull.stream().distinct()
              .collect(Collectors.toList()))
          .orElse(new ArrayList<>()));
      initializeRoles(accountNotNull, roles);
    });
  }

  private @NotNull ArrayList<Role> getRoles(List<Long> roleIds) {
    // 查询缓存中存在的数据
    List<RoleRedisPO> roleRedisPOS = roleRedisRepository.findAllById(
      roleIds);
    // 缓存中存在的角色ID
    List<Long> cachedCollectionOfRoleIDs = roleRedisPOS.stream()
      .map(RoleRedisPO::getId)
      .collect(Collectors.toList());
    // 已缓存的角色
    List<Role> cachedCollectionOfRole = roleRedisPOS.stream()
      .flatMap(roleRedisPO -> roleConvertor.toEntity(roleRedisPO).stream())
      .collect(
        Collectors.toList());
    // 未缓存的角色
    List<Role> uncachedCollectionOfRole = Optional.of(
        CollectionUtils.subtract(roleIds, cachedCollectionOfRoleIDs))
      .filter(CollectionUtils::isNotEmpty).map(
        uncachedCollectionOfRoleId -> roleRepository.findAllById(
            uncachedCollectionOfRoleId)
          .stream()
          .flatMap(rolePO -> roleConvertor.toEntity(rolePO).stream())
          .collect(
            Collectors.toList())).orElse(new ArrayList<>());
    // 未缓存的角色放入缓存
    if (CollectionUtils.isNotEmpty(uncachedCollectionOfRole)) {
      roleRedisRepository.saveAll(uncachedCollectionOfRole.stream()
        .flatMap(authority -> roleConvertor.toRoleRedisPO(authority).stream())
        .collect(
          Collectors.toList()));
    }
    // 合并已缓存和未缓存的角色
    return new ArrayList<>(
      CollectionUtils.union(cachedCollectionOfRole, uncachedCollectionOfRole));
  }

  private void setRolesWithCodes(Account account, List<String> codes) {
    Optional.ofNullable(account).ifPresent(accountNotNull -> {
      ArrayList<Role> roles = getRolesByCodes(
        Optional.ofNullable(codes).map(
            codeIdsNotNull -> codeIdsNotNull.stream().distinct()
              .collect(Collectors.toList()))
          .orElse(new ArrayList<>()));
      initializeRoles(accountNotNull, roles);
    });
  }

  private void initializeRoles(@NotNull Account accountNotNull, ArrayList<Role> roles) {
    accountNotNull.setRoles(roles);
    List<Long> ancestorIds = roles.stream().filter(Role::isHasDescendant)
      .map(Role::getId)
      .collect(Collectors.toList());
    if (CollectionUtils.isNotEmpty(ancestorIds)) {
      accountNotNull.setDescendantRoles(
        getRoles(rolePathRepository.findByAncestorIdIn(
          ancestorIds).stream().map(RolePathPO::getId).map(
          RolePathPOId::getDescendantId).distinct().collect(Collectors.toList())));
    }
  }

  private @NotNull ArrayList<Role> getRolesByCodes(List<String> codes) {
    // 查询缓存中存在的数据
    List<RoleRedisPO> roleRedisPOS = roleRedisRepository.findByCodeIn(
      codes);
    // 缓存中存在的角色编码
    List<String> cachedCollectionOfRoleCodes = roleRedisPOS.stream()
      .map(RoleRedisPO::getCode)
      .collect(Collectors.toList());
    // 已缓存的角色
    List<Role> cachedCollectionOfRole = roleRedisPOS.stream()
      .flatMap(roleRedisPO -> roleConvertor.toEntity(roleRedisPO).stream())
      .collect(
        Collectors.toList());
    // 未缓存的角色
    List<Role> uncachedCollectionOfRole = Optional.of(
        CollectionUtils.subtract(codes, cachedCollectionOfRoleCodes))
      .filter(CollectionUtils::isNotEmpty).map(
        uncachedCollectionOfRoleId -> roleRepository.findByCodeIn(uncachedCollectionOfRoleId)
          .stream()
          .flatMap(rolePO -> roleConvertor.toEntity(rolePO).stream())
          .collect(
            Collectors.toList())).orElse(new ArrayList<>());
    // 未缓存的角色放入缓存
    if (CollectionUtils.isNotEmpty(uncachedCollectionOfRole)) {
      roleRedisRepository.saveAll(uncachedCollectionOfRole.stream()
        .flatMap(authority -> roleConvertor.toRoleRedisPO(authority).stream())
        .collect(
          Collectors.toList()));
    }
    // 合并已缓存和未缓存的角色
    return new ArrayList<>(
      CollectionUtils.union(cachedCollectionOfRole, uncachedCollectionOfRole));
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Account> toBasicInfoEntity(AccountPO accountPO) {
    return Optional.ofNullable(accountPO).flatMap(accountDataObject -> {
      Account account = AccountMapper.INSTANCE.toEntity(accountDataObject);
      return getBasicInfoAccount(accountDataObject, account);
    });
  }

  @NotNull
  private Optional<Account> getBasicInfoAccount(@NotNull AccountPO accountDataObject,
    Account account) {
    return Optional.ofNullable(account).map(accountNotNull -> {
      accountNotNull.setAddresses(
        accountAddressMongodbRepository.findByUserId(accountDataObject.getId()).stream().map(
          AccountMapper.INSTANCE::toAccountAddress).collect(Collectors.toList()));
      accountNotNull.setSystemSettings(
        accountSystemSettingsMongodbRepository.findByUserId(accountDataObject.getId()).stream()
          .flatMap(accountSystemSettingsMongodbPO -> this.toAccountSystemSettings(
            accountSystemSettingsMongodbPO).stream())
          .collect(Collectors.toList()));
      setDigitalPreference(accountNotNull);
      return accountNotNull;
    });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Account> toEntity(AccountRedisPO accountRedisPO) {
    return Optional.ofNullable(accountRedisPO).map(AccountMapper.INSTANCE::toEntity)
      .map(account -> {
        setRolesWithIds(account, getRoleIds(account.getId()));
        account.setSystemSettings(
          accountSystemSettingsMongodbRepository.findByUserId(account.getId()).stream()
            .flatMap(accountSystemSettingsMongodbPO -> this.toAccountSystemSettings(
              accountSystemSettingsMongodbPO).stream())
            .collect(Collectors.toList()));
        setDigitalPreference(account);
        return account;
      });
  }

  private @NotNull List<Long> getRoleIds(Long account) {
    return accountRoleRedisRepository.findById(account)
      .map(AccountRoleRedisPO::getRoleIds).orElseGet(() -> {
        List<Long> roleIds = accountRoleRepository.findByAccountId(account)
          .stream()
          .map(AccountRolePO::getId).map(AccountRolePOId::getRoleId)
          .collect(Collectors.toList());
        accountRoleRedisRepository.save(
          new AccountRoleRedisPO(account, roleIds));
        return roleIds;
      });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<AccountPO> toPO(Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toPO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountRedisPO> toAccountRedisPO(Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toAccountRedisPO);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> toEntity(AccountRegisterCmd accountRegisterCmd) {
    return Optional.ofNullable(accountRegisterCmd).map(accountRegisterCmdNotNull -> {
      Account account = AccountMapper.INSTANCE.toEntity(accountRegisterCmdNotNull);
      setRolesWithCodes(account, Optional.ofNullable(accountRegisterCmdNotNull.getRoleCodes())
        .orElse(new ArrayList<>()));
      Optional.ofNullable(account.getAddresses())
        .filter(CollectionUtils::isNotEmpty)
        .ifPresent(accountAddresses -> accountAddresses.forEach(
          accountAddress -> accountAddress.setUserId(account.getId())));
      Optional.ofNullable(account.getSystemSettings())
        .ifPresentOrElse(
          accountSystemSettings -> accountSystemSettings.forEach(
            accountSystemSettingsItem -> accountSystemSettingsItem.setUserId(account.getId())),
          () -> account.setSystemSettings(Collections.singletonList(
            AccountSystemSettings.builder()
              .userId(
                account.getId()).enabled(true).profile(
                AccountSystemSettingsDefaultValueConstants.DEFAULT_ACCOUNT_SYSTEM_SETTINGS_PROFILE_VALUE)
              .name(
                AccountSystemSettingsDefaultValueConstants.DEFAULT_ACCOUNT_SYSTEM_SETTINGS_NAME_VALUE)
              .enabled(true)
              .build()))
        );
      return account;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> toEntity(AccountUpdateByIdCmd accountUpdateByIdCmd) {
    return Optional.ofNullable(accountUpdateByIdCmd).flatMap(accountUpdateByIdCmdNotNull -> {
      Optional.ofNullable(accountUpdateByIdCmdNotNull.getId())
        .orElseThrow(() -> new MuMuException(ResponseCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
      return accountRepository.findById(accountUpdateByIdCmdNotNull.getId())
        .flatMap(this::toEntity).flatMap(account -> {
          String emailBeforeUpdated = account.getEmail();
          String usernameBeforeUpdated = account.getUsername();
          AccountMapper.INSTANCE.toEntity(accountUpdateByIdCmdNotNull, account);
          Optional.ofNullable(account.getAddresses()).filter(CollectionUtils::isNotEmpty)
            .ifPresent(accountAddresses -> accountAddresses.forEach(
              accountAddress -> accountAddress.setUserId(account.getId())));
          String emailAfterUpdated = account.getEmail();
          String usernameAfterUpdated = account.getUsername();
          if (StringUtils.isNotBlank(emailAfterUpdated) && !emailAfterUpdated.equals(
            emailBeforeUpdated
          ) && (accountRepository.existsByEmail(emailAfterUpdated)
            || accountArchivedRepository.existsByEmail(emailAfterUpdated))) {
            throw new MuMuException(ResponseCode.ACCOUNT_EMAIL_ALREADY_EXISTS);
          }
          if (StringUtils.isNotBlank(usernameAfterUpdated) && !usernameAfterUpdated.equals(
            usernameBeforeUpdated
          ) && (accountRepository.existsByUsername(usernameAfterUpdated)
            || accountArchivedRepository.existsByUsername(usernameAfterUpdated))) {
            throw new MuMuException(ResponseCode.ACCOUNT_NAME_ALREADY_EXISTS);
          }
          return Optional.of(account);
        });
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> toEntity(AccountUpdateRoleCmd accountUpdateRoleCmd) {
    return Optional.ofNullable(accountUpdateRoleCmd).flatMap(accountUpdateRoleCmdNotNull -> {
      Optional.ofNullable(accountUpdateRoleCmdNotNull.getId())
        .orElseThrow(() -> new MuMuException(ResponseCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
      Optional<AccountPO> accountPOOptional = accountRepository.findById(
        accountUpdateRoleCmdNotNull.getId());
      AccountPO accountPO = accountPOOptional.orElseThrow(
        () -> new MuMuException(ResponseCode.ACCOUNT_DOES_NOT_EXIST));
      return toEntity(accountPO).map(account -> {
        Optional.ofNullable(accountUpdateRoleCmdNotNull.getRoleCodes())
          .ifPresent(roleCodes -> setRolesWithCodes(account, roleCodes));
        return account;
      });
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<AccountCurrentLoginDTO> toCurrentLoginQueryDTO(
    Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toCurrentLoginQueryDTO);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountBasicInfoDTO> toBasicInfoDTO(
    Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toBasicInfoDTO);
  }

  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<AccountArchivedPO> toArchivedPO(
    AccountPO accountPO) {
    return Optional.ofNullable(accountPO).map(AccountMapper.INSTANCE::toArchivedPO);
  }

  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<AccountPO> toPO(
    AccountArchivedPO accountArchivedPO) {
    return Optional.ofNullable(accountArchivedPO).map(AccountMapper.INSTANCE::toPO);
  }

  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<AccountAddressMongodbPO> toAccountAddressPO(
    AccountAddress accountAddress) {
    return Optional.ofNullable(accountAddress).map(AccountMapper.INSTANCE::toAccountAddressPO);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountSystemSettingsMongodbPO> toAccountSystemSettingMongodbPO(
    AccountSystemSettings accountSystemSettings) {
    return Optional.ofNullable(accountSystemSettings)
      .map(AccountMapper.INSTANCE::toAccountSystemSettingMongodbPO);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountSystemSettings> toAccountSystemSettings(
    AccountSystemSettingsMongodbPO accountSystemSettingsMongodbPO) {
    return Optional.ofNullable(accountSystemSettingsMongodbPO)
      .map(AccountMapper.INSTANCE::toAccountSystemSettings);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountSystemSettings> toAccountSystemSettings(
    AccountAddSystemSettingsCmd accountAddSystemSettingsCmd) {
    return Optional.ofNullable(accountAddSystemSettingsCmd)
      .map(AccountMapper.INSTANCE::toAccountSystemSettings);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountSystemSettings> toAccountSystemSettings(
    AccountModifySystemSettingsBySettingsIdCmd accountModifySystemSettingsBySettingsIdCmd) {
    return Optional.ofNullable(accountModifySystemSettingsBySettingsIdCmd)
      .flatMap(accountModifySystemSettingsBySettingsIdCmdNotNull -> {
        Optional.ofNullable(accountModifySystemSettingsBySettingsIdCmdNotNull.getId())
          .orElseThrow(() -> new MuMuException(ResponseCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
        return accountSystemSettingsMongodbRepository.findById(
            accountModifySystemSettingsBySettingsIdCmdNotNull.getId())
          .flatMap(this::toAccountSystemSettings).flatMap(accountSystemSettings -> {
            AccountMapper.INSTANCE.toAccountSystemSettings(
              accountModifySystemSettingsBySettingsIdCmdNotNull,
              accountSystemSettings);
            return Optional.of(accountSystemSettings);
          });
      });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountSystemSettingsMongodbPO> resetAccountSystemSettingMongodbPO(
    AccountSystemSettingsMongodbPO accountSystemSettingsMongodbPO) {
    return Optional.ofNullable(accountSystemSettingsMongodbPO)
      .map(systemSettingsMongodbPO -> {
        // id，userId,profile,name,enabled,version属性不重置
        AccountMapper.INSTANCE.toAccountSystemSettingMongodbPO(
          new AccountSystemSettingsMongodbPO(systemSettingsMongodbPO.getId(),
            systemSettingsMongodbPO.getUserId(),
            systemSettingsMongodbPO.getProfile(),
            systemSettingsMongodbPO.getName(),
            systemSettingsMongodbPO.getEnabled(),
            systemSettingsMongodbPO.getVersion()),
          systemSettingsMongodbPO);
        return systemSettingsMongodbPO;
      });
  }


  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<AccountAddress> toEntity(
    AccountAddAddressCmd accountAddAddressCmd) {
    return Optional.ofNullable(accountAddAddressCmd).map(
      AccountMapper.INSTANCE::toAccountAddress);
  }

  @API(status = Status.STABLE, since = "2.1.0")
  public List<AccountRolePO> toAccountRolePOS(Account account) {
    return Optional.ofNullable(account).flatMap(accountNotNull -> Optional.ofNullable(
        accountNotNull.getRoles())).filter(CollectionUtils::isNotEmpty)
      .map(roles -> roles.stream().map(role -> {
        AccountRolePO accountRolePO = new AccountRolePO();
        accountRolePO.setId(
          AccountRolePOId.builder().roleId(role.getId()).userId(account.getId()).build());
        accountRolePO.setAccount(accountRepository.findById(account.getId()).orElse(null));
        accountRolePO.setRole(roleRepository.findById(role.getId()).orElse(null));
        return accountRolePO;
      }).collect(Collectors.toList())).orElse(new ArrayList<>());
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountFindAllDTO> toFindAllDTO(
    Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toFindAllDTO);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountFindAllSliceDTO> toFindAllSliceDTO(
    Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toFindAllSliceDTO);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Account> toEntity(
    AccountFindAllCmd accountFindAllCmd) {
    return Optional.ofNullable(accountFindAllCmd).map(AccountMapper.INSTANCE::toEntity)
      .map(account -> {
        Optional.ofNullable(accountFindAllCmd.getRoleIds())
          .ifPresent(roleIds -> setRolesWithIds(account, roleIds));
        return account;
      });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Account> toEntity(
    AccountFindAllSliceCmd accountFindAllSliceCmd) {
    return Optional.ofNullable(accountFindAllSliceCmd).map(AccountMapper.INSTANCE::toEntity)
      .map(account -> {
        Optional.ofNullable(accountFindAllSliceCmd.getRoleIds())
          .ifPresent(roleIds -> setRolesWithIds(account, roleIds));
        return account;
      });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountCurrentLoginGrpcDTO> toAccountCurrentLoginGrpcDTO(
    AccountCurrentLoginDTO accountCurrentLoginDTO) {
    return Optional.ofNullable(accountCurrentLoginDTO)
      .map(AccountMapper.INSTANCE::toAccountCurrentLoginGrpcDTO)
      .map(accountCurrentLoginGrpcDTO -> accountCurrentLoginGrpcDTO.toBuilder()
        .addAllRoles(Optional.ofNullable(accountCurrentLoginDTO.getRoles())
          .map(roles -> roles.stream().map(role -> {
            AccountRoleCurrentLoginQueryGrpcDTO accountRoleCurrentLoginQueryGrpcDTO = AccountMapper.INSTANCE.toAccountRoleCurrentLoginQueryGrpcDTO(
              role);
            return accountRoleCurrentLoginQueryGrpcDTO.toBuilder().addAllPermissions(
              Optional.ofNullable(role.getPermissions()).map(
                accountRolePermissionCurrentLoginQueryDTOS -> accountRolePermissionCurrentLoginQueryDTOS.stream()
                  .map(AccountMapper.INSTANCE::toAccountRolePermissionCurrentLoginQueryGrpcDTO)
                  .collect(Collectors.toList())).orElse(new ArrayList<>())).build();
          }).collect(Collectors.toList())).orElse(new ArrayList<>()))
        .addAllAddresses(Optional.ofNullable(accountCurrentLoginDTO.getAddresses())
          .map(accountAddressCurrentLoginQueryDTOS -> accountAddressCurrentLoginQueryDTOS.stream()
            .map(AccountMapper.INSTANCE::toAccountAddressCurrentLoginQueryGrpcDTO)
            .collect(Collectors.toList())).orElse(new ArrayList<>()))
        .addAllSystemSettings(Optional.ofNullable(accountCurrentLoginDTO.getSystemSettings())
          .map(
            accountSystemSettingsCurrentLoginQueryDTOS -> accountSystemSettingsCurrentLoginQueryDTOS.stream()
              .map(AccountMapper.INSTANCE::toAccountSystemSettingsCurrentLoginQueryGrpcDTO)
              .collect(Collectors.toList())).orElse(new ArrayList<>())).build());
  }

  @API(status = Status.STABLE, since = "2.6.0")
  public Optional<AccountNearbyDTO> toAccountNearbyDTO(
    Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toAccountNearbyDTO);
  }
}
