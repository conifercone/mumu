/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
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
package com.sky.centaur.file.infrastructure.streamfile.convertor;

import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.file.client.dto.co.StreamFileDownloadCo;
import com.sky.centaur.file.client.dto.co.StreamFileRemoveCo;
import com.sky.centaur.file.client.dto.co.StreamFileSyncUploadCo;
import com.sky.centaur.file.domain.stream.StreamFile;
import com.sky.centaur.file.infrastructure.streamfile.gatewayimpl.minio.dataobject.StreamFileMinioDo;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * 流式文件转换类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Component
public class StreamFileConvertor {


  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<StreamFile> toEntity(StreamFileSyncUploadCo streamFileSyncUploadCo) {
    return Optional.ofNullable(streamFileSyncUploadCo)
        .map(uploadCo -> {
          StreamFile streamFile = StreamFileMapper.INSTANCE.toEntity(uploadCo);
          try (InputStream streamFileContent = uploadCo.getContent()) {
            if (streamFileContent == null) {
              throw new CentaurException(ResultCode.FILE_CONTENT_CANNOT_BE_EMPTY);
            } else {
              streamFile.setContent(streamFileContent);
            }
          } catch (IOException e) {
            throw new CentaurException(ResultCode.INPUT_STREAM_CONVERSION_FAILED);
          }
          return streamFile;
        });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<StreamFile> toEntity(StreamFileRemoveCo streamFileRemoveCo) {
    return Optional.ofNullable(streamFileRemoveCo).map(StreamFileMapper.INSTANCE::toEntity);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<StreamFile> toEntity(StreamFileDownloadCo streamFileDownloadCo) {
    return Optional.ofNullable(streamFileDownloadCo)
        .map(downloadCo -> {
          StreamFile streamFile = StreamFileMapper.INSTANCE.toEntity(downloadCo);
          if (ObjectUtils.isEmpty(streamFile.getStorageAddress())) {
            throw new CentaurException(ResultCode.FILE_STORAGE_ADDRESS_CANNOT_BE_EMPTY);
          }
          if (ObjectUtils.isEmpty(streamFile.getName())) {
            throw new CentaurException(ResultCode.FILE_NAME_CANNOT_BE_EMPTY);
          }
          return streamFile;
        });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<StreamFileMinioDo> toMinioDo(StreamFile streamFile) {
    return Optional.ofNullable(streamFile)
        .map(file -> {
          StreamFileMinioDo transform = StreamFileMapper.INSTANCE.toMinioDo(file);
          try (InputStream streamFileContent = streamFile.getContent()) {
            if (streamFileContent != null) {
              transform.setContent(streamFileContent);
            }
          } catch (IOException e) {
            throw new CentaurException(ResultCode.INPUT_STREAM_CONVERSION_FAILED);
          }
          return transform;
        });
  }
}
