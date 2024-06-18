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

import com.expediagroup.beans.BeanUtils;
import com.expediagroup.beans.transformer.BeanTransformer;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.file.client.dto.co.StreamFileDownloadCo;
import com.sky.centaur.file.client.dto.co.StreamFileSyncUploadCo;
import com.sky.centaur.file.domain.stream.StreamFile;
import com.sky.centaur.file.infrastructure.streamfile.gatewayimpl.minio.dataobject.StreamFileMinioDo;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.util.ObjectUtils;

/**
 * 流式文件转换类
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
public final class StreamFileConvertor {

  private static final BeanTransformer BEAN_TRANSFORMER = new BeanUtils().getTransformer()
      .setDefaultValueForMissingField(true)
      .setDefaultValueForMissingPrimitiveField(false);

  private StreamFileConvertor() {
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public static Optional<StreamFile> toEntity(StreamFileSyncUploadCo streamFileSyncUploadCo) {
    return Optional.ofNullable(streamFileSyncUploadCo)
        .map(uploadCo -> {
          if (uploadCo.getContent() == null) {
            throw new CentaurException(ResultCode.FILE_CONTENT_CANNOT_BE_EMPTY);
          }
          StreamFile streamFile = BEAN_TRANSFORMER.skipTransformationForField("content")
              .transform(uploadCo,
                  StreamFile.class);
          streamFile.setContent(uploadCo.getContent());
          return streamFile;
        });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public static Optional<StreamFile> toEntity(StreamFileDownloadCo streamFileDownloadCo) {
    return Optional.ofNullable(streamFileDownloadCo)
        .map(downloadCo -> {
          StreamFile streamFile = BEAN_TRANSFORMER.skipTransformationForField("content")
              .transform(downloadCo,
                  StreamFile.class);
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
  public static Optional<StreamFileMinioDo> toMinioDo(StreamFile streamFile) {
    return Optional.ofNullable(streamFile)
        .map(file -> {
          StreamFileMinioDo transform = BEAN_TRANSFORMER.skipTransformationForField("content")
              .transform(file,
                  StreamFileMinioDo.class);
          if (streamFile.getContent() != null) {
            transform.setContent(streamFile.getContent());
          }
          return transform;
        });
  }
}
