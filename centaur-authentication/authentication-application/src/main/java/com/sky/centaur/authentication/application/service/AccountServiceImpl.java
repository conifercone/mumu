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
import com.sky.centaur.authentication.application.account.executor.AccountChangePasswordCmdExe;
import com.sky.centaur.authentication.application.account.executor.AccountCurrentLoginQueryCmdExe;
import com.sky.centaur.authentication.application.account.executor.AccountDeleteCurrentCmdExe;
import com.sky.centaur.authentication.application.account.executor.AccountDisableCmdExe;
import com.sky.centaur.authentication.application.account.executor.AccountOnlineStatisticsCmdExe;
import com.sky.centaur.authentication.application.account.executor.AccountPasswordVerifyCmdExe;
import com.sky.centaur.authentication.application.account.executor.AccountRegisterCmdExe;
import com.sky.centaur.authentication.application.account.executor.AccountResetPasswordCmdExe;
import com.sky.centaur.authentication.application.account.executor.AccountUpdateByIdCmdExe;
import com.sky.centaur.authentication.application.account.executor.AccountUpdateRoleCmdExe;
import com.sky.centaur.authentication.client.api.AccountService;
import com.sky.centaur.authentication.client.api.grpc.AccountRegisterGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AccountRegisterGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AccountServiceGrpc.AccountServiceImplBase;
import com.sky.centaur.authentication.client.api.grpc.AccountUpdateByIdGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AccountUpdateByIdGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AccountUpdateRoleGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AccountUpdateRoleGrpcCo;
import com.sky.centaur.authentication.client.dto.AccountChangePasswordCmd;
import com.sky.centaur.authentication.client.dto.AccountDisableCmd;
import com.sky.centaur.authentication.client.dto.AccountPasswordVerifyCmd;
import com.sky.centaur.authentication.client.dto.AccountRegisterCmd;
import com.sky.centaur.authentication.client.dto.AccountResetPasswordCmd;
import com.sky.centaur.authentication.client.dto.AccountUpdateByIdCmd;
import com.sky.centaur.authentication.client.dto.AccountUpdateRoleCmd;
import com.sky.centaur.authentication.client.dto.co.AccountCurrentLoginQueryCo;
import com.sky.centaur.authentication.client.dto.co.AccountOnlineStatisticsCo;
import com.sky.centaur.authentication.client.dto.co.AccountRegisterCo;
import com.sky.centaur.authentication.client.dto.co.AccountUpdateByIdCo;
import com.sky.centaur.authentication.client.dto.co.AccountUpdateRoleCo;
import com.sky.centaur.basis.enums.SexEnum;
import com.sky.centaur.basis.exception.CentaurException;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.GRpcService;
import org.lognet.springboot.grpc.recovery.GRpcRuntimeExceptionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 账户功能实现
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class})
@Observed(name = "AccountServiceImpl")
public class AccountServiceImpl extends AccountServiceImplBase implements AccountService {

  private final AccountRegisterCmdExe accountRegisterCmdExe;

  private final AccountUpdateByIdCmdExe accountUpdateByIdCmdExe;

  private final AccountDisableCmdExe accountDisableCmdExe;

  private final AccountCurrentLoginQueryCmdExe accountCurrentLoginQueryCmdExe;

  private final AccountOnlineStatisticsCmdExe accountOnlineStatisticsCmdExe;

  private final AccountResetPasswordCmdExe accountResetPasswordCmdExe;

  private final AccountDeleteCurrentCmdExe accountDeleteCurrentCmdExe;

  private final AccountUpdateRoleCmdExe accountUpdateRoleCmdExe;
  private final AccountPasswordVerifyCmdExe accountPasswordVerifyCmdExe;
  private final AccountChangePasswordCmdExe accountChangePasswordCmdExe;

