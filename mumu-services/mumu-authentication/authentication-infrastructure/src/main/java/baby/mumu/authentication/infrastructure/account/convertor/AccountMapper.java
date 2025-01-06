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

import baby.mumu.authentication.client.api.grpc.AccountAddressCurrentLoginQueryGrpcDTO;
import baby.mumu.authentication.client.api.grpc.AccountCurrentLoginGrpcDTO;
import baby.mumu.authentication.client.api.grpc.AccountRoleCurrentLoginQueryGrpcDTO;
import baby.mumu.authentication.client.api.grpc.AccountRolePermissionCurrentLoginQueryGrpcDTO;
import baby.mumu.authentication.client.api.grpc.AccountSystemSettingsCurrentLoginQueryGrpcDTO;
import baby.mumu.authentication.client.cmds.AccountAddAddressCmd;
import baby.mumu.authentication.client.cmds.AccountAddSystemSettingsCmd;
import baby.mumu.authentication.client.cmds.AccountFindAllCmd;
import baby.mumu.authentication.client.cmds.AccountFindAllSliceCmd;
import baby.mumu.authentication.client.cmds.AccountModifySystemSettingsBySettingsIdCmd;
import baby.mumu.authentication.client.cmds.AccountRegisterCmd;
import baby.mumu.authentication.client.cmds.AccountRegisterCmd.AccountAddressRegisterCmd;
import baby.mumu.authentication.client.cmds.AccountUpdateByIdCmd;
import baby.mumu.authentication.client.cmds.AccountUpdateByIdCmd.AccountAddressUpdateByIdCmd;
import baby.mumu.authentication.client.dto.AccountBasicInfoDTO;
import baby.mumu.authentication.client.dto.AccountCurrentLoginDTO;
import baby.mumu.authentication.client.dto.AccountCurrentLoginDTO.AccountAddressCurrentLoginQueryDTO;
import baby.mumu.authentication.client.dto.AccountCurrentLoginDTO.AccountRoleCurrentLoginQueryDTO;
import baby.mumu.authentication.client.dto.AccountCurrentLoginDTO.AccountRolePermissionCurrentLoginQueryDTO;
import baby.mumu.authentication.client.dto.AccountCurrentLoginDTO.AccountSystemSettingsCurrentLoginQueryDTO;
import baby.mumu.authentication.client.dto.AccountFindAllDTO;
import baby.mumu.authentication.client.dto.AccountFindAllSliceDTO;
import baby.mumu.authentication.client.dto.AccountNearbyDTO;
import baby.mumu.authentication.domain.account.Account;
import baby.mumu.authentication.domain.account.AccountAddress;
import baby.mumu.authentication.domain.account.AccountSystemSettings;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.po.AccountArchivedPO;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.po.AccountPO;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.mongodb.po.AccountAddressMongodbPO;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.mongodb.po.AccountSystemSettingsMongodbPO;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.redis.po.AccountRedisPO;
import baby.mumu.basis.mappers.BaseMapper;
import baby.mumu.basis.mappers.DataTransferObjectMapper;
import baby.mumu.basis.mappers.GrpcMapper;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Account mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper extends GrpcMapper, DataTransferObjectMapper, BaseMapper {

  AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

  @API(status = Status.STABLE, since = "1.0.1")
  Account toEntity(AccountPO accountPO);

  @API(status = Status.STABLE, since = "2.2.0")
  Account toEntity(AccountRedisPO accountRedisPO);

  @API(status = Status.STABLE, since = "2.0.0")
  AccountAddress toAccountAddress(AccountAddressMongodbPO accountAddressMongodbPO);

  @API(status = Status.STABLE, since = "2.0.0")
  AccountAddressMongodbPO toAccountAddressPO(AccountAddress accountAddress);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountSystemSettingsMongodbPO toAccountSystemSettingMongodbPO(
    AccountSystemSettings accountSystemSettings);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountSystemSettings toAccountSystemSettings(
    AccountSystemSettingsMongodbPO accountSystemSettingsMongodbPO);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountSystemSettings toAccountSystemSettings(
    AccountAddSystemSettingsCmd accountAddSystemSettingsCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  void toAccountSystemSettingMongodbPO(
    AccountSystemSettingsMongodbPO accountSystemSettingsMongodbPOSource,
    @MappingTarget AccountSystemSettingsMongodbPO accountSystemSettingsMongodbPOTarget);

  @API(status = Status.STABLE, since = "2.2.0")
  void toAccountSystemSettings(
    AccountModifySystemSettingsBySettingsIdCmd accountModifySystemSettingsBySettingsIdCmd,
    @MappingTarget AccountSystemSettings accountSystemSettings);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountRedisPO toAccountRedisPO(Account account);

  @API(status = Status.STABLE, since = "2.0.0")
  AccountAddress toAccountAddress(AccountAddAddressCmd accountAddAddressCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  AccountPO toPO(Account account);

  @API(status = Status.STABLE, since = "1.0.1")
  Account toEntity(AccountRegisterCmd accountRegisterCmd);

  @API(status = Status.STABLE, since = "2.1.0")
  AccountAddress toAccountAddress(AccountAddressRegisterCmd accountAddressRegisterCmd);

  @API(status = Status.STABLE, since = "2.1.0")
  AccountAddress toAccountAddress(AccountAddressUpdateByIdCmd accountAddressUpdateByIdCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  void toEntity(AccountUpdateByIdCmd accountUpdateByIdCmd, @MappingTarget Account account);

  @API(status = Status.STABLE, since = "1.0.1")
  AccountCurrentLoginDTO toCurrentLoginQueryDTO(Account account);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountBasicInfoDTO toBasicInfoDTO(Account account);

  @API(status = Status.STABLE, since = "1.0.4")
  AccountArchivedPO toArchivedPO(AccountPO accountPO);

  @API(status = Status.STABLE, since = "1.0.4")
  AccountPO toPO(AccountArchivedPO accountArchivedPO);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountFindAllDTO toFindAllDTO(Account account);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountFindAllSliceDTO toFindAllSliceDTO(Account account);

  @API(status = Status.STABLE, since = "2.2.0")
  Account toEntity(AccountFindAllCmd accountFindAllCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  Account toEntity(AccountFindAllSliceCmd accountFindAllSliceCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountCurrentLoginGrpcDTO toAccountCurrentLoginGrpcDTO(
    AccountCurrentLoginDTO accountCurrentLoginDTO);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountAddressCurrentLoginQueryGrpcDTO toAccountAddressCurrentLoginQueryGrpcDTO(
    AccountAddressCurrentLoginQueryDTO accountAddressCurrentLoginQueryDTO);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountRoleCurrentLoginQueryGrpcDTO toAccountRoleCurrentLoginQueryGrpcDTO(
    AccountRoleCurrentLoginQueryDTO accountRoleCurrentLoginQueryDTO);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountRolePermissionCurrentLoginQueryGrpcDTO toAccountRolePermissionCurrentLoginQueryGrpcDTO(
    AccountRolePermissionCurrentLoginQueryDTO accountRolePermissionCurrentLoginQueryDTO);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountSystemSettingsCurrentLoginQueryGrpcDTO toAccountSystemSettingsCurrentLoginQueryGrpcDTO(
    AccountSystemSettingsCurrentLoginQueryDTO accountSystemSettingsCurrentLoginQueryDTO);

  @API(status = Status.STABLE, since = "2.6.0")
  AccountNearbyDTO toAccountNearbyDTO(Account account);
}
