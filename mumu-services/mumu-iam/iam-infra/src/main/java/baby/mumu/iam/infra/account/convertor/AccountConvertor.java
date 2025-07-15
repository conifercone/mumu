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

package baby.mumu.iam.infra.account.convertor;

import baby.mumu.basis.enums.DigitalPreferenceEnum;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.kotlin.tools.PhoneUtils;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.iam.client.api.grpc.AccountCurrentLoginGrpcDTO;
import baby.mumu.iam.client.api.grpc.AccountRoleGrpcDTO;
import baby.mumu.iam.client.cmds.AccountAddAddressCmd;
import baby.mumu.iam.client.cmds.AccountAddSystemSettingsCmd;
import baby.mumu.iam.client.cmds.AccountFindAllCmd;
import baby.mumu.iam.client.cmds.AccountFindAllSliceCmd;
import baby.mumu.iam.client.cmds.AccountModifyAddressByAddressIdCmd;
import baby.mumu.iam.client.cmds.AccountModifySystemSettingsBySettingsIdCmd;
import baby.mumu.iam.client.cmds.AccountRegisterCmd;
import baby.mumu.iam.client.cmds.AccountUpdateByIdCmd;
import baby.mumu.iam.client.cmds.AccountUpdateRoleCmd;
import baby.mumu.iam.client.dto.AccountBasicInfoDTO;
import baby.mumu.iam.client.dto.AccountCurrentLoginDTO;
import baby.mumu.iam.client.dto.AccountFindAllDTO;
import baby.mumu.iam.client.dto.AccountFindAllSliceDTO;
import baby.mumu.iam.client.dto.AccountNearbyDTO;
import baby.mumu.iam.client.dto.AccountUpdatedDataDTO;
import baby.mumu.iam.domain.account.Account;
import baby.mumu.iam.domain.account.AccountAddress;
import baby.mumu.iam.domain.account.AccountAvatar;
import baby.mumu.iam.domain.account.AccountSystemSettings;
import baby.mumu.iam.domain.role.Role;
import baby.mumu.iam.infra.account.gatewayimpl.cache.po.AccountCacheablePO;
import baby.mumu.iam.infra.account.gatewayimpl.database.AccountArchivedRepository;
import baby.mumu.iam.infra.account.gatewayimpl.database.AccountRepository;
import baby.mumu.iam.infra.account.gatewayimpl.database.po.AccountArchivedPO;
import baby.mumu.iam.infra.account.gatewayimpl.database.po.AccountPO;
import baby.mumu.iam.infra.account.gatewayimpl.document.AccountAddressDocumentRepository;
import baby.mumu.iam.infra.account.gatewayimpl.document.AccountAvatarDocumentRepository;
import baby.mumu.iam.infra.account.gatewayimpl.document.AccountSystemSettingsDocumentRepository;
import baby.mumu.iam.infra.account.gatewayimpl.document.po.AccountAddressDocumentPO;
import baby.mumu.iam.infra.account.gatewayimpl.document.po.AccountAvatarDocumentPO;
import baby.mumu.iam.infra.account.gatewayimpl.document.po.AccountSystemSettingsDocumentPO;
import baby.mumu.iam.infra.relations.cache.AccountRoleCacheRepository;
import baby.mumu.iam.infra.relations.cache.AccountRoleCacheablePO;
import baby.mumu.iam.infra.relations.database.AccountRolePO;
import baby.mumu.iam.infra.relations.database.AccountRolePOId;
import baby.mumu.iam.infra.relations.database.AccountRoleRepository;
import baby.mumu.iam.infra.relations.database.RolePathPO;
import baby.mumu.iam.infra.relations.database.RolePathPOId;
import baby.mumu.iam.infra.relations.database.RolePathRepository;
import baby.mumu.iam.infra.role.convertor.RoleConvertor;
import baby.mumu.iam.infra.role.gatewayimpl.cache.RoleCacheRepository;
import baby.mumu.iam.infra.role.gatewayimpl.cache.po.RoleCacheablePO;
import baby.mumu.iam.infra.role.gatewayimpl.database.RoleRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 账号信息转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
public class AccountConvertor {

