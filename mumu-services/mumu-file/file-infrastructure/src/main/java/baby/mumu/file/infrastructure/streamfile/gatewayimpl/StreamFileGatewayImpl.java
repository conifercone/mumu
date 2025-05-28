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
package baby.mumu.file.infrastructure.streamfile.gatewayimpl;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.file.domain.stream.StreamFile;
import baby.mumu.file.domain.stream.gateway.StreamFileGateway;
import baby.mumu.file.infrastructure.streamfile.convertor.StreamFileConvertor;
import baby.mumu.file.infrastructure.streamfile.gatewayimpl.storage.StreamFileStorageRepository;
import baby.mumu.file.infrastructure.streamfile.gatewayimpl.storage.po.StreamFileStoragePO;
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

  private final StreamFileStorageRepository streamFileStorageRepository;
  private final StreamFileConvertor streamFileConvertor;

  @Autowired
  public StreamFileGatewayImpl(StreamFileStorageRepository streamFileStorageRepository,
    StreamFileConvertor streamFileConvertor) {
    this.streamFileStorageRepository = streamFileStorageRepository;
    this.streamFileConvertor = streamFileConvertor;
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.1")
  public void uploadFile(StreamFile streamFile) {
    StreamFileStoragePO streamFileStoragePO = Optional.ofNullable(streamFile)
      .flatMap(streamFileConvertor::toStoragePO)
      .filter(storagePO -> storagePO.getContent() != null && StringUtils.isNotBlank(
        storagePO.getName()))
      .orElseThrow(() -> new MuMuException(ResponseCode.FILE_CONTENT_CANNOT_BE_EMPTY));
    if (StringUtils.isNotBlank(streamFileStoragePO.getStorageAddress())) {
      streamFileStorageRepository.createStorageAddress(streamFileStoragePO.getStorageAddress());
    } else {
      throw new MuMuException(ResponseCode.FILE_STORAGE_ADDRESS_CANNOT_BE_EMPTY);
    }
    streamFileStorageRepository.uploadFile(streamFileStoragePO);
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<InputStream> download(StreamFile streamFile) {
    return Optional.ofNullable(streamFile).flatMap(streamFileConvertor::toStoragePO).filter(
      storagePO -> {
        if (ObjectUtils.isEmpty(storagePO.getStorageAddress())) {
          throw new MuMuException(ResponseCode.FILE_STORAGE_ADDRESS_CANNOT_BE_EMPTY);
        } else if (!streamFileStorageRepository.storageAddressExists(
          storagePO.getStorageAddress())) {
          throw new MuMuException(ResponseCode.THE_FILE_STORAGE_ADDRESS_DOES_NOT_EXIST);
        }
        if (ObjectUtils.isEmpty(storagePO.getName())) {
          throw new MuMuException(ResponseCode.FILE_NAME_CANNOT_BE_EMPTY);
        }
        if (!streamFileStorageRepository.existed(storagePO)) {
          throw new MuMuException(ResponseCode.FILE_DOES_NOT_EXIST);
        }
        return true;
      }).flatMap(streamFileStorageRepository::download);
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.1")
  public void removeFile(StreamFile streamFile) {
    StreamFileStoragePO streamFileStoragePO = Optional.ofNullable(streamFile)
      .flatMap(streamFileConvertor::toStoragePO).filter(streamFileStorageRepository::existed)
      .orElseThrow(() -> new MuMuException(ResponseCode.FILE_DOES_NOT_EXIST));
    streamFileStorageRepository.removeFile(streamFileStoragePO);
  }
}
