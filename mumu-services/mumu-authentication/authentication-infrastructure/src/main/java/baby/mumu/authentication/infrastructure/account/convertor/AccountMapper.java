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
import baby.mumu.authentication.client.api.grpc.AccountCurrentLoginGrpcCo;
import baby.mumu.authentication.client.api.grpc.AccountRoleAuthorityCurrentLoginQueryGrpcCo;
import baby.mumu.authentication.client.api.grpc.AccountRoleCurrentLoginQueryGrpcCo;
import baby.mumu.authentication.client.api.grpc.AccountSystemSettingsCurrentLoginQueryGrpcCo;
import baby.mumu.authentication.client.dto.AccountAddAddressCmd;
import baby.mumu.authentication.client.dto.AccountAddSystemSettingsCmd;
import baby.mumu.authentication.client.dto.AccountFindAllCmd;
import baby.mumu.authentication.client.dto.AccountFindAllSliceCmd;
import baby.mumu.authentication.client.dto.AccountModifySystemSettingsBySettingsIdCmd;
import baby.mumu.authentication.client.dto.AccountRegisterCmd;
import baby.mumu.authentication.client.dto.AccountRegisterCmd.AccountAddressRegisterCmd;
import baby.mumu.authentication.client.dto.AccountUpdateByIdCmd;
import baby.mumu.authentication.client.dto.AccountUpdateByIdCmd.AccountAddressUpdateByIdCmd;
import baby.mumu.authentication.client.dto.co.AccountBasicInfoCo;
import baby.mumu.authentication.client.dto.co.AccountCurrentLoginCo;
import baby.mumu.authentication.client.dto.co.AccountCurrentLoginCo.AccountAddressCurrentLoginQueryCo;
import baby.mumu.authentication.client.dto.co.AccountCurrentLoginCo.AccountRoleAuthorityCurrentLoginQueryCo;
import baby.mumu.authentication.client.dto.co.AccountCurrentLoginCo.AccountRoleCurrentLoginQueryCo;
import baby.mumu.authentication.client.dto.co.AccountCurrentLoginCo.AccountSystemSettingsCurrentLoginQueryCo;
import baby.mumu.authentication.client.dto.co.AccountFindAllCo;
import baby.mumu.authentication.client.dto.co.AccountFindAllSliceCo;
import baby.mumu.authentication.domain.account.Account;
import baby.mumu.authentication.domain.account.AccountAddress;
import baby.mumu.authentication.domain.account.AccountSystemSettings;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountAddressDo;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountArchivedDo;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountDo;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.mongodb.dataobject.AccountSystemSettingsMongodbDo;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.redis.dataobject.AccountRedisDo;
import baby.mumu.basis.kotlin.tools.CommonUtil;
import baby.mumu.basis.mappers.GrpcMapper;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.AfterMapping;
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
public interface AccountMapper extends GrpcMapper {

  AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

  @API(status = Status.STABLE, since = "1.0.1")
  Account toEntity(AccountDo accountDo);

  @API(status = Status.STABLE, since = "2.2.0")
  Account toEntity(AccountRedisDo accountRedisDo);

  @API(status = Status.STABLE, since = "2.0.0")
  AccountAddress toAccountAddress(AccountAddressDo accountAddressDo);