  private final RoleConvertor roleConvertor;
  private final AccountRepository accountRepository;
  private final RoleRepository roleRepository;
  private final AccountArchivedRepository accountArchivedRepository;
  private final AccountAddressDocumentRepository accountAddressDocumentRepository;
  private final AccountRoleRepository accountRoleRepository;
  private final RoleCacheRepository roleCacheRepository;
  private final AccountSystemSettingsDocumentRepository accountSystemSettingsDocumentRepository;
  private final AccountRoleCacheRepository accountRoleCacheRepository;
  private final RolePathRepository rolePathRepository;
  private final AccountAvatarDocumentRepository accountAvatarDocumentRepository;

  @Autowired
  public AccountConvertor(RoleConvertor roleConvertor, AccountRepository accountRepository,
    RoleRepository roleRepository,
    AccountArchivedRepository accountArchivedRepository,
    AccountAddressDocumentRepository accountAddressDocumentRepository,
    AccountRoleRepository accountRoleRepository,
    RoleCacheRepository roleCacheRepository,
    AccountSystemSettingsDocumentRepository accountSystemSettingsDocumentRepository,
    AccountRoleCacheRepository accountRoleCacheRepository,
    RolePathRepository rolePathRepository,
    AccountAvatarDocumentRepository accountAvatarDocumentRepository) {
    this.roleConvertor = roleConvertor;
    this.accountRepository = accountRepository;
    this.roleRepository = roleRepository;
    this.accountArchivedRepository = accountArchivedRepository;
    this.accountAddressDocumentRepository = accountAddressDocumentRepository;
    this.accountRoleRepository = accountRoleRepository;
    this.roleCacheRepository = roleCacheRepository;
    this.accountSystemSettingsDocumentRepository = accountSystemSettingsDocumentRepository;
    this.accountRoleCacheRepository = accountRoleCacheRepository;
    this.rolePathRepository = rolePathRepository;
    this.accountAvatarDocumentRepository = accountAvatarDocumentRepository;
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
    Optional.ofNullable(account).ifPresent(accountNotNull -> {
      int age = accountNotNull.getAge();
      if (age <= 34) {
        accountNotNull.setDigitalPreference(DigitalPreferenceEnum.DIGITAL_NATIVE);
      } else if (age <= 54) {
        accountNotNull.setDigitalPreference(DigitalPreferenceEnum.DIGITAL_IMMIGRANT);
      } else {
        accountNotNull.setDigitalPreference(DigitalPreferenceEnum.TRADITIONAL_USER);
      }
    });
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
    List<RoleCacheablePO> roleCacheablePOS = roleCacheRepository.findAllById(
      roleIds);
    // 缓存中存在的角色ID
    List<Long> cachedCollectionOfRoleIDs = roleCacheablePOS.stream()
      .map(RoleCacheablePO::getId)
      .collect(Collectors.toList());
    // 已缓存的角色
    List<Role> cachedCollectionOfRole = roleCacheablePOS.stream()
      .flatMap(roleCacheablePO -> roleConvertor.toEntity(roleCacheablePO).stream())
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
      roleCacheRepository.saveAll(uncachedCollectionOfRole.stream()
        .flatMap(authority -> roleConvertor.toRoleCacheablePO(authority).stream())
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
    List<RoleCacheablePO> roleCacheablePOS = roleCacheRepository.findByCodeIn(
      codes);
    // 缓存中存在的角色编码
    List<String> cachedCollectionOfRoleCodes = roleCacheablePOS.stream()
      .map(RoleCacheablePO::getCode)
      .collect(Collectors.toList());
    // 已缓存的角色
    List<Role> cachedCollectionOfRole = roleCacheablePOS.stream()
      .flatMap(roleCacheablePO -> roleConvertor.toEntity(roleCacheablePO).stream())
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
      roleCacheRepository.saveAll(uncachedCollectionOfRole.stream()
        .flatMap(authority -> roleConvertor.toRoleCacheablePO(authority).stream())
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
    return Optional.ofNullable(accountPO)
      .flatMap(accountDataObject -> getBasicInfoAccount(accountDataObject,
        AccountMapper.INSTANCE.toEntity(accountDataObject)));
  }

  @NotNull
  private Optional<Account> getBasicInfoAccount(@NotNull AccountPO accountDataObject,
    Account account) {
    return Optional.ofNullable(account).map(accountNotNull -> {
      List<AccountAddressDocumentPO> accountAddressDocumentPOList = accountAddressDocumentRepository.findByAccountId(
        accountDataObject.getId());
      if (CollectionUtils.isNotEmpty(accountAddressDocumentPOList)) {
        accountNotNull.setAddresses(
          accountAddressDocumentPOList.stream().map(
            AccountMapper.INSTANCE::toAccountAddress).collect(Collectors.toList()));
      }
      accountAvatarDocumentRepository.findByAccountId(accountDataObject.getId())
        .map(AccountMapper.INSTANCE::toAccountAvatar).ifPresent(accountNotNull::setAvatar);
      List<AccountSystemSettingsDocumentPO> accountSystemSettingsDocumentPOList = accountSystemSettingsDocumentRepository.findByAccountId(
        accountDataObject.getId());
      if (CollectionUtils.isNotEmpty(accountSystemSettingsDocumentPOList)) {
        accountNotNull.setSystemSettings(
          accountSystemSettingsDocumentPOList.stream()
            .flatMap(accountSystemSettingsDocumentPO -> this.toAccountSystemSettings(
              accountSystemSettingsDocumentPO).stream())
            .collect(Collectors.toList()));
      }
      setDigitalPreference(accountNotNull);
      return accountNotNull;
    });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Account> toEntity(AccountCacheablePO accountCacheablePO) {
    return Optional.ofNullable(accountCacheablePO).map(AccountMapper.INSTANCE::toEntity)
      .map(account -> {
        setRolesWithIds(account, getRoleIds(account.getId()));
        setDigitalPreference(account);
        return account;
      });
  }

  private @NotNull List<Long> getRoleIds(Long account) {
    return accountRoleCacheRepository.findById(account)
      .map(AccountRoleCacheablePO::getRoleIds).orElseGet(() -> {
        List<Long> roleIds = accountRoleRepository.findByAccountId(account)
          .stream()
          .map(AccountRolePO::getId).map(AccountRolePOId::getRoleId)
          .collect(Collectors.toList());
        accountRoleCacheRepository.save(
          new AccountRoleCacheablePO(account, roleIds));
        return roleIds;
      });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<AccountPO> toAccountPO(Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toAccountPO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountCacheablePO> toAccountCacheablePO(Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toAccountCacheablePO);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> toEntity(AccountRegisterCmd accountRegisterCmd) {
    return Optional.ofNullable(accountRegisterCmd).map(accountRegisterCmdNotNull -> {
      // 校验账号手机号是否合法
      if (StringUtils.isNoneBlank(accountRegisterCmdNotNull.getPhone(),
        accountRegisterCmdNotNull.getPhoneCountryCode()) && !PhoneUtils.isValidPhoneNumber(
        accountRegisterCmdNotNull.getPhone(),
        accountRegisterCmdNotNull.getPhoneCountryCode())) {
        throw new MuMuException(ResponseCode.INVALID_PHONE_NUMBER);
      }
      Account account = AccountMapper.INSTANCE.toEntity(accountRegisterCmdNotNull);
      // 根据角色code设置账号角色相关信息
      setRolesWithCodes(account, Optional.ofNullable(accountRegisterCmdNotNull.getRoleCodes())
        .orElse(new ArrayList<>()));
      // 设置地址所属的账号ID
      Optional.ofNullable(account.getAddresses())
        .filter(CollectionUtils::isNotEmpty)
        .ifPresent(accountAddresses -> accountAddresses.forEach(
          accountAddress -> accountAddress.setAccountId(account.getId())));
      // 设置头像所属的账号ID
      Optional.ofNullable(account.getAvatar())
        .ifPresent(accountAvatar -> accountAvatar.setAccountId(account.getId()));
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
              accountAddress -> accountAddress.setAccountId(account.getId())));
          String emailAfterUpdated = account.getEmail();
          String usernameAfterUpdated = account.getUsername();
          if (StringUtils.isNoneBlank(account.getPhone(), account.getPhoneCountryCode())
            && !PhoneUtils.isValidPhoneNumber(account.getPhone(),
            account.getPhoneCountryCode())) {
            throw new MuMuException(ResponseCode.INVALID_PHONE_NUMBER);
          }
          // 校验修改后的账号邮箱唯一性
          if (StringUtils.isNotBlank(emailAfterUpdated) && !emailAfterUpdated.equals(
            emailBeforeUpdated
          ) && (accountRepository.existsByEmail(emailAfterUpdated)
            || accountArchivedRepository.existsByEmail(emailAfterUpdated))) {
            throw new MuMuException(ResponseCode.ACCOUNT_EMAIL_ALREADY_EXISTS);
          }
          // 校验修改后的账号名唯一性
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
  public Optional<AccountCurrentLoginDTO> toAccountCurrentLoginDTO(
    Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toAccountCurrentLoginDTO);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountBasicInfoDTO> toAccountBasicInfoDTO(
    Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toAccountBasicInfoDTO);
  }

  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<AccountArchivedPO> toAccountArchivedPO(
    AccountPO accountPO) {
    return Optional.ofNullable(accountPO).map(AccountMapper.INSTANCE::toAccountArchivedPO);
  }

  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<AccountPO> toAccountPO(
    AccountArchivedPO accountArchivedPO) {
    return Optional.ofNullable(accountArchivedPO).map(AccountMapper.INSTANCE::toAccountPO);
  }

  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<AccountAddressDocumentPO> toAccountAddressDocumentPO(
    AccountAddress accountAddress) {
    return Optional.ofNullable(accountAddress)
      .map(AccountMapper.INSTANCE::toAccountAddressDocumentPO);
  }

  @API(status = Status.STABLE, since = "2.11.0")
  public Optional<AccountAvatarDocumentPO> toAccountAvatarDocumentPO(
    AccountAvatar accountAvatar) {
    return Optional.ofNullable(accountAvatar)
      .map(AccountMapper.INSTANCE::toAccountAvatarDocumentPO);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountSystemSettingsDocumentPO> toAccountSystemSettingsDocumentPO(
    AccountSystemSettings accountSystemSettings) {
    return Optional.ofNullable(accountSystemSettings)
      .map(AccountMapper.INSTANCE::toAccountSystemSettingsDocumentPO);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountSystemSettings> toAccountSystemSettings(
    AccountSystemSettingsDocumentPO accountSystemSettingsDocumentPO) {
    return Optional.ofNullable(accountSystemSettingsDocumentPO)
      .map(AccountMapper.INSTANCE::toAccountSystemSettings);
  }

  @API(status = Status.STABLE, since = "2.6.0")
  public Optional<AccountAddress> toAccountAddress(
    AccountAddressDocumentPO accountAddressDocumentPO) {
    return Optional.ofNullable(accountAddressDocumentPO)
      .map(AccountMapper.INSTANCE::toAccountAddress);
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
        return accountSystemSettingsDocumentRepository.findById(
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
  public Optional<AccountSystemSettingsDocumentPO> resetAccountSystemSettingsDocumentPO(
    AccountSystemSettingsDocumentPO accountSystemSettingsDocumentPO) {
    return Optional.ofNullable(accountSystemSettingsDocumentPO)
      .map(systemSettingsDocumentPO -> {
        AccountMapper.INSTANCE.toAccountSystemSettingsDocumentPO(
          new AccountSystemSettingsDocumentPO(systemSettingsDocumentPO.getId(),
            systemSettingsDocumentPO.getAccountId(),
            systemSettingsDocumentPO.getProfile(),
            systemSettingsDocumentPO.getName(),
            systemSettingsDocumentPO.isDefaultSystemSettings(),
            systemSettingsDocumentPO.getVersion()),
          systemSettingsDocumentPO);
        return systemSettingsDocumentPO;
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
          AccountRolePOId.builder().roleId(role.getId()).accountId(account.getId()).build());
        accountRolePO.setAccount(accountRepository.findById(account.getId()).orElse(null));
        accountRolePO.setRole(roleRepository.findById(role.getId()).orElse(null));
        return accountRolePO;
      }).collect(Collectors.toList())).orElse(new ArrayList<>());
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountFindAllDTO> toAccountFindAllDTO(
    Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toAccountFindAllDTO);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AccountFindAllSliceDTO> toAccountFindAllSliceDTO(
    Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toAccountFindAllSliceDTO);
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
            AccountRoleGrpcDTO accountRoleCurrentLoginQueryGrpcDTO = AccountMapper.INSTANCE.toAccountRoleGrpcDTO(
              role);
            return accountRoleCurrentLoginQueryGrpcDTO.toBuilder().addAllPermissions(
              Optional.ofNullable(role.getPermissions()).map(
                accountRolePermissionCurrentLoginQueryDTOS -> accountRolePermissionCurrentLoginQueryDTOS.stream()
                  .map(
                    AccountMapper.INSTANCE::toAccountRolePermissionGrpcDTO)
                  .collect(Collectors.toList())).orElse(new ArrayList<>())).build();
          }).collect(Collectors.toList())).orElse(new ArrayList<>()))
        .addAllAddresses(Optional.ofNullable(accountCurrentLoginDTO.getAddresses())
          .map(
            accountAddressCurrentLoginQueryDTOS -> accountAddressCurrentLoginQueryDTOS.stream()
              .map(AccountMapper.INSTANCE::toAccountAddressGrpcDTO)
              .collect(Collectors.toList())).orElse(new ArrayList<>()))
        .addAllSystemSettings(Optional.ofNullable(accountCurrentLoginDTO.getSystemSettings())
          .map(
            accountSystemSettingsCurrentLoginQueryDTOS -> accountSystemSettingsCurrentLoginQueryDTOS.stream()
              .map(
                AccountMapper.INSTANCE::toAccountSystemSettingsGrpcDTO)
              .collect(Collectors.toList())).orElse(new ArrayList<>())).build());
  }

  @API(status = Status.STABLE, since = "2.6.0")
  public Optional<AccountNearbyDTO> toAccountNearbyDTO(
    Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toAccountNearbyDTO);
  }

  @API(status = Status.STABLE, since = "2.13.0")
  public Optional<AccountUpdatedDataDTO> toAccountUpdatedDataDTO(
    Account account) {
    return Optional.ofNullable(account).map(AccountMapper.INSTANCE::toAccountUpdatedDataDTO);
  }

  @API(status = Status.STABLE, since = "2.6.0")
  public Optional<AccountAddress> toAccountAddress(
    AccountModifyAddressByAddressIdCmd accountModifyAddressByAddressIdCmd) {
    return Optional.ofNullable(accountModifyAddressByAddressIdCmd)
      .flatMap(modifyAddressByAddressIdCmd -> {
        Optional.ofNullable(modifyAddressByAddressIdCmd.getId())
          .orElseThrow(() -> new MuMuException(ResponseCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
        return accountAddressDocumentRepository.findById(
            modifyAddressByAddressIdCmd.getId())
          .flatMap(this::toAccountAddress).flatMap(accountAddress -> {
            AccountMapper.INSTANCE.toAccountAddress(
              modifyAddressByAddressIdCmd,
              accountAddress);
            return Optional.of(accountAddress);
          });
      });
  }
}
