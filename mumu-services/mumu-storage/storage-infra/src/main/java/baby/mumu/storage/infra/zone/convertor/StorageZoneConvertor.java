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

package baby.mumu.storage.infra.zone.convertor;

import baby.mumu.storage.client.cmds.StorageZoneAddCmd;
import baby.mumu.storage.domain.zone.StorageZone;
import baby.mumu.storage.infra.zone.gatewayimpl.database.po.StorageZonePO;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 存储区域转换类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.13.0
 */
@Component
public class StorageZoneConvertor {

    @API(status = Status.STABLE, since = "2.13.0")
    public Optional<StorageZone> toEntity(
        StorageZonePO storageZonePO) {
        return Optional.ofNullable(storageZonePO)
            .map(StorageZoneMapper.INSTANCE::toEntity);
    }

    @API(status = Status.STABLE, since = "2.13.0")
    public Optional<StorageZonePO> toStorageZonePO(
        StorageZone storageZone) {
        return Optional.ofNullable(storageZone)
            .map(StorageZoneMapper.INSTANCE::toStorageZonePO);
    }

    @API(status = Status.STABLE, since = "2.13.0")
    public Optional<StorageZone> toEntity(
        StorageZoneAddCmd storageZoneAddCmd) {
        return Optional.ofNullable(storageZoneAddCmd)
            .map(StorageZoneMapper.INSTANCE::toEntity);
    }
}
