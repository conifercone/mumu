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

import baby.mumu.authentication.client.api.grpc.AccountAddressCurrentLoginQueryGrpcCo;
import baby.mumu.authentication.client.api.grpc.AccountCurrentLoginGrpcDTO;
import baby.mumu.authentication.client.api.grpc.AccountRoleCurrentLoginQueryGrpcCo;
import baby.mumu.authentication.client.api.grpc.AccountRolePermissionCurrentLoginQueryGrpcCo;
import baby.mumu.authentication.client.api.grpc.AccountSystemSettingsCurrentLoginQueryGrpcCo;
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
import baby.mumu.authentication.client.dto.AccountCurrentLoginDTO.AccountAddressCurrentLoginQueryCo;
import baby.mumu.authentication.client.dto.AccountCurrentLoginDTO.AccountRoleCurrentLoginQueryCo;
import baby.mumu.authentication.client.dto.AccountCurrentLoginDTO.AccountRolePermissionCurrentLoginQueryCo;
import baby.mumu.authentication.client.dto.AccountCurrentLoginDTO.AccountSystemSettingsCurrentLoginQueryCo;
import baby.mumu.authentication.client.dto.AccountFindAllDTO;
import baby.mumu.authentication.client.dto.AccountFindAllSliceDTO;
import baby.mumu.authentication.domain.account.Account;
import baby.mumu.authentication.domain.account.AccountAddress;
import baby.mumu.authentication.domain.account.AccountSystemSettings;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountAddressDO;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountArchivedDO;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountDO;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.mongodb.dataobject.AccountSystemSettingsMongodbDO;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.redis.dataobject.AccountRedisDO;
import baby.mumu.basis.mappers.BaseMapper;
import baby.mumu.basis.mappers.ClientObjectMapper;
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
public interface AccountMapper extends GrpcMapper, ClientObjectMapper, BaseMapper {

  AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

  @API(status = Status.STABLE, since = "1.0.1")
  Account toEntity(AccountDO accountDo);

  @API(status = Status.STABLE, since = "2.2.0")
  Account toEntity(AccountRedisDO accountRedisDo);

  @API(status = Status.STABLE, since = "2.0.0")
  AccountAddress toAccountAddress(AccountAddressDO accountAddressDo);

  @API(status = Status.STABLE, since = "2.0.0")
  AccountAddressDO toAccountAddressDO(AccountAddress accountAddress);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountSystemSettingsMongodbDO toAccountSystemSettingMongodbDO(
    AccountSystemSettings accountSystemSettings);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountSystemSettings toAccountSystemSettings(
    AccountSystemSettingsMongodbDO accountSystemSettingsMongodbDo);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountSystemSettings toAccountSystemSettings(
    AccountAddSystemSettingsCmd accountAddSystemSettingsCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  void toAccountSystemSettingMongodbDO(
    AccountSystemSettingsMongodbDO accountSystemSettingsMongodbDOSource,
    @MappingTarget AccountSystemSettingsMongodbDO accountSystemSettingsMongodbDOTarget);

  @API(status = Status.STABLE, since = "2.2.0")
  void toAccountSystemSettings(
    AccountModifySystemSettingsBySettingsIdCmd accountModifySystemSettingsBySettingsIdCmd,
    @MappingTarget AccountSystemSettings accountSystemSettings);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountRedisDO toAccountRedisDO(Account account);

  @API(status = Status.STABLE, since = "2.0.0")
  AccountAddress toAccountAddress(AccountAddAddressCmd accountAddAddressCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  AccountDO toDataObject(Account account);

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
  AccountArchivedDO toArchivedDO(AccountDO accountDo);

  @API(status = Status.STABLE, since = "1.0.4")
  AccountDO toDataObject(AccountArchivedDO accountArchivedDo);

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
  AccountAddressCurrentLoginQueryGrpcCo toAccountAddressCurrentLoginQueryGrpcDTO(
    AccountAddressCurrentLoginQueryCo accountAddressCurrentLoginQueryCo);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountRoleCurrentLoginQueryGrpcCo toAccountRoleCurrentLoginQueryGrpcDTO(
    AccountRoleCurrentLoginQueryCo accountRoleCurrentLoginQueryCo);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountRolePermissionCurrentLoginQueryGrpcCo toAccountRolePermissionCurrentLoginQueryGrpcDTO(
    AccountRolePermissionCurrentLoginQueryCo accountRolePermissionCurrentLoginQueryCo);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountSystemSettingsCurrentLoginQueryGrpcCo toAccountSystemSettingsCurrentLoginQueryGrpcDTO(
    AccountSystemSettingsCurrentLoginQueryCo accountSystemSettingsCurrentLoginQueryCo);


}
