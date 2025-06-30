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
import baby.mumu.storage.domain.file.FileMetadata;
import baby.mumu.storage.domain.file.gateway.FileGateway;
import baby.mumu.storage.infra.file.convertor.FileConvertor;
import baby.mumu.storage.infra.file.gatewayimpl.database.FileMetadataRepository;
import baby.mumu.storage.infra.file.gatewayimpl.database.po.FileMetadataPO;
import baby.mumu.storage.infra.file.gatewayimpl.storage.FileStorageRepository;
import io.micrometer.observation.annotation.Observed;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文件领域网关实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.12.0
 */
@Component
@Observed(name = "FileGatewayImpl")
public class FileGatewayImpl implements FileGateway {

  private final FileMetadataRepository fileMetadataRepository;
  private final FileConvertor fileConvertor;
  private final FileStorageRepository fileStorageRepository;

  @Autowired
  public FileGatewayImpl(FileMetadataRepository fileMetadataRepository,
    FileConvertor fileConvertor, FileStorageRepository fileStorageRepository) {
    this.fileMetadataRepository = fileMetadataRepository;
    this.fileConvertor = fileConvertor;
    this.fileStorageRepository = fileStorageRepository;
  }

  /**
   * {@inheritDoc}
   *
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "2.12.0")
  public Long upload(File file) {
    if (file == null || file.getMetadata() == null) {
      return null;
    }

    FileMetadataPO fileMetadataPO = fileConvertor.toFileMetadataPO(file.getMetadata())
      .orElseThrow(() -> new MuMuException(ResponseCode.FILE_METADATA_INVALID));

    try {
      // 上传文件
      fileStorageRepository.upload(file);
    } catch (Exception e) {
      throw new MuMuException(ResponseCode.FILE_UPLOAD_FAILED);
    }

    try {
      // 保存元数据（数据库操作）
      fileMetadataRepository.persist(fileMetadataPO);
    } catch (Exception e) {
      // 元数据保存失败，尝试回滚上传
      try {
        fileStorageRepository.delete(file);
      } catch (Exception ex) {
        throw new MuMuException(ResponseCode.FILE_DELETION_FAILED);
      }
      throw new MuMuException(ResponseCode.FILE_METADATA_PERSIST_FAILED);
    }
    return fileMetadataPO.getId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "2.12.0")
  public void deleteByMetadataId(Long fileMetadataId) {
    if (fileMetadataId == null) {
      return;
    }
    FileMetadataPO fileMetadataPO = fileMetadataRepository.findById(fileMetadataId)
      .orElseThrow(() -> new MuMuException(ResponseCode.FILE_DOES_NOT_EXIST));
    FileMetadata fileMetadata = fileConvertor.toEntity(fileMetadataPO)
      .orElseThrow(() -> new MuMuException(ResponseCode.FILE_METADATA_INVALID));

    try {
      // 删除文件
      File file = new File();
      file.setMetadata(fileMetadata);
      fileStorageRepository.delete(file);
    } catch (Exception e) {
      throw new MuMuException(ResponseCode.FILE_DELETION_FAILED);
    }

    try {
      // 删除文件元数据
      fileMetadataRepository.deleteById(fileMetadataId);
    } catch (Exception e) {
      throw new MuMuException(ResponseCode.FILE_DELETION_FAILED);
    }
  }
}
