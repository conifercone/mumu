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

package baby.mumu.iam.application.account.convertor;

import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.kotlin.tools.PhoneUtils;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.iam.client.api.grpc.AccountCurrentLoginGrpcDTO;
import baby.mumu.iam.client.api.grpc.AccountRoleGrpcDTO;
import baby.mumu.iam.client.cmds.*;
import baby.mumu.iam.client.dto.*;
import baby.mumu.iam.domain.account.Account;
import baby.mumu.iam.domain.account.AccountAddress;
import baby.mumu.iam.domain.account.AccountSystemSettings;
import baby.mumu.iam.domain.account.gateway.AccountGateway;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 账号信息组装器 (Application Layer)
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
public class AccountAssemblerConvertor {

    private final AccountGateway accountGateway;

    @Autowired
    public AccountAssemblerConvertor(AccountGateway accountGateway) {
        this.accountGateway = accountGateway;
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<Account> toEntity(AccountRegisterCmd accountRegisterCmd) {
        return Optional.ofNullable(accountRegisterCmd).map(accountRegisterCmdNotNull -> {
            // 校验账号手机号是否合法
            if (StringUtils.isNoneBlank(accountRegisterCmdNotNull.getPhone(),
                accountRegisterCmdNotNull.getPhoneCountryCode()) && !PhoneUtils.isValidPhoneNumber(
                accountRegisterCmdNotNull.getPhone(),
                accountRegisterCmdNotNull.getPhoneCountryCode())) {
                throw new ApplicationException(ResponseCode.INVALID_PHONE_NUMBER);
            }
            Account account = AccountAssemblerMapper.INSTANCE.toEntity(accountRegisterCmdNotNull);
            // 根据角色code设置账号角色相关信息
            Optional.ofNullable(accountRegisterCmdNotNull.getRoleCodes())
                .filter(CollectionUtils::isNotEmpty)
                .ifPresent(roleCodes -> accountGateway.setRolesWithCodes(account, roleCodes));
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
                .orElseThrow(() -> new ApplicationException(ResponseCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
            return accountGateway.findById(accountUpdateByIdCmdNotNull.getId())
                .flatMap(account -> {
                    String emailBeforeUpdated = account.getEmail();
                    String usernameBeforeUpdated = account.getUsername();
                    AccountAssemblerMapper.INSTANCE.toEntity(accountUpdateByIdCmdNotNull, account);
                    Optional.ofNullable(account.getAddresses()).filter(CollectionUtils::isNotEmpty)
                        .ifPresent(accountAddresses -> accountAddresses.forEach(
                            accountAddress -> accountAddress.setAccountId(account.getId())));
                    String emailAfterUpdated = account.getEmail();
                    String usernameAfterUpdated = account.getUsername();
                    if (StringUtils.isNoneBlank(account.getPhone(), account.getPhoneCountryCode())
                        && !PhoneUtils.isValidPhoneNumber(account.getPhone(),
                        account.getPhoneCountryCode())) {
                        throw new ApplicationException(ResponseCode.INVALID_PHONE_NUMBER);
                    }
                    // 校验修改后的账号邮箱唯一性
                    if (StringUtils.isNotBlank(emailAfterUpdated) && !emailAfterUpdated.equals(
                        emailBeforeUpdated
                    ) && accountGateway.existsByEmail(emailAfterUpdated)) {
                        throw new ApplicationException(ResponseCode.ACCOUNT_EMAIL_ALREADY_EXISTS);
                    }
                    // 校验修改后的账号名唯一性
                    if (StringUtils.isNotBlank(usernameAfterUpdated) && !usernameAfterUpdated.equals(
                        usernameBeforeUpdated
                    ) && accountGateway.existsByUsername(usernameAfterUpdated)) {
                        throw new ApplicationException(ResponseCode.ACCOUNT_NAME_ALREADY_EXISTS);
                    }
                    return Optional.of(account);
                });
        });
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<Account> toEntity(AccountUpdateRoleCmd accountUpdateRoleCmd) {
        return Optional.ofNullable(accountUpdateRoleCmd).flatMap(accountUpdateRoleCmdNotNull -> {
            Optional.ofNullable(accountUpdateRoleCmdNotNull.getId())
                .orElseThrow(() -> new ApplicationException(ResponseCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
            return accountGateway.findById(accountUpdateRoleCmdNotNull.getId()).map(account -> {
                Optional.ofNullable(accountUpdateRoleCmdNotNull.getRoleCodes())
                    .ifPresent(roleCodes -> accountGateway.setRolesWithCodes(account, roleCodes));
                return account;
            });
        });
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<AccountCurrentLoginDTO> toAccountCurrentLoginDTO(
        Account account) {
        return Optional.ofNullable(account).map(AccountAssemblerMapper.INSTANCE::toAccountCurrentLoginDTO);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<AccountBasicInfoDTO> toAccountBasicInfoDTO(
        Account account) {
        return Optional.ofNullable(account).map(AccountAssemblerMapper.INSTANCE::toAccountBasicInfoDTO);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<AccountSystemSettings> toAccountSystemSettings(
        AccountAddSystemSettingsCmd accountAddSystemSettingsCmd) {
        return Optional.ofNullable(accountAddSystemSettingsCmd)
            .map(AccountAssemblerMapper.INSTANCE::toAccountSystemSettings);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<AccountSystemSettings> toAccountSystemSettings(
        AccountModifySystemSettingsBySettingsIdCmd accountModifySystemSettingsBySettingsIdCmd) {
        return Optional.ofNullable(accountModifySystemSettingsBySettingsIdCmd)
            .flatMap(accountModifySystemSettingsBySettingsIdCmdNotNull -> {
                Optional.ofNullable(accountModifySystemSettingsBySettingsIdCmdNotNull.getId())
                    .orElseThrow(() -> new ApplicationException(ResponseCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
                return accountGateway.findSystemSettingsById(
                        accountModifySystemSettingsBySettingsIdCmdNotNull.getId())
                    .map(accountSystemSettings -> {
                        AccountAssemblerMapper.INSTANCE.toAccountSystemSettings(
                            accountModifySystemSettingsBySettingsIdCmdNotNull,
                            accountSystemSettings);
                        return accountSystemSettings;
                    });
            });
    }


    @API(status = Status.STABLE, since = "2.0.0")
    public Optional<AccountAddress> toEntity(
        AccountAddAddressCmd accountAddAddressCmd) {
        return Optional.ofNullable(accountAddAddressCmd).map(
            AccountAssemblerMapper.INSTANCE::toAccountAddress);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<AccountFindAllDTO> toAccountFindAllDTO(
        Account account) {
        return Optional.ofNullable(account).map(AccountAssemblerMapper.INSTANCE::toAccountFindAllDTO);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<AccountFindAllSliceDTO> toAccountFindAllSliceDTO(
        Account account) {
        return Optional.ofNullable(account).map(AccountAssemblerMapper.INSTANCE::toAccountFindAllSliceDTO);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<Account> toEntity(
        AccountFindAllCmd accountFindAllCmd) {
        return Optional.ofNullable(accountFindAllCmd).map(AccountAssemblerMapper.INSTANCE::toEntity)
            .map(account -> {
                Optional.ofNullable(accountFindAllCmd.getRoleIds())
                    .ifPresent(roleIds -> accountGateway.setRolesWithIds(account, roleIds));
                return account;
            });
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<Account> toEntity(
        AccountFindAllSliceCmd accountFindAllSliceCmd) {
        return Optional.ofNullable(accountFindAllSliceCmd).map(AccountAssemblerMapper.INSTANCE::toEntity)
            .map(account -> {
                Optional.ofNullable(accountFindAllSliceCmd.getRoleIds())
                    .ifPresent(roleIds -> accountGateway.setRolesWithIds(account, roleIds));
                return account;
            });
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<AccountCurrentLoginGrpcDTO> toAccountCurrentLoginGrpcDTO(
        AccountCurrentLoginDTO accountCurrentLoginDTO) {
        return Optional.ofNullable(accountCurrentLoginDTO)
            .map(AccountAssemblerMapper.INSTANCE::toAccountCurrentLoginGrpcDTO)
            .map(accountCurrentLoginGrpcDTO -> accountCurrentLoginGrpcDTO.toBuilder()
                .addAllRoles(Optional.ofNullable(accountCurrentLoginDTO.getRoles())
                    .map(roles -> roles.stream().map(role -> {
                        AccountRoleGrpcDTO accountRoleCurrentLoginQueryGrpcDTO =
                            AccountAssemblerMapper.INSTANCE.toAccountRoleGrpcDTO(
                                role);
                        return accountRoleCurrentLoginQueryGrpcDTO.toBuilder().addAllPermissions(
                            Optional.ofNullable(role.getPermissions()).map(
                                accountRolePermissionCurrentLoginQueryDTOS -> accountRolePermissionCurrentLoginQueryDTOS.stream()
                                    .map(
                                        AccountAssemblerMapper.INSTANCE::toAccountRolePermissionGrpcDTO)
                                    .collect(Collectors.toList())).orElse(new ArrayList<>())).build();
                    }).collect(Collectors.toList())).orElse(new ArrayList<>()))
                .addAllAddresses(Optional.ofNullable(accountCurrentLoginDTO.getAddresses())
                    .map(
                        accountAddressCurrentLoginQueryDTOS -> accountAddressCurrentLoginQueryDTOS.stream()
                            .map(AccountAssemblerMapper.INSTANCE::toAccountAddressGrpcDTO)
                            .collect(Collectors.toList())).orElse(new ArrayList<>()))
                .addAllSystemSettings(Optional.ofNullable(accountCurrentLoginDTO.getSystemSettings())
                    .map(
                        accountSystemSettingsCurrentLoginQueryDTOS -> accountSystemSettingsCurrentLoginQueryDTOS.stream()
                            .map(
                                AccountAssemblerMapper.INSTANCE::toAccountSystemSettingsGrpcDTO)
                            .collect(Collectors.toList())).orElse(new ArrayList<>())).build());
    }

    @API(status = Status.STABLE, since = "2.6.0")
    public Optional<AccountNearbyDTO> toAccountNearbyDTO(
        Account account) {
        return Optional.ofNullable(account).map(AccountAssemblerMapper.INSTANCE::toAccountNearbyDTO);
    }

    @API(status = Status.STABLE, since = "2.13.0")
    public Optional<AccountUpdatedDataDTO> toAccountUpdatedDataDTO(
        Account account) {
        return Optional.ofNullable(account).map(AccountAssemblerMapper.INSTANCE::toAccountUpdatedDataDTO);
    }

    @API(status = Status.STABLE, since = "2.6.0")
    public Optional<AccountAddress> toAccountAddress(
        AccountModifyAddressByAddressIdCmd accountModifyAddressByAddressIdCmd) {
        return Optional.ofNullable(accountModifyAddressByAddressIdCmd)
            .flatMap(modifyAddressByAddressIdCmd -> {
                Optional.ofNullable(modifyAddressByAddressIdCmd.getId())
                    .orElseThrow(() -> new ApplicationException(ResponseCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
                return accountGateway.findAddressById(
                        modifyAddressByAddressIdCmd.getId())
                    .map(accountAddress -> {
                        AccountAssemblerMapper.INSTANCE.toAccountAddress(
                            modifyAddressByAddressIdCmd,
                            accountAddress);
                        return accountAddress;
                    });
            });
    }

    @API(status = Status.STABLE, since = "1.0.1")
    public void toEntity(AccountUpdateByIdCmd accountUpdateByIdCmd, @MappingTarget Account account) {
        Optional.ofNullable(accountUpdateByIdCmd).ifPresent(cmd -> AccountAssemblerMapper.INSTANCE.toEntity(cmd,
            account));
    }
}


