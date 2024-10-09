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
package baby.mumu.authentication.application.service;

import baby.mumu.authentication.application.role.executor.RoleAddCmdExe;
import baby.mumu.authentication.application.role.executor.RoleArchiveByIdCmdExe;
import baby.mumu.authentication.application.role.executor.RoleDeleteByIdCmdExe;
import baby.mumu.authentication.application.role.executor.RoleFindAllCmdExe;
import baby.mumu.authentication.application.role.executor.RoleFindAllSliceCmdExe;
import baby.mumu.authentication.application.role.executor.RoleRecoverFromArchiveByIdCmdExe;
import baby.mumu.authentication.application.role.executor.RoleUpdateCmdExe;
import baby.mumu.authentication.client.api.RoleService;
import baby.mumu.authentication.client.api.grpc.PageOfRoleFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.PageOfRoleFindAllGrpcCo.Builder;
import baby.mumu.authentication.client.api.grpc.RoleAddGrpcCmd;
import baby.mumu.authentication.client.api.grpc.RoleAddGrpcCo;
import baby.mumu.authentication.client.api.grpc.RoleDeleteByIdGrpcCmd;
import baby.mumu.authentication.client.api.grpc.RoleFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.RoleFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.RoleFindAllGrpcQueryCo;
import baby.mumu.authentication.client.api.grpc.RoleServiceGrpc.RoleServiceImplBase;
import baby.mumu.authentication.client.api.grpc.RoleUpdateGrpcCmd;
import baby.mumu.authentication.client.api.grpc.RoleUpdateGrpcCo;
import baby.mumu.authentication.client.dto.RoleAddCmd;
import baby.mumu.authentication.client.dto.RoleArchiveByIdCmd;
import baby.mumu.authentication.client.dto.RoleDeleteByIdCmd;
import baby.mumu.authentication.client.dto.RoleFindAllCmd;
import baby.mumu.authentication.client.dto.RoleFindAllSliceCmd;
import baby.mumu.authentication.client.dto.RoleRecoverFromArchiveByIdCmd;
import baby.mumu.authentication.client.dto.RoleUpdateCmd;
import baby.mumu.authentication.client.dto.co.RoleAddCo;
import baby.mumu.authentication.client.dto.co.RoleFindAllCo;
import baby.mumu.authentication.client.dto.co.RoleFindAllQueryCo;
import baby.mumu.authentication.client.dto.co.RoleFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.RoleUpdateCo;
import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.GRpcService;
import org.lognet.springboot.grpc.recovery.GRpcRuntimeExceptionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class, ClientIpInterceptor.class})
@Observed(name = "RoleServiceImpl")
public class RoleServiceImpl extends RoleServiceImplBase implements RoleService {

  private final RoleAddCmdExe roleAddCmdExe;
  private final RoleDeleteByIdCmdExe roleDeleteByIdCmdExe;
  private final RoleUpdateCmdExe roleUpdateCmdExe;
  private final RoleFindAllCmdExe roleFindAllCmdExe;
  private final RoleArchiveByIdCmdExe roleArchiveByIdCmdExe;
  private final RoleRecoverFromArchiveByIdCmdExe roleRecoverFromArchiveByIdCmdExe;
  private final RoleFindAllSliceCmdExe roleFindAllSliceCmdExe;

