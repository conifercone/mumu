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
import baby.mumu.storage.client.api.grpc.FileServiceGrpc;
import baby.mumu.storage.client.api.grpc.FileServiceGrpc.FileServiceBlockingStub;
import baby.mumu.storage.client.api.grpc.FileServiceGrpc.FileServiceFutureStub;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * 文件对外提供grpc调用实例
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.14.0
 */
public class FileGrpcService extends StorageGrpcService implements DisposableBean {

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


  @SuppressWarnings("unused")
  @API(status = Status.STABLE, since = "2.14.0")
  public void deleteByMetadataId(Int64Value metadataId,
    CallCredentials callCredentials) {
    Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .ifPresentOrElse(ch -> {
        channel = ch;
        deleteByMetadataIdFromGrpc(metadataId, callCredentials);
      }, () -> {
        throw new MuMuException(GRPC_SERVICE_NOT_FOUND);
      });
  }

  @SuppressWarnings("UnusedReturnValue")
  @API(status = Status.STABLE, since = "2.14.0")
  public ListenableFuture<Empty> syncDeleteByMetadataId(
    Int64Value roleId,
    CallCredentials callCredentials) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return syncDeleteByMetadataIdFromGrpc(roleId, callCredentials);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }


  private void deleteByMetadataIdFromGrpc(
    Int64Value metadataId,
    CallCredentials callCredentials) {
    FileServiceBlockingStub fileServiceBlockingStub = FileServiceGrpc.newBlockingStub(channel);
    // noinspection ResultOfMethodCallIgnored
    fileServiceBlockingStub.withCallCredentials(callCredentials)
      .deleteByMetadataId(metadataId);
  }

  private @NonNull ListenableFuture<Empty> syncDeleteByMetadataIdFromGrpc(
    Int64Value metadataId,
    CallCredentials callCredentials) {
    FileServiceFutureStub fileServiceFutureStub = FileServiceGrpc.newFutureStub(
      channel);
    return fileServiceFutureStub.withCallCredentials(callCredentials)
      .deleteByMetadataId(metadataId);
  }

}
