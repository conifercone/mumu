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
import java.io.InputStream;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
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
  private final StorageProperties storageProperties;

  @Autowired
  public FileS3StorageRepository(S3Client s3Client, StorageProperties storageProperties) {
    this.s3Client = s3Client;
    this.storageProperties = storageProperties;
  }

  @Override
  public void upload(@NonNull File file) {
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
    // 上传
    PutObjectRequest request = PutObjectRequest.builder()
      .bucket(storageZoneCode)
      .key(String.valueOf(fileMetadata.getId()))
      .contentType(fileMetadata.getContentType())
      .build();

    s3Client.putObject(request, RequestBody.fromInputStream(file.getContent(), fileSize));
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
