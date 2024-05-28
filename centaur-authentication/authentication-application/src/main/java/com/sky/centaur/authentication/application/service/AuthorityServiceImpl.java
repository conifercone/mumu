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

import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import com.sky.centaur.authentication.application.authority.executor.AuthorityAddCmdExe;
import com.sky.centaur.authentication.application.authority.executor.AuthorityDeleteCmdExe;
import com.sky.centaur.authentication.application.authority.executor.AuthorityFindAllCmdExe;
import com.sky.centaur.authentication.application.authority.executor.AuthorityUpdateCmdExe;
import com.sky.centaur.authentication.client.api.AuthorityService;
import com.sky.centaur.authentication.client.api.grpc.AuthorityAddGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AuthorityAddGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AuthorityDeleteGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AuthorityDeleteGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AuthorityFindAllGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AuthorityFindAllGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AuthorityServiceGrpc.AuthorityServiceImplBase;
import com.sky.centaur.authentication.client.api.grpc.AuthorityUpdateGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AuthorityUpdateGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.PageOfAuthorityFindAllGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.PageOfAuthorityFindAllGrpcCo.Builder;
import com.sky.centaur.authentication.client.dto.AuthorityAddCmd;
import com.sky.centaur.authentication.client.dto.AuthorityDeleteCmd;
import com.sky.centaur.authentication.client.dto.AuthorityFindAllCmd;
import com.sky.centaur.authentication.client.dto.AuthorityUpdateCmd;
import com.sky.centaur.authentication.client.dto.co.AuthorityAddCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityDeleteCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityFindAllCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityUpdateCo;
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
 * 权限管理
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class})
@Observed(name = "AuthorityServiceImpl")
public class AuthorityServiceImpl extends AuthorityServiceImplBase implements AuthorityService {

  private final AuthorityAddCmdExe authorityAddCmdExe;

  private final AuthorityDeleteCmdExe authorityDeleteCmdExe;

  private final AuthorityUpdateCmdExe authorityUpdateCmdExe;

  private final AuthorityFindAllCmdExe authorityFindAllCmdExe;

  @Autowired
  public AuthorityServiceImpl(AuthorityAddCmdExe authorityAddCmdExe,
      AuthorityDeleteCmdExe authorityDeleteCmdExe, AuthorityUpdateCmdExe authorityUpdateCmdExe,
      AuthorityFindAllCmdExe authorityFindAllCmdExe) {
    this.authorityAddCmdExe = authorityAddCmdExe;
    this.authorityDeleteCmdExe = authorityDeleteCmdExe;
    this.authorityUpdateCmdExe = authorityUpdateCmdExe;
    this.authorityFindAllCmdExe = authorityFindAllCmdExe;
  }

  @Override
  public AuthorityAddCo add(AuthorityAddCmd authorityAddCmd) {
    return authorityAddCmdExe.execute(authorityAddCmd);
  }

