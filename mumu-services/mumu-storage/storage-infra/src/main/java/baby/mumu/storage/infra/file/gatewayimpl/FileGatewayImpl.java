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
import baby.mumu.storage.infra.file.gatewayimpl.database.FileMetadataRepository;
import baby.mumu.storage.infra.file.gatewayimpl.storage.FileStorageRepository;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
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

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "2.12.0")
  public void upload(File file) {
    Optional.ofNullable(file).ifPresent(fileNonNull -> {
      // 保存文件元数据
      fileConvertor.toFileMetadataPO(fileNonNull.getMetadata())
          .ifPresent(fileMetadataPO -> {
            fileMetadataRepository.persist(fileMetadataPO);
            fileNonNull.getMetadata().setId(fileMetadataPO.getId());
            // 文件上传
            try {
              fileStorageRepository.upload(fileNonNull);
            } catch (Exception e) {
              throw new MuMuException(ResponseCode.FILE_UPLOAD_FAILED);
            }
          });
    });
  }
}
