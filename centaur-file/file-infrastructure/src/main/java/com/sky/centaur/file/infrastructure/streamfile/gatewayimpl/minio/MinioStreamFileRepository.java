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
package com.sky.centaur.file.infrastructure.streamfile.gatewayimpl.minio;

import com.sky.centaur.file.infrastructure.streamfile.gatewayimpl.minio.dataobject.StreamFileMinioDo;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * minio操作类
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
@Component
@ConditionalOnBean(MinioClient.class)
public class MinioStreamFileRepository {

  private final MinioClient minioClient;

  @Autowired
  public MinioStreamFileRepository(MinioClient minioClient) {
    this.minioClient = minioClient;
  }

  @API(status = Status.STABLE, since = "1.0.1")
  public void uploadFile(StreamFileMinioDo streamFileMinioDo) throws Exception {
    if (streamFileMinioDo != null) {
      minioClient.putObject(
          PutObjectArgs.builder()
              .bucket(streamFileMinioDo.getStorageAddress())
              .object(streamFileMinioDo.getName())
              .stream(streamFileMinioDo.getContent(), streamFileMinioDo.getContent().available(),
                  -1)
              .build());
    }
  }

  /**
   * 初始化文件存储地址
   *
   * @param storageAddress 文件存储地址
   */
  @API(status = Status.STABLE, since = "1.0.1")
  public void createStorageAddress(String storageAddress) throws Exception {
    if (!storageAddressExists(storageAddress)) {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(storageAddress).build());
    }
  }

  /**
   * 判断文件存储地址是否存在，true：存在，false：不存在
   *
   * @param storageAddress 文件存储地址
   * @return 是否存在
   */
  @API(status = Status.STABLE, since = "1.0.1")
  public boolean storageAddressExists(String storageAddress) throws Exception {
    return minioClient.bucketExists(BucketExistsArgs.builder().bucket(storageAddress).build());
  }

}
