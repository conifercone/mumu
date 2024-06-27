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
package com.sky.centaur.file.client.api;

import static com.sky.centaur.basis.response.ResultCode.GRPC_SERVICE_NOT_FOUND;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.file.client.api.grpc.StreamFileDownloadGrpcCmd;
import com.sky.centaur.file.client.api.grpc.StreamFileDownloadGrpcResult;
import com.sky.centaur.file.client.api.grpc.StreamFileRemoveGrpcCmd;
import com.sky.centaur.file.client.api.grpc.StreamFileServiceGrpc;
import com.sky.centaur.file.client.api.grpc.StreamFileServiceGrpc.StreamFileServiceFutureStub;
import io.grpc.ManagedChannel;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.security.AuthCallCredentials;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * 流文件对外提供grpc调用实例
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
public class StreamFileGrpcService extends FileGrpcService implements
    DisposableBean {

  private ManagedChannel channel;

  public StreamFileGrpcService(
      DiscoveryClient discoveryClient,
      ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    super(discoveryClient, grpcClientInterceptorObjectProvider);
  }

  @Override
  public void destroy() {
    if (channel != null) {
      channel.shutdown();
    }
  }

  @API(status = Status.STABLE, since = "1.0.1")
  public StreamFileDownloadGrpcResult download(StreamFileDownloadGrpcCmd streamFileDownloadGrpcCmd,
      AuthCallCredentials callCredentials)
      throws ExecutionException, InterruptedException, TimeoutException {
    if (channel != null) {
      return downloadFromGrpc(streamFileDownloadGrpcCmd, callCredentials);
    } else {
      Optional<ManagedChannel> managedChannelUsePlaintext = getManagedChannelUsePlaintext();
      if (managedChannelUsePlaintext.isPresent()) {
        channel = managedChannelUsePlaintext.get();
        return downloadFromGrpc(streamFileDownloadGrpcCmd, callCredentials);
      } else {
        throw new CentaurException(GRPC_SERVICE_NOT_FOUND);
      }
    }
  }

  @API(status = Status.STABLE, since = "1.0.1")
  public ListenableFuture<StreamFileDownloadGrpcResult> syncDownload(
      StreamFileDownloadGrpcCmd streamFileDownloadGrpcCmd,
      AuthCallCredentials callCredentials) {
    if (channel != null) {
      return syncDownloadFromGrpc(streamFileDownloadGrpcCmd, callCredentials);
    } else {
      return getManagedChannelUsePlaintext().map(managedChannel -> {
        channel = managedChannel;
        return syncDownloadFromGrpc(streamFileDownloadGrpcCmd, callCredentials);
      }).orElse(null);
    }
  }

  @API(status = Status.STABLE, since = "1.0.1")
  public Empty removeFile(StreamFileRemoveGrpcCmd streamFileRemoveGrpcCmd,
      AuthCallCredentials callCredentials)
      throws ExecutionException, InterruptedException, TimeoutException {
    if (channel != null) {
      return removeFileFromGrpc(streamFileRemoveGrpcCmd, callCredentials);
    } else {
      Optional<ManagedChannel> managedChannelUsePlaintext = getManagedChannelUsePlaintext();
      if (managedChannelUsePlaintext.isPresent()) {
        channel = managedChannelUsePlaintext.get();
        return removeFileFromGrpc(streamFileRemoveGrpcCmd, callCredentials);
      } else {
        throw new CentaurException(GRPC_SERVICE_NOT_FOUND);
      }
    }
  }

  @API(status = Status.STABLE, since = "1.0.1")
  public ListenableFuture<Empty> syncRemoveFile(
      StreamFileRemoveGrpcCmd streamFileRemoveGrpcCmd,
      AuthCallCredentials callCredentials) {
    if (channel != null) {
      return syncRemoveFileFromGrpc(streamFileRemoveGrpcCmd, callCredentials);
    } else {
      return getManagedChannelUsePlaintext().map(managedChannel -> {
        channel = managedChannel;
        return syncRemoveFileFromGrpc(streamFileRemoveGrpcCmd, callCredentials);
      }).orElse(null);
    }
  }


  private StreamFileDownloadGrpcResult downloadFromGrpc(
      StreamFileDownloadGrpcCmd streamFileDownloadGrpcCmd,
      AuthCallCredentials callCredentials)
      throws ExecutionException, InterruptedException, TimeoutException {
    StreamFileServiceFutureStub streamFileServiceFutureStub = StreamFileServiceGrpc.newFutureStub(
        channel);
    return streamFileServiceFutureStub.withCallCredentials(callCredentials)
        .download(streamFileDownloadGrpcCmd).get(3, TimeUnit.SECONDS);
  }

  private @NotNull ListenableFuture<StreamFileDownloadGrpcResult> syncDownloadFromGrpc(
      StreamFileDownloadGrpcCmd streamFileDownloadGrpcCmd,
      AuthCallCredentials callCredentials) {
    StreamFileServiceFutureStub streamFileServiceFutureStub = StreamFileServiceGrpc.newFutureStub(
        channel);
    return streamFileServiceFutureStub.withCallCredentials(callCredentials)
        .download(streamFileDownloadGrpcCmd);
  }

  private Empty removeFileFromGrpc(
      StreamFileRemoveGrpcCmd streamFileRemoveGrpcCmd,
      AuthCallCredentials callCredentials)
      throws ExecutionException, InterruptedException, TimeoutException {
    StreamFileServiceFutureStub streamFileServiceFutureStub = StreamFileServiceGrpc.newFutureStub(
        channel);
    return streamFileServiceFutureStub.withCallCredentials(callCredentials)
        .removeFile(streamFileRemoveGrpcCmd).get(3, TimeUnit.SECONDS);
  }

  private @NotNull ListenableFuture<Empty> syncRemoveFileFromGrpc(
      StreamFileRemoveGrpcCmd streamFileRemoveGrpcCmd,
      AuthCallCredentials callCredentials) {
    StreamFileServiceFutureStub streamFileServiceFutureStub = StreamFileServiceGrpc.newFutureStub(
        channel);
    return streamFileServiceFutureStub.withCallCredentials(callCredentials)
        .removeFile(streamFileRemoveGrpcCmd);
  }

}
