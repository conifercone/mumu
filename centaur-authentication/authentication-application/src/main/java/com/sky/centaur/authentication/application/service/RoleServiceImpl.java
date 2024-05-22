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

package com.sky.centaur.authentication.application.service;

import com.sky.centaur.authentication.application.role.executor.RoleAddCmdExe;
import com.sky.centaur.authentication.application.role.executor.RoleDeleteCmdExe;
import com.sky.centaur.authentication.application.role.executor.RoleFindAllCmdExe;
import com.sky.centaur.authentication.application.role.executor.RoleUpdateCmdExe;
import com.sky.centaur.authentication.client.api.RoleService;
import com.sky.centaur.authentication.client.api.grpc.PageOfRoleFindAllGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.PageOfRoleFindAllGrpcCo.Builder;
import com.sky.centaur.authentication.client.api.grpc.RoleAddGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.RoleAddGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.RoleDeleteGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.RoleDeleteGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.RoleFindAllGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.RoleFindAllGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.RoleServiceGrpc.RoleServiceImplBase;
import com.sky.centaur.authentication.client.api.grpc.RoleUpdateGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.RoleUpdateGrpcCo;
import com.sky.centaur.authentication.client.dto.RoleAddCmd;
import com.sky.centaur.authentication.client.dto.RoleDeleteCmd;
import com.sky.centaur.authentication.client.dto.RoleFindAllCmd;
import com.sky.centaur.authentication.client.dto.RoleUpdateCmd;
import com.sky.centaur.authentication.client.dto.co.RoleAddCo;
import com.sky.centaur.authentication.client.dto.co.RoleDeleteCo;
import com.sky.centaur.authentication.client.dto.co.RoleFindAllCo;
import com.sky.centaur.authentication.client.dto.co.RoleUpdateCo;
import com.sky.centaur.basis.exception.CentaurException;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.GRpcService;
import org.lognet.springboot.grpc.recovery.GRpcRuntimeExceptionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * 角色管理
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class})
@Observed(name = "RoleServiceImpl")
public class RoleServiceImpl extends RoleServiceImplBase implements RoleService {

  private final RoleAddCmdExe roleAddCmdExe;

  private final RoleDeleteCmdExe roleDeleteCmdExe;

  private final RoleUpdateCmdExe roleUpdateCmdExe;

  private final RoleFindAllCmdExe roleFindAllCmdExe;

  @Autowired
  public RoleServiceImpl(RoleAddCmdExe roleAddCmdExe, RoleDeleteCmdExe roleDeleteCmdExe,
      RoleUpdateCmdExe roleUpdateCmdExe, RoleFindAllCmdExe roleFindAllCmdExe) {
    this.roleAddCmdExe = roleAddCmdExe;
    this.roleDeleteCmdExe = roleDeleteCmdExe;
    this.roleUpdateCmdExe = roleUpdateCmdExe;
    this.roleFindAllCmdExe = roleFindAllCmdExe;
  }

  @Override
  public RoleAddCo add(RoleAddCmd roleAddCmd) {
    return roleAddCmdExe.execute(roleAddCmd);
  }

  @Override
  public RoleDeleteCo delete(RoleDeleteCmd roleDeleteCmd) {
    return roleDeleteCmdExe.execute(roleDeleteCmd);
  }

  @Override
  public RoleUpdateCo updateById(RoleUpdateCmd roleUpdateCmd) {
    return roleUpdateCmdExe.execute(roleUpdateCmd);
  }

  @Override
  public Page<RoleFindAllCo> findAll(RoleFindAllCmd roleFindAllCmd) {
    return roleFindAllCmdExe.execute(roleFindAllCmd);
  }

