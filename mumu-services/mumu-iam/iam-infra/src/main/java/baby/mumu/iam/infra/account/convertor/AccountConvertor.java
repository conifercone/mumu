/*
 * Copyright (c) 2024-2026, the original author or authors.
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
import baby.mumu.iam.domain.account.Account;
import baby.mumu.iam.domain.account.AccountAddress;
import baby.mumu.iam.domain.account.AccountAvatar;
import baby.mumu.iam.domain.account.AccountSystemSettings;
import baby.mumu.iam.domain.role.Role;
import baby.mumu.iam.infra.account.gatewayimpl.cache.po.AccountCacheablePO;
import baby.mumu.iam.infra.account.gatewayimpl.database.AccountRepository;
import baby.mumu.iam.infra.account.gatewayimpl.database.po.AccountArchivedPO;
import baby.mumu.iam.infra.account.gatewayimpl.database.po.AccountPO;
import baby.mumu.iam.infra.account.gatewayimpl.document.AccountAddressDocumentRepository;
import baby.mumu.iam.infra.account.gatewayimpl.document.AccountAvatarDocumentRepository;
import baby.mumu.iam.infra.account.gatewayimpl.document.AccountSystemSettingsDocumentRepository;
import baby.mumu.iam.infra.account.gatewayimpl.document.po.AccountAddressDocumentPO;
import baby.mumu.iam.infra.account.gatewayimpl.document.po.AccountAvatarDocumentPO;
import baby.mumu.iam.infra.account.gatewayimpl.document.po.AccountSystemSettingsDocumentPO;
import baby.mumu.iam.infra.account.mapper.AccountPersistenceMapper;
import baby.mumu.iam.infra.relations.cache.AccountRoleCacheRepository;
import baby.mumu.iam.infra.relations.cache.AccountRoleCacheablePO;
import baby.mumu.iam.infra.relations.database.*;
import baby.mumu.iam.infra.role.convertor.RoleConvertor;
import baby.mumu.iam.infra.role.gatewayimpl.cache.RoleCacheRepository;
import baby.mumu.iam.infra.role.gatewayimpl.cache.po.RoleCacheablePO;
import baby.mumu.iam.infra.role.gatewayimpl.database.RoleRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 账号信息转换器 (Infrastructure Layer)
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
public class AccountConvertor {

    private final RoleConvertor roleConvertor;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
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
        this.accountAddressDocumentRepository = accountAddressDocumentRepository;
        this.accountRoleRepository = accountRoleRepository;
        this.roleCacheRepository = roleCacheRepository;
        this.accountSystemSettingsDocumentRepository = accountSystemSettingsDocumentRepository;
        this.accountRoleCacheRepository = accountRoleCacheRepository;
        this.rolePathRepository = rolePathRepository;
        this.accountAvatarDocumentRepository = accountAvatarDocumentRepository;
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<Account> toEntity(AccountPO accountPO) {
        return Optional.ofNullable(accountPO).flatMap(accountDataObject -> {
            Account account = AccountPersistenceMapper.INSTANCE.toEntity(accountDataObject);
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

    public void setRolesWithIds(Account account, List<Long> roleIds) {
        Optional.ofNullable(account).ifPresent(accountNotNull -> {
            ArrayList<Role> roles = getRoles(
                Optional.ofNullable(roleIds).map(
                        roleIdsNotNull -> roleIdsNotNull.stream().distinct()
                            .collect(Collectors.toList()))
                    .orElse(new ArrayList<>()));
            initializeRoles(accountNotNull, roles);
        });
    }

    private @NonNull ArrayList<Role> getRoles(List<Long> roleIds) {
        // 查询缓存中存在的数据
        List<RoleCacheablePO> roleCacheablePOS = roleCacheRepository.findAllById(
            roleIds);
        // 缓存中存在的角色ID
        List<Long> cachedCollectionOfRoleIDs = roleCacheablePOS.stream()
            .map(RoleCacheablePO::getId)
            .collect(Collectors.toList());
        // 已缓存的角色
        List<Role> cachedCollectionOfRole = roleConvertor.toEntitiesFromCacheablePO(roleCacheablePOS);
        // 未缓存的角色
        List<Role> uncachedCollectionOfRole = Optional.of(
                CollectionUtils.subtract(roleIds, cachedCollectionOfRoleIDs))
            .filter(CollectionUtils::isNotEmpty).map(
                uncachedCollectionOfRoleId -> roleConvertor.toEntities(roleRepository.findAllById(
                    uncachedCollectionOfRoleId))).orElse(new ArrayList<>());
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

    public void setRolesWithCodes(Account account, List<String> codes) {
        Optional.ofNullable(account).ifPresent(accountNotNull -> {
            ArrayList<Role> roles = getRolesByCodes(
                Optional.ofNullable(codes).map(
                        codeIdsNotNull -> codeIdsNotNull.stream().distinct()
                            .collect(Collectors.toList()))
                    .orElse(new ArrayList<>()));
            initializeRoles(accountNotNull, roles);
        });
    }

    private void initializeRoles(@NonNull Account accountNotNull, ArrayList<Role> roles) {
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

    private @NonNull ArrayList<Role> getRolesByCodes(List<String> codes) {
        // 查询缓存中存在的数据
        List<RoleCacheablePO> roleCacheablePOS = roleCacheRepository.findByCodeIn(
            codes);
        // 缓存中存在的角色编码
        List<String> cachedCollectionOfRoleCodes = roleCacheablePOS.stream()
            .map(RoleCacheablePO::getCode)
            .collect(Collectors.toList());
        // 已缓存的角色
        List<Role> cachedCollectionOfRole = roleConvertor.toEntitiesFromCacheablePO(roleCacheablePOS);
        // 未缓存的角色
        List<Role> uncachedCollectionOfRole = Optional.of(
                CollectionUtils.subtract(codes, cachedCollectionOfRoleCodes))
            .filter(CollectionUtils::isNotEmpty).map(
                uncachedCollectionOfRoleId -> roleConvertor.toEntities(roleRepository.findByCodeIn(
                    uncachedCollectionOfRoleId))).orElse(new ArrayList<>());
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

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<Account> toBasicInfoEntity(AccountPO accountPO) {
        return Optional.ofNullable(accountPO)
            .flatMap(accountDataObject -> getBasicInfoAccount(accountDataObject,
                AccountPersistenceMapper.INSTANCE.toEntity(accountDataObject)));
    }

    @NonNull
    private Optional<Account> getBasicInfoAccount(@NonNull AccountPO accountDataObject,
                                                  Account account) {
        return Optional.ofNullable(account).map(accountNotNull -> {
            List<AccountAddressDocumentPO> accountAddressDocumentPOList = accountAddressDocumentRepository.findByAccountId(
                accountDataObject.getId());
            if (CollectionUtils.isNotEmpty(accountAddressDocumentPOList)) {
                accountNotNull.setAddresses(
                    accountAddressDocumentPOList.stream().map(
                        AccountPersistenceMapper.INSTANCE::toAccountAddress).collect(Collectors.toList()));
            }
            accountAvatarDocumentRepository.findByAccountId(accountDataObject.getId())
                .map(AccountPersistenceMapper.INSTANCE::toAccountAvatar).ifPresent(accountNotNull::setAvatar);
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

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<Account> toEntity(AccountCacheablePO accountCacheablePO) {
        return Optional.ofNullable(accountCacheablePO).map(AccountPersistenceMapper.INSTANCE::toEntity)
            .map(account -> {
                setRolesWithIds(account, getRoleIds(account.getId()));
                setDigitalPreference(account);
                return account;
            });
    }

    private @NonNull List<Long> getRoleIds(Long account) {
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

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<AccountPO> toAccountPO(Account account) {
        return Optional.ofNullable(account).map(AccountPersistenceMapper.INSTANCE::toAccountPO);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<AccountCacheablePO> toAccountCacheablePO(Account account) {
        return Optional.ofNullable(account).map(AccountPersistenceMapper.INSTANCE::toAccountCacheablePO);
    }

    @API(status = Status.STABLE, since = "1.0.4")
    public Optional<AccountArchivedPO> toAccountArchivedPO(
        AccountPO accountPO) {
        return Optional.ofNullable(accountPO).map(AccountPersistenceMapper.INSTANCE::toAccountArchivedPO);
    }

    @API(status = Status.STABLE, since = "1.0.4")
    public Optional<AccountPO> toAccountPO(
        AccountArchivedPO accountArchivedPO) {
        return Optional.ofNullable(accountArchivedPO).map(AccountPersistenceMapper.INSTANCE::toAccountPO);
    }

    @API(status = Status.STABLE, since = "2.0.0")
    public Optional<AccountAddressDocumentPO> toAccountAddressDocumentPO(
        AccountAddress accountAddress) {
        return Optional.ofNullable(accountAddress)
            .map(AccountPersistenceMapper.INSTANCE::toAccountAddressDocumentPO);
    }

    @API(status = Status.STABLE, since = "2.11.0")
    public Optional<AccountAvatarDocumentPO> toAccountAvatarDocumentPO(
        AccountAvatar accountAvatar) {
        return Optional.ofNullable(accountAvatar)
            .map(AccountPersistenceMapper.INSTANCE::toAccountAvatarDocumentPO);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<AccountSystemSettingsDocumentPO> toAccountSystemSettingsDocumentPO(
        AccountSystemSettings accountSystemSettings) {
        return Optional.ofNullable(accountSystemSettings)
            .map(AccountPersistenceMapper.INSTANCE::toAccountSystemSettingsDocumentPO);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<AccountSystemSettings> toAccountSystemSettings(
        AccountSystemSettingsDocumentPO accountSystemSettingsDocumentPO) {
        return Optional.ofNullable(accountSystemSettingsDocumentPO)
            .map(AccountPersistenceMapper.INSTANCE::toAccountSystemSettings);
    }

    @API(status = Status.STABLE, since = "2.6.0")
    public Optional<AccountAddress> toAccountAddress(
        AccountAddressDocumentPO accountAddressDocumentPO) {
        return Optional.ofNullable(accountAddressDocumentPO)
            .map(AccountPersistenceMapper.INSTANCE::toAccountAddress);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<AccountSystemSettingsDocumentPO> resetAccountSystemSettingsDocumentPO(
        AccountSystemSettingsDocumentPO accountSystemSettingsDocumentPO) {
        return Optional.ofNullable(accountSystemSettingsDocumentPO)
            .map(systemSettingsDocumentPO -> {
                AccountPersistenceMapper.INSTANCE.toAccountSystemSettingsDocumentPO(
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

    @API(status = Status.STABLE, since = "2.1.0")
    public List<AccountRolePO> toAccountRolePOS(Account account) {
        return Optional.ofNullable(account).flatMap(accountNotNull -> Optional.ofNullable(
                accountNotNull.getRoles())).filter(CollectionUtils::isNotEmpty)
            .map(roles -> roles.stream().map(role -> {
                AccountRolePO accountRolePO = new AccountRolePO();
                accountRolePO.setId(
                    AccountRolePOId.builder().roleId(role.getId()).accountId(account.getId()).build());
                accountRepository.findById(account.getId()).ifPresent(accountRolePO::setAccount);
                roleRepository.findById(role.getId()).ifPresent(accountRolePO::setRole);
                return accountRolePO;
            }).collect(Collectors.toList())).orElse(new ArrayList<>());
    }
}
