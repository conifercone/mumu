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

import baby.mumu.basis.enums.StorageZonePolicyEnum;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.storage.domain.file.File;
import baby.mumu.storage.domain.file.FileMetadata;
import baby.mumu.storage.domain.zone.StorageZone;
import baby.mumu.storage.infra.file.gatewayimpl.storage.FileStorageRepository;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import java.io.InputStream;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
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
  public void upload(@NonNull File file) throws Exception {
    FileMetadata fileMetadata = Optional.ofNullable(file.getMetadata())
      .orElseThrow(() -> new MuMuException(ResponseCode.FILE_METADATA_INVALID));
    StorageZone storageZone = Optional.ofNullable(fileMetadata.getStorageZone())
      .orElseThrow(() -> new MuMuException(ResponseCode.STORAGE_ZONE_CANNOT_BE_EMPTY));
    Long fileSize = Optional.ofNullable(fileMetadata.getSize())
      .filter(size -> size > 0)
      .orElseThrow(() -> new MuMuException(ResponseCode.FILE_CONTENT_CANNOT_BE_EMPTY));
    if (StringUtils.isBlank(fileMetadata.getStoredFilename())) {
      throw new MuMuException(ResponseCode.FILE_NAME_CANNOT_BE_EMPTY);
    }
    // 确保 Bucket 存在
    String storageZoneCode = storageZone.getCode();
    createBucketIfNeeded(storageZoneCode, storageZone.getPolicy());
    // 上传
    minioClient.putObject(
      PutObjectArgs.builder()
        .bucket(storageZoneCode)
        .object(file.getMetadata().getStoragePath())
        .stream(file.getContent(), fileSize, -1)
        .contentType(file.getMetadata().getContentType())
        .build()
    );
  }

  private void createBucketIfNeeded(String storageZoneCode, StorageZonePolicyEnum storageZonePolicy)
    throws Exception {
    if (!minioClient.bucketExists(
      BucketExistsArgs.builder().bucket(storageZoneCode).build())) {
      minioClient.makeBucket(
        MakeBucketArgs.builder().bucket(storageZoneCode).build());
      if (StorageZonePolicyEnum.PUBLIC.equals(storageZonePolicy)) {
        /*
         * s3:GetBucketLocation: 获取桶的位置
         * s3:ListBucket: 列出桶内文件（GET /bucket?prefix=xxx）
         * s3:ListBucketMultipartUploads: 列出分片上传记录（如用多段上传）
         * s3:PutObject: 上传对象
         * s3:GetObject: 下载对象
         * s3:DeleteObject: 删除对象
         * s3:AbortMultipartUpload: 终止未完成的分片上传
         * s3:ListMultipartUploadParts: 列出已上传的分片
         */
        String policy = """
          {
            "Version": "2012-10-17",
            "Statement": [
              {
                "Effect": "Allow",
                "Principal": { "AWS": "*" },
                "Action": [
                  "s3:GetBucketLocation",
                  "s3:ListBucket",
                  "s3:ListBucketMultipartUploads"
                ],
                "Resource": ["arn:aws:s3:::%s"]
              },
              {
                "Effect": "Allow",
                "Principal": { "AWS": "*" },
                "Action": [
                  "s3:AbortMultipartUpload",
                  "s3:DeleteObject",
                  "s3:GetObject",
                  "s3:ListMultipartUploadParts",
                  "s3:PutObject"
                ],
                "Resource": ["arn:aws:s3:::%s/*"]
              }
            ]
          }
          """.formatted(storageZoneCode, storageZoneCode);

        minioClient.setBucketPolicy(
          SetBucketPolicyArgs.builder()
            .bucket(storageZoneCode)
            .config(policy)
            .build()
        );
      }
    }
  }

  @Override
  public void delete(@NonNull File file) throws Exception {
    minioClient.removeObject(
      RemoveObjectArgs.builder()
        .bucket(file.getMetadata().getStorageZone().getCode())
        .object(file.getMetadata().getStoragePath())
        .build()
    );
  }

  @Override
  public InputStream download(@NonNull File file) throws Exception {
    return minioClient.getObject(
      GetObjectArgs.builder().bucket(file.getMetadata().getStorageZone().getCode())
        .object(file.getMetadata().getStoragePath()).build());
  }
}
