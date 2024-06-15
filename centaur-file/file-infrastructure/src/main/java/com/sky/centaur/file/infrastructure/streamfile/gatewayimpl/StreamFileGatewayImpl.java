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
package com.sky.centaur.file.infrastructure.streamfile.gatewayimpl;

import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.file.domain.stream.StreamFile;
import com.sky.centaur.file.domain.stream.gateway.StreamFileGateway;
import com.sky.centaur.file.infrastructure.streamfile.convertor.StreamFileConvertor;
import com.sky.centaur.file.infrastructure.streamfile.gatewayimpl.minio.MinioStreamFileRepository;
import com.sky.centaur.file.infrastructure.streamfile.gatewayimpl.minio.dataobject.StreamFileMinioDo;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 流式文件领域网关实现类
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
@Component
@Observed(name = "StreamFileGatewayImpl")
public class StreamFileGatewayImpl implements StreamFileGateway {

  private final MinioStreamFileRepository minioStreamFileRepository;

  @Autowired
  public StreamFileGatewayImpl(MinioStreamFileRepository minioStreamFileRepository) {
    this.minioStreamFileRepository = minioStreamFileRepository;
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.1")
  public void uploadFile(StreamFile streamFile) throws Exception {
    StreamFileMinioDo streamFileMinioDo = Optional.ofNullable(streamFile)
        .flatMap(StreamFileConvertor::toMinioDo)
        .filter(minioDo -> minioDo.getContent() != null && StringUtils.hasText(
            minioDo.getName()))
        .orElseThrow(() -> new CentaurException(ResultCode.FILE_CONTENT_CANNOT_BE_EMPTY));
    if (StringUtils.hasText(streamFileMinioDo.getStorageAddress())) {
      minioStreamFileRepository.createStorageAddress(streamFileMinioDo.getStorageAddress());
    } else {
      throw new CentaurException(ResultCode.FILE_STORAGE_ADDRESS_CANNOT_BE_EMPTY);
    }
    minioStreamFileRepository.uploadFile(streamFileMinioDo);
  }
}
