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

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import baby.mumu.storage.application.file.executor.FileDownloadCmdExe;
import baby.mumu.storage.application.file.executor.FileRemoveCmdExe;
import baby.mumu.storage.application.file.executor.FileSyncUploadCmdExe;
import baby.mumu.storage.client.api.FileService;
import baby.mumu.storage.client.api.grpc.FileDownloadGrpcCmd;
import baby.mumu.storage.client.api.grpc.FileDownloadGrpcResult;
import baby.mumu.storage.client.api.grpc.FileRemoveGrpcCmd;
import baby.mumu.storage.client.api.grpc.FileServiceGrpc.FileServiceImplBase;
import baby.mumu.storage.client.cmds.FileDownloadCmd;
import baby.mumu.storage.client.cmds.FileRemoveCmd;
import baby.mumu.storage.client.cmds.FileSyncUploadCmd;
import baby.mumu.storage.infrastructure.file.convertor.FileConvertor;
import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import java.io.InputStream;
import net.devh.boot.grpc.server.service.GrpcService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 文件接口实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Service
@GrpcService(interceptors = {ObservationGrpcServerInterceptor.class, ClientIpInterceptor.class})
@Observed(name = "FileServiceImpl")
public class FileServiceImpl extends FileServiceImplBase implements FileService {

  private final FileSyncUploadCmdExe fileSyncUploadCmdExe;
  private final FileDownloadCmdExe fileDownloadCmdExe;
  private final FileRemoveCmdExe fileRemoveCmdExe;
  private final FileConvertor fileConvertor;

  @Autowired
  public FileServiceImpl(FileSyncUploadCmdExe fileSyncUploadCmdExe,
    FileDownloadCmdExe fileDownloadCmdExe,
    FileRemoveCmdExe fileRemoveCmdExe, FileConvertor fileConvertor) {
    this.fileSyncUploadCmdExe = fileSyncUploadCmdExe;
    this.fileDownloadCmdExe = fileDownloadCmdExe;
    this.fileRemoveCmdExe = fileRemoveCmdExe;
    this.fileConvertor = fileConvertor;
  }

  @Override
  public void syncUploadFile(FileSyncUploadCmd fileSyncUploadCmd) {
    fileSyncUploadCmdExe.execute(fileSyncUploadCmd);
  }

  @Override
  public InputStream download(FileDownloadCmd fileDownloadCmd) {
    return fileDownloadCmdExe.execute(fileDownloadCmd);
  }

  @Override
  public void removeFile(FileRemoveCmd fileRemoveCmd) {
    fileRemoveCmdExe.execute(fileRemoveCmd);
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void download(FileDownloadGrpcCmd request,
    @NotNull StreamObserver<FileDownloadGrpcResult> responseObserver) {
    FileDownloadCmd fileDownloadCmd = new FileDownloadCmd();
    fileDownloadCmd.setName(request.getName());
    fileDownloadCmd.setRename(request.getRename());
    fileDownloadCmd.setStorageAddress(request.getStorageAddress());
    try (InputStream inputStream = fileDownloadCmdExe.execute(fileDownloadCmd)) {
      ByteString byteString = ByteString.copyFrom(inputStream.readAllBytes());
      BytesValue bytesValue = BytesValue.newBuilder().setValue(byteString).build();
      responseObserver.onNext(
        FileDownloadGrpcResult.newBuilder().setFileContent(bytesValue).build());
      responseObserver.onCompleted();
    } catch (Exception e) {
      throw new MuMuException(ResponseCode.FILE_DOWNLOAD_FAILED);
    }
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void removeFile(FileRemoveGrpcCmd request,
    @NotNull StreamObserver<Empty> responseObserver) {
    fileConvertor.toFileRemoveCmd(request).ifPresentOrElse((fileRemoveCmd) -> {
      fileRemoveCmdExe.execute(fileRemoveCmd);
      responseObserver.onNext(Empty.newBuilder().build());
      responseObserver.onCompleted();
    }, () -> {
      responseObserver.onNext(Empty.newBuilder().build());
      responseObserver.onCompleted();
    });
  }
}
