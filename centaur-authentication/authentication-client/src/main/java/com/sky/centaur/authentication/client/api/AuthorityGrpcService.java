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

import static com.sky.centaur.basis.response.ResultCode.GRPC_SERVICE_NOT_FOUND;

import com.google.common.util.concurrent.ListenableFuture;
import com.sky.centaur.authentication.client.api.grpc.AuthorityAddGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AuthorityAddGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AuthorityServiceGrpc;
import com.sky.centaur.authentication.client.api.grpc.AuthorityServiceGrpc.AuthorityServiceFutureStub;
import com.sky.centaur.basis.exception.CentaurException;
import io.grpc.ManagedChannel;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.security.AuthCallCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * 权限对外提供grpc调用实例
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public class AuthorityGrpcService extends AuthenticationGrpcService implements DisposableBean {

  private ManagedChannel channel;

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityGrpcService.class);

  public AuthorityGrpcService(
      DiscoveryClient consulDiscoveryClient) {
    super(consulDiscoveryClient);
  }

  @Override
  public void destroy() {
    if (channel != null) {
      channel.shutdown();
    }
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public AuthorityAddGrpcCo add(AuthorityAddGrpcCmd authorityAddGrpcCmd,
      AuthCallCredentials callCredentials)
      throws ExecutionException, InterruptedException, TimeoutException {
    if (channel == null) {
      Optional<ManagedChannel> managedChannelUsePlaintext = getManagedChannelUsePlaintext();
      if (managedChannelUsePlaintext.isPresent()) {
        channel = managedChannelUsePlaintext.get();
        return addFromGrpc(authorityAddGrpcCmd, callCredentials);
      } else {
        LOGGER.error(GRPC_SERVICE_NOT_FOUND.getResultMsg());
        throw new CentaurException(GRPC_SERVICE_NOT_FOUND);
      }
    } else {
      return addFromGrpc(authorityAddGrpcCmd, callCredentials);
    }

  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<AuthorityAddGrpcCo> syncAdd(AuthorityAddGrpcCmd authorityAddGrpcCmd,
      AuthCallCredentials callCredentials) {
    if (channel == null) {
      return getManagedChannelUsePlaintext().map(managedChannel -> {
        channel = managedChannel;
        return syncAddFromGrpc(authorityAddGrpcCmd, callCredentials);
      }).orElse(null);
    } else {
      return syncAddFromGrpc(authorityAddGrpcCmd, callCredentials);
    }

  }

  private AuthorityAddGrpcCo addFromGrpc(AuthorityAddGrpcCmd authorityAddGrpcCmd,
      AuthCallCredentials callCredentials)
      throws ExecutionException, InterruptedException, TimeoutException {
    AuthorityServiceFutureStub authorityServiceFutureStub = AuthorityServiceGrpc.newFutureStub(
        channel);
    return authorityServiceFutureStub.withCallCredentials(callCredentials)
        .add(authorityAddGrpcCmd).get(3, TimeUnit.SECONDS);
  }

  private @NotNull ListenableFuture<AuthorityAddGrpcCo> syncAddFromGrpc(
      AuthorityAddGrpcCmd authorityAddGrpcCmd,
      AuthCallCredentials callCredentials) {
    AuthorityServiceFutureStub authorityServiceFutureStub = AuthorityServiceGrpc.newFutureStub(
        channel);
    return authorityServiceFutureStub.withCallCredentials(callCredentials)
        .add(authorityAddGrpcCmd);
  }

}
