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

import baby.mumu.basis.mappers.BaseMapper;
import baby.mumu.basis.mappers.DataTransferObjectMapper;
import baby.mumu.basis.mappers.GeoGrpcMapper;
import baby.mumu.basis.mappers.GeoMapper;
import baby.mumu.basis.mappers.GrpcMapper;
import baby.mumu.iam.client.api.grpc.AccountAddressGrpcDTO;
import baby.mumu.iam.client.api.grpc.AccountCurrentLoginGrpcDTO;
import baby.mumu.iam.client.api.grpc.AccountRoleGrpcDTO;
import baby.mumu.iam.client.api.grpc.AccountRolePermissionGrpcDTO;
import baby.mumu.iam.client.api.grpc.AccountSystemSettingsGrpcDTO;
import baby.mumu.iam.client.cmds.AccountAddAddressCmd;
import baby.mumu.iam.client.cmds.AccountAddSystemSettingsCmd;
import baby.mumu.iam.client.cmds.AccountFindAllCmd;
import baby.mumu.iam.client.cmds.AccountFindAllSliceCmd;
import baby.mumu.iam.client.cmds.AccountModifyAddressByAddressIdCmd;
import baby.mumu.iam.client.cmds.AccountModifySystemSettingsBySettingsIdCmd;
import baby.mumu.iam.client.cmds.AccountRegisterCmd;
import baby.mumu.iam.client.cmds.AccountRegisterCmd.AccountAddressRegisterCmd;
import baby.mumu.iam.client.cmds.AccountUpdateByIdCmd;
import baby.mumu.iam.client.dto.AccountBasicInfoDTO;
import baby.mumu.iam.client.dto.AccountCurrentLoginDTO;
import baby.mumu.iam.client.dto.AccountCurrentLoginDTO.AccountAddressDTO;
import baby.mumu.iam.client.dto.AccountCurrentLoginDTO.AccountPermissionDTO;
import baby.mumu.iam.client.dto.AccountCurrentLoginDTO.AccountRoleDTO;
import baby.mumu.iam.client.dto.AccountCurrentLoginDTO.AccountSystemSettingDTO;
import baby.mumu.iam.client.dto.AccountFindAllDTO;
import baby.mumu.iam.client.dto.AccountFindAllSliceDTO;
import baby.mumu.iam.client.dto.AccountNearbyDTO;
import baby.mumu.iam.client.dto.AccountUpdatedDataDTO;
import baby.mumu.iam.domain.account.Account;
import baby.mumu.iam.domain.account.AccountAddress;
import baby.mumu.iam.domain.account.AccountAvatar;
import baby.mumu.iam.domain.account.AccountSystemSettings;
import baby.mumu.iam.infra.account.gatewayimpl.cache.po.AccountCacheablePO;
import baby.mumu.iam.infra.account.gatewayimpl.database.po.AccountArchivedPO;
import baby.mumu.iam.infra.account.gatewayimpl.database.po.AccountPO;
import baby.mumu.iam.infra.account.gatewayimpl.document.po.AccountAddressDocumentPO;
import baby.mumu.iam.infra.account.gatewayimpl.document.po.AccountAvatarDocumentPO;
import baby.mumu.iam.infra.account.gatewayimpl.document.po.AccountSystemSettingsDocumentPO;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Account mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper extends GrpcMapper, DataTransferObjectMapper, BaseMapper, GeoMapper,
  GeoGrpcMapper {

  AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

  @API(status = Status.STABLE, since = "1.0.1")
  Account toEntity(AccountPO accountPO);

  @API(status = Status.STABLE, since = "2.2.0")
  Account toEntity(AccountCacheablePO accountCacheablePO);

  @API(status = Status.STABLE, since = "2.0.0")
  AccountAddress toAccountAddress(AccountAddressDocumentPO accountAddressDocumentPO);

  @API(status = Status.STABLE, since = "2.10.0")
  AccountAvatar toAccountAvatar(AccountAvatarDocumentPO accountAvatarDocumentPO);

  @API(status = Status.STABLE, since = "2.0.0")
  AccountAddressDocumentPO toAccountAddressDocumentPO(AccountAddress accountAddress);

  @API(status = Status.STABLE, since = "2.11.0")
  AccountAvatarDocumentPO toAccountAvatarDocumentPO(AccountAvatar accountAvatar);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountSystemSettingsDocumentPO toAccountSystemSettingsDocumentPO(
    AccountSystemSettings accountSystemSettings);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountSystemSettings toAccountSystemSettings(
    AccountSystemSettingsDocumentPO accountSystemSettingsDocumentPO);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountSystemSettings toAccountSystemSettings(
    AccountAddSystemSettingsCmd accountAddSystemSettingsCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  void toAccountSystemSettingsDocumentPO(
    AccountSystemSettingsDocumentPO accountSystemSettingsDocumentPOSource,
    @MappingTarget AccountSystemSettingsDocumentPO accountSystemSettingsDocumentPOTarget);

  @API(status = Status.STABLE, since = "2.2.0")
  void toAccountSystemSettings(
    AccountModifySystemSettingsBySettingsIdCmd accountModifySystemSettingsBySettingsIdCmd,
    @MappingTarget AccountSystemSettings accountSystemSettings);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountCacheablePO toAccountCacheablePO(Account account);

  @API(status = Status.STABLE, since = "2.0.0")
  AccountAddress toAccountAddress(AccountAddAddressCmd accountAddAddressCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  AccountPO toAccountPO(Account account);

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

  @API(status = Status.STABLE, since = "1.0.4")
  AccountArchivedPO toAccountArchivedPO(AccountPO accountPO);

  @API(status = Status.STABLE, since = "1.0.4")
  AccountPO toAccountPO(AccountArchivedPO accountArchivedPO);

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
