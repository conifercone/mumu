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

package baby.mumu.storage.application.service;

import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.storage.application.file.executor.FileDeleteByMetadataIdCmdExe;
import baby.mumu.storage.application.file.executor.FileDownloadByMetadataIdCmdExe;
import baby.mumu.storage.application.file.executor.FileFindMetaByMetaIdCmdExe;
import baby.mumu.storage.application.file.executor.FileUploadCmdExe;
import baby.mumu.storage.client.api.FileService;
import baby.mumu.storage.client.api.grpc.FileServiceGrpc.FileServiceImplBase;
import baby.mumu.storage.client.dto.FileFindMetaByMetaIdDTO;
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletResponse;
import net.devh.boot.grpc.server.service.GrpcService;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.12.0
 */
@Service
@Observed(name = "FileServiceImpl")
@GrpcService(interceptors = {ObservationGrpcServerInterceptor.class, ClientIpInterceptor.class})
public class FileServiceImpl extends FileServiceImplBase implements FileService {

  private final FileUploadCmdExe fileUploadCmdExe;
  private final FileDeleteByMetadataIdCmdExe fileDeleteByMetadataIdCmdExe;
  private final FileDownloadByMetadataIdCmdExe fileDownloadByMetadataIdCmdExe;
  private final FileFindMetaByMetaIdCmdExe fileFindMetaByMetaIdCmdExe;

  public FileServiceImpl(FileUploadCmdExe fileUploadCmdExe,
    FileDeleteByMetadataIdCmdExe fileDeleteByMetadataIdCmdExe,
    FileDownloadByMetadataIdCmdExe fileDownloadByMetadataIdCmdExe,
    FileFindMetaByMetaIdCmdExe fileFindMetaByMetaIdCmdExe) {
    this.fileUploadCmdExe = fileUploadCmdExe;
    this.fileDeleteByMetadataIdCmdExe = fileDeleteByMetadataIdCmdExe;
    this.fileDownloadByMetadataIdCmdExe = fileDownloadByMetadataIdCmdExe;
    this.fileFindMetaByMetaIdCmdExe = fileFindMetaByMetaIdCmdExe;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Long upload(Long storageZoneId,
    MultipartFile multipartFile) {
    return fileUploadCmdExe.execute(storageZoneId, multipartFile);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteByMetadataId(Long metadataId) {
    fileDeleteByMetadataIdCmdExe.execute(metadataId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void downloadByMetadataId(Long metadataId, HttpServletResponse httpServletResponse) {
    fileDownloadByMetadataIdCmdExe.execute(metadataId, httpServletResponse);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FileFindMetaByMetaIdDTO findMetaByMetaId(Long metadataId) {
    return fileFindMetaByMetaIdCmdExe.execute(metadataId);
  }

  @Override
  public void deleteByMetadataId(@NonNull Int64Value request,
    @NonNull StreamObserver<Empty> responseObserver) {
    fileDeleteByMetadataIdCmdExe.execute(request.getValue());
    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }
}
