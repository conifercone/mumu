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

package baby.mumu.storage.infra.file.convertor;

import baby.mumu.basis.mappers.GrpcMapper;
import baby.mumu.storage.client.api.grpc.FileRemoveGrpcCmd;
import baby.mumu.storage.client.cmds.FileDownloadCmd;
import baby.mumu.storage.client.cmds.FileRemoveCmd;
import baby.mumu.storage.client.cmds.FileSyncUploadCmd;
import baby.mumu.storage.domain.file.File;
import baby.mumu.storage.infra.file.gatewayimpl.storage.po.FileStoragePO;
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
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileMapper extends GrpcMapper {

  FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

  @API(status = Status.STABLE, since = "1.0.1")
  File toEntity(FileSyncUploadCmd fileSyncUploadCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  File toEntity(FileRemoveCmd fileRemoveCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  File toEntity(FileDownloadCmd fileDownloadCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  FileStoragePO toStoragePO(File file);

  @API(status = Status.STABLE, since = "2.2.0")
  FileRemoveCmd toFileRemoveCmd(FileRemoveGrpcCmd fileRemoveGrpcCmd);
}
