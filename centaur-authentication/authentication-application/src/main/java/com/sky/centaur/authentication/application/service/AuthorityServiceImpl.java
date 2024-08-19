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

import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import com.sky.centaur.authentication.application.authority.executor.AuthorityAddCmdExe;
import com.sky.centaur.authentication.application.authority.executor.AuthorityArchiveByIdCmdExe;
import com.sky.centaur.authentication.application.authority.executor.AuthorityDeleteByIdCmdExe;
import com.sky.centaur.authentication.application.authority.executor.AuthorityFindAllCmdExe;
import com.sky.centaur.authentication.application.authority.executor.AuthorityFindByIdCmdExe;
import com.sky.centaur.authentication.application.authority.executor.AuthorityRecoverFromArchiveByIdCmdExe;
import com.sky.centaur.authentication.application.authority.executor.AuthorityUpdateCmdExe;
import com.sky.centaur.authentication.client.api.AuthorityService;
import com.sky.centaur.authentication.client.api.grpc.AuthorityAddGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AuthorityAddGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AuthorityDeleteByIdGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AuthorityFindAllGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AuthorityFindAllGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AuthorityServiceGrpc.AuthorityServiceImplBase;
import com.sky.centaur.authentication.client.api.grpc.AuthorityUpdateGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AuthorityUpdateGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.PageOfAuthorityFindAllGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.PageOfAuthorityFindAllGrpcCo.Builder;
import com.sky.centaur.authentication.client.dto.AuthorityAddCmd;
import com.sky.centaur.authentication.client.dto.AuthorityArchiveByIdCmd;
import com.sky.centaur.authentication.client.dto.AuthorityDeleteByIdCmd;
import com.sky.centaur.authentication.client.dto.AuthorityFindAllCmd;
import com.sky.centaur.authentication.client.dto.AuthorityFindByIdCmd;
import com.sky.centaur.authentication.client.dto.AuthorityRecoverFromArchiveByIdCmd;
import com.sky.centaur.authentication.client.dto.AuthorityUpdateCmd;
import com.sky.centaur.authentication.client.dto.co.AuthorityAddCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityFindAllCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityFindByIdCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityUpdateCo;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.GRpcService;
import org.lognet.springboot.grpc.recovery.GRpcRuntimeExceptionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  private final AuthorityDeleteByIdCmdExe authorityDeleteByIdCmdExe;
  private final AuthorityUpdateCmdExe authorityUpdateCmdExe;
  private final AuthorityFindAllCmdExe authorityFindAllCmdExe;
  private final AuthorityFindByIdCmdExe authorityFindByIdCmdExe;
  private final AuthorityArchiveByIdCmdExe authorityArchiveByIdCmdExe;
  private final AuthorityRecoverFromArchiveByIdCmdExe authorityRecoverFromArchiveByIdCmdExe;

  @Autowired
  public AuthorityServiceImpl(AuthorityAddCmdExe authorityAddCmdExe,
      AuthorityDeleteByIdCmdExe authorityDeleteByIdCmdExe,
      AuthorityUpdateCmdExe authorityUpdateCmdExe,
      AuthorityFindAllCmdExe authorityFindAllCmdExe,
      AuthorityFindByIdCmdExe authorityFindByIdCmdExe,
      AuthorityArchiveByIdCmdExe authorityArchiveByIdCmdExe,
      AuthorityRecoverFromArchiveByIdCmdExe authorityRecoverFromArchiveByIdCmdExe) {
    this.authorityAddCmdExe = authorityAddCmdExe;
    this.authorityDeleteByIdCmdExe = authorityDeleteByIdCmdExe;
    this.authorityUpdateCmdExe = authorityUpdateCmdExe;
    this.authorityFindAllCmdExe = authorityFindAllCmdExe;
    this.authorityFindByIdCmdExe = authorityFindByIdCmdExe;
    this.authorityArchiveByIdCmdExe = authorityArchiveByIdCmdExe;
    this.authorityRecoverFromArchiveByIdCmdExe = authorityRecoverFromArchiveByIdCmdExe;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void add(AuthorityAddCmd authorityAddCmd) {
    authorityAddCmdExe.execute(authorityAddCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void add(AuthorityAddGrpcCmd request,
      StreamObserver<Empty> responseObserver) {
    AuthorityAddCmd authorityAddCmd = new AuthorityAddCmd();
    AuthorityAddCo authorityAddCo = getAuthorityAddCo(
        request);
    authorityAddCmd.setAuthorityAddCo(authorityAddCo);
    try {
      authorityAddCmdExe.execute(authorityAddCmd);

    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(Empty.newBuilder().build());
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
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(AuthorityDeleteByIdCmd authorityDeleteByIdCmd) {
    authorityDeleteByIdCmdExe.execute(authorityDeleteByIdCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateById(AuthorityUpdateCmd authorityUpdateCmd) {
    authorityUpdateCmdExe.execute(authorityUpdateCmd);
  }

  @Override
  public Page<AuthorityFindAllCo> findAll(
      AuthorityFindAllCmd authorityFindAllCmd) {
    return authorityFindAllCmdExe.execute(authorityFindAllCmd);
  }

  @Override
  public AuthorityFindByIdCo findById(AuthorityFindByIdCmd authorityFindByIdCmd) {
    return authorityFindByIdCmdExe.execute(authorityFindByIdCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteById(@NotNull AuthorityDeleteByIdGrpcCmd request,
      StreamObserver<Empty> responseObserver) {
    AuthorityDeleteByIdCmd authorityDeleteByIdCmd = new AuthorityDeleteByIdCmd();
    //noinspection DuplicatedCode
    authorityDeleteByIdCmd.setId(request.hasId() ? request.getId().getValue() : null);
    try {
      authorityDeleteByIdCmdExe.execute(authorityDeleteByIdCmd);
    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateById(AuthorityUpdateGrpcCmd request,
      StreamObserver<Empty> responseObserver) {
    AuthorityUpdateCmd authorityUpdateCmd = new AuthorityUpdateCmd();
    AuthorityUpdateCo authorityUpdateCo = getAuthorityUpdateCo(
        request);
    authorityUpdateCmd.setAuthorityUpdateCo(authorityUpdateCo);
    try {
      authorityUpdateCmdExe.execute(authorityUpdateCmd);
    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
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
    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void archiveById(AuthorityArchiveByIdCmd authorityArchiveByIdCmd) {
    authorityArchiveByIdCmdExe.execute(authorityArchiveByIdCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(
      AuthorityRecoverFromArchiveByIdCmd authorityRecoverFromArchiveByIdCmd) {
    authorityRecoverFromArchiveByIdCmdExe.execute(authorityRecoverFromArchiveByIdCmd);
  }
}
