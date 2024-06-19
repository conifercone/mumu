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
package com.sky.centaur.file.application.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.sky.centaur.file.application.streamfile.executor.StreamFileDownloadCmdExe;
import com.sky.centaur.file.application.streamfile.executor.StreamFileSyncUploadCmdExe;
import com.sky.centaur.file.client.api.StreamFileService;
import com.sky.centaur.file.client.api.grpc.StreamFileDownloadGrpcCmd;
import com.sky.centaur.file.client.api.grpc.StreamFileDownloadGrpcCo;
import com.sky.centaur.file.client.api.grpc.StreamFileDownloadGrpcResult;
import com.sky.centaur.file.client.api.grpc.StreamFileServiceGrpc.StreamFileServiceImplBase;
import com.sky.centaur.file.client.dto.StreamFileDownloadCmd;
import com.sky.centaur.file.client.dto.StreamFileSyncUploadCmd;
import com.sky.centaur.file.client.dto.co.StreamFileDownloadCo;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import java.io.InputStream;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.GRpcService;
import org.lognet.springboot.grpc.recovery.GRpcRuntimeExceptionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * 流式文件接口实现类
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class})
@Observed(name = "StreamFileServiceImpl")
public class StreamFileServiceImpl extends StreamFileServiceImplBase implements StreamFileService {

  private final StreamFileSyncUploadCmdExe streamFileSyncUploadCmdExe;
  private final StreamFileDownloadCmdExe streamFileDownloadCmdExe;

  @Autowired
  public StreamFileServiceImpl(StreamFileSyncUploadCmdExe streamFileSyncUploadCmdExe,
      StreamFileDownloadCmdExe streamFileDownloadCmdExe) {
    this.streamFileSyncUploadCmdExe = streamFileSyncUploadCmdExe;
    this.streamFileDownloadCmdExe = streamFileDownloadCmdExe;
  }

  @Override
  public void syncUploadFile(StreamFileSyncUploadCmd streamFileSyncUploadCmd) {
    streamFileSyncUploadCmdExe.execute(streamFileSyncUploadCmd);
  }

  @Override
  public InputStream download(StreamFileDownloadCmd streamFileDownloadCmd) {
    return streamFileDownloadCmdExe.execute(streamFileDownloadCmd);
  }

  @NotNull
  private static StreamFileDownloadCo getStreamFileDownloadCo(
      @NotNull StreamFileDownloadGrpcCmd request) {
    StreamFileDownloadCo streamFileDownloadCo = new StreamFileDownloadCo();
    StreamFileDownloadGrpcCo streamFileDownloadGrpcCo = request.getStreamFileDownloadGrpcCo();
    streamFileDownloadCo.setName(
        streamFileDownloadGrpcCo.hasName() ? streamFileDownloadGrpcCo.getName().getValue() : null);
    streamFileDownloadCo.setRename(
        streamFileDownloadGrpcCo.hasRename() ? streamFileDownloadGrpcCo.getRename().getValue()
            : null);
    streamFileDownloadCo.setStorageAddress(
        streamFileDownloadGrpcCo.hasStorageAddress() ? streamFileDownloadGrpcCo.getStorageAddress()
            .getValue() : null);
    return streamFileDownloadCo;
  }

  @Override
  @PreAuthorize("hasAuthority('message.read')")
  public void download(StreamFileDownloadGrpcCmd request,
      @NotNull StreamObserver<StreamFileDownloadGrpcResult> responseObserver) {
    StreamFileDownloadCmd streamFileDownloadCmd = new StreamFileDownloadCmd();
    StreamFileDownloadCo streamFileDownloadCo = getStreamFileDownloadCo(
        request);
    streamFileDownloadCmd.setStreamFileDownloadCo(streamFileDownloadCo);
    try (InputStream inputStream = streamFileDownloadCmdExe.execute(streamFileDownloadCmd)) {
      ByteString byteString = ByteString.copyFrom(inputStream.readAllBytes());
      BytesValue bytesValue = BytesValue.newBuilder().setValue(byteString).build();
      responseObserver.onNext(
          StreamFileDownloadGrpcResult.newBuilder().setFileContent(bytesValue).build());
      responseObserver.onCompleted();
    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
  }
}
