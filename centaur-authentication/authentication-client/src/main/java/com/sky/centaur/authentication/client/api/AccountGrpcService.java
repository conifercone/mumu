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
package com.sky.centaur.authentication.client.api;

import com.google.common.util.concurrent.ListenableFuture;
import com.sky.centaur.authentication.client.api.grpc.AccountRegisterGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AccountRegisterGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AccountServiceGrpc;
import com.sky.centaur.authentication.client.api.grpc.AccountServiceGrpc.AccountServiceFutureStub;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import io.grpc.ManagedChannel;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * 账户对外提供grpc调用实例
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public class AccountGrpcService extends AuthenticationGrpcService implements DisposableBean {

  private ManagedChannel channel;

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountGrpcService.class);

  public AccountGrpcService(
      DiscoveryClient consulDiscoveryClient,
      ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    super(consulDiscoveryClient, grpcClientInterceptorObjectProvider);
  }

  @Override
  public void destroy() {
    if (channel != null) {
      channel.shutdown();
    }
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public AccountRegisterGrpcCo register(AccountRegisterGrpcCmd accountRegisterGrpcCmd)
      throws ExecutionException, InterruptedException, TimeoutException {
    if (channel == null) {
      Optional<ManagedChannel> managedChannelUsePlaintext = getManagedChannelUsePlaintext();
      if (managedChannelUsePlaintext.isPresent()) {
        channel = managedChannelUsePlaintext.get();
        return registerFromGrpc(accountRegisterGrpcCmd);
      } else {
        LOGGER.error(ResultCode.GRPC_SERVICE_NOT_FOUND.getResultMsg());
        throw new CentaurException(ResultCode.GRPC_SERVICE_NOT_FOUND);
      }
    } else {
      return registerFromGrpc(accountRegisterGrpcCmd);
    }
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<AccountRegisterGrpcCo> syncRegister(
      AccountRegisterGrpcCmd accountRegisterGrpcCmd) {
    if (channel == null) {
      return getManagedChannelUsePlaintext().map(managedChannel -> {
        channel = managedChannel;
        return syncRegisterFromGrpc(accountRegisterGrpcCmd);
      }).orElse(null);
    } else {
      return syncRegisterFromGrpc(accountRegisterGrpcCmd);
    }
  }

  private AccountRegisterGrpcCo registerFromGrpc(
      AccountRegisterGrpcCmd accountRegisterGrpcCmd)
      throws ExecutionException, InterruptedException, TimeoutException {
    AccountServiceFutureStub accountServiceFutureStub = AccountServiceGrpc.newFutureStub(
        channel);
    return accountServiceFutureStub.register(accountRegisterGrpcCmd).get(3, TimeUnit.SECONDS);
  }

  private @NotNull ListenableFuture<AccountRegisterGrpcCo> syncRegisterFromGrpc(
      AccountRegisterGrpcCmd accountRegisterGrpcCmd) {
    AccountServiceFutureStub accountServiceFutureStub = AccountServiceGrpc.newFutureStub(
        channel);
    return accountServiceFutureStub.register(accountRegisterGrpcCmd);
  }

}
