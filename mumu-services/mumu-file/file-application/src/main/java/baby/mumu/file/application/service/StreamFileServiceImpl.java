/*
 * Copyright (c) 2024-2024, the original author or authors.
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
package baby.mumu.file.application.service;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import baby.mumu.file.application.streamfile.executor.StreamFileDownloadCmdExe;
import baby.mumu.file.application.streamfile.executor.StreamFileRemoveCmdExe;
import baby.mumu.file.application.streamfile.executor.StreamFileSyncUploadCmdExe;
import baby.mumu.file.client.api.StreamFileService;
import baby.mumu.file.client.api.grpc.StreamFileDownloadGrpcCmd;
import baby.mumu.file.client.api.grpc.StreamFileDownloadGrpcResult;
import baby.mumu.file.client.api.grpc.StreamFileRemoveGrpcCmd;
import baby.mumu.file.client.api.grpc.StreamFileServiceGrpc.StreamFileServiceImplBase;
import baby.mumu.file.client.dto.StreamFileDownloadCmd;
import baby.mumu.file.client.dto.StreamFileRemoveCmd;
import baby.mumu.file.client.dto.StreamFileSyncUploadCmd;
import baby.mumu.file.infrastructure.streamfile.convertor.StreamFileConvertor;
import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 流式文件接口实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class, ClientIpInterceptor.class})
@Observed(name = "StreamFileServiceImpl")
public class StreamFileServiceImpl extends StreamFileServiceImplBase implements StreamFileService {

  private final StreamFileSyncUploadCmdExe streamFileSyncUploadCmdExe;
  private final StreamFileDownloadCmdExe streamFileDownloadCmdExe;
  private final StreamFileRemoveCmdExe streamFileRemoveCmdExe;
  private final StreamFileConvertor streamFileConvertor;

  @Autowired
  public StreamFileServiceImpl(StreamFileSyncUploadCmdExe streamFileSyncUploadCmdExe,
    StreamFileDownloadCmdExe streamFileDownloadCmdExe,
    StreamFileRemoveCmdExe streamFileRemoveCmdExe, StreamFileConvertor streamFileConvertor) {
    this.streamFileSyncUploadCmdExe = streamFileSyncUploadCmdExe;
    this.streamFileDownloadCmdExe = streamFileDownloadCmdExe;
    this.streamFileRemoveCmdExe = streamFileRemoveCmdExe;
    this.streamFileConvertor = streamFileConvertor;
  }

  @Override
  public void syncUploadFile(StreamFileSyncUploadCmd streamFileSyncUploadCmd) {
    streamFileSyncUploadCmdExe.execute(streamFileSyncUploadCmd);
  }

  @Override
  public InputStream download(StreamFileDownloadCmd streamFileDownloadCmd) {
    return streamFileDownloadCmdExe.execute(streamFileDownloadCmd);
  }

  @Override
  public void removeFile(StreamFileRemoveCmd streamFileRemoveCmd) {
    streamFileRemoveCmdExe.execute(streamFileRemoveCmd);
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void download(StreamFileDownloadGrpcCmd request,
    @NotNull StreamObserver<StreamFileDownloadGrpcResult> responseObserver) {
    StreamFileDownloadCmd streamFileDownloadCmd = new StreamFileDownloadCmd();
    streamFileDownloadCmd.setName(
      request.hasName() ? request.getName().getValue() : null);
    streamFileDownloadCmd.setRename(
      request.hasRename() ? request.getRename().getValue()
        : null);
    streamFileDownloadCmd.setStorageAddress(
      request.hasStorageAddress() ? request.getStorageAddress()
        .getValue() : null);
    try (InputStream inputStream = streamFileDownloadCmdExe.execute(streamFileDownloadCmd)) {
      ByteString byteString = ByteString.copyFrom(inputStream.readAllBytes());
      BytesValue bytesValue = BytesValue.newBuilder().setValue(byteString).build();
      responseObserver.onNext(
        StreamFileDownloadGrpcResult.newBuilder().setFileContent(bytesValue).build());
      responseObserver.onCompleted();
    } catch (Exception e) {
      throw new MuMuException(ResponseCode.FILE_DOWNLOAD_FAILED);
    }
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void removeFile(StreamFileRemoveGrpcCmd request,
    @NotNull StreamObserver<Empty> responseObserver) {
    streamFileConvertor.toStreamFileRemoveCmd(request).ifPresentOrElse((streamFileRemoveCmd) -> {
      streamFileRemoveCmdExe.execute(streamFileRemoveCmd);
      responseObserver.onNext(Empty.newBuilder().build());
      responseObserver.onCompleted();
    }, () -> {
      responseObserver.onNext(Empty.newBuilder().build());
      responseObserver.onCompleted();
    });
  }
}
