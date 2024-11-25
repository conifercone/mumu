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
import baby.mumu.authentication.application.account.executor.AccountAddSystemSettingsCmdExe;
import baby.mumu.authentication.application.account.executor.AccountArchiveByIdCmdExe;
import baby.mumu.authentication.application.account.executor.AccountBasicInfoQueryByIdCmdExe;
import baby.mumu.authentication.application.account.executor.AccountChangePasswordCmdExe;
import baby.mumu.authentication.application.account.executor.AccountCurrentLoginQueryCmdExe;
import baby.mumu.authentication.application.account.executor.AccountDeleteCurrentCmdExe;
import baby.mumu.authentication.application.account.executor.AccountDisableCmdExe;
import baby.mumu.authentication.application.account.executor.AccountFindAllCmdExe;
import baby.mumu.authentication.application.account.executor.AccountFindAllSliceCmdExe;
import baby.mumu.authentication.application.account.executor.AccountLogoutCmdExe;
import baby.mumu.authentication.application.account.executor.AccountModifySystemSettingsBySettingsIdCmdExe;
import baby.mumu.authentication.application.account.executor.AccountOfflineCmdExe;
import baby.mumu.authentication.application.account.executor.AccountOnlineStatisticsCmdExe;
import baby.mumu.authentication.application.account.executor.AccountPasswordVerifyCmdExe;
import baby.mumu.authentication.application.account.executor.AccountRecoverFromArchiveByIdCmdExe;
import baby.mumu.authentication.application.account.executor.AccountRegisterCmdExe;
import baby.mumu.authentication.application.account.executor.AccountResetPasswordCmdExe;
import baby.mumu.authentication.application.account.executor.AccountResetSystemSettingsBySettingsIdCmdExe;
import baby.mumu.authentication.application.account.executor.AccountUpdateByIdCmdExe;
import baby.mumu.authentication.application.account.executor.AccountUpdateRoleCmdExe;
import baby.mumu.authentication.client.api.AccountService;
import baby.mumu.authentication.client.api.grpc.AccountCurrentLoginGrpcCo;
import baby.mumu.authentication.client.api.grpc.AccountServiceGrpc.AccountServiceImplBase;
import baby.mumu.authentication.client.dto.AccountAddAddressCmd;
import baby.mumu.authentication.client.dto.AccountAddSystemSettingsCmd;
import baby.mumu.authentication.client.dto.AccountChangePasswordCmd;
import baby.mumu.authentication.client.dto.AccountDeleteCurrentCmd;
import baby.mumu.authentication.client.dto.AccountFindAllCmd;
import baby.mumu.authentication.client.dto.AccountFindAllSliceCmd;
import baby.mumu.authentication.client.dto.AccountModifySystemSettingsBySettingsIdCmd;
import baby.mumu.authentication.client.dto.AccountPasswordVerifyCmd;
import baby.mumu.authentication.client.dto.AccountRegisterCmd;
import baby.mumu.authentication.client.dto.AccountUpdateByIdCmd;
import baby.mumu.authentication.client.dto.AccountUpdateRoleCmd;
import baby.mumu.authentication.client.dto.co.AccountBasicInfoCo;
import baby.mumu.authentication.client.dto.co.AccountCurrentLoginCo;
import baby.mumu.authentication.client.dto.co.AccountFindAllCo;
import baby.mumu.authentication.client.dto.co.AccountFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.AccountOnlineStatisticsCo;
import baby.mumu.authentication.infrastructure.account.convertor.AccountConvertor;
import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
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
  private final AccountBasicInfoQueryByIdCmdExe accountBasicInfoQueryByIdCmdExe;
  private final AccountResetSystemSettingsBySettingsIdCmdExe accountResetSystemSettingsBySettingsIdCmdExe;
  private final AccountModifySystemSettingsBySettingsIdCmdExe accountModifySystemSettingsBySettingsIdCmdExe;
  private final AccountAddSystemSettingsCmdExe accountAddSystemSettingsCmdExe;
  private final AccountLogoutCmdExe accountLogoutCmdExe;
  private final AccountOfflineCmdExe accountOfflineCmdExe;
  private final AccountFindAllCmdExe accountFindAllCmdExe;
  private final AccountFindAllSliceCmdExe accountFindAllSliceCmdExe;
  private final AccountConvertor accountConvertor;

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
    AccountAddAddressCmdExe accountAddAddressCmdExe,
    AccountBasicInfoQueryByIdCmdExe accountBasicInfoQueryByIdCmdExe,
    AccountResetSystemSettingsBySettingsIdCmdExe accountResetSystemSettingsBySettingsIdCmdExe,
    AccountModifySystemSettingsBySettingsIdCmdExe accountModifySystemSettingsBySettingsIdCmdExe,
    AccountAddSystemSettingsCmdExe accountAddSystemSettingsCmdExe,
    AccountLogoutCmdExe accountLogoutCmdExe, AccountOfflineCmdExe accountOfflineCmdExe,
    AccountFindAllCmdExe accountFindAllCmdExe,
    AccountFindAllSliceCmdExe accountFindAllSliceCmdExe, AccountConvertor accountConvertor) {
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
    this.accountBasicInfoQueryByIdCmdExe = accountBasicInfoQueryByIdCmdExe;
    this.accountResetSystemSettingsBySettingsIdCmdExe = accountResetSystemSettingsBySettingsIdCmdExe;
    this.accountModifySystemSettingsBySettingsIdCmdExe = accountModifySystemSettingsBySettingsIdCmdExe;
    this.accountAddSystemSettingsCmdExe = accountAddSystemSettingsCmdExe;
    this.accountLogoutCmdExe = accountLogoutCmdExe;
    this.accountOfflineCmdExe = accountOfflineCmdExe;
    this.accountFindAllCmdExe = accountFindAllCmdExe;
    this.accountFindAllSliceCmdExe = accountFindAllSliceCmdExe;
    this.accountConvertor = accountConvertor;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void register(AccountRegisterCmd accountRegisterCmd) {
    accountRegisterCmdExe.execute(accountRegisterCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateById(AccountUpdateByIdCmd accountUpdateByIdCmd) {
    accountUpdateByIdCmdExe.execute(accountUpdateByIdCmd);
  }


  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateRoleById(AccountUpdateRoleCmd accountUpdateRoleCmd) {
    accountUpdateRoleCmdExe.execute(accountUpdateRoleCmd);
  }


  @Override
  @Transactional(rollbackFor = Exception.class)
  public void disable(Long id) {
    accountDisableCmdExe.execute(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void logout() {
    accountLogoutCmdExe.execute();
  }


  @Override
  @Transactional(rollbackFor = Exception.class)
  public AccountCurrentLoginCo queryCurrentLoginAccount() {
    return accountCurrentLoginQueryCmdExe.execute();
  }

  @Override
  public AccountOnlineStatisticsCo onlineAccounts() {
    return accountOnlineStatisticsCmdExe.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void resetPassword(Long id) {
    accountResetPasswordCmdExe.execute(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void resetSystemSettingsBySettingsId(
    String systemSettingsId) {
    accountResetSystemSettingsBySettingsIdCmdExe.execute(systemSettingsId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void modifySystemSettingsBySettingsId(
    AccountModifySystemSettingsBySettingsIdCmd accountModifySystemSettingsBySettingsIdCmd) {
    accountModifySystemSettingsBySettingsIdCmdExe.execute(
      accountModifySystemSettingsBySettingsIdCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void addSystemSettings(
    AccountAddSystemSettingsCmd accountAddSystemSettingsCmd) {
    accountAddSystemSettingsCmdExe.execute(
      accountAddSystemSettingsCmd);
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
  public void archiveById(Long accountId) {
    accountArchiveByIdCmdExe.execute(accountId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AccountBasicInfoCo getAccountBasicInfoById(Long id) {
    return accountBasicInfoQueryByIdCmdExe.execute(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(
    Long accountId) {
    accountRecoverFromArchiveByIdCmdExe.execute(accountId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void addAddress(AccountAddAddressCmd accountAddAddressCmd) {
    accountAddAddressCmdExe.execute(accountAddAddressCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void offline(Long accountId) {
    accountOfflineCmdExe.execute(accountId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Page<AccountFindAllCo> findAll(AccountFindAllCmd accountFindAllCmd) {
    return accountFindAllCmdExe.execute(accountFindAllCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Slice<AccountFindAllSliceCo> findAllSlice(AccountFindAllSliceCmd accountFindAllSliceCmd) {
    return accountFindAllSliceCmdExe.execute(accountFindAllSliceCmd);
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  @Transactional(rollbackFor = Exception.class)
  public void queryCurrentLoginAccount(Empty request,
    StreamObserver<AccountCurrentLoginGrpcCo> responseObserver) {
    AccountCurrentLoginCo accountCurrentLoginCo = accountCurrentLoginQueryCmdExe.execute();
    responseObserver.onNext(accountConvertor.toAccountCurrentLoginGrpcCo(accountCurrentLoginCo)
      .orElse(AccountCurrentLoginGrpcCo.getDefaultInstance()));
    responseObserver.onCompleted();

  }
}
