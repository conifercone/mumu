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

import baby.mumu.authentication.application.account.executor.AccountAddAddressCmdExe;
import baby.mumu.authentication.application.account.executor.AccountArchiveByIdCmdExe;
import baby.mumu.authentication.application.account.executor.AccountChangePasswordCmdExe;
import baby.mumu.authentication.application.account.executor.AccountCurrentLoginQueryCmdExe;
import baby.mumu.authentication.application.account.executor.AccountDeleteCurrentCmdExe;
import baby.mumu.authentication.application.account.executor.AccountDisableCmdExe;
import baby.mumu.authentication.application.account.executor.AccountOnlineStatisticsCmdExe;
import baby.mumu.authentication.application.account.executor.AccountPasswordVerifyCmdExe;
import baby.mumu.authentication.application.account.executor.AccountRecoverFromArchiveByIdCmdExe;
import baby.mumu.authentication.application.account.executor.AccountRegisterCmdExe;
import baby.mumu.authentication.application.account.executor.AccountResetPasswordCmdExe;
import baby.mumu.authentication.application.account.executor.AccountUpdateByIdCmdExe;
import baby.mumu.authentication.application.account.executor.AccountUpdateRoleCmdExe;
import baby.mumu.authentication.client.api.AccountService;
import baby.mumu.authentication.client.api.grpc.AccountAddressRegisterGrpcCo;
import baby.mumu.authentication.client.api.grpc.AccountDisableGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AccountDisableGrpcCo;
import baby.mumu.authentication.client.api.grpc.AccountRegisterGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AccountRegisterGrpcCo;
import baby.mumu.authentication.client.api.grpc.AccountServiceGrpc.AccountServiceImplBase;
import baby.mumu.authentication.client.api.grpc.AccountUpdateByIdGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AccountUpdateByIdGrpcCo;
import baby.mumu.authentication.client.api.grpc.AccountUpdateRoleGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AccountUpdateRoleGrpcCo;
import baby.mumu.authentication.client.dto.AccountAddAddressCmd;
import baby.mumu.authentication.client.dto.AccountArchiveByIdCmd;
import baby.mumu.authentication.client.dto.AccountChangePasswordCmd;
import baby.mumu.authentication.client.dto.AccountDeleteCurrentCmd;
import baby.mumu.authentication.client.dto.AccountDisableCmd;
import baby.mumu.authentication.client.dto.AccountPasswordVerifyCmd;
import baby.mumu.authentication.client.dto.AccountRecoverFromArchiveByIdCmd;
import baby.mumu.authentication.client.dto.AccountRegisterCmd;
import baby.mumu.authentication.client.dto.AccountResetPasswordCmd;
import baby.mumu.authentication.client.dto.AccountUpdateByIdCmd;
import baby.mumu.authentication.client.dto.AccountUpdateRoleCmd;
import baby.mumu.authentication.client.dto.co.AccountCurrentLoginQueryCo;
import baby.mumu.authentication.client.dto.co.AccountDisableCo;
import baby.mumu.authentication.client.dto.co.AccountOnlineStatisticsCo;
import baby.mumu.authentication.client.dto.co.AccountRegisterCo;
import baby.mumu.authentication.client.dto.co.AccountRegisterCo.AccountAddressRegisterCo;
import baby.mumu.authentication.client.dto.co.AccountUpdateByIdCo;
import baby.mumu.authentication.client.dto.co.AccountUpdateRoleCo;
import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.enums.LanguageEnum;
import baby.mumu.basis.enums.SexEnum;
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import java.time.LocalDate;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.GRpcService;
import org.lognet.springboot.grpc.recovery.GRpcRuntimeExceptionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 账户功能实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class, ClientIpInterceptor.class})
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
  private final AccountArchiveByIdCmdExe accountArchiveByIdCmdExe;
  private final AccountRecoverFromArchiveByIdCmdExe accountRecoverFromArchiveByIdCmdExe;
  private final AccountAddAddressCmdExe accountAddAddressCmdExe;

  @Autowired
  public AccountServiceImpl(AccountRegisterCmdExe accountRegisterCmdExe,
      AccountUpdateByIdCmdExe accountUpdateByIdCmdExe, AccountDisableCmdExe accountDisableCmdExe,
      AccountCurrentLoginQueryCmdExe accountCurrentLoginQueryCmdExe,
      AccountOnlineStatisticsCmdExe accountOnlineStatisticsCmdExe,
      AccountResetPasswordCmdExe accountResetPasswordCmdExe,
      AccountDeleteCurrentCmdExe accountDeleteCurrentCmdExe,
      AccountUpdateRoleCmdExe accountUpdateRoleCmdExe,
      AccountPasswordVerifyCmdExe accountPasswordVerifyCmdExe,
      AccountChangePasswordCmdExe accountChangePasswordCmdExe,
      AccountArchiveByIdCmdExe accountArchiveByIdCmdExe,
      AccountRecoverFromArchiveByIdCmdExe accountRecoverFromArchiveByIdCmdExe,
      AccountAddAddressCmdExe accountAddAddressCmdExe) {
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
    this.accountArchiveByIdCmdExe = accountArchiveByIdCmdExe;
    this.accountRecoverFromArchiveByIdCmdExe = accountRecoverFromArchiveByIdCmdExe;
    this.accountAddAddressCmdExe = accountAddAddressCmdExe;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void register(AccountRegisterCmd accountRegisterCmd) {
    accountRegisterCmdExe.execute(accountRegisterCmd);
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void register(@NotNull AccountRegisterGrpcCmd request,
      StreamObserver<Empty> responseObserver) {
    AccountRegisterCmd accountRegisterCmd = new AccountRegisterCmd();
    accountRegisterCmd.setCaptchaId(
        request.hasCaptchaId() ? request.getCaptchaId().getValue() : null);
    accountRegisterCmd.setCaptcha(
        request.hasCaptcha() ? request.getCaptcha().getValue() : null);
    AccountRegisterCo accountRegisterCo = getAccountRegisterCo(
        request);
    accountRegisterCmd.setAccountRegisterCo(accountRegisterCo);
    try {
      accountRegisterCmdExe.execute(accountRegisterCmd);
    } catch (Exception e) {
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
    accountRegisterCo.setId(
        accountRegisterGrpcCo.hasId() ? accountRegisterGrpcCo.getId().getValue() : null);
    accountRegisterCo.setUsername(
        accountRegisterGrpcCo.hasUsername() ? accountRegisterGrpcCo.getUsername().getValue()
            : null);
    accountRegisterCo.setPassword(
        accountRegisterGrpcCo.hasPassword() ? accountRegisterGrpcCo.getPassword().getValue()
            : null);
    accountRegisterCo.setRoleCode(
        accountRegisterGrpcCo.hasRoleCode() ? accountRegisterGrpcCo.getRoleCode().getValue()
            : null);
    accountRegisterCo.setAvatarUrl(
        accountRegisterGrpcCo.hasAvatarUrl() ? accountRegisterGrpcCo.getAvatarUrl().getValue()
            : null);
    accountRegisterCo.setPhone(
        accountRegisterGrpcCo.hasPhone() ? accountRegisterGrpcCo.getPhone().getValue() : null);
    accountRegisterCo.setEmail(
        accountRegisterGrpcCo.hasEmail() ? accountRegisterGrpcCo.getEmail().getValue() : null);
    accountRegisterCo.setTimezone(
        accountRegisterGrpcCo.hasTimezone() ? accountRegisterGrpcCo.getTimezone().getValue()
            : null);
    accountRegisterCo.setSex(
        accountRegisterGrpcCo.hasSex() ? SexEnum.valueOf(accountRegisterGrpcCo.getSex().name())
            : null);
    accountRegisterCo.setLanguage(accountRegisterGrpcCo.hasLanguage() ? LanguageEnum.valueOf(
        accountRegisterGrpcCo.getLanguage().name()) : null);
    accountRegisterCo.setBirthday(accountRegisterGrpcCo.hasBirthday() ? LocalDate.of(
        accountRegisterGrpcCo.getBirthday().getYear().getValue(),
        accountRegisterGrpcCo.getBirthday().getMonth().getValue(),
        accountRegisterGrpcCo.getBirthday().getDay().getValue()) : null);
    accountRegisterCo.setAddress(accountRegisterGrpcCo.hasAddress() ? getAccountAddressRegisterCo(
        accountRegisterGrpcCo.getAddress()) : null);
    return accountRegisterCo;
  }

  private static @NotNull AccountAddressRegisterCo getAccountAddressRegisterCo(
      @NotNull AccountAddressRegisterGrpcCo accountAddressRegisterGrpcCo) {
    AccountAddressRegisterCo accountAddressRegisterCo = new AccountAddressRegisterCo();
    accountAddressRegisterCo.setId(
        accountAddressRegisterGrpcCo.hasId() ? accountAddressRegisterGrpcCo.getId().getValue()
            : null);
    accountAddressRegisterCo.setStreet(
        accountAddressRegisterGrpcCo.hasStreet() ? accountAddressRegisterGrpcCo.getStreet()
            .getValue() : null);
    accountAddressRegisterCo.setCity(
        accountAddressRegisterGrpcCo.hasCity() ? accountAddressRegisterGrpcCo.getCity().getValue()
            : null);
    accountAddressRegisterCo.setState(
        accountAddressRegisterGrpcCo.hasState() ? accountAddressRegisterGrpcCo.getState().getValue()
            : null);
    accountAddressRegisterCo.setPostalCode(
        accountAddressRegisterGrpcCo.hasPostalCode() ? accountAddressRegisterGrpcCo.getPostalCode()
            .getValue() : null);
    accountAddressRegisterCo.setCountry(
        accountAddressRegisterGrpcCo.hasCountry() ? accountAddressRegisterGrpcCo.getCountry()
            .getValue() : null);
    return accountAddressRegisterCo;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
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
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void updateById(AccountUpdateByIdGrpcCmd request,
      StreamObserver<Empty> responseObserver) {
    AccountUpdateByIdCmd accountUpdateByIdCmd = new AccountUpdateByIdCmd();
    AccountUpdateByIdCo accountUpdateByIdCo = getAccountUpdateByIdCo(
        request);
    accountUpdateByIdCmd.setAccountUpdateByIdCo(accountUpdateByIdCo);
    try {
      accountUpdateByIdCmdExe.execute(accountUpdateByIdCmd);
    } catch (Exception e) {
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
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void updateRoleById(AccountUpdateRoleGrpcCmd request,
      StreamObserver<Empty> responseObserver) {
    AccountUpdateRoleCmd accountUpdateRoleCmd = new AccountUpdateRoleCmd();
    AccountUpdateRoleCo accountUpdateRoleCo = getAccountUpdateRoleCo(
        request);
    accountUpdateRoleCmd.setAccountUpdateRoleCo(accountUpdateRoleCo);
    try {
      accountUpdateRoleCmdExe.execute(accountUpdateRoleCmd);
    } catch (Exception e) {
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

  @NotNull
  private static AccountDisableCo getAccountDisableCo(
      @NotNull AccountDisableGrpcCmd request) {
    AccountDisableCo accountDisableCo = new AccountDisableCo();
    AccountDisableGrpcCo accountDisableGrpcCo = request.getAccountDisableGrpcCo();
    accountDisableCo.setId(
        accountDisableGrpcCo.hasId() ? accountDisableGrpcCo.getId().getValue() : null);
    return accountDisableCo;
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void disable(AccountDisableGrpcCmd request, StreamObserver<Empty> responseObserver) {
    AccountDisableCmd accountDisableCmd = new AccountDisableCmd();
    AccountDisableCo accountDisableCo = getAccountDisableCo(
        request);
    accountDisableCmd.setAccountDisableCo(accountDisableCo);
    try {
      accountDisableCmdExe.execute(accountDisableCmd);
    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
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
  public void deleteCurrentAccount(AccountDeleteCurrentCmd accountDeleteCurrentCmd) {
    accountDeleteCurrentCmdExe.execute(accountDeleteCurrentCmd);
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

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void archiveById(AccountArchiveByIdCmd accountArchiveByIdCmd) {
    accountArchiveByIdCmdExe.execute(accountArchiveByIdCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(
      AccountRecoverFromArchiveByIdCmd accountRecoverFromArchiveByIdCmd) {
    accountRecoverFromArchiveByIdCmdExe.execute(accountRecoverFromArchiveByIdCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void addAddress(AccountAddAddressCmd accountAddAddressCmd) {
    accountAddAddressCmdExe.execute(accountAddAddressCmd);
  }
}
