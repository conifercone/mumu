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
import com.sky.centaur.authentication.client.api.AuthorityService;
import com.sky.centaur.authentication.client.api.grpc.AuthorityAddGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AuthorityAddGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AuthorityServiceGrpc.AuthorityServiceImplBase;
import com.sky.centaur.authentication.client.dto.AuthorityAddCmd;
import com.sky.centaur.authentication.client.dto.co.AuthorityAddCo;
import com.sky.centaur.basis.exception.CentaurException;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.GRpcService;
import org.lognet.springboot.grpc.recovery.GRpcRuntimeExceptionWrapper;
import org.springframework.stereotype.Service;

/**
 * 权限管理
 *
 * @author 单开宇
 * @since 2024-02-23
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class})
@Observed(name = "AuthorityServiceImpl")
public class AuthorityServiceImpl extends AuthorityServiceImplBase implements AuthorityService {

  @Resource
  AuthorityAddCmdExe authorityAddCmdExe;

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
}
