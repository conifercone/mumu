/*
 * Copyright (c) 2024-2025, the original author or authors.
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

package baby.mumu.iam.client.api;

import static baby.mumu.basis.response.ResponseCode.GRPC_SERVICE_NOT_FOUND;

import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.iam.client.api.grpc.PageOfRoleFindAllGrpcDTO;
import baby.mumu.iam.client.api.grpc.RoleFindAllGrpcCmd;
import baby.mumu.iam.client.api.grpc.RoleFindByIdGrpcDTO;
import baby.mumu.iam.client.api.grpc.RoleServiceGrpc;
import baby.mumu.iam.client.api.grpc.RoleServiceGrpc.RoleServiceBlockingStub;
import baby.mumu.iam.client.api.grpc.RoleServiceGrpc.RoleServiceFutureStub;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Int64Value;
import io.grpc.ManagedChannel;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.grpc.client.GrpcChannelFactory;

/**
 * 角色对外提供grpc调用实例
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
public class RoleGrpcService extends IAMGrpcService implements DisposableBean {

  private ManagedChannel channel;

  public RoleGrpcService(
    DiscoveryClient discoveryClient,
    GrpcChannelFactory grpcChannelFactory) {
    super(discoveryClient, grpcChannelFactory);
  }

  @Override
  public void destroy() {
    Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdown);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public PageOfRoleFindAllGrpcDTO findAll(RoleFindAllGrpcCmd roleFindAllGrpcCmd) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannel)
      .map(ch -> {
        channel = ch;
        return findAllFromGrpc(roleFindAllGrpcCmd);
      })
      .orElseThrow(() -> new ApplicationException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public ListenableFuture<PageOfRoleFindAllGrpcDTO> syncFindAll(
    RoleFindAllGrpcCmd roleFindAllGrpcCmd) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannel)
      .map(ch -> {
        channel = ch;
        return syncFindAllFromGrpc(roleFindAllGrpcCmd);
      })
      .orElseThrow(() -> new ApplicationException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "2.4.0")
  public RoleFindByIdGrpcDTO findById(Int64Value roleId) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannel)
      .map(ch -> {
        channel = ch;
        return findByIdFromGrpc(roleId);
      })
      .orElseThrow(() -> new ApplicationException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "2.4.0")
  public ListenableFuture<RoleFindByIdGrpcDTO> syncFindById(
    Int64Value roleId) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannel)
      .map(ch -> {
        channel = ch;
        return syncFindByIdFromGrpc(roleId);
      })
      .orElseThrow(() -> new ApplicationException(GRPC_SERVICE_NOT_FOUND));
  }

  private PageOfRoleFindAllGrpcDTO findAllFromGrpc(
    RoleFindAllGrpcCmd roleFindAllGrpcCmd) {
    RoleServiceBlockingStub roleServiceBlockingStub = RoleServiceGrpc.newBlockingStub(channel);
    return roleServiceBlockingStub
      .findAll(roleFindAllGrpcCmd);
  }

  private @NonNull ListenableFuture<PageOfRoleFindAllGrpcDTO> syncFindAllFromGrpc(
    RoleFindAllGrpcCmd roleFindAllGrpcCmd) {
    RoleServiceFutureStub roleServiceFutureStub = RoleServiceGrpc.newFutureStub(
      channel);
    return roleServiceFutureStub
      .findAll(roleFindAllGrpcCmd);
  }

  private RoleFindByIdGrpcDTO findByIdFromGrpc(
    Int64Value roleId) {
    RoleServiceBlockingStub roleServiceBlockingStub = RoleServiceGrpc.newBlockingStub(channel);
    return roleServiceBlockingStub
      .findById(roleId);
  }

  private @NonNull ListenableFuture<RoleFindByIdGrpcDTO> syncFindByIdFromGrpc(
    Int64Value roleId) {
    RoleServiceFutureStub roleServiceFutureStub = RoleServiceGrpc.newFutureStub(
      channel);
    return roleServiceFutureStub
      .findById(roleId);
  }


}
