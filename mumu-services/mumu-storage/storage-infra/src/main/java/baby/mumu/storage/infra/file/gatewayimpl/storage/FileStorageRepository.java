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

package baby.mumu.storage.infra.file.gatewayimpl.storage;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.storage.infra.file.gatewayimpl.storage.po.FileStoragePO;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * 文件存储操作类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Component
@ConditionalOnBean(MinioClient.class)
public class FileStorageRepository {

  private final MinioClient minioClient;

  @Autowired
  public FileStorageRepository(MinioClient minioClient) {
    this.minioClient = minioClient;
  }

  @API(status = Status.STABLE, since = "1.0.1")
  public void uploadFile(FileStoragePO fileStoragePO) {
    Optional.ofNullable(fileStoragePO).ifPresent(storagePO -> {
      try (InputStream storagePOContent = storagePO.getContent()) {
        minioClient.putObject(
          PutObjectArgs.builder()
            .bucket(storagePO.getStorageAddress())
            .object(storagePO.getName())
            .stream(storagePOContent, storagePOContent.available(),
              -1)
            .build());
      } catch (ErrorResponseException | InsufficientDataException | InternalException |
               InvalidKeyException | InvalidResponseException | IOException |
               NoSuchAlgorithmException | ServerException | XmlParserException e) {
        throw new MuMuException(ResponseCode.FILE_UPLOAD_FAILED);
      }
    });

  }

  /**
   * 初始化文件存储地址
   *
   * @param storageAddress 文件存储地址
   */
  @API(status = Status.STABLE, since = "1.0.1")
  public void createStorageAddress(String storageAddress) {
    if (!storageAddressExists(storageAddress)) {
      try {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(storageAddress).build());
      } catch (ErrorResponseException | InsufficientDataException | InternalException |
               InvalidKeyException | InvalidResponseException | IOException |
               NoSuchAlgorithmException | ServerException | XmlParserException e) {
        throw new MuMuException(ResponseCode.FILE_STORAGE_ADDRESS_CREATION_FAILED);
      }
    }
  }

  /**
   * 判断文件存储地址是否存在，true：存在，false：不存在
   *
   * @param storageAddress 文件存储地址
   * @return 是否存在
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  @API(status = Status.STABLE, since = "1.0.1")
  public boolean storageAddressExists(String storageAddress) {
    try {
      return minioClient.bucketExists(BucketExistsArgs.builder().bucket(storageAddress).build());
    } catch (ErrorResponseException | InsufficientDataException | InternalException |
             InvalidKeyException | InvalidResponseException | IOException |
             NoSuchAlgorithmException | ServerException | XmlParserException e) {
      return false;
    }
  }

  /**
   * 获取文件流
   *
   * @param fileStoragePO 文件存储数据对象
   * @return 二进制流
   */
  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<InputStream> download(FileStoragePO fileStoragePO) {
    return Optional.ofNullable(fileStoragePO).map(storagePO -> {
      try {
        return minioClient.getObject(
          GetObjectArgs.builder()
            .bucket(storagePO.getStorageAddress())
            .object(storagePO.getName())
            .build());
      } catch (ErrorResponseException | InsufficientDataException | InternalException |
               InvalidKeyException | InvalidResponseException | IOException |
               NoSuchAlgorithmException | ServerException | XmlParserException e) {
        throw new MuMuException(ResponseCode.FILE_DOWNLOAD_FAILED);
      }
    });
  }


  /**
   * 判断文件是否存在
   *
   * @param fileStoragePO 文件存储数据对象
   * @return 是否存在
   */
  @API(status = Status.STABLE, since = "1.0.1")
  public boolean existed(FileStoragePO fileStoragePO) {
    boolean exist = true;
    try {
      minioClient.statObject(
        StatObjectArgs.builder().bucket(fileStoragePO.getStorageAddress())
          .object(fileStoragePO.getName()).build());
    } catch (Exception e) {
      exist = false;
    }
    return exist;
  }

  /**
   * 删除文件
   *
   * @param fileStoragePO 文件存储数据对象
   */
  @API(status = Status.STABLE, since = "1.0.1")
  public void removeFile(FileStoragePO fileStoragePO) {
    Optional.ofNullable(fileStoragePO).ifPresent(storagePO -> {
      try {
        minioClient.removeObject(
          RemoveObjectArgs.builder()
            .bucket(storagePO.getStorageAddress())
            .object(storagePO.getName())
            .build());
      } catch (ErrorResponseException | InsufficientDataException | InternalException |
               InvalidKeyException | InvalidResponseException | IOException |
               NoSuchAlgorithmException | ServerException | XmlParserException e) {
        throw new MuMuException(ResponseCode.FILE_DELETION_FAILED);
      }
    });
  }
}
