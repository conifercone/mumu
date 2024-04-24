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
import jakarta.annotation.Resource;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.GRpcService;
import org.lognet.springboot.grpc.recovery.GRpcRuntimeExceptionWrapper;
import org.springframework.data.domain.Page;
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

  @Resource
  AuthorityAddCmdExe authorityAddCmdExe;

  @Resource
  AuthorityDeleteCmdExe authorityDeleteCmdExe;

  @Resource
  AuthorityUpdateCmdExe authorityUpdateCmdExe;

  @Resource
  AuthorityFindAllCmdExe authorityFindAllCmdExe;

  @Override
  public AuthorityAddCo add(AuthorityAddCmd authorityAddCmd) {
    return authorityAddCmdExe.execute(authorityAddCmd);
  }

  @Override
  public void add(AuthorityAddGrpcCmd request,
      StreamObserver<AuthorityAddGrpcCo> responseObserver) {
    AuthorityAddCmd authorityAddCmd = new AuthorityAddCmd();
    AuthorityAddCo authorityAddCo = getAuthorityAddCo(
        request);
    authorityAddCmd.setAuthorityAddCo(authorityAddCo);
    try {
      authorityAddCmdExe.execute(authorityAddCmd);
    } catch (CentaurException e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(request.getAuthorityAddCo());
    responseObserver.onCompleted();
  }

  @NotNull
  private static AuthorityAddCo getAuthorityAddCo(
      @NotNull AuthorityAddGrpcCmd request) {
    AuthorityAddCo authorityAddCo = new AuthorityAddCo();
    AuthorityAddGrpcCo authorityAddGrpcCo = request.getAuthorityAddCo();
    authorityAddCo.setId(authorityAddGrpcCo.getId());
    authorityAddCo.setCode(authorityAddGrpcCo.getCode());
    authorityAddCo.setName(authorityAddGrpcCo.getName());
    return authorityAddCo;
  }

  @NotNull
  private static AuthorityDeleteCo getAuthorityDeleteCo(
      @NotNull AuthorityDeleteGrpcCmd request) {
    AuthorityDeleteCo authorityDeleteCo = new AuthorityDeleteCo();
    AuthorityDeleteGrpcCo authorityDeleteGrpcCo = request.getAuthorityDeleteCo();
    authorityDeleteCo.setId(authorityDeleteGrpcCo.getId());
    return authorityDeleteCo;
  }

  @NotNull
  private static AuthorityUpdateCo getAuthorityUpdateCo(
      @NotNull AuthorityUpdateGrpcCmd request) {
    AuthorityUpdateCo authorityUpdateCo = new AuthorityUpdateCo();
    AuthorityUpdateGrpcCo authorityUpdateGrpcCo = request.getAuthorityUpdateCo();
    authorityUpdateCo.setId(authorityUpdateGrpcCo.getId());
    authorityUpdateCo.setCode(authorityUpdateGrpcCo.getCode());
    authorityUpdateCo.setName(authorityUpdateGrpcCo.getName());
    return authorityUpdateCo;
  }

  @NotNull
  private static AuthorityFindAllCo getAuthorityFindAllCo(
      @NotNull AuthorityFindAllGrpcCmd request) {
    AuthorityFindAllCo authorityFindAllCo = new AuthorityFindAllCo();
    AuthorityFindAllGrpcCo authorityFindAllGrpcCo = request.getAuthorityFindAllCo();
    authorityFindAllCo.setId(authorityFindAllGrpcCo.getId());
    authorityFindAllCo.setCode(authorityFindAllGrpcCo.getCode());
    authorityFindAllCo.setName(authorityFindAllGrpcCo.getName());
    return authorityFindAllCo;
  }

  @Override
  public AuthorityDeleteCo delete(AuthorityDeleteCmd authorityDeleteCmd) {
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
  public void delete(AuthorityDeleteGrpcCmd request,
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
  public void findAll(AuthorityFindAllGrpcCmd request,
      StreamObserver<PageOfAuthorityFindAllGrpcCo> responseObserver) {
    AuthorityFindAllCmd authorityFindAllCmd = new AuthorityFindAllCmd();
    authorityFindAllCmd.setAuthorityFindAllCo(getAuthorityFindAllCo(request));
    authorityFindAllCmd.setPageNo(request.getPageNo());
    authorityFindAllCmd.setPageSize(request.getPageSize());
    Builder builder = PageOfAuthorityFindAllGrpcCo.newBuilder();
    try {
      Page<AuthorityFindAllCo> authorityFindAllCos = authorityFindAllCmdExe.execute(
          authorityFindAllCmd);
      List<AuthorityFindAllGrpcCo> findAllGrpcCos = authorityFindAllCos.getContent().stream()
          .map(authorityFindAllCo -> AuthorityFindAllGrpcCo.newBuilder()
              .setId(authorityFindAllCo.getId())
              .setCode(authorityFindAllCo.getCode()).setName(
                  authorityFindAllCo.getName()).build()).toList();
      builder.addAllContent(findAllGrpcCos);
      builder.setTotalPages(authorityFindAllCos.getTotalPages());
    } catch (CentaurException e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }
}
