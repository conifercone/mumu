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

import baby.mumu.authentication.client.api.grpc.AuthorityAddGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AuthorityDeleteByIdGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AuthorityFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AuthorityServiceGrpc;
import baby.mumu.authentication.client.api.grpc.AuthorityServiceGrpc.AuthorityServiceBlockingStub;
import baby.mumu.authentication.client.api.grpc.AuthorityServiceGrpc.AuthorityServiceFutureStub;
import baby.mumu.authentication.client.api.grpc.AuthorityUpdateGrpcCmd;
import baby.mumu.authentication.client.api.grpc.PageOfAuthorityFindAllGrpcCo;
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
 * 权限对外提供grpc调用实例
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public class AuthorityGrpcService extends AuthenticationGrpcService implements
    DisposableBean {

  private ManagedChannel channel;

  public AuthorityGrpcService(
      DiscoveryClient discoveryClient,
      ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    super(discoveryClient, grpcClientInterceptorObjectProvider);
  }

  @Override
  public void destroy() {
    Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdown);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Empty add(AuthorityAddGrpcCmd authorityAddGrpcCmd,
      AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return addFromGrpc(authorityAddGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<Empty> syncAdd(AuthorityAddGrpcCmd authorityAddGrpcCmd,
      AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return syncAddFromGrpc(authorityAddGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Empty deleteById(AuthorityDeleteByIdGrpcCmd authorityDeleteByIdGrpcCmd,
      AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return deleteByIdFromGrpc(authorityDeleteByIdGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<Empty> syncDeleteById(
      AuthorityDeleteByIdGrpcCmd authorityDeleteByIdGrpcCmd,
      AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return syncDeleteByIdFromGrpc(authorityDeleteByIdGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Empty updateById(AuthorityUpdateGrpcCmd authorityUpdateGrpcCmd,
      AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return updateByIdFromGrpc(authorityUpdateGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<Empty> syncUpdateById(
      AuthorityUpdateGrpcCmd authorityUpdateGrpcCmd,
      AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return syncUpdateByIdFromGrpc(authorityUpdateGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public PageOfAuthorityFindAllGrpcCo findAll(AuthorityFindAllGrpcCmd authorityFindAllGrpcCmd,
      AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return findAllFromGrpc(authorityFindAllGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<PageOfAuthorityFindAllGrpcCo> syncFindAll(
      AuthorityFindAllGrpcCmd authorityFindAllGrpcCmd,
      AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return syncFindAllFromGrpc(authorityFindAllGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  private Empty addFromGrpc(AuthorityAddGrpcCmd authorityAddGrpcCmd,
      AuthCallCredentials callCredentials) {
    AuthorityServiceBlockingStub authorityServiceBlockingStub = AuthorityServiceGrpc.newBlockingStub(
        channel);
    return authorityServiceBlockingStub.withCallCredentials(callCredentials)
        .add(authorityAddGrpcCmd);
  }

  private @NotNull ListenableFuture<Empty> syncAddFromGrpc(
      AuthorityAddGrpcCmd authorityAddGrpcCmd,
      AuthCallCredentials callCredentials) {
    AuthorityServiceFutureStub authorityServiceFutureStub = AuthorityServiceGrpc.newFutureStub(
        channel);
    return authorityServiceFutureStub.withCallCredentials(callCredentials)
        .add(authorityAddGrpcCmd);
  }

  private Empty deleteByIdFromGrpc(AuthorityDeleteByIdGrpcCmd authorityDeleteByIdGrpcCmd,
      AuthCallCredentials callCredentials) {
    AuthorityServiceBlockingStub authorityServiceBlockingStub = AuthorityServiceGrpc.newBlockingStub(
        channel);
    return authorityServiceBlockingStub.withCallCredentials(callCredentials)
        .deleteById(authorityDeleteByIdGrpcCmd);
  }

  private @NotNull ListenableFuture<Empty> syncDeleteByIdFromGrpc(
      AuthorityDeleteByIdGrpcCmd authorityDeleteByIdGrpcCmd,
      AuthCallCredentials callCredentials) {
    AuthorityServiceFutureStub authorityServiceFutureStub = AuthorityServiceGrpc.newFutureStub(
        channel);
    return authorityServiceFutureStub.withCallCredentials(callCredentials)
        .deleteById(authorityDeleteByIdGrpcCmd);
  }

  private Empty updateByIdFromGrpc(AuthorityUpdateGrpcCmd authorityUpdateGrpcCmd,
      AuthCallCredentials callCredentials) {
    AuthorityServiceBlockingStub authorityServiceBlockingStub = AuthorityServiceGrpc.newBlockingStub(
        channel);
    return authorityServiceBlockingStub.withCallCredentials(callCredentials)
        .updateById(authorityUpdateGrpcCmd);
  }

  private @NotNull ListenableFuture<Empty> syncUpdateByIdFromGrpc(
      AuthorityUpdateGrpcCmd authorityUpdateGrpcCmd,
      AuthCallCredentials callCredentials) {
    AuthorityServiceFutureStub authorityServiceFutureStub = AuthorityServiceGrpc.newFutureStub(
        channel);
    return authorityServiceFutureStub.withCallCredentials(callCredentials)
        .updateById(authorityUpdateGrpcCmd);
  }

  private PageOfAuthorityFindAllGrpcCo findAllFromGrpc(
      AuthorityFindAllGrpcCmd authorityFindAllGrpcCmd,
      AuthCallCredentials callCredentials) {
    AuthorityServiceBlockingStub authorityServiceBlockingStub = AuthorityServiceGrpc.newBlockingStub(
        channel);
    return authorityServiceBlockingStub.withCallCredentials(callCredentials)
        .findAll(authorityFindAllGrpcCmd);
  }

  private @NotNull ListenableFuture<PageOfAuthorityFindAllGrpcCo> syncFindAllFromGrpc(
      AuthorityFindAllGrpcCmd authorityFindAllGrpcCmd,
      AuthCallCredentials callCredentials) {
    AuthorityServiceFutureStub authorityServiceFutureStub = AuthorityServiceGrpc.newFutureStub(
        channel);
    return authorityServiceFutureStub.withCallCredentials(callCredentials)
        .findAll(authorityFindAllGrpcCmd);
  }

}
