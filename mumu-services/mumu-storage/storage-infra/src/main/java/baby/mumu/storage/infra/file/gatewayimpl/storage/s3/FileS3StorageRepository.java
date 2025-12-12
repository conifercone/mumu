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

package baby.mumu.storage.infra.file.gatewayimpl.storage.s3;

import baby.mumu.basis.enums.StorageZonePolicyEnum;
import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.storage.client.config.StorageProperties;
import baby.mumu.storage.client.config.StorageProperties.S3;
import baby.mumu.storage.domain.file.File;
import baby.mumu.storage.domain.file.FileMetadata;
import baby.mumu.storage.domain.zone.StorageZone;
import baby.mumu.storage.infra.file.gatewayimpl.storage.FileStorageRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketCannedACL;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest.Builder;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;

/**
 * 文件S3存储
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.16.0
 */
@Component
@ConditionalOnProperty(prefix = "mumu.storage", value = "storage-media-type", havingValue = "S3", matchIfMissing = true)
public class FileS3StorageRepository implements FileStorageRepository {

  private final S3Client s3Client;
  private final S3AsyncClient s3AsyncClient;
  private final StorageProperties storageProperties;
  private static final Logger log = LoggerFactory.getLogger(
    FileS3StorageRepository.class);


  @Autowired
  public FileS3StorageRepository(S3Client s3Client, S3AsyncClient s3AsyncClient,
    StorageProperties storageProperties) {
    this.s3Client = s3Client;
    this.s3AsyncClient = s3AsyncClient;
    this.storageProperties = storageProperties;
  }

  @Override
  public void upload(@NonNull File file) throws IOException {
    // noinspection DuplicatedCode
    FileMetadata fileMetadata = Optional.ofNullable(file.getMetadata())
      .orElseThrow(() -> new ApplicationException(ResponseCode.FILE_METADATA_INVALID));
    StorageZone storageZone = Optional.ofNullable(fileMetadata.getStorageZone())
      .orElseThrow(() -> new ApplicationException(ResponseCode.STORAGE_ZONE_CANNOT_BE_EMPTY));
    Long fileSize = Optional.ofNullable(fileMetadata.getSize())
      .filter(size -> size > 0)
      .orElseThrow(() -> new ApplicationException(ResponseCode.FILE_CONTENT_CANNOT_BE_EMPTY));
    if (StringUtils.isBlank(fileMetadata.getStoredFilename())) {
      throw new ApplicationException(ResponseCode.FILE_NAME_CANNOT_BE_EMPTY);
    }
    // 确保 Bucket 存在
    String storageZoneCode = storageZone.getCode();
    createBucketIfNeeded(storageZoneCode, storageZone.getPolicy());
    if (fileSize > 0 && fileSize <= 50L * 1024 * 1024) {
      // 上传
      PutObjectRequest request = PutObjectRequest.builder()
        .bucket(storageZoneCode)
        .key(String.valueOf(fileMetadata.getId()))
        .contentType(fileMetadata.getContentType())
        .build();

      s3Client.putObject(request, RequestBody.fromInputStream(file.getContent(), fileSize));
    } else {
      try (S3TransferManager transferManager = S3TransferManager.builder()
        .s3Client(s3AsyncClient)
        .build()) {

        Path temp = Files.createTempFile("upload-", ".tmp");
        Files.copy(file.getContent(), temp, StandardCopyOption.REPLACE_EXISTING);

        UploadFileRequest request = UploadFileRequest.builder()
          .putObjectRequest(
            p -> p.bucket(storageZoneCode).contentType(fileMetadata.getContentType())
              .key(String.valueOf(fileMetadata.getId())))
          .source(temp)
          .build();
        FileUpload upload = transferManager.uploadFile(request);

        try {
          // 等待上传完成（若失败会抛异常）
          upload.completionFuture().join();

          // 上传成功 → 删除临时文件
          Files.deleteIfExists(temp);

        } catch (Exception e) {
          // 上传失败 → 尝试删除临时文件
          try {
            Files.deleteIfExists(temp);
          } catch (IOException ex) {
            // 记录日志即可，不应覆盖原始异常
            FileS3StorageRepository.log.error("Failed to delete temp file: {}", ex.getMessage());
          }

          // 再次抛出上传异常
          throw new RuntimeException("S3 upload failed", e);
        }
      }
    }
  }

  private void createBucketIfNeeded(String storageZoneCode,
    StorageZonePolicyEnum storageZonePolicy) {
    S3 s3 = storageProperties.getS3();
    if (!bucketExists(storageZoneCode)) {
      Builder builder = CreateBucketRequest.builder()
        .createBucketConfiguration(
          CreateBucketConfiguration.builder()
            .locationConstraint(s3.getRegion())
            .build()
        )
        .bucket(storageZoneCode);
      if (StorageZonePolicyEnum.PUBLIC.equals(storageZonePolicy)) {
        builder.acl(BucketCannedACL.PUBLIC_READ_WRITE);
      } else if (StorageZonePolicyEnum.PRIVATE.equals(storageZonePolicy)) {
        builder.acl(BucketCannedACL.PRIVATE);
      }
      s3Client.createBucket(builder.build());
    }
  }

  @Override
  public void delete(@NonNull File file) {
    FileMetadata metadata = file.getMetadata();
    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
      .bucket(metadata.getStorageZone().getCode())
      .key(String.valueOf(metadata.getId()))
      .build();
    s3Client.deleteObject(deleteObjectRequest);
  }

  @Override
  public InputStream download(@NonNull File file) {
    FileMetadata metadata = file.getMetadata();
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
      .bucket(metadata.getStorageZone().getCode())
      .key(String.valueOf(metadata.getId()))
      .build();
    return s3Client.getObjectAsBytes(getObjectRequest).asInputStream();
  }

  /**
   * 判断 Bucket 是否存在
   */
  public boolean bucketExists(String storageZoneCode) {
    try {
      s3Client.headBucket(HeadBucketRequest.builder().bucket(storageZoneCode).build());
      return true;
    } catch (NoSuchBucketException e) {
      return false;
    } catch (S3Exception e) {
      // 403: 没权限也意味着存在，但不可访问
      if (e.statusCode() == 403) {
        return true;
      }
      throw e;
    }
  }
}
