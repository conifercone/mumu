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

package baby.mumu.storage.infra.file.gatewayimpl;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.storage.domain.file.File;
import baby.mumu.storage.domain.file.gateway.FileGateway;
import baby.mumu.storage.infra.file.convertor.FileConvertor;
import baby.mumu.storage.infra.file.gatewayimpl.storage.FileStorageRepository;
import baby.mumu.storage.infra.file.gatewayimpl.storage.po.FileStoragePO;
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
 * 文件领域网关实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Component
@Observed(name = "FileGatewayImpl")
public class FileGatewayImpl implements FileGateway {

  private final FileStorageRepository fileStorageRepository;
  private final FileConvertor fileConvertor;

  @Autowired
  public FileGatewayImpl(FileStorageRepository fileStorageRepository,
    FileConvertor fileConvertor) {
    this.fileStorageRepository = fileStorageRepository;
    this.fileConvertor = fileConvertor;
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.1")
  public void uploadFile(File file) {
    FileStoragePO fileStoragePO = Optional.ofNullable(file)
      .flatMap(fileConvertor::toStoragePO)
      .filter(storagePO -> storagePO.getContent() != null && StringUtils.isNotBlank(
        storagePO.getName()))
      .orElseThrow(() -> new MuMuException(ResponseCode.FILE_CONTENT_CANNOT_BE_EMPTY));
    if (StringUtils.isNotBlank(fileStoragePO.getStorageAddress())) {
      fileStorageRepository.createStorageAddress(fileStoragePO.getStorageAddress());
    } else {
      throw new MuMuException(ResponseCode.FILE_STORAGE_ADDRESS_CANNOT_BE_EMPTY);
    }
    fileStorageRepository.uploadFile(fileStoragePO);
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<InputStream> download(File file) {
    return Optional.ofNullable(file).flatMap(fileConvertor::toStoragePO).filter(
      storagePO -> {
        if (ObjectUtils.isEmpty(storagePO.getStorageAddress())) {
          throw new MuMuException(ResponseCode.FILE_STORAGE_ADDRESS_CANNOT_BE_EMPTY);
        } else if (!fileStorageRepository.storageAddressExists(
          storagePO.getStorageAddress())) {
          throw new MuMuException(ResponseCode.THE_FILE_STORAGE_ADDRESS_DOES_NOT_EXIST);
        }
        if (ObjectUtils.isEmpty(storagePO.getName())) {
          throw new MuMuException(ResponseCode.FILE_NAME_CANNOT_BE_EMPTY);
        }
        if (!fileStorageRepository.existed(storagePO)) {
          throw new MuMuException(ResponseCode.FILE_DOES_NOT_EXIST);
        }
        return true;
      }).flatMap(fileStorageRepository::download);
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.1")
  public void removeFile(File file) {
    FileStoragePO fileStoragePO = Optional.ofNullable(file)
      .flatMap(fileConvertor::toStoragePO).filter(fileStorageRepository::existed)
      .orElseThrow(() -> new MuMuException(ResponseCode.FILE_DOES_NOT_EXIST));
    fileStorageRepository.removeFile(fileStoragePO);
  }
}
