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

import com.sky.centaur.authentication.client.api.grpc.RoleAddGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.RoleAddGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.RoleServiceGrpc;
import com.sky.centaur.authentication.client.api.grpc.RoleServiceGrpc.RoleServiceFutureStub;
import io.grpc.ManagedChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Nullable;
import org.lognet.springboot.grpc.security.AuthCallCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * 角色对外提供grpc调用实例
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public class RoleGrpcService extends AuthenticationGrpcService implements DisposableBean {

  private ManagedChannel channel;

  private static final Logger LOGGER = LoggerFactory.getLogger(RoleGrpcService.class);

  public RoleGrpcService(
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
  public RoleAddGrpcCo add(RoleAddGrpcCmd roleAddGrpcCmd, AuthCallCredentials callCredentials) {
    if (channel == null) {
      return getManagedChannelUsePlaintext().map(managedChannel -> {
        channel = managedChannel;
        return extracted(roleAddGrpcCmd, callCredentials);
      }).orElse(null);
    } else {
      return extracted(roleAddGrpcCmd, callCredentials);
    }
  }

  private @Nullable RoleAddGrpcCo extracted(RoleAddGrpcCmd roleAddGrpcCmd,
      AuthCallCredentials callCredentials) {
    RoleServiceFutureStub roleServiceFutureStub = RoleServiceGrpc.newFutureStub(channel);
    try {
      return roleServiceFutureStub.withCallCredentials(callCredentials).add(roleAddGrpcCmd)
          .get(3, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      LOGGER.error(e.getMessage());
      return null;
    }
  }

}
