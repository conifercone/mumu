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

import static baby.mumu.basis.response.ResultCode.GRPC_SERVICE_NOT_FOUND;

import baby.mumu.authentication.client.api.grpc.AccountDisableGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AccountRegisterGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AccountServiceGrpc;
import baby.mumu.authentication.client.api.grpc.AccountServiceGrpc.AccountServiceBlockingStub;
import baby.mumu.authentication.client.api.grpc.AccountServiceGrpc.AccountServiceFutureStub;
import baby.mumu.authentication.client.api.grpc.AccountUpdateByIdGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AccountUpdateRoleGrpcCmd;
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
 * 账户对外提供grpc调用实例
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public class AccountGrpcService extends AuthenticationGrpcService implements DisposableBean {

  private ManagedChannel channel;

  public AccountGrpcService(
      DiscoveryClient discoveryClient,
      ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    super(discoveryClient, grpcClientInterceptorObjectProvider);
  }

  @Override
  public void destroy() {
    Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdown);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Empty register(AccountRegisterGrpcCmd accountRegisterGrpcCmd) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return registerFromGrpc(accountRegisterGrpcCmd);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<Empty> syncRegister(
      AccountRegisterGrpcCmd accountRegisterGrpcCmd) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return syncRegisterFromGrpc(accountRegisterGrpcCmd);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Empty updateById(AccountUpdateByIdGrpcCmd accountUpdateByIdGrpcCmd,
      AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return updateByIdFromGrpc(accountUpdateByIdGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<Empty> syncUpdateById(
      AccountUpdateByIdGrpcCmd accountUpdateByIdGrpcCmd, AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return syncUpdateByIdFromGrpc(accountUpdateByIdGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Empty updateRoleById(AccountUpdateRoleGrpcCmd accountUpdateRoleGrpcCmd,
      AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return updateRoleByIdFromGrpc(accountUpdateRoleGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<Empty> syncUpdateRoleById(
      AccountUpdateRoleGrpcCmd accountUpdateRoleGrpcCmd, AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return syncUpdateRoleByIdFromGrpc(accountUpdateRoleGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Empty disable(AccountDisableGrpcCmd accountDisableGrpcCmd,
      AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return disableFromGrpc(accountDisableGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<Empty> syncDisable(
      AccountDisableGrpcCmd accountDisableGrpcCmd, AuthCallCredentials callCredentials) {
    return Optional.ofNullable(channel)
        .or(this::getManagedChannelUsePlaintext)
        .map(ch -> {
          channel = ch;
          return syncDisableFromGrpc(accountDisableGrpcCmd, callCredentials);
        })
        .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  private Empty registerFromGrpc(
      AccountRegisterGrpcCmd accountRegisterGrpcCmd) {
    AccountServiceBlockingStub accountServiceBlockingStub = AccountServiceGrpc.newBlockingStub(
        channel);
    return accountServiceBlockingStub.register(accountRegisterGrpcCmd);
  }

  private @NotNull ListenableFuture<Empty> syncRegisterFromGrpc(
      AccountRegisterGrpcCmd accountRegisterGrpcCmd) {
    AccountServiceFutureStub accountServiceFutureStub = AccountServiceGrpc.newFutureStub(
        channel);
    return accountServiceFutureStub.register(accountRegisterGrpcCmd);
  }


  private Empty updateByIdFromGrpc(
      AccountUpdateByIdGrpcCmd accountUpdateByIdGrpcCmd, AuthCallCredentials callCredentials) {
    AccountServiceBlockingStub accountServiceBlockingStub = AccountServiceGrpc.newBlockingStub(
        channel);
    return accountServiceBlockingStub.withCallCredentials(callCredentials)
        .updateById(accountUpdateByIdGrpcCmd);
  }

  private @NotNull ListenableFuture<Empty> syncUpdateByIdFromGrpc(
      AccountUpdateByIdGrpcCmd accountUpdateByIdGrpcCmd, AuthCallCredentials callCredentials) {
    AccountServiceFutureStub accountServiceFutureStub = AccountServiceGrpc.newFutureStub(
        channel);
    return accountServiceFutureStub.withCallCredentials(callCredentials)
        .updateById(accountUpdateByIdGrpcCmd);
  }

  private Empty updateRoleByIdFromGrpc(
      AccountUpdateRoleGrpcCmd accountUpdateRoleGrpcCmd, AuthCallCredentials callCredentials) {
    AccountServiceBlockingStub accountServiceBlockingStub = AccountServiceGrpc.newBlockingStub(
        channel);
    return accountServiceBlockingStub.withCallCredentials(callCredentials)
        .updateRoleById(accountUpdateRoleGrpcCmd);
  }

  private @NotNull ListenableFuture<Empty> syncUpdateRoleByIdFromGrpc(
      AccountUpdateRoleGrpcCmd accountUpdateRoleGrpcCmd, AuthCallCredentials callCredentials) {
    AccountServiceFutureStub accountServiceFutureStub = AccountServiceGrpc.newFutureStub(
        channel);
    return accountServiceFutureStub.withCallCredentials(callCredentials)
        .updateRoleById(accountUpdateRoleGrpcCmd);
  }

  private Empty disableFromGrpc(
      AccountDisableGrpcCmd accountDisableGrpcCmd, AuthCallCredentials callCredentials) {
    AccountServiceBlockingStub accountServiceBlockingStub = AccountServiceGrpc.newBlockingStub(
        channel);
    return accountServiceBlockingStub.withCallCredentials(callCredentials)
        .disable(accountDisableGrpcCmd);
  }

  private @NotNull ListenableFuture<Empty> syncDisableFromGrpc(
      AccountDisableGrpcCmd accountDisableGrpcCmd, AuthCallCredentials callCredentials) {
    AccountServiceFutureStub accountServiceFutureStub = AccountServiceGrpc.newFutureStub(
        channel);
    return accountServiceFutureStub.withCallCredentials(callCredentials)
        .disable(accountDisableGrpcCmd);
  }

}
