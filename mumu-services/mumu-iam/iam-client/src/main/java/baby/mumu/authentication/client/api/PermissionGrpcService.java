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

package baby.mumu.authentication.client.api;

import static baby.mumu.basis.response.ResponseCode.GRPC_SERVICE_NOT_FOUND;

import baby.mumu.authentication.client.api.grpc.PageOfPermissionFindAllGrpcDTO;
import baby.mumu.authentication.client.api.grpc.PermissionFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.PermissionFindByIdGrpcDTO;
import baby.mumu.authentication.client.api.grpc.PermissionServiceGrpc;
import baby.mumu.authentication.client.api.grpc.PermissionServiceGrpc.PermissionServiceBlockingStub;
import baby.mumu.authentication.client.api.grpc.PermissionServiceGrpc.PermissionServiceFutureStub;
import baby.mumu.basis.exception.MuMuException;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Int64Value;
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
 * 权限对外提供grpc调用实例
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
public class PermissionGrpcService extends AuthenticationGrpcService implements
  DisposableBean {

  private ManagedChannel channel;

  public PermissionGrpcService(
    DiscoveryClient discoveryClient,
    ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    super(discoveryClient, grpcClientInterceptorObjectProvider);
  }

  @Override
  public void destroy() {
    Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdown);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public PageOfPermissionFindAllGrpcDTO findAll(PermissionFindAllGrpcCmd permissionFindAllGrpcCmd,
    CallCredentials callCredentials) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return findAllFromGrpc(permissionFindAllGrpcCmd, callCredentials);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<PageOfPermissionFindAllGrpcDTO> syncFindAll(
    PermissionFindAllGrpcCmd permissionFindAllGrpcCmd,
    CallCredentials callCredentials) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return syncFindAllFromGrpc(permissionFindAllGrpcCmd, callCredentials);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "2.3.0")
  public PermissionFindByIdGrpcDTO findById(Int64Value id,
    CallCredentials callCredentials) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return findByIdFromGrpc(id, callCredentials);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "2.3.0")
  public ListenableFuture<PermissionFindByIdGrpcDTO> syncFindById(
    Int64Value id,
    CallCredentials callCredentials) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return syncFindByIdFromGrpc(id, callCredentials);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  private PageOfPermissionFindAllGrpcDTO findAllFromGrpc(
    PermissionFindAllGrpcCmd permissionFindAllGrpcCmd,
    CallCredentials callCredentials) {
    PermissionServiceBlockingStub permissionServiceBlockingStub = PermissionServiceGrpc.newBlockingStub(
      channel);
    return permissionServiceBlockingStub.withCallCredentials(callCredentials)
      .findAll(permissionFindAllGrpcCmd);
  }

  private @NotNull ListenableFuture<PageOfPermissionFindAllGrpcDTO> syncFindAllFromGrpc(
    PermissionFindAllGrpcCmd permissionFindAllGrpcCmd,
    CallCredentials callCredentials) {
    PermissionServiceFutureStub permissionServiceFutureStub = PermissionServiceGrpc.newFutureStub(
      channel);
    return permissionServiceFutureStub.withCallCredentials(callCredentials)
      .findAll(permissionFindAllGrpcCmd);
  }

  private PermissionFindByIdGrpcDTO findByIdFromGrpc(
    Int64Value id,
    CallCredentials callCredentials) {
    PermissionServiceBlockingStub permissionServiceBlockingStub = PermissionServiceGrpc.newBlockingStub(
      channel);
    return permissionServiceBlockingStub.withCallCredentials(callCredentials)
      .findById(id);
  }

  private @NotNull ListenableFuture<PermissionFindByIdGrpcDTO> syncFindByIdFromGrpc(
    Int64Value id,
    CallCredentials callCredentials) {
    PermissionServiceFutureStub permissionServiceFutureStub = PermissionServiceGrpc.newFutureStub(
      channel);
    return permissionServiceFutureStub.withCallCredentials(callCredentials)
      .findById(id);
  }
}