  @Autowired
  public RoleServiceImpl(RoleAddCmdExe roleAddCmdExe, RoleDeleteByIdCmdExe roleDeleteByIdCmdExe,
      RoleUpdateCmdExe roleUpdateCmdExe, RoleFindAllCmdExe roleFindAllCmdExe,
      RoleArchiveByIdCmdExe roleArchiveByIdCmdExe,
      RoleRecoverFromArchiveByIdCmdExe roleRecoverFromArchiveByIdCmdExe,
      RoleFindAllSliceCmdExe roleFindAllSliceCmdExe) {
    this.roleAddCmdExe = roleAddCmdExe;
    this.roleDeleteByIdCmdExe = roleDeleteByIdCmdExe;
    this.roleUpdateCmdExe = roleUpdateCmdExe;
    this.roleFindAllCmdExe = roleFindAllCmdExe;
    this.roleArchiveByIdCmdExe = roleArchiveByIdCmdExe;
    this.roleRecoverFromArchiveByIdCmdExe = roleRecoverFromArchiveByIdCmdExe;
    this.roleFindAllSliceCmdExe = roleFindAllSliceCmdExe;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void add(RoleAddCmd roleAddCmd) {
    roleAddCmdExe.execute(roleAddCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(RoleDeleteByIdCmd roleDeleteByIdCmd) {
    roleDeleteByIdCmdExe.execute(roleDeleteByIdCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateById(RoleUpdateCmd roleUpdateCmd) {
    roleUpdateCmdExe.execute(roleUpdateCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Page<RoleFindAllCo> findAll(RoleFindAllCmd roleFindAllCmd) {
    return roleFindAllCmdExe.execute(roleFindAllCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Slice<RoleFindAllSliceCo> findAllSlice(RoleFindAllSliceCmd roleFindAllSliceCmd) {
    return roleFindAllSliceCmdExe.execute(roleFindAllSliceCmd);
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void add(RoleAddGrpcCmd request, StreamObserver<Empty> responseObserver) {
    RoleAddCmd roleAddCmd = new RoleAddCmd();
    RoleAddCo roleAddCo = getRoleAddCo(request);
    roleAddCmd.setRoleAddCo(roleAddCo);
    try {
      roleAddCmdExe.execute(roleAddCmd);
    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  @NotNull
  private static RoleAddCo getRoleAddCo(
      @NotNull RoleAddGrpcCmd request) {
    RoleAddCo roleAddCo = new RoleAddCo();
    RoleAddGrpcCo roleAddGrpcCo = request.getRoleAddCo();
    //noinspection DuplicatedCode
    roleAddCo.setId(roleAddGrpcCo.hasId() ? roleAddGrpcCo.getId().getValue() : null);
    roleAddCo.setCode(roleAddGrpcCo.hasCode() ? roleAddGrpcCo.getCode().getValue() : null);
    roleAddCo.setName(roleAddGrpcCo.hasName() ? roleAddGrpcCo.getName().getValue() : null);
    roleAddCo.setAuthorities(roleAddGrpcCo.getAuthoritiesList());
    return roleAddCo;
  }


  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void deleteById(@NotNull RoleDeleteByIdGrpcCmd request,
      StreamObserver<Empty> responseObserver) {
    RoleDeleteByIdCmd roleDeleteByIdCmd = new RoleDeleteByIdCmd();
    //noinspection DuplicatedCode
    roleDeleteByIdCmd.setId(request.hasId() ? request.getId().getValue() : null);
    try {
      roleDeleteByIdCmdExe.execute(roleDeleteByIdCmd);
    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  @NotNull
  private static RoleUpdateCo getRoleUpdateCo(
      @NotNull RoleUpdateGrpcCmd request) {
    RoleUpdateCo roleUpdateCo = new RoleUpdateCo();
    RoleUpdateGrpcCo roleUpdateGrpcCo = request.getRoleUpdateCo();
    //noinspection DuplicatedCode
    roleUpdateCo.setId(roleUpdateGrpcCo.hasId() ? roleUpdateGrpcCo.getId().getValue() : null);
    roleUpdateCo.setCode(roleUpdateGrpcCo.hasCode() ? roleUpdateGrpcCo.getCode().getValue() : null);
    roleUpdateCo.setName(roleUpdateGrpcCo.hasName() ? roleUpdateGrpcCo.getName().getValue() : null);
    roleUpdateCo.setAuthorities(roleUpdateGrpcCo.getAuthoritiesList());
    return roleUpdateCo;
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void updateById(RoleUpdateGrpcCmd request,
      StreamObserver<Empty> responseObserver) {
    RoleUpdateCmd roleUpdateCmd = new RoleUpdateCmd();
    RoleUpdateCo roleUpdateCo = getRoleUpdateCo(
        request);
    roleUpdateCmd.setRoleUpdateCo(roleUpdateCo);
    try {
      roleUpdateCmdExe.execute(roleUpdateCmd);
    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  @NotNull
  private static RoleFindAllQueryCo getRoleFindAllQueryCo(
      @NotNull RoleFindAllGrpcCmd request) {
    RoleFindAllQueryCo roleFindAllQueryCo = new RoleFindAllQueryCo();
    RoleFindAllGrpcQueryCo roleFindAllGrpcQueryCo = request.getRoleFindAllQueryCo();
    //noinspection DuplicatedCode
    roleFindAllQueryCo.setId(
        roleFindAllGrpcQueryCo.hasId() ? roleFindAllGrpcQueryCo.getId().getValue() : null);
    roleFindAllQueryCo.setCode(
        roleFindAllGrpcQueryCo.hasCode() ? roleFindAllGrpcQueryCo.getCode().getValue() : null);
    roleFindAllQueryCo.setName(
        roleFindAllGrpcQueryCo.hasName() ? roleFindAllGrpcQueryCo.getName().getValue() : null);
    return roleFindAllQueryCo;
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void findAll(RoleFindAllGrpcCmd request,
      StreamObserver<PageOfRoleFindAllGrpcCo> responseObserver) {
    RoleFindAllCmd roleFindAllCmd = new RoleFindAllCmd();
    roleFindAllCmd.setRoleFindAllQueryCo(getRoleFindAllQueryCo(request));
    roleFindAllCmd.setPageNo(request.hasPageNo() ? request.getPageNo().getValue() : 0);
    roleFindAllCmd.setPageSize(request.hasPageSize() ? request.getPageSize().getValue() : 10);
    Builder builder = PageOfRoleFindAllGrpcCo.newBuilder();
    try {
      Page<RoleFindAllCo> roleFindAllCos = roleFindAllCmdExe.execute(
          roleFindAllCmd);
      List<RoleFindAllGrpcCo> findAllGrpcCos = roleFindAllCos.getContent().stream()
          .map(roleFindAllCo -> RoleFindAllGrpcCo.newBuilder()
              .setId(Int64Value.of(roleFindAllCo.getId()))
              .setCode(StringValue.of(roleFindAllCo.getCode())).setName(
                  StringValue.of(roleFindAllCo.getName()))
              .addAllAuthorities(roleFindAllCo.getAuthorities())
              .build()).toList();
      builder.addAllContent(findAllGrpcCos);
      builder.setTotalPages(roleFindAllCos.getTotalPages());
    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void archiveById(RoleArchiveByIdCmd roleArchiveByIdCmd) {
    roleArchiveByIdCmdExe.execute(roleArchiveByIdCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(RoleRecoverFromArchiveByIdCmd roleRecoverFromArchiveByIdCmd) {
    roleRecoverFromArchiveByIdCmdExe.execute(roleRecoverFromArchiveByIdCmd);
  }
}
