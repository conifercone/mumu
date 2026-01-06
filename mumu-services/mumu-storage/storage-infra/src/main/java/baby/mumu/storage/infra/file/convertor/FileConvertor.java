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
import baby.mumu.genix.client.api.PrimaryKeyGrpcService;
import baby.mumu.storage.client.dto.FileFindMetaByMetaIdDTO;
import baby.mumu.storage.domain.file.File;
import baby.mumu.storage.domain.file.FileMetadata;
import baby.mumu.storage.domain.zone.StorageZone;
import baby.mumu.storage.infra.file.gatewayimpl.database.po.FileMetadataPO;
import baby.mumu.storage.infra.zone.convertor.StorageZoneConvertor;
import baby.mumu.storage.infra.zone.gatewayimpl.database.StorageZoneRepository;
import baby.mumu.storage.infra.zone.gatewayimpl.database.po.StorageZonePO;
import org.apache.tika.Tika;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

/**
 * 文件信息转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.12.0
 */
@Component
public class FileConvertor {

    private final Tika tika = new Tika();
    private final PrimaryKeyGrpcService primaryKeyGrpcService;
    private final StorageZoneRepository storageZoneRepository;
    private final StorageZoneConvertor storageZoneConvertor;

    public FileConvertor(PrimaryKeyGrpcService primaryKeyGrpcService,
                         StorageZoneRepository storageZoneRepository,
                         StorageZoneConvertor storageZoneConvertor) {
        this.primaryKeyGrpcService = primaryKeyGrpcService;
        this.storageZoneRepository = storageZoneRepository;
        this.storageZoneConvertor = storageZoneConvertor;
    }


    @API(status = Status.STABLE, since = "2.12.0")
    public Optional<FileMetadataPO> toFileMetadataPO(FileMetadata fileMetadata) {
        return Optional.ofNullable(fileMetadata)
            .map(FileMapper.INSTANCE::toFileMetadataPO).map(fileMetadataPO -> {
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
            .map(FileMapper.INSTANCE::toEntity).map(fileMetadata -> {
                StorageZonePO storageZonePO = storageZoneRepository.findById(
                        fileMetadataPO.getStorageZoneId())
                    .orElseThrow(
                        () -> new ApplicationException(ResponseCode.THE_STORAGE_ZONE_DOES_NOT_EXIST));
                StorageZone storageZone = storageZoneConvertor.toEntity(storageZonePO)
                    .orElseThrow(() -> new ApplicationException(ResponseCode.STORAGE_ZONE_INVALID));
                fileMetadata.setStorageZone(storageZone);
                return fileMetadata;
            });
    }

    @API(status = Status.STABLE, since = "2.12.0")
    public Optional<File> toEntity(Long storageZoneId, MultipartFile multipartFile) {
        return Optional.ofNullable(multipartFile).map(_ -> {
            File file = new File();
            try {
                byte[] fileBytes = multipartFile.getBytes();
                file.setContent(new ByteArrayInputStream(fileBytes));
                FileMetadata fileMetadata = new FileMetadata();
                fileMetadata.setId(primaryKeyGrpcService.snowflake());
                StorageZonePO storageZonePO = storageZoneRepository.findById(storageZoneId)
                    .orElseThrow(
                        () -> new ApplicationException(ResponseCode.THE_STORAGE_ZONE_DOES_NOT_EXIST));
                StorageZone storageZone = storageZoneConvertor.toEntity(storageZonePO)
                    .orElseThrow(() -> new ApplicationException(ResponseCode.STORAGE_ZONE_INVALID));
                fileMetadata.setStorageZone(storageZone);
                fileMetadata.setSize(multipartFile.getSize());
                fileMetadata.setContentType(
                    tika.detect(new ByteArrayInputStream(fileBytes), multipartFile.getOriginalFilename()));
                fileMetadata.setOriginalFilename(multipartFile.getOriginalFilename());
                fileMetadata.setStoredFilename(multipartFile.getOriginalFilename());
                fileMetadata.setStoragePath(fileMetadata.getId() + "/" + fileMetadata.getStoredFilename());
                file.setMetadata(fileMetadata);
            } catch (IOException e) {
                throw new ApplicationException(ResponseCode.INPUT_STREAM_CONVERSION_FAILED);
            }
            return file;
        });
    }

    @API(status = Status.STABLE, since = "2.13.0")
    public Optional<FileFindMetaByMetaIdDTO> toFileFindMetaByMetaIdDTO(
        FileMetadata fileMetadata) {
        return Optional.ofNullable(fileMetadata)
            .map(FileMapper.INSTANCE::toFileFindMetaByMetaIdDTO);
    }
}
