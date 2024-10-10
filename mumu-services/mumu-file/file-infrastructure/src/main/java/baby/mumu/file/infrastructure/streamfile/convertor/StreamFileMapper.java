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
package baby.mumu.file.infrastructure.streamfile.convertor;

import baby.mumu.file.client.dto.co.StreamFileDownloadCo;
import baby.mumu.file.client.dto.co.StreamFileRemoveCo;
import baby.mumu.file.client.dto.co.StreamFileSyncUploadCo;
import baby.mumu.file.domain.stream.StreamFile;
import baby.mumu.file.infrastructure.streamfile.gatewayimpl.minio.dataobject.StreamFileMinioDo;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * StreamFile mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StreamFileMapper {

  StreamFileMapper INSTANCE = Mappers.getMapper(StreamFileMapper.class);

  @API(status = Status.STABLE, since = "1.0.1")
  StreamFile toEntity(StreamFileSyncUploadCo streamFileSyncUploadCo);

  @API(status = Status.STABLE, since = "1.0.1")
  StreamFile toEntity(StreamFileRemoveCo streamFileRemoveCo);

  @API(status = Status.STABLE, since = "1.0.1")
  StreamFile toEntity(StreamFileDownloadCo streamFileDownloadCo);

  @API(status = Status.STABLE, since = "1.0.1")
  StreamFileMinioDo toMinioDo(StreamFile streamFile);
}
