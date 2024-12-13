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
package baby.mumu.authentication.client.api;

import static baby.mumu.basis.response.ResponseCode.GRPC_SERVICE_NOT_FOUND;

import baby.mumu.authentication.client.api.grpc.PageOfRoleFindAllGrpcDTO;
import baby.mumu.authentication.client.api.grpc.RoleFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.RoleFindByIdGrpcDTO;
import baby.mumu.authentication.client.api.grpc.RoleServiceGrpc;
import baby.mumu.authentication.client.api.grpc.RoleServiceGrpc.RoleServiceBlockingStub;
import baby.mumu.authentication.client.api.grpc.RoleServiceGrpc.RoleServiceFutureStub;
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
 * 角色对外提供grpc调用实例
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public class RoleGrpcService extends AuthenticationGrpcService implements DisposableBean {

  private ManagedChannel channel;

  public RoleGrpcService(
    DiscoveryClient discoveryClient,
    ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    super(discoveryClient, grpcClientInterceptorObjectProvider);
  }

  @Override
  public void destroy() {
    Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdown);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public PageOfRoleFindAllGrpcDTO findAll(RoleFindAllGrpcCmd roleFindAllGrpcCmd,
    CallCredentials callCredentials) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return findAllFromGrpc(roleFindAllGrpcCmd, callCredentials);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<PageOfRoleFindAllGrpcDTO> syncFindAll(
    RoleFindAllGrpcCmd roleFindAllGrpcCmd,
    CallCredentials callCredentials) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return syncFindAllFromGrpc(roleFindAllGrpcCmd, callCredentials);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "2.4.0")
  public RoleFindByIdGrpcDTO findById(Int64Value roleId,
    CallCredentials callCredentials) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return findByIdFromGrpc(roleId, callCredentials);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "2.4.0")
  public ListenableFuture<RoleFindByIdGrpcDTO> syncFindById(
    Int64Value roleId,
    CallCredentials callCredentials) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return syncFindByIdFromGrpc(roleId, callCredentials);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  private PageOfRoleFindAllGrpcDTO findAllFromGrpc(
    RoleFindAllGrpcCmd roleFindAllGrpcCmd,
    CallCredentials callCredentials) {
    RoleServiceBlockingStub roleServiceBlockingStub = RoleServiceGrpc.newBlockingStub(channel);
    return roleServiceBlockingStub.withCallCredentials(callCredentials)
      .findAll(roleFindAllGrpcCmd);
  }

  private @NotNull ListenableFuture<PageOfRoleFindAllGrpcDTO> syncFindAllFromGrpc(
    RoleFindAllGrpcCmd roleFindAllGrpcCmd,
    CallCredentials callCredentials) {
    RoleServiceFutureStub roleServiceFutureStub = RoleServiceGrpc.newFutureStub(
      channel);
    return roleServiceFutureStub.withCallCredentials(callCredentials)
      .findAll(roleFindAllGrpcCmd);
  }

  private RoleFindByIdGrpcDTO findByIdFromGrpc(
    Int64Value roleId,
    CallCredentials callCredentials) {
    RoleServiceBlockingStub roleServiceBlockingStub = RoleServiceGrpc.newBlockingStub(channel);
    return roleServiceBlockingStub.withCallCredentials(callCredentials)
      .findById(roleId);
  }

  private @NotNull ListenableFuture<RoleFindByIdGrpcDTO> syncFindByIdFromGrpc(
    Int64Value roleId,
    CallCredentials callCredentials) {
    RoleServiceFutureStub roleServiceFutureStub = RoleServiceGrpc.newFutureStub(
      channel);
    return roleServiceFutureStub.withCallCredentials(callCredentials)
      .findById(roleId);
  }


}
