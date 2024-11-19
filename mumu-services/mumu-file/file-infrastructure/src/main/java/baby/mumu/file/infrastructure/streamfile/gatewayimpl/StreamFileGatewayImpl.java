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
package baby.mumu.file.infrastructure.streamfile.gatewayimpl;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.file.domain.stream.StreamFile;
import baby.mumu.file.domain.stream.gateway.StreamFileGateway;
import baby.mumu.file.infrastructure.streamfile.convertor.StreamFileConvertor;
import baby.mumu.file.infrastructure.streamfile.gatewayimpl.minio.MinioStreamFileRepository;
import baby.mumu.file.infrastructure.streamfile.gatewayimpl.minio.dataobject.StreamFileMinioDo;
import io.micrometer.observation.annotation.Observed;
import java.io.InputStream;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * 流式文件领域网关实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Component
@Observed(name = "StreamFileGatewayImpl")
public class StreamFileGatewayImpl implements StreamFileGateway {

  private final MinioStreamFileRepository minioStreamFileRepository;
  private final StreamFileConvertor streamFileConvertor;

  @Autowired
  public StreamFileGatewayImpl(MinioStreamFileRepository minioStreamFileRepository,
    StreamFileConvertor streamFileConvertor) {
    this.minioStreamFileRepository = minioStreamFileRepository;
    this.streamFileConvertor = streamFileConvertor;
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.1")
  public void uploadFile(StreamFile streamFile) {
    StreamFileMinioDo streamFileMinioDo = Optional.ofNullable(streamFile)
      .flatMap(streamFileConvertor::toMinioDo)
      .filter(minioDo -> minioDo.getContent() != null && StringUtils.isNotBlank(
        minioDo.getName()))
      .orElseThrow(() -> new MuMuException(ResponseCode.FILE_CONTENT_CANNOT_BE_EMPTY));
    if (StringUtils.isNotBlank(streamFileMinioDo.getStorageAddress())) {
      minioStreamFileRepository.createStorageAddress(streamFileMinioDo.getStorageAddress());
    } else {
      throw new MuMuException(ResponseCode.FILE_STORAGE_ADDRESS_CANNOT_BE_EMPTY);
    }
    minioStreamFileRepository.uploadFile(streamFileMinioDo);
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<InputStream> download(StreamFile streamFile) {
    return Optional.ofNullable(streamFile).flatMap(streamFileConvertor::toMinioDo).filter(
      file -> {
        if (ObjectUtils.isEmpty(file.getStorageAddress())) {
          throw new MuMuException(ResponseCode.FILE_STORAGE_ADDRESS_CANNOT_BE_EMPTY);
        } else if (!minioStreamFileRepository.storageAddressExists(file.getStorageAddress())) {
          throw new MuMuException(ResponseCode.THE_FILE_STORAGE_ADDRESS_DOES_NOT_EXIST);
        }
        if (ObjectUtils.isEmpty(file.getName())) {
          throw new MuMuException(ResponseCode.FILE_NAME_CANNOT_BE_EMPTY);
        }
        if (!minioStreamFileRepository.existed(file)) {
          throw new MuMuException(ResponseCode.FILE_DOES_NOT_EXIST);
        }
        return Boolean.TRUE;
      }).flatMap(minioStreamFileRepository::download);
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.1")
  public void removeFile(StreamFile streamFile) {
    StreamFileMinioDo streamFileMinioDo = Optional.ofNullable(streamFile)
      .flatMap(streamFileConvertor::toMinioDo).filter(minioStreamFileRepository::existed)
      .orElseThrow(() -> new MuMuException(ResponseCode.FILE_DOES_NOT_EXIST));
    minioStreamFileRepository.removeFile(streamFileMinioDo);
  }
}
