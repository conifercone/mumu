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
import baby.mumu.authentication.client.dto.co.AccountCurrentLoginQueryCo;
import baby.mumu.authentication.client.dto.co.AccountRegisterCo;
import baby.mumu.authentication.client.dto.co.AccountRegisterCo.AccountAddressRegisterCo;
import baby.mumu.authentication.client.dto.co.AccountUpdateByIdCo;
import baby.mumu.authentication.client.dto.co.AccountUpdateByIdCo.AccountAddressUpdateByIdCo;
import baby.mumu.authentication.domain.account.Account;
import baby.mumu.authentication.domain.account.Account4Desc;
import baby.mumu.authentication.domain.account.AccountAddress;
import baby.mumu.authentication.domain.account.AccountAddress4Desc;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountAddressDo;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountArchivedDo;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountArchivedDo4Desc;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountDo;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountDo4Desc;
import baby.mumu.basis.kotlin.tools.CommonUtil;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * Account mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountMapper {

  AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

  @Mappings(value = {
      @Mapping(target = Account4Desc.role, ignore = true),
      @Mapping(target = Account4Desc.authorities, ignore = true),
      @Mapping(target = Account4Desc.addresses, ignore = true)
  })
  @API(status = Status.STABLE, since = "1.0.1")
  void toEntity(AccountDo accountDo, @MappingTarget Account account);

  @API(status = Status.STABLE, since = "2.0.0")
  AccountAddress toAccountAddress(AccountAddressDo accountAddressDo);

  @API(status = Status.STABLE, since = "2.0.0")
  AccountAddressDo toDataObject(AccountAddress accountAddress);

  @Mappings(value = {
      @Mapping(target = AccountAddress4Desc.userId, ignore = true)
  })
  @API(status = Status.STABLE, since = "2.0.0")
  AccountAddress toAccountAddress(AccountAddAddressCo accountAddAddressCo);

  @Mappings(value = {
      @Mapping(target = AccountDo4Desc.role, ignore = true),
  })
  @API(status = Status.STABLE, since = "1.0.1")
  AccountDo toDataObject(Account account);

  @Mappings(value = {
      @Mapping(target = Account4Desc.role, ignore = true),
      @Mapping(target = Account4Desc.authorities, ignore = true),
  })
  @API(status = Status.STABLE, since = "1.0.1")
  void toEntity(AccountRegisterCo accountRegisterCo, @MappingTarget Account account);

  @Mappings(value = {
      @Mapping(target = AccountAddress4Desc.userId, ignore = true)
  })
  @API(status = Status.STABLE, since = "2.1.0")
  AccountAddress toAccountAddress(AccountAddressRegisterCo accountAddressRegisterCo);

  @Mappings(value = {
      @Mapping(target = AccountAddress4Desc.userId, ignore = true)
  })
  @API(status = Status.STABLE, since = "2.1.0")
  AccountAddress toAccountAddress(AccountAddressUpdateByIdCo accountAddressUpdateByIdCo);

  @Mappings(value = {
      @Mapping(target = Account4Desc.role, ignore = true),
      @Mapping(target = Account4Desc.authorities, ignore = true),
  })
  @API(status = Status.STABLE, since = "1.0.1")
  void toEntity(AccountUpdateByIdCo accountUpdateByIdCo, @MappingTarget Account account);

  @API(status = Status.STABLE, since = "1.0.1")
  AccountCurrentLoginQueryCo toCurrentLoginQueryCo(Account account);

  @API(status = Status.STABLE, since = "1.0.4")
  @Mappings(value = {
      @Mapping(target = AccountArchivedDo4Desc.roleId, ignore = true)
  })
  AccountArchivedDo toArchivedDo(AccountDo accountDo);

  @API(status = Status.STABLE, since = "1.0.4")
  @Mappings(value = {
      @Mapping(target = AccountDo4Desc.role, ignore = true)
  })
  AccountDo toDataObject(AccountArchivedDo accountArchivedDo);

  @AfterMapping
  default void convertToAccountTimezone(
      @MappingTarget AccountCurrentLoginQueryCo accountCurrentLoginQueryCo) {
    CommonUtil.convertToAccountZone(accountCurrentLoginQueryCo);
  }
}