  @Autowired
  public AccountServiceImpl(AccountRegisterCmdExe accountRegisterCmdExe,
      AccountUpdateByIdCmdExe accountUpdateByIdCmdExe, AccountDisableCmdExe accountDisableCmdExe,
      AccountCurrentLoginQueryCmdExe accountCurrentLoginQueryCmdExe,
      AccountOnlineStatisticsCmdExe accountOnlineStatisticsCmdExe,
      AccountResetPasswordCmdExe accountResetPasswordCmdExe,
      AccountDeleteCurrentCmdExe accountDeleteCurrentCmdExe,
      AccountUpdateRoleCmdExe accountUpdateRoleCmdExe,
      AccountPasswordVerifyCmdExe accountPasswordVerifyCmdExe,
      AccountChangePasswordCmdExe accountChangePasswordCmdExe) {
    this.accountRegisterCmdExe = accountRegisterCmdExe;
    this.accountUpdateByIdCmdExe = accountUpdateByIdCmdExe;
    this.accountDisableCmdExe = accountDisableCmdExe;
    this.accountCurrentLoginQueryCmdExe = accountCurrentLoginQueryCmdExe;
    this.accountOnlineStatisticsCmdExe = accountOnlineStatisticsCmdExe;
    this.accountResetPasswordCmdExe = accountResetPasswordCmdExe;
    this.accountDeleteCurrentCmdExe = accountDeleteCurrentCmdExe;
    this.accountUpdateRoleCmdExe = accountUpdateRoleCmdExe;
    this.accountPasswordVerifyCmdExe = accountPasswordVerifyCmdExe;
    this.accountChangePasswordCmdExe = accountChangePasswordCmdExe;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void register(AccountRegisterCmd accountRegisterCmd) {
    accountRegisterCmdExe.execute(accountRegisterCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void register(AccountRegisterGrpcCmd request,
      StreamObserver<Empty> responseObserver) {
    AccountRegisterCmd accountRegisterCmd = new AccountRegisterCmd();
    AccountRegisterCo accountRegisterCo = getAccountRegisterCo(
        request);
    accountRegisterCmd.setAccountRegisterCo(accountRegisterCo);
    try {
      accountRegisterCmdExe.execute(accountRegisterCmd);
    } catch (CentaurException e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(Empty.newBuilder().build());
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
    accountRegisterCo.setRoleCode(accountRegisterGrpcCo.getRoleCode());
    accountRegisterCo.setAvatarUrl(accountRegisterGrpcCo.getAvatarUrl());
    accountRegisterCo.setPhone(accountRegisterGrpcCo.getPhone());
    accountRegisterCo.setEmail(accountRegisterGrpcCo.getEmail());
    accountRegisterCo.setTimezone(accountRegisterGrpcCo.getTimezone());
    accountRegisterCo.setSex(SexEnum.valueOf(accountRegisterGrpcCo.getSex().name()));
    return accountRegisterCo;
  }

  @Override
  public void updateById(AccountUpdateByIdCmd accountUpdateByIdCmd) {
    accountUpdateByIdCmdExe.execute(accountUpdateByIdCmd);
  }

  @NotNull
  private static AccountUpdateByIdCo getAccountUpdateByIdCo(
      @NotNull AccountUpdateByIdGrpcCmd request) {
    AccountUpdateByIdCo accountUpdateByIdCo = new AccountUpdateByIdCo();
    AccountUpdateByIdGrpcCo accountUpdateByIdGrpcCo = request.getAccountUpdateByIdGrpcCo();
    accountUpdateByIdCo.setId(
        accountUpdateByIdGrpcCo.hasId() ? accountUpdateByIdGrpcCo.getId().getValue() : null);
    accountUpdateByIdCo.setAvatarUrl(
        accountUpdateByIdGrpcCo.hasAvatarUrl() ? accountUpdateByIdGrpcCo.getAvatarUrl().getValue()
            : null);
    accountUpdateByIdCo.setPhone(
        accountUpdateByIdGrpcCo.hasPhone() ? accountUpdateByIdGrpcCo.getPhone().getValue() : null);
    accountUpdateByIdCo.setSex(accountUpdateByIdGrpcCo.hasSex() ? SexEnum.valueOf(
        accountUpdateByIdGrpcCo.getSex().name()) : null);
    accountUpdateByIdCo.setEmail(
        accountUpdateByIdGrpcCo.hasEmail() ? accountUpdateByIdGrpcCo.getEmail().getValue() : null);
    accountUpdateByIdCo.setTimezone(
        accountUpdateByIdGrpcCo.hasTimezone() ? accountUpdateByIdGrpcCo.getTimezone().getValue()
            : null);
    return accountUpdateByIdCo;
  }

  @Override
  @PreAuthorize("hasAuthority('message.write')")
  @Transactional(rollbackFor = Exception.class)
  public void updateById(AccountUpdateByIdGrpcCmd request,
      StreamObserver<Empty> responseObserver) {
    AccountUpdateByIdCmd accountUpdateByIdCmd = new AccountUpdateByIdCmd();
    AccountUpdateByIdCo accountUpdateByIdCo = getAccountUpdateByIdCo(
        request);
    accountUpdateByIdCmd.setAccountUpdateByIdCo(accountUpdateByIdCo);
    try {
      accountUpdateByIdCmdExe.execute(accountUpdateByIdCmd);
    } catch (CentaurException e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateRoleById(AccountUpdateRoleCmd accountUpdateRoleCmd) {
    accountUpdateRoleCmdExe.execute(accountUpdateRoleCmd);
  }

  @NotNull
  private static AccountUpdateRoleCo getAccountUpdateRoleCo(
      @NotNull AccountUpdateRoleGrpcCmd request) {
    AccountUpdateRoleCo accountUpdateRoleCo = new AccountUpdateRoleCo();
    AccountUpdateRoleGrpcCo accountUpdateRoleGrpcCo = request.getAccountUpdateRoleGrpcCo();
    accountUpdateRoleCo.setId(
        accountUpdateRoleGrpcCo.hasId() ? accountUpdateRoleGrpcCo.getId().getValue() : null);
    accountUpdateRoleCo.setRoleCode(
        accountUpdateRoleGrpcCo.hasRoleCode() ? accountUpdateRoleGrpcCo.getRoleCode().getValue()
            : null);
    return accountUpdateRoleCo;
  }

  @Override
  @PreAuthorize("hasRole('admin')")
  @Transactional(rollbackFor = Exception.class)
  public void updateRoleById(AccountUpdateRoleGrpcCmd request,
      StreamObserver<Empty> responseObserver) {
    AccountUpdateRoleCmd accountUpdateRoleCmd = new AccountUpdateRoleCmd();
    AccountUpdateRoleCo accountUpdateRoleCo = getAccountUpdateRoleCo(
        request);
    accountUpdateRoleCmd.setAccountUpdateRoleCo(accountUpdateRoleCo);
    try {
      accountUpdateRoleCmdExe.execute(accountUpdateRoleCmd);
    } catch (CentaurException e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void disable(AccountDisableCmd accountDisableCmd) {
    accountDisableCmdExe.execute(accountDisableCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AccountCurrentLoginQueryCo queryCurrentLoginAccount() {
    return accountCurrentLoginQueryCmdExe.execute();
  }

  @Override
  public AccountOnlineStatisticsCo onlineAccounts() {
    return accountOnlineStatisticsCmdExe.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void resetPassword(AccountResetPasswordCmd accountResetPasswordCmd) {
    accountResetPasswordCmdExe.execute(accountResetPasswordCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteCurrentAccount() {
    accountDeleteCurrentCmdExe.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean verifyPassword(AccountPasswordVerifyCmd accountPasswordVerifyCmd) {
    return accountPasswordVerifyCmdExe.execute(accountPasswordVerifyCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void changePassword(AccountChangePasswordCmd accountChangePasswordCmd) {
    accountChangePasswordCmdExe.execute(accountChangePasswordCmd);
  }
}
