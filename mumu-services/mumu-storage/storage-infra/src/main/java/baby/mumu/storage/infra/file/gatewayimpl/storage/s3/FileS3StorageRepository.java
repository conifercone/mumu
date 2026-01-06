/*
 * Copyright (c) 2024-2026, the original author or authors.
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
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest.Builder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    public FileS3StorageRepository(S3Client s3Client,
                                   StorageProperties storageProperties) {
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
        createStorageZone(file);
        if (fileSize > 0 && fileSize <= 50L * 1024 * 1024) {
            // 上传
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket(storageZoneCode)
                .key(String.valueOf(fileMetadata.getId()))
                .contentType(fileMetadata.getContentType())
                .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getContent(), fileSize));
        } else {
            uploadStreamAsMultipart(storageZoneCode, String.valueOf(fileMetadata.getId()),
                file.getContent(),
                fileMetadata.getContentType());
        }
    }

    @Override
    public void createStorageZone(File file) {
        S3 s3 = storageProperties.getS3();
        StorageZone storageZone = file.getMetadata().getStorageZone();
        if (!storageZoneExists(file)) {
            Builder builder = CreateBucketRequest.builder()
                .createBucketConfiguration(
                    CreateBucketConfiguration.builder()
                        .locationConstraint(s3.getRegion())
                        .build()
                )
                .bucket(storageZone.getCode());
            if (StorageZonePolicyEnum.PUBLIC.equals(storageZone.getPolicy())) {
                builder.acl(BucketCannedACL.PUBLIC_READ_WRITE);
            } else if (StorageZonePolicyEnum.PRIVATE.equals(storageZone.getPolicy())) {
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
     * 使用 InputStream 进行分片上传到 S3
     */
    public void uploadStreamAsMultipart(String bucket,
                                        String key,
                                        InputStream inputStream,
                                        String contentType) {

        // 每个分片大小（至少 5 MB，最后一片可以小于 5MB）
        final int partSize = 5 * 1024 * 1024; // 5MB

        // 1. 初始化 multipart upload
        CreateMultipartUploadRequest createMultipartUploadRequest =
            CreateMultipartUploadRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        CreateMultipartUploadResponse createResponse =
            s3Client.createMultipartUpload(createMultipartUploadRequest);

        String uploadId = createResponse.uploadId();

        List<CompletedPart> completedParts = new ArrayList<>();
        byte[] buffer = new byte[partSize];
        int partNumber = 1;

        try {
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {

                byte[] bytesToUpload;
                if (bytesRead == buffer.length) {
                    bytesToUpload = buffer;
                } else {
                    // 最后一块可能没读满 buffer
                    bytesToUpload = Arrays.copyOf(buffer, bytesRead);
                }

                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .uploadId(uploadId)
                    .partNumber(partNumber)
                    .contentLength((long) bytesRead)
                    .build();

                UploadPartResponse uploadPartResponse = s3Client.uploadPart(
                    uploadPartRequest,
                    RequestBody.fromBytes(bytesToUpload)
                );

                completedParts.add(
                    CompletedPart.builder()
                        .partNumber(partNumber)
                        .eTag(uploadPartResponse.eTag())
                        .build()
                );

                partNumber++;
            }

            // 2. 所有分片上传完，合并分片
            CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(completedParts)
                .build();

            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                CompleteMultipartUploadRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .uploadId(uploadId)
                    .multipartUpload(completedMultipartUpload)
                    .build();

            s3Client.completeMultipartUpload(completeMultipartUploadRequest);

        } catch (Exception e) {
            // 3. 出异常中止 upload
            AbortMultipartUploadRequest abortMultipartUploadRequest =
                AbortMultipartUploadRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .uploadId(uploadId)
                    .build();
            s3Client.abortMultipartUpload(abortMultipartUploadRequest);
            throw new RuntimeException("Multipart upload failed", e);
        }
    }

    /**
     * 判断 Bucket 是否存在
     */
    @Override
    public boolean storageZoneExists(File file) {
        try {
            s3Client.headBucket(
                HeadBucketRequest.builder().bucket(file.getMetadata().getStorageZone().getCode()).build());
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
