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

import baby.mumu.basis.mappers.BaseMapper;
import baby.mumu.basis.mappers.DataTransferObjectMapper;
import baby.mumu.basis.mappers.GrpcMapper;
import baby.mumu.storage.client.dto.FileFindMetaByMetaIdDTO;
import baby.mumu.storage.domain.file.FileMetadata;
import baby.mumu.storage.infra.file.gatewayimpl.database.po.FileMetadataPO;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * File mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.12.0
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileMapper extends GrpcMapper, DataTransferObjectMapper, BaseMapper {

    FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

    @API(status = Status.STABLE, since = "2.12.0")
    FileMetadataPO toFileMetadataPO(FileMetadata fileMetadata);

    @API(status = Status.STABLE, since = "2.12.0")
    FileMetadata toEntity(FileMetadataPO fileMetadataPO);

    @API(status = Status.STABLE, since = "2.13.0")
    FileFindMetaByMetaIdDTO toFileFindMetaByMetaIdDTO(
        FileMetadata fileMetadata);
}