  @Override
  @PreAuthorize("hasRole('admin')")
  public void add(AuthorityAddGrpcCmd request,
      StreamObserver<AuthorityAddGrpcCo> responseObserver) {
    AuthorityAddCmd authorityAddCmd = new AuthorityAddCmd();
    AuthorityAddCo authorityAddCo = getAuthorityAddCo(
        request);
    authorityAddCmd.setAuthorityAddCo(authorityAddCo);
    AuthorityAddGrpcCo authorityAddGrpcCo = request.getAuthorityAddCo();
    try {
      AuthorityAddCo addCo = authorityAddCmdExe.execute(authorityAddCmd);
      authorityAddGrpcCo = authorityAddGrpcCo.toBuilder().setId(Int64Value.of(addCo.getId()))
          .build();
    } catch (CentaurException e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(authorityAddGrpcCo);
    responseObserver.onCompleted();
  }

  @NotNull
  private static AuthorityAddCo getAuthorityAddCo(
      @NotNull AuthorityAddGrpcCmd request) {
    AuthorityAddCo authorityAddCo = new AuthorityAddCo();
    AuthorityAddGrpcCo authorityAddGrpcCo = request.getAuthorityAddCo();
    authorityAddCo.setId(authorityAddGrpcCo.hasId() ? authorityAddGrpcCo.getId().getValue() : null);
    authorityAddCo.setCode(
        authorityAddGrpcCo.hasCode() ? authorityAddGrpcCo.getCode().getValue() : null);
    authorityAddCo.setName(
        authorityAddGrpcCo.hasName() ? authorityAddGrpcCo.getName().getValue() : null);
    return authorityAddCo;
  }

  @NotNull
  private static AuthorityDeleteCo getAuthorityDeleteCo(
      @NotNull AuthorityDeleteGrpcCmd request) {
    AuthorityDeleteCo authorityDeleteCo = new AuthorityDeleteCo();
    AuthorityDeleteGrpcCo authorityDeleteGrpcCo = request.getAuthorityDeleteCo();
    authorityDeleteCo.setId(
        authorityDeleteGrpcCo.hasId() ? authorityDeleteGrpcCo.getId().getValue() : null);
    return authorityDeleteCo;
  }

  @NotNull
  private static AuthorityUpdateCo getAuthorityUpdateCo(
      @NotNull AuthorityUpdateGrpcCmd request) {
    AuthorityUpdateCo authorityUpdateCo = new AuthorityUpdateCo();
    AuthorityUpdateGrpcCo authorityUpdateGrpcCo = request.getAuthorityUpdateCo();
    authorityUpdateCo.setId(
        authorityUpdateGrpcCo.hasId() ? authorityUpdateGrpcCo.getId().getValue() : null);
    authorityUpdateCo.setCode(
        authorityUpdateGrpcCo.hasCode() ? authorityUpdateGrpcCo.getCode().getValue() : null);
    authorityUpdateCo.setName(
        authorityUpdateGrpcCo.hasName() ? authorityUpdateGrpcCo.getName().getValue() : null);
    return authorityUpdateCo;
  }

  @NotNull
  private static AuthorityFindAllCo getAuthorityFindAllCo(
      @NotNull AuthorityFindAllGrpcCmd request) {
    AuthorityFindAllCo authorityFindAllCo = new AuthorityFindAllCo();
    AuthorityFindAllGrpcCo authorityFindAllGrpcCo = request.getAuthorityFindAllCo();
    authorityFindAllCo.setId(
        authorityFindAllGrpcCo.hasId() ? authorityFindAllGrpcCo.getId().getValue() : null);
    authorityFindAllCo.setCode(
        authorityFindAllGrpcCo.hasCode() ? authorityFindAllGrpcCo.getCode().getValue() : null);
    authorityFindAllCo.setName(
        authorityFindAllGrpcCo.hasName() ? authorityFindAllGrpcCo.getName().getValue() : null);
    return authorityFindAllCo;
  }

  @Override
  public AuthorityDeleteCo deleteById(AuthorityDeleteCmd authorityDeleteCmd) {
    return authorityDeleteCmdExe.execute(authorityDeleteCmd);
  }

  @Override
  public AuthorityUpdateCo updateById(AuthorityUpdateCmd authorityUpdateCmd) {
    return authorityUpdateCmdExe.execute(authorityUpdateCmd);
  }

  @Override
  public Page<AuthorityFindAllCo> findAll(
      AuthorityFindAllCmd authorityFindAllCmd) {
    return authorityFindAllCmdExe.execute(authorityFindAllCmd);
  }

  @Override
  @PreAuthorize("hasRole('admin')")
  public void deleteById(AuthorityDeleteGrpcCmd request,
      StreamObserver<AuthorityDeleteGrpcCo> responseObserver) {
    AuthorityDeleteCmd authorityDeleteCmd = new AuthorityDeleteCmd();
    AuthorityDeleteCo authorityDeleteCo = getAuthorityDeleteCo(
        request);
    authorityDeleteCmd.setAuthorityDeleteCo(authorityDeleteCo);
    try {
      authorityDeleteCmdExe.execute(authorityDeleteCmd);
    } catch (CentaurException e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(request.getAuthorityDeleteCo());
    responseObserver.onCompleted();
  }

  @Override
  @PreAuthorize("hasRole('admin')")
  public void updateById(AuthorityUpdateGrpcCmd request,
      StreamObserver<AuthorityUpdateGrpcCo> responseObserver) {
    AuthorityUpdateCmd authorityUpdateCmd = new AuthorityUpdateCmd();
    AuthorityUpdateCo authorityUpdateCo = getAuthorityUpdateCo(
        request);
    authorityUpdateCmd.setAuthorityUpdateCo(authorityUpdateCo);
    try {
      authorityUpdateCmdExe.execute(authorityUpdateCmd);
    } catch (CentaurException e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(request.getAuthorityUpdateCo());
    responseObserver.onCompleted();
  }

  @Override
  @PreAuthorize("hasRole('admin')")
  public void findAll(AuthorityFindAllGrpcCmd request,
      StreamObserver<PageOfAuthorityFindAllGrpcCo> responseObserver) {
    AuthorityFindAllCmd authorityFindAllCmd = new AuthorityFindAllCmd();
    authorityFindAllCmd.setAuthorityFindAllCo(getAuthorityFindAllCo(request));
    authorityFindAllCmd.setPageNo(request.hasPageNo() ? request.getPageNo().getValue() : 0);
    authorityFindAllCmd.setPageSize(request.hasPageSize() ? request.getPageSize().getValue() : 10);
    Builder builder = PageOfAuthorityFindAllGrpcCo.newBuilder();
    try {
      Page<AuthorityFindAllCo> authorityFindAllCos = authorityFindAllCmdExe.execute(
          authorityFindAllCmd);
      List<AuthorityFindAllGrpcCo> findAllGrpcCos = authorityFindAllCos.getContent().stream()
          .map(authorityFindAllCo -> AuthorityFindAllGrpcCo.newBuilder()
              .setId(Int64Value.of(authorityFindAllCo.getId()))
              .setCode(StringValue.of(authorityFindAllCo.getCode())).setName(
                  StringValue.of(authorityFindAllCo.getName())).build()).toList();
      builder.addAllContent(findAllGrpcCos);
      builder.setTotalPages(authorityFindAllCos.getTotalPages());
    } catch (CentaurException e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }
}
