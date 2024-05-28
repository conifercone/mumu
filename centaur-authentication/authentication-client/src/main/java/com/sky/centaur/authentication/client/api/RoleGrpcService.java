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
import com.sky.centaur.authentication.client.api.grpc.PageOfRoleFindAllGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.RoleAddGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.RoleAddGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.RoleDeleteGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.RoleDeleteGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.RoleFindAllGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.RoleServiceGrpc;
import com.sky.centaur.authentication.client.api.grpc.RoleServiceGrpc.RoleServiceFutureStub;
import com.sky.centaur.authentication.client.api.grpc.RoleUpdateGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.RoleUpdateGrpcCo;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
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
 * 角色对外提供grpc调用实例
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public final class RoleGrpcService extends AuthenticationGrpcService implements DisposableBean {

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
  public RoleAddGrpcCo add(RoleAddGrpcCmd roleAddGrpcCmd, AuthCallCredentials callCredentials)
      throws ExecutionException, InterruptedException, TimeoutException {
    if (channel != null) {
      return addFromGrpc(roleAddGrpcCmd, callCredentials);
    } else {
      Optional<ManagedChannel> managedChannelUsePlaintext = getManagedChannelUsePlaintext();
      if (managedChannelUsePlaintext.isPresent()) {
        channel = managedChannelUsePlaintext.get();
        return addFromGrpc(roleAddGrpcCmd, callCredentials);
      } else {
        LOGGER.error(ResultCode.GRPC_SERVICE_NOT_FOUND.getResultMsg());
        throw new CentaurException(ResultCode.GRPC_SERVICE_NOT_FOUND);
      }
    }
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<RoleAddGrpcCo> syncAdd(RoleAddGrpcCmd roleAddGrpcCmd,
      AuthCallCredentials callCredentials) {
    if (channel != null) {
      return syncAddFromGrpc(roleAddGrpcCmd, callCredentials);
    } else {
      return getManagedChannelUsePlaintext().map(managedChannel -> {
        channel = managedChannel;
        return syncAddFromGrpc(roleAddGrpcCmd, callCredentials);
      }).orElse(null);
    }
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public RoleDeleteGrpcCo deleteById(RoleDeleteGrpcCmd roleDeleteGrpcCmd,
      AuthCallCredentials callCredentials)
      throws ExecutionException, InterruptedException, TimeoutException {
    //noinspection DuplicatedCode
    if (channel != null) {
      return deleteByIdFromGrpc(roleDeleteGrpcCmd, callCredentials);
    } else {
      Optional<ManagedChannel> managedChannelUsePlaintext = getManagedChannelUsePlaintext();
      if (managedChannelUsePlaintext.isPresent()) {
        channel = managedChannelUsePlaintext.get();
        return deleteByIdFromGrpc(roleDeleteGrpcCmd, callCredentials);
      } else {
        LOGGER.error(GRPC_SERVICE_NOT_FOUND.getResultMsg());
        throw new CentaurException(GRPC_SERVICE_NOT_FOUND);
      }
    }

  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<RoleDeleteGrpcCo> syncDeleteById(
      RoleDeleteGrpcCmd roleDeleteGrpcCmd,
      AuthCallCredentials callCredentials) {
    if (channel != null) {
      return syncDeleteByIdFromGrpc(roleDeleteGrpcCmd, callCredentials);
    } else {
      return getManagedChannelUsePlaintext().map(managedChannel -> {
        channel = managedChannel;
        return syncDeleteByIdFromGrpc(roleDeleteGrpcCmd, callCredentials);
      }).orElse(null);
    }

  }

  @API(status = Status.STABLE, since = "1.0.0")
  public RoleUpdateGrpcCo updateById(RoleUpdateGrpcCmd roleUpdateGrpcCmd,
      AuthCallCredentials callCredentials)
      throws ExecutionException, InterruptedException, TimeoutException {
    //noinspection DuplicatedCode
    if (channel != null) {
      return updateByIdFromGrpc(roleUpdateGrpcCmd, callCredentials);
    } else {
      Optional<ManagedChannel> managedChannelUsePlaintext = getManagedChannelUsePlaintext();
      if (managedChannelUsePlaintext.isPresent()) {
        channel = managedChannelUsePlaintext.get();
        return updateByIdFromGrpc(roleUpdateGrpcCmd, callCredentials);
      } else {
        LOGGER.error(GRPC_SERVICE_NOT_FOUND.getResultMsg());
        throw new CentaurException(GRPC_SERVICE_NOT_FOUND);
      }
    }

  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<RoleUpdateGrpcCo> syncUpdateById(
      RoleUpdateGrpcCmd roleUpdateGrpcCmd,
      AuthCallCredentials callCredentials) {
    if (channel != null) {
      return syncUpdateByIdFromGrpc(roleUpdateGrpcCmd, callCredentials);
    } else {
      return getManagedChannelUsePlaintext().map(managedChannel -> {
        channel = managedChannel;
        return syncUpdateByIdFromGrpc(roleUpdateGrpcCmd, callCredentials);
      }).orElse(null);
    }

  }

  @API(status = Status.STABLE, since = "1.0.0")
  public PageOfRoleFindAllGrpcCo findAll(RoleFindAllGrpcCmd roleFindAllGrpcCmd,
      AuthCallCredentials callCredentials)
      throws ExecutionException, InterruptedException, TimeoutException {
    //noinspection DuplicatedCode
    if (channel != null) {
      return findAllFromGrpc(roleFindAllGrpcCmd, callCredentials);
    } else {
      Optional<ManagedChannel> managedChannelUsePlaintext = getManagedChannelUsePlaintext();
      if (managedChannelUsePlaintext.isPresent()) {
        channel = managedChannelUsePlaintext.get();
        return findAllFromGrpc(roleFindAllGrpcCmd, callCredentials);
      } else {
        LOGGER.error(GRPC_SERVICE_NOT_FOUND.getResultMsg());
        throw new CentaurException(GRPC_SERVICE_NOT_FOUND);
      }
    }

  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<PageOfRoleFindAllGrpcCo> syncFindAll(
      RoleFindAllGrpcCmd roleFindAllGrpcCmd,
      AuthCallCredentials callCredentials) {
    if (channel != null) {
      return syncFindAllFromGrpc(roleFindAllGrpcCmd, callCredentials);
    } else {
      return getManagedChannelUsePlaintext().map(managedChannel -> {
        channel = managedChannel;
        return syncFindAllFromGrpc(roleFindAllGrpcCmd, callCredentials);
      }).orElse(null);
    }

  }

  private RoleAddGrpcCo addFromGrpc(RoleAddGrpcCmd roleAddGrpcCmd,
      AuthCallCredentials callCredentials)
      throws ExecutionException, InterruptedException, TimeoutException {
    RoleServiceFutureStub roleServiceFutureStub = RoleServiceGrpc.newFutureStub(channel);
    return roleServiceFutureStub.withCallCredentials(callCredentials).add(roleAddGrpcCmd)
        .get(3, TimeUnit.SECONDS);
  }

  private @NotNull ListenableFuture<RoleAddGrpcCo> syncAddFromGrpc(RoleAddGrpcCmd roleAddGrpcCmd,
      AuthCallCredentials callCredentials) {
    RoleServiceFutureStub roleServiceFutureStub = RoleServiceGrpc.newFutureStub(channel);
    return roleServiceFutureStub.withCallCredentials(callCredentials).add(roleAddGrpcCmd);
  }

  private RoleDeleteGrpcCo deleteByIdFromGrpc(RoleDeleteGrpcCmd roleDeleteGrpcCmd,
      AuthCallCredentials callCredentials)
      throws ExecutionException, InterruptedException, TimeoutException {
    RoleServiceFutureStub roleServiceFutureStub = RoleServiceGrpc.newFutureStub(
        channel);
    return roleServiceFutureStub.withCallCredentials(callCredentials)
        .deleteById(roleDeleteGrpcCmd).get(3, TimeUnit.SECONDS);
  }

  private @NotNull ListenableFuture<RoleDeleteGrpcCo> syncDeleteByIdFromGrpc(
      RoleDeleteGrpcCmd roleDeleteGrpcCmd,
      AuthCallCredentials callCredentials) {
    RoleServiceFutureStub roleServiceFutureStub = RoleServiceGrpc.newFutureStub(
        channel);
    return roleServiceFutureStub.withCallCredentials(callCredentials)
        .deleteById(roleDeleteGrpcCmd);
  }

  private RoleUpdateGrpcCo updateByIdFromGrpc(RoleUpdateGrpcCmd roleUpdateGrpcCmd,
      AuthCallCredentials callCredentials)
      throws ExecutionException, InterruptedException, TimeoutException {
    RoleServiceFutureStub roleServiceFutureStub = RoleServiceGrpc.newFutureStub(
        channel);
    return roleServiceFutureStub.withCallCredentials(callCredentials)
        .updateById(roleUpdateGrpcCmd).get(3, TimeUnit.SECONDS);
  }

  private @NotNull ListenableFuture<RoleUpdateGrpcCo> syncUpdateByIdFromGrpc(
      RoleUpdateGrpcCmd roleUpdateGrpcCmd,
      AuthCallCredentials callCredentials) {
    RoleServiceFutureStub roleServiceFutureStub = RoleServiceGrpc.newFutureStub(
        channel);
    return roleServiceFutureStub.withCallCredentials(callCredentials)
        .updateById(roleUpdateGrpcCmd);
  }

  private PageOfRoleFindAllGrpcCo findAllFromGrpc(
      RoleFindAllGrpcCmd roleFindAllGrpcCmd,
      AuthCallCredentials callCredentials)
      throws ExecutionException, InterruptedException, TimeoutException {
    RoleServiceFutureStub roleServiceFutureStub = RoleServiceGrpc.newFutureStub(
        channel);
    return roleServiceFutureStub.withCallCredentials(callCredentials)
        .findAll(roleFindAllGrpcCmd).get(3, TimeUnit.SECONDS);
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