  @API(status = Status.STABLE, since = "2.0.0")
  AccountAddressDo toAccountAddressDo(AccountAddress accountAddress);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountSystemSettingsMongodbDo toAccountSystemSettingMongodbDo(
    AccountSystemSettings accountSystemSettings);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountSystemSettings toAccountSystemSettings(
    AccountSystemSettingsMongodbDo accountSystemSettingsMongodbDo);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountSystemSettings toAccountSystemSettings(
    AccountAddSystemSettingsCmd accountAddSystemSettingsCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  void toAccountSystemSettingMongodbDo(
    AccountSystemSettingsMongodbDo accountSystemSettingsMongodbDoSource,
    @MappingTarget AccountSystemSettingsMongodbDo accountSystemSettingsMongodbDoTarget);

  @API(status = Status.STABLE, since = "2.2.0")
  void toAccountSystemSettings(
    AccountModifySystemSettingsBySettingsIdCmd accountModifySystemSettingsBySettingsIdCmd,
    @MappingTarget AccountSystemSettings accountSystemSettings);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountRedisDo toAccountRedisDo(Account account);

  @API(status = Status.STABLE, since = "2.0.0")
  AccountAddress toAccountAddress(AccountAddAddressCmd accountAddAddressCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  AccountDo toDataObject(Account account);

  @API(status = Status.STABLE, since = "1.0.1")
  Account toEntity(AccountRegisterCmd accountRegisterCmd);

  @API(status = Status.STABLE, since = "2.1.0")
  AccountAddress toAccountAddress(AccountAddressRegisterCmd accountAddressRegisterCmd);

  @API(status = Status.STABLE, since = "2.1.0")
  AccountAddress toAccountAddress(AccountAddressUpdateByIdCmd accountAddressUpdateByIdCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  void toEntity(AccountUpdateByIdCmd accountUpdateByIdCmd, @MappingTarget Account account);

  @API(status = Status.STABLE, since = "1.0.1")
  AccountCurrentLoginCo toCurrentLoginQueryCo(Account account);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountBasicInfoCo toBasicInfoCo(Account account);

  @API(status = Status.STABLE, since = "1.0.4")
  AccountArchivedDo toArchivedDo(AccountDo accountDo);

  @API(status = Status.STABLE, since = "1.0.4")
  AccountDo toDataObject(AccountArchivedDo accountArchivedDo);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountFindAllCo toFindAllCo(Account account);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountFindAllSliceCo toFindAllSliceCo(Account account);

  @API(status = Status.STABLE, since = "2.2.0")
  Account toEntity(AccountFindAllCmd accountFindAllCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  Account toEntity(AccountFindAllSliceCmd accountFindAllSliceCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountCurrentLoginGrpcCo toAccountCurrentLoginGrpcCo(
    AccountCurrentLoginCo accountCurrentLoginCo);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountAddressCurrentLoginQueryGrpcCo toAccountAddressCurrentLoginQueryGrpcCo(
    AccountAddressCurrentLoginQueryCo accountAddressCurrentLoginQueryCo);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountRoleCurrentLoginQueryGrpcCo toAccountRoleCurrentLoginQueryGrpcCo(
    AccountRoleCurrentLoginQueryCo accountRoleCurrentLoginQueryCo);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountRoleAuthorityCurrentLoginQueryGrpcCo toAccountRoleAuthorityCurrentLoginQueryGrpcCo(
    AccountRoleAuthorityCurrentLoginQueryCo accountRoleAuthorityCurrentLoginQueryCo);

  @API(status = Status.STABLE, since = "2.2.0")
  AccountSystemSettingsCurrentLoginQueryGrpcCo toAccountSystemSettingsCurrentLoginQueryGrpcCo(
    AccountSystemSettingsCurrentLoginQueryCo accountSystemSettingsCurrentLoginQueryCo);

  @AfterMapping
  default void convertToAccountTimezone(
    @MappingTarget AccountCurrentLoginCo accountCurrentLoginCo) {
    CommonUtil.convertToAccountZone(accountCurrentLoginCo);
  }

  @AfterMapping
  default void convertToAccountTimezone(
    @MappingTarget AccountBasicInfoCo accountBasicInfoCo) {
    CommonUtil.convertToAccountZone(accountBasicInfoCo);
  }

  @AfterMapping
  default void convertToAccountTimezone(
    @MappingTarget AccountFindAllCo accountFindAllCo) {
    CommonUtil.convertToAccountZone(accountFindAllCo);
  }

  @AfterMapping
  default void convertToAccountTimezone(
    @MappingTarget AccountFindAllSliceCo accountFindAllSliceCo) {
    CommonUtil.convertToAccountZone(accountFindAllSliceCo);
  }

  default LocalDateTime offsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
    return Optional.ofNullable(offsetDateTime)
      .map(OffsetDateTime::toLocalDateTime).orElse(null);
  }
}
