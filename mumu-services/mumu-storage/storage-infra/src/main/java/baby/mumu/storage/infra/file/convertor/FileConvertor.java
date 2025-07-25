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

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.storage.domain.file.File;
import baby.mumu.storage.domain.file.FileMetadata;
import baby.mumu.storage.infra.file.gatewayimpl.database.po.FileMetadataPO;
import baby.mumu.unique.client.api.PrimaryKeyGrpcService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import org.apache.tika.Tika;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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

  public FileConvertor(PrimaryKeyGrpcService primaryKeyGrpcService) {
    this.primaryKeyGrpcService = primaryKeyGrpcService;
  }


  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.12.0")
  public Optional<FileMetadataPO> toFileMetadataPO(FileMetadata fileMetadata) {
    return Optional.ofNullable(fileMetadata)
      .map(FileMapper.INSTANCE::toFileMetadataPO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.12.0")
  public Optional<FileMetadata> toEntity(FileMetadataPO fileMetadataPO) {
    return Optional.ofNullable(fileMetadataPO)
      .map(FileMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "2.12.0")
  public Optional<File> toEntity(String storageZone, MultipartFile multipartFile) {
    return Optional.ofNullable(multipartFile).map(_ -> {
      File file = new File();
      try {
        byte[] fileBytes = multipartFile.getBytes();
        file.setContent(new ByteArrayInputStream(fileBytes));
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setId(primaryKeyGrpcService.snowflake());
        fileMetadata.setStorageZone(storageZone);
        fileMetadata.setSize(multipartFile.getSize());
        fileMetadata.setContentType(tika.detect(new ByteArrayInputStream(fileBytes)));
        fileMetadata.setOriginalFilename(multipartFile.getOriginalFilename());
        fileMetadata.setStoredFilename(multipartFile.getOriginalFilename());
        fileMetadata.setStoragePath(fileMetadata.getId() + "/" + fileMetadata.getStoredFilename());
        file.setMetadata(fileMetadata);
      } catch (IOException e) {
        throw new MuMuException(ResponseCode.INPUT_STREAM_CONVERSION_FAILED);
      }
      return file;
    });
  }
}
