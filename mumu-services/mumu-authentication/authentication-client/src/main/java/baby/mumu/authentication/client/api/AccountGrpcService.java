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

import baby.mumu.authentication.client.api.grpc.AccountCurrentLoginGrpcDTO;
import baby.mumu.authentication.client.api.grpc.AccountServiceGrpc;
import baby.mumu.authentication.client.api.grpc.AccountServiceGrpc.AccountServiceBlockingStub;
import baby.mumu.authentication.client.api.grpc.AccountServiceGrpc.AccountServiceFutureStub;
import baby.mumu.basis.exception.MuMuException;
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

  @API(status = Status.STABLE, since = "2.2.0")
  public AccountCurrentLoginGrpcDTO queryCurrentLoginAccount(
    CallCredentials authCallCredentials) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return queryCurrentLoginAccountFromGrpc(authCallCredentials);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public ListenableFuture<AccountCurrentLoginGrpcDTO> syncQueryCurrentLoginAccount(
    CallCredentials authCallCredentials) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return syncQueryCurrentLoginAccountFromGrpc(authCallCredentials);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  private AccountCurrentLoginGrpcDTO queryCurrentLoginAccountFromGrpc(
    CallCredentials authCallCredentials) {
    AccountServiceBlockingStub accountServiceBlockingStub = AccountServiceGrpc.newBlockingStub(
      channel);
    return accountServiceBlockingStub.withCallCredentials(authCallCredentials)
      .queryCurrentLoginAccount(Empty.getDefaultInstance());
  }

  private @NotNull ListenableFuture<AccountCurrentLoginGrpcDTO> syncQueryCurrentLoginAccountFromGrpc(
    CallCredentials authCallCredentials) {
    AccountServiceFutureStub accountServiceFutureStub = AccountServiceGrpc.newFutureStub(
      channel);
    return accountServiceFutureStub.withCallCredentials(authCallCredentials)
      .queryCurrentLoginAccount(Empty.getDefaultInstance());
  }

}
