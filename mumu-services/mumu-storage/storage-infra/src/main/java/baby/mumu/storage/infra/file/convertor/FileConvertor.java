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
import baby.mumu.storage.client.api.grpc.FileRemoveGrpcCmd;
import baby.mumu.storage.client.cmds.FileDownloadCmd;
import baby.mumu.storage.client.cmds.FileRemoveCmd;
import baby.mumu.storage.client.cmds.FileSyncUploadCmd;
import baby.mumu.storage.domain.file.File;
import baby.mumu.storage.infra.file.gatewayimpl.storage.po.FileStoragePO;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * 文件转换类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Component
public class FileConvertor {


  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<File> toEntity(FileSyncUploadCmd fileSyncUploadCmd) {
    return Optional.ofNullable(fileSyncUploadCmd)
      .map(fileSyncUploadCmdNonNull -> {
        File file = FileMapper.INSTANCE.toEntity(fileSyncUploadCmdNonNull);
        try (InputStream fileContent = fileSyncUploadCmdNonNull.getContent()) {
          if (fileContent == null) {
            throw new MuMuException(ResponseCode.FILE_CONTENT_CANNOT_BE_EMPTY);
          } else {
            file.setContent(fileContent);
          }
        } catch (IOException e) {
          throw new MuMuException(ResponseCode.INPUT_STREAM_CONVERSION_FAILED);
        }
        return file;
      });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<File> toEntity(FileRemoveCmd fileRemoveCmd) {
    return Optional.ofNullable(fileRemoveCmd).map(FileMapper.INSTANCE::toEntity);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<File> toEntity(FileDownloadCmd fileDownloadCmd) {
    return Optional.ofNullable(fileDownloadCmd)
      .map(fileDownloadCmdNonNull -> {
        File file = FileMapper.INSTANCE.toEntity(fileDownloadCmdNonNull);
        if (ObjectUtils.isEmpty(file.getStorageAddress())) {
          throw new MuMuException(ResponseCode.FILE_STORAGE_ADDRESS_CANNOT_BE_EMPTY);
        }
        if (ObjectUtils.isEmpty(file.getName())) {
          throw new MuMuException(ResponseCode.FILE_NAME_CANNOT_BE_EMPTY);
        }
        return file;
      });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<FileStoragePO> toFileStoragePO(File file) {
    return Optional.ofNullable(file)
      .map(fileNonNull -> {
        FileStoragePO transform = FileMapper.INSTANCE.toFileStoragePO(fileNonNull);
        try (InputStream fileContent = file.getContent()) {
          if (fileContent != null) {
            transform.setContent(fileContent);
          }
        } catch (IOException e) {
          throw new MuMuException(ResponseCode.INPUT_STREAM_CONVERSION_FAILED);
        }
        return transform;
      });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<FileRemoveCmd> toFileRemoveCmd(
    FileRemoveGrpcCmd fileRemoveGrpcCmd) {
    return Optional.ofNullable(fileRemoveGrpcCmd)
      .map(FileMapper.INSTANCE::toFileRemoveCmd);
  }
}
