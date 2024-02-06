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

import com.sky.centaur.authentication.application.account.executor.AccountRegisterCmdExe;
import com.sky.centaur.authentication.client.api.AccountService;
import com.sky.centaur.authentication.client.api.grpc.AccountRegisterGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AccountRegisterGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AccountServiceGrpc.AccountServiceImplBase;
import com.sky.centaur.authentication.client.dto.AccountRegisterCmd;
import com.sky.centaur.authentication.client.dto.co.AccountRegisterCo;
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
 * 账户功能实现
 *
 * @author 单开宇
 * @since 2024-01-16
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class})
@Observed(name = "AccountServiceImpl")
public class AccountServiceImpl extends AccountServiceImplBase implements AccountService {

  @Resource
  private AccountRegisterCmdExe accountRegisterCmdExe;

  @Override
  public AccountRegisterCo register(AccountRegisterCmd accountRegisterCmd) {
    return accountRegisterCmdExe.execute(accountRegisterCmd);
  }

  @Override
  public void register(AccountRegisterGrpcCmd request,
      StreamObserver<AccountRegisterGrpcCo> responseObserver) {
    AccountRegisterCmd accountRegisterCmd = new AccountRegisterCmd();
    AccountRegisterCo accountRegisterCo = getAccountRegisterCo(
        request);
    accountRegisterCmd.setAccountRegisterCo(accountRegisterCo);
    try {
      accountRegisterCmdExe.execute(accountRegisterCmd);
    } catch (CentaurException e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(request.getAccountRegisterCo());
    responseObserver.onCompleted();
  }

  @NotNull
  private static AccountRegisterCo getAccountRegisterCo(
      @NotNull AccountRegisterGrpcCmd request) {
    AccountRegisterCo accountRegisterCo = new AccountRegisterCo();
    AccountRegisterGrpcCo accountRegisterGrpcCo = request.getAccountRegisterCo();
    accountRegisterCo.setId(accountRegisterGrpcCo.getId());
    accountRegisterCo.setUsername(accountRegisterGrpcCo.getUsername());
    accountRegisterCo.setPassword(accountRegisterGrpcCo.getPassword());
    return accountRegisterCo;
  }
}
