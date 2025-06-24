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

package baby.mumu.storage.client.api;

import static baby.mumu.basis.response.ResponseCode.GRPC_SERVICE_NOT_FOUND;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.storage.client.api.grpc.FileDownloadGrpcCmd;
import baby.mumu.storage.client.api.grpc.FileDownloadGrpcResult;
import baby.mumu.storage.client.api.grpc.FileRemoveGrpcCmd;
import baby.mumu.storage.client.api.grpc.FileServiceGrpc;
import baby.mumu.storage.client.api.grpc.FileServiceGrpc.FileServiceBlockingStub;
import baby.mumu.storage.client.api.grpc.FileServiceGrpc.FileServiceFutureStub;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * 流文件对外提供grpc调用实例
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
public class FileGrpcService extends StorageGrpcService implements
  DisposableBean {

  private ManagedChannel channel;

  public FileGrpcService(
    DiscoveryClient discoveryClient,
    ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    super(discoveryClient, grpcClientInterceptorObjectProvider);
  }

  @Override
  public void destroy() {
    Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdown);
  }

  @API(status = Status.STABLE, since = "1.0.1")
  public FileDownloadGrpcResult download(FileDownloadGrpcCmd fileDownloadGrpcCmd,
    CallCredentials callCredentials) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return downloadFromGrpc(fileDownloadGrpcCmd, callCredentials);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.1")
  public ListenableFuture<FileDownloadGrpcResult> syncDownload(
    FileDownloadGrpcCmd fileDownloadGrpcCmd,
    CallCredentials callCredentials) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return syncDownloadFromGrpc(fileDownloadGrpcCmd, callCredentials);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.1")
  public Empty removeFile(FileRemoveGrpcCmd fileRemoveGrpcCmd,
    CallCredentials callCredentials) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return removeFileFromGrpc(fileRemoveGrpcCmd, callCredentials);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.1")
  public ListenableFuture<Empty> syncRemoveFile(
    FileRemoveGrpcCmd fileRemoveGrpcCmd,
    CallCredentials callCredentials) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return syncRemoveFileFromGrpc(fileRemoveGrpcCmd, callCredentials);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }


  private FileDownloadGrpcResult downloadFromGrpc(
    FileDownloadGrpcCmd fileDownloadGrpcCmd,
    CallCredentials callCredentials) {
    FileServiceBlockingStub fileServiceBlockingStub = FileServiceGrpc.newBlockingStub(
      channel);
    return fileServiceBlockingStub.withCallCredentials(callCredentials)
      .download(fileDownloadGrpcCmd);
  }

  private @NotNull ListenableFuture<FileDownloadGrpcResult> syncDownloadFromGrpc(
    FileDownloadGrpcCmd fileDownloadGrpcCmd,
    CallCredentials callCredentials) {
    FileServiceFutureStub fileServiceFutureStub = FileServiceGrpc.newFutureStub(
      channel);
    return fileServiceFutureStub.withCallCredentials(callCredentials)
      .download(fileDownloadGrpcCmd);
  }

  private Empty removeFileFromGrpc(
    FileRemoveGrpcCmd fileRemoveGrpcCmd,
    CallCredentials callCredentials) {
    FileServiceBlockingStub fileServiceBlockingStub = FileServiceGrpc.newBlockingStub(
      channel);
    return fileServiceBlockingStub.withCallCredentials(callCredentials)
      .removeFile(fileRemoveGrpcCmd);
  }

  private @NotNull ListenableFuture<Empty> syncRemoveFileFromGrpc(
    FileRemoveGrpcCmd fileRemoveGrpcCmd,
    CallCredentials callCredentials) {
    FileServiceFutureStub fileServiceFutureStub = FileServiceGrpc.newFutureStub(
      channel);
    return fileServiceFutureStub.withCallCredentials(callCredentials)
      .removeFile(fileRemoveGrpcCmd);
  }

}
