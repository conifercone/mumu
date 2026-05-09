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

package baby.mumu.iam.infra.account.mapper;

import baby.mumu.basis.mappers.BaseMapper;
import baby.mumu.basis.mappers.GeoMapper;
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
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Account infrastructure persistence mapper
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountPersistenceMapper extends BaseMapper, GeoMapper {

    AccountPersistenceMapper INSTANCE = Mappers.getMapper(AccountPersistenceMapper.class);

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
    void toAccountSystemSettingsDocumentPO(
        AccountSystemSettingsDocumentPO accountSystemSettingsDocumentPOSource,
        @MappingTarget AccountSystemSettingsDocumentPO accountSystemSettingsDocumentPOTarget);

    @API(status = Status.STABLE, since = "2.2.0")
    AccountCacheablePO toAccountCacheablePO(Account account);

    @API(status = Status.STABLE, since = "1.0.1")
    AccountPO toAccountPO(Account account);

    @API(status = Status.STABLE, since = "1.0.4")
    AccountArchivedPO toAccountArchivedPO(AccountPO accountPO);

    @API(status = Status.STABLE, since = "1.0.4")
    AccountPO toAccountPO(AccountArchivedPO accountArchivedPO);
}
