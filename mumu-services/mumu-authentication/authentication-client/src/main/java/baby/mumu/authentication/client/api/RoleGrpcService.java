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

import baby.mumu.authentication.client.api.grpc.PageOfRoleFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.RoleAddGrpcCmd;
import baby.mumu.authentication.client.api.grpc.RoleDeleteByIdGrpcCmd;
import baby.mumu.authentication.client.api.grpc.RoleFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.RoleServiceGrpc;
import baby.mumu.authentication.client.api.grpc.RoleServiceGrpc.RoleServiceBlockingStub;
import baby.mumu.authentication.client.api.grpc.RoleServiceGrpc.RoleServiceFutureStub;
import baby.mumu.authentication.client.api.grpc.RoleUpdateGrpcCmd;
import baby.mumu.basis.exception.MuMuException;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.security.AuthCallCredentials;
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
  public Empty add(RoleAddGrpcCmd roleAddGrpcCmd, AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return addFromGrpc(roleAddGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<Empty> syncAdd(RoleAddGrpcCmd roleAddGrpcCmd,
      AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return syncAddFromGrpc(roleAddGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Empty deleteById(RoleDeleteByIdGrpcCmd roleDeleteByIdGrpcCmd,
      AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return deleteByIdFromGrpc(roleDeleteByIdGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<Empty> syncDeleteById(
      RoleDeleteByIdGrpcCmd roleDeleteByIdGrpcCmd,
      AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return syncDeleteByIdFromGrpc(roleDeleteByIdGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Empty updateById(RoleUpdateGrpcCmd roleUpdateGrpcCmd,
      AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return updateByIdFromGrpc(roleUpdateGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<Empty> syncUpdateById(
      RoleUpdateGrpcCmd roleUpdateGrpcCmd,
      AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return syncUpdateByIdFromGrpc(roleUpdateGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public PageOfRoleFindAllGrpcCo findAll(RoleFindAllGrpcCmd roleFindAllGrpcCmd,
      AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return findAllFromGrpc(roleFindAllGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<PageOfRoleFindAllGrpcCo> syncFindAll(
      RoleFindAllGrpcCmd roleFindAllGrpcCmd,
      AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return syncFindAllFromGrpc(roleFindAllGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  private Empty addFromGrpc(RoleAddGrpcCmd roleAddGrpcCmd,
      AuthCallCredentials callCredentials) {
    RoleServiceBlockingStub roleServiceBlockingStub = RoleServiceGrpc.newBlockingStub(channel);
    return roleServiceBlockingStub.withCallCredentials(callCredentials).add(roleAddGrpcCmd);
  }

  private @NotNull ListenableFuture<Empty> syncAddFromGrpc(RoleAddGrpcCmd roleAddGrpcCmd,
      AuthCallCredentials callCredentials) {
    RoleServiceFutureStub roleServiceFutureStub = RoleServiceGrpc.newFutureStub(channel);
    return roleServiceFutureStub.withCallCredentials(callCredentials).add(roleAddGrpcCmd);
  }

  private Empty deleteByIdFromGrpc(RoleDeleteByIdGrpcCmd roleDeleteByIdGrpcCmd,
      AuthCallCredentials callCredentials) {
    RoleServiceBlockingStub roleServiceBlockingStub = RoleServiceGrpc.newBlockingStub(channel);
    return roleServiceBlockingStub.withCallCredentials(callCredentials)
        .deleteById(roleDeleteByIdGrpcCmd);
  }

  private @NotNull ListenableFuture<Empty> syncDeleteByIdFromGrpc(
      RoleDeleteByIdGrpcCmd roleDeleteByIdGrpcCmd,
      AuthCallCredentials callCredentials) {
    RoleServiceFutureStub roleServiceFutureStub = RoleServiceGrpc.newFutureStub(
        channel);
    return roleServiceFutureStub.withCallCredentials(callCredentials)
        .deleteById(roleDeleteByIdGrpcCmd);
  }

  private Empty updateByIdFromGrpc(RoleUpdateGrpcCmd roleUpdateGrpcCmd,
      AuthCallCredentials callCredentials) {
    RoleServiceBlockingStub roleServiceBlockingStub = RoleServiceGrpc.newBlockingStub(channel);
    return roleServiceBlockingStub.withCallCredentials(callCredentials)
        .updateById(roleUpdateGrpcCmd);
  }

  private @NotNull ListenableFuture<Empty> syncUpdateByIdFromGrpc(
      RoleUpdateGrpcCmd roleUpdateGrpcCmd,
      AuthCallCredentials callCredentials) {
    RoleServiceFutureStub roleServiceFutureStub = RoleServiceGrpc.newFutureStub(
        channel);
    return roleServiceFutureStub.withCallCredentials(callCredentials)
        .updateById(roleUpdateGrpcCmd);
  }

  private PageOfRoleFindAllGrpcCo findAllFromGrpc(
      RoleFindAllGrpcCmd roleFindAllGrpcCmd,
      AuthCallCredentials callCredentials) {
    RoleServiceBlockingStub roleServiceBlockingStub = RoleServiceGrpc.newBlockingStub(channel);
    return roleServiceBlockingStub.withCallCredentials(callCredentials)
        .findAll(roleFindAllGrpcCmd);
  }

  private @NotNull ListenableFuture<PageOfRoleFindAllGrpcCo> syncFindAllFromGrpc(
      RoleFindAllGrpcCmd roleFindAllGrpcCmd,
      AuthCallCredentials callCredentials) {
    RoleServiceFutureStub roleServiceFutureStub = RoleServiceGrpc.newFutureStub(
        channel);
    return roleServiceFutureStub.withCallCredentials(callCredentials)
        .findAll(roleFindAllGrpcCmd);
  }

}
