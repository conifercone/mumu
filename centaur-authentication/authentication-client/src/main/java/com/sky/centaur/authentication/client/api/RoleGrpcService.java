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
import com.google.protobuf.Empty;
import com.sky.centaur.authentication.client.api.grpc.PageOfRoleFindAllGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.RoleAddGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.RoleDeleteByIdGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.RoleFindAllGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.RoleServiceGrpc;
import com.sky.centaur.authentication.client.api.grpc.RoleServiceGrpc.RoleServiceBlockingStub;
import com.sky.centaur.authentication.client.api.grpc.RoleServiceGrpc.RoleServiceFutureStub;
import com.sky.centaur.authentication.client.api.grpc.RoleUpdateGrpcCmd;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
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
 * @author kaiyu.shan
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
    if (channel != null) {
      return addFromGrpc(roleAddGrpcCmd, callCredentials);
    } else {
      Optional<ManagedChannel> managedChannelUsePlaintext = getManagedChannelUsePlaintext();
      if (managedChannelUsePlaintext.isPresent()) {
        channel = managedChannelUsePlaintext.get();
        return addFromGrpc(roleAddGrpcCmd, callCredentials);
      } else {
        throw new CentaurException(ResultCode.GRPC_SERVICE_NOT_FOUND);
      }
    }
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<Empty> syncAdd(RoleAddGrpcCmd roleAddGrpcCmd,
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
  public Empty deleteById(RoleDeleteByIdGrpcCmd roleDeleteByIdGrpcCmd,
      AuthCallCredentials callCredentials) {
    //noinspection DuplicatedCode
    if (channel != null) {
      return deleteByIdFromGrpc(roleDeleteByIdGrpcCmd, callCredentials);
    } else {
      Optional<ManagedChannel> managedChannelUsePlaintext = getManagedChannelUsePlaintext();
      if (managedChannelUsePlaintext.isPresent()) {
        channel = managedChannelUsePlaintext.get();
        return deleteByIdFromGrpc(roleDeleteByIdGrpcCmd, callCredentials);
      } else {
        throw new CentaurException(GRPC_SERVICE_NOT_FOUND);
      }
    }
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<Empty> syncDeleteById(
      RoleDeleteByIdGrpcCmd roleDeleteByIdGrpcCmd,
      AuthCallCredentials callCredentials) {
    if (channel != null) {
      return syncDeleteByIdFromGrpc(roleDeleteByIdGrpcCmd, callCredentials);
    } else {
      return getManagedChannelUsePlaintext().map(managedChannel -> {
        channel = managedChannel;
        return syncDeleteByIdFromGrpc(roleDeleteByIdGrpcCmd, callCredentials);
      }).orElse(null);
    }
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Empty updateById(RoleUpdateGrpcCmd roleUpdateGrpcCmd,
      AuthCallCredentials callCredentials) {
    //noinspection DuplicatedCode
    if (channel != null) {
      return updateByIdFromGrpc(roleUpdateGrpcCmd, callCredentials);
    } else {
      Optional<ManagedChannel> managedChannelUsePlaintext = getManagedChannelUsePlaintext();
      if (managedChannelUsePlaintext.isPresent()) {
        channel = managedChannelUsePlaintext.get();
        return updateByIdFromGrpc(roleUpdateGrpcCmd, callCredentials);
      } else {
        throw new CentaurException(GRPC_SERVICE_NOT_FOUND);
      }
    }
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<Empty> syncUpdateById(
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
      AuthCallCredentials callCredentials) {
    //noinspection DuplicatedCode
    if (channel != null) {
      return findAllFromGrpc(roleFindAllGrpcCmd, callCredentials);
    } else {
      Optional<ManagedChannel> managedChannelUsePlaintext = getManagedChannelUsePlaintext();
      if (managedChannelUsePlaintext.isPresent()) {
        channel = managedChannelUsePlaintext.get();
        return findAllFromGrpc(roleFindAllGrpcCmd, callCredentials);
      } else {
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
