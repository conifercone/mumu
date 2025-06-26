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

package baby.mumu.storage.infra.file.gatewayimpl.storage.minio;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.storage.domain.file.File;
import baby.mumu.storage.infra.file.gatewayimpl.storage.FileStorageRepository;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 文件minio存储
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.12.0
 */
@Component
@ConditionalOnProperty(prefix = "mumu.storage", value = "storage-media-type", havingValue = "MINIO", matchIfMissing = true)
public class FileMinioStorageRepository implements FileStorageRepository {

  private final MinioClient minioClient;

  @Autowired
  public FileMinioStorageRepository(MinioClient minioClient) {
    this.minioClient = minioClient;
  }

  @Override
  public void upload(@NotNull File file) throws Exception {
    if (StringUtils.isBlank(file.getMetadata().getStoredFilename())) {
      throw new MuMuException(ResponseCode.FILE_NAME_CANNOT_BE_EMPTY);
    }
    if (StringUtils.isBlank(file.getMetadata().getStorageZone())) {
      throw new MuMuException(ResponseCode.FILE_STORAGE_ZONE_CANNOT_BE_EMPTY);
    }
    if (file.getMetadata().getSize() == null || file.getMetadata().getSize() == 0) {
      throw new MuMuException(ResponseCode.FILE_CONTENT_CANNOT_BE_EMPTY);
    }
    // 确保 Bucket 存在
    if (!minioClient.bucketExists(
      BucketExistsArgs.builder().bucket(file.getMetadata().getStorageZone()).build())) {
      minioClient.makeBucket(
        MakeBucketArgs.builder().bucket(file.getMetadata().getStorageZone()).build());
    }

    // 上传
    minioClient.putObject(
      PutObjectArgs.builder()
        .bucket(file.getMetadata().getStorageZone())
        .object(file.getMetadata().getStoragePath())
        .stream(file.getContent(), file.getMetadata().getSize(), -1)
        .contentType(file.getMetadata().getContentType())
        .build()
    );
  }

  @Override
  public void delete(@NotNull File file) throws Exception {
    minioClient.removeObject(
      RemoveObjectArgs.builder()
        .bucket(file.getMetadata().getStorageZone())
        .object(file.getMetadata().getStoragePath())
        .build()
    );
  }
}
