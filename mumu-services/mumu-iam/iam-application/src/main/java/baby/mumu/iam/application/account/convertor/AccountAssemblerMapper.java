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

import baby.mumu.basis.mappers.DataTransferObjectMapper;
import baby.mumu.basis.mappers.GeoGrpcMapper;
import baby.mumu.basis.mappers.GeoMapper;
import baby.mumu.basis.mappers.GrpcMapper;
import baby.mumu.iam.client.api.grpc.*;
import baby.mumu.iam.client.cmds.*;
import baby.mumu.iam.client.cmds.AccountRegisterCmd.AccountAddressRegisterCmd;
import baby.mumu.iam.client.dto.*;
import baby.mumu.iam.client.dto.AccountCurrentLoginDTO.AccountAddressDTO;
import baby.mumu.iam.client.dto.AccountCurrentLoginDTO.AccountPermissionDTO;
import baby.mumu.iam.client.dto.AccountCurrentLoginDTO.AccountRoleDTO;
import baby.mumu.iam.client.dto.AccountCurrentLoginDTO.AccountSystemSettingDTO;
import baby.mumu.iam.domain.account.Account;
import baby.mumu.iam.domain.account.AccountAddress;
import baby.mumu.iam.domain.account.AccountSystemSettings;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * Account application assembler mapper
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountAssemblerMapper extends GrpcMapper, DataTransferObjectMapper, GeoMapper,
    GeoGrpcMapper {

    AccountAssemblerMapper INSTANCE = Mappers.getMapper(AccountAssemblerMapper.class);

    @API(status = Status.STABLE, since = "2.2.0")
    AccountSystemSettings toAccountSystemSettings(
        AccountAddSystemSettingsCmd accountAddSystemSettingsCmd);

    @API(status = Status.STABLE, since = "2.2.0")
    void toAccountSystemSettings(
        AccountModifySystemSettingsBySettingsIdCmd accountModifySystemSettingsBySettingsIdCmd,
        @MappingTarget AccountSystemSettings accountSystemSettings);

    @API(status = Status.STABLE, since = "2.0.0")
    AccountAddress toAccountAddress(AccountAddAddressCmd accountAddAddressCmd);

    @API(status = Status.STABLE, since = "1.0.1")
    Account toEntity(AccountRegisterCmd accountRegisterCmd);

    @API(status = Status.STABLE, since = "2.1.0")
    AccountAddress toAccountAddress(AccountAddressRegisterCmd accountAddressRegisterCmd);

    @API(status = Status.STABLE, since = "2.6.0")
    void toAccountAddress(
        AccountModifyAddressByAddressIdCmd accountModifyAddressByAddressIdCmd,
        @MappingTarget AccountAddress accountAddress);

    @API(status = Status.STABLE, since = "1.0.1")
    void toEntity(AccountUpdateByIdCmd accountUpdateByIdCmd, @MappingTarget Account account);

    @API(status = Status.STABLE, since = "1.0.1")
    AccountCurrentLoginDTO toAccountCurrentLoginDTO(Account account);

    @API(status = Status.STABLE, since = "2.2.0")
    AccountBasicInfoDTO toAccountBasicInfoDTO(Account account);

    @API(status = Status.STABLE, since = "2.2.0")
    AccountFindAllDTO toAccountFindAllDTO(Account account);

    @API(status = Status.STABLE, since = "2.2.0")
    AccountFindAllSliceDTO toAccountFindAllSliceDTO(Account account);

    @API(status = Status.STABLE, since = "2.2.0")
    Account toEntity(AccountFindAllCmd accountFindAllCmd);

    @API(status = Status.STABLE, since = "2.2.0")
    Account toEntity(AccountFindAllSliceCmd accountFindAllSliceCmd);

    @API(status = Status.STABLE, since = "2.2.0")
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    AccountCurrentLoginGrpcDTO toAccountCurrentLoginGrpcDTO(
        AccountCurrentLoginDTO accountCurrentLoginDTO);

    @API(status = Status.STABLE, since = "2.2.0")
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    AccountAddressGrpcDTO toAccountAddressGrpcDTO(
        AccountAddressDTO accountAddressDTO);

    @API(status = Status.STABLE, since = "2.2.0")
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    AccountRoleGrpcDTO toAccountRoleGrpcDTO(
        AccountRoleDTO accountRoleDTO);

    @API(status = Status.STABLE, since = "2.2.0")
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    AccountRolePermissionGrpcDTO toAccountRolePermissionGrpcDTO(
        AccountPermissionDTO accountPermissionDTO);

    @API(status = Status.STABLE, since = "2.2.0")
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    AccountSystemSettingsGrpcDTO toAccountSystemSettingsGrpcDTO(
        AccountSystemSettingDTO accountSystemSettingDTO);

    @API(status = Status.STABLE, since = "2.6.0")
    AccountNearbyDTO toAccountNearbyDTO(Account account);

    @API(status = Status.STABLE, since = "2.13.0")
    AccountUpdatedDataDTO toAccountUpdatedDataDTO(Account account);
}