  @Override
  @PreAuthorize("hasRole('admin')")
  public void add(RoleAddGrpcCmd request, StreamObserver<RoleAddGrpcCo> responseObserver) {
    RoleAddCmd roleAddCmd = new RoleAddCmd();
    RoleAddCo roleAddCo = getRoleAddCo(request);
    roleAddCmd.setRoleAddCo(roleAddCo);
    try {
      roleAddCmdExe.execute(roleAddCmd);
    } catch (CentaurException e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(request.getRoleAddCo());
    responseObserver.onCompleted();
  }

  @NotNull
  private static RoleAddCo getRoleAddCo(
      @NotNull RoleAddGrpcCmd request) {
    RoleAddCo roleAddCo = new RoleAddCo();
    RoleAddGrpcCo roleAddGrpcCo = request.getRoleAddCo();
    roleAddCo.setId(roleAddGrpcCo.getId());
    roleAddCo.setCode(roleAddGrpcCo.getCode());
    roleAddCo.setName(roleAddGrpcCo.getName());
    roleAddCo.setAuthorities(roleAddGrpcCo.getAuthoritiesList());
    return roleAddCo;
  }

  @NotNull
  private static RoleDeleteCo getRoleDeleteCo(
      @NotNull RoleDeleteGrpcCmd request) {
    RoleDeleteCo roleDeleteCo = new RoleDeleteCo();
    RoleDeleteGrpcCo roleDeleteGrpcCo = request.getRoleDeleteCo();
    roleDeleteCo.setId(roleDeleteGrpcCo.getId());
    return roleDeleteCo;
  }

  @Override
  public void delete(RoleDeleteGrpcCmd request, StreamObserver<RoleDeleteGrpcCo> responseObserver) {
    RoleDeleteCmd roleDeleteCmd = new RoleDeleteCmd();
    RoleDeleteCo roleDeleteCo = getRoleDeleteCo(
        request);
    roleDeleteCmd.setRoleDeleteCo(roleDeleteCo);
    try {
      roleDeleteCmdExe.execute(roleDeleteCmd);
    } catch (CentaurException e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(request.getRoleDeleteCo());
    responseObserver.onCompleted();
  }

  @NotNull
  private static RoleUpdateCo getRoleUpdateCo(
      @NotNull RoleUpdateGrpcCmd request) {
    RoleUpdateCo roleUpdateCo = new RoleUpdateCo();
    RoleUpdateGrpcCo roleUpdateGrpcCo = request.getRoleUpdateCo();
    roleUpdateCo.setId(roleUpdateGrpcCo.getId());
    roleUpdateCo.setCode(roleUpdateGrpcCo.getCode());
    roleUpdateCo.setName(roleUpdateGrpcCo.getName());
    roleUpdateCo.setAuthorities(roleUpdateGrpcCo.getAuthoritiesList());
    return roleUpdateCo;
  }

  @Override
  public void updateById(RoleUpdateGrpcCmd request,
      StreamObserver<RoleUpdateGrpcCo> responseObserver) {
    RoleUpdateCmd roleUpdateCmd = new RoleUpdateCmd();
    RoleUpdateCo roleUpdateCo = getRoleUpdateCo(
        request);
    roleUpdateCmd.setRoleUpdateCo(roleUpdateCo);
    try {
      roleUpdateCmdExe.execute(roleUpdateCmd);
    } catch (CentaurException e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(request.getRoleUpdateCo());
    responseObserver.onCompleted();
  }

  @NotNull
  private static RoleFindAllCo getRoleFindAllCo(
      @NotNull RoleFindAllGrpcCmd request) {
    RoleFindAllCo roleFindAllCo = new RoleFindAllCo();
    RoleFindAllGrpcCo roleFindAllGrpcCo = request.getRoleFindAllCo();
    roleFindAllCo.setId(roleFindAllGrpcCo.getId());
    roleFindAllCo.setCode(roleFindAllGrpcCo.getCode());
    roleFindAllCo.setName(roleFindAllGrpcCo.getName());
    roleFindAllCo.setAuthorities(roleFindAllGrpcCo.getAuthoritiesList());
    return roleFindAllCo;
  }

  @Override
  public void findAll(RoleFindAllGrpcCmd request,
      StreamObserver<PageOfRoleFindAllGrpcCo> responseObserver) {
    RoleFindAllCmd roleFindAllCmd = new RoleFindAllCmd();
    roleFindAllCmd.setRoleFindAllCo(getRoleFindAllCo(request));
    roleFindAllCmd.setPageNo(request.getPageNo());
    roleFindAllCmd.setPageSize(request.getPageSize());
    Builder builder = PageOfRoleFindAllGrpcCo.newBuilder();
    try {
      Page<RoleFindAllCo> roleFindAllCos = roleFindAllCmdExe.execute(
          roleFindAllCmd);
      List<RoleFindAllGrpcCo> findAllGrpcCos = roleFindAllCos.getContent().stream()
          .map(roleFindAllCo -> RoleFindAllGrpcCo.newBuilder()
              .setId(roleFindAllCo.getId())
              .setCode(roleFindAllCo.getCode()).setName(
                  roleFindAllCo.getName()).addAllAuthorities(roleFindAllCo.getAuthorities())
              .build()).toList();
      builder.addAllContent(findAllGrpcCos);
      builder.setTotalPages(roleFindAllCos.getTotalPages());
    } catch (CentaurException e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }
}
