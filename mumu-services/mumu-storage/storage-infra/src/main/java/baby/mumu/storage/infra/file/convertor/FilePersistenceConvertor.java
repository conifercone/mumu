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

package baby.mumu.storage.infra.file.convertor;

import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.storage.domain.file.FileMetadata;
import baby.mumu.storage.domain.zone.StorageZone;
import baby.mumu.storage.infra.file.gatewayimpl.database.po.FileMetadataPO;
import baby.mumu.storage.infra.file.mapper.FilePersistenceMapper;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 文件持久化转换器 (Infrastructure Layer)
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.12.0
 */
@Component
public class FilePersistenceConvertor {

    @API(status = Status.STABLE, since = "2.12.0")
    public Optional<FileMetadataPO> toFileMetadataPO(FileMetadata fileMetadata) {
        return Optional.ofNullable(fileMetadata)
            .map(FilePersistenceMapper.INSTANCE::toFileMetadataPO).map(fileMetadataPO -> {
                Long storageZoneId = Optional.ofNullable(fileMetadata.getStorageZone())
                    .map(StorageZone::getId)
                    .orElseThrow(
                        () -> new ApplicationException(ResponseCode.THE_STORAGE_ZONE_DOES_NOT_EXIST));
                fileMetadataPO.setStorageZoneId(storageZoneId);
                return fileMetadataPO;
            });
    }

    @API(status = Status.STABLE, since = "2.12.0")
    public Optional<FileMetadata> toEntity(FileMetadataPO fileMetadataPO) {
        return Optional.ofNullable(fileMetadataPO)
            .map(FilePersistenceMapper.INSTANCE::toEntity);
    }
}
