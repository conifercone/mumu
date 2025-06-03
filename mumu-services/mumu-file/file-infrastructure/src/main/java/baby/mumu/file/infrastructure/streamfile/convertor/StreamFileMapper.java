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

package baby.mumu.file.infrastructure.streamfile.convertor;

import baby.mumu.basis.mappers.GrpcMapper;
import baby.mumu.file.client.api.grpc.StreamFileRemoveGrpcCmd;
import baby.mumu.file.client.cmds.StreamFileDownloadCmd;
import baby.mumu.file.client.cmds.StreamFileRemoveCmd;
import baby.mumu.file.client.cmds.StreamFileSyncUploadCmd;
import baby.mumu.file.domain.stream.StreamFile;
import baby.mumu.file.infrastructure.streamfile.gatewayimpl.storage.po.StreamFileStoragePO;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * StreamFile mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StreamFileMapper extends GrpcMapper {

  StreamFileMapper INSTANCE = Mappers.getMapper(StreamFileMapper.class);

  @API(status = Status.STABLE, since = "1.0.1")
  StreamFile toEntity(StreamFileSyncUploadCmd streamFileSyncUploadCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  StreamFile toEntity(StreamFileRemoveCmd streamFileRemoveCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  StreamFile toEntity(StreamFileDownloadCmd streamFileDownloadCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  StreamFileStoragePO toStoragePO(StreamFile streamFile);

  @API(status = Status.STABLE, since = "2.2.0")
  StreamFileRemoveCmd toStreamFileRemoveCmd(StreamFileRemoveGrpcCmd streamFileRemoveGrpcCmd);
}
