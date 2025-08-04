/*
 * Copyright (c) 2024-2025, the original author or authors.
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

package baby.mumu.iam.application.service;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import baby.mumu.iam.application.account.executor.AccountAddAddressCmdExe;
import baby.mumu.iam.application.account.executor.AccountAddSystemSettingsCmdExe;
import baby.mumu.iam.application.account.executor.AccountArchiveByIdCmdExe;
import baby.mumu.iam.application.account.executor.AccountBasicInfoQueryByIdCmdExe;
import baby.mumu.iam.application.account.executor.AccountChangePasswordCmdExe;
import baby.mumu.iam.application.account.executor.AccountCurrentLoginQueryCmdExe;
import baby.mumu.iam.application.account.executor.AccountDeleteAddressByIdCmdExe;
import baby.mumu.iam.application.account.executor.AccountDeleteCurrentCmdExe;
import baby.mumu.iam.application.account.executor.AccountDeleteSystemSettingsByIdCmdExe;
import baby.mumu.iam.application.account.executor.AccountDisableCmdExe;
import baby.mumu.iam.application.account.executor.AccountFindAllCmdExe;
import baby.mumu.iam.application.account.executor.AccountFindAllSliceCmdExe;
import baby.mumu.iam.application.account.executor.AccountLogoutCmdExe;
import baby.mumu.iam.application.account.executor.AccountModifyAddressByAddressIdCmdExe;
import baby.mumu.iam.application.account.executor.AccountModifySystemSettingsBySettingsIdCmdExe;
import baby.mumu.iam.application.account.executor.AccountNearbyCmdExe;
import baby.mumu.iam.application.account.executor.AccountOfflineCmdExe;
import baby.mumu.iam.application.account.executor.AccountOnlineStatisticsCmdExe;
import baby.mumu.iam.application.account.executor.AccountPasswordVerifyCmdExe;
import baby.mumu.iam.application.account.executor.AccountRecoverFromArchiveByIdCmdExe;
import baby.mumu.iam.application.account.executor.AccountRegisterCmdExe;
import baby.mumu.iam.application.account.executor.AccountResetPasswordCmdExe;
import baby.mumu.iam.application.account.executor.AccountResetSystemSettingsBySettingsIdCmdExe;
import baby.mumu.iam.application.account.executor.AccountSetDefaultAddressCmdExe;
import baby.mumu.iam.application.account.executor.AccountSetDefaultSystemSettingsCmdExe;
import baby.mumu.iam.application.account.executor.AccountUpdateByIdCmdExe;
import baby.mumu.iam.application.account.executor.AccountUpdateRoleCmdExe;
import baby.mumu.iam.client.api.AccountService;
import baby.mumu.iam.client.api.grpc.AccountCurrentLoginGrpcDTO;
import baby.mumu.iam.client.api.grpc.AccountServiceGrpc.AccountServiceImplBase;
import baby.mumu.iam.client.cmds.AccountAddAddressCmd;
import baby.mumu.iam.client.cmds.AccountAddSystemSettingsCmd;
import baby.mumu.iam.client.cmds.AccountChangePasswordCmd;
import baby.mumu.iam.client.cmds.AccountDeleteCurrentCmd;
import baby.mumu.iam.client.cmds.AccountFindAllCmd;
import baby.mumu.iam.client.cmds.AccountFindAllSliceCmd;
import baby.mumu.iam.client.cmds.AccountModifyAddressByAddressIdCmd;
import baby.mumu.iam.client.cmds.AccountModifySystemSettingsBySettingsIdCmd;
import baby.mumu.iam.client.cmds.AccountPasswordVerifyCmd;
import baby.mumu.iam.client.cmds.AccountRegisterCmd;
import baby.mumu.iam.client.cmds.AccountUpdateByIdCmd;
import baby.mumu.iam.client.cmds.AccountUpdateRoleCmd;
import baby.mumu.iam.client.dto.AccountBasicInfoDTO;
import baby.mumu.iam.client.dto.AccountCurrentLoginDTO;
import baby.mumu.iam.client.dto.AccountFindAllDTO;
import baby.mumu.iam.client.dto.AccountFindAllSliceDTO;
import baby.mumu.iam.client.dto.AccountNearbyDTO;
import baby.mumu.iam.client.dto.AccountOnlineStatisticsDTO;
import baby.mumu.iam.client.dto.AccountUpdatedDataDTO;
import baby.mumu.iam.infra.account.convertor.AccountConvertor;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import net.devh.boot.grpc.server.service.GrpcService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 账号功能实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Service
@GrpcService(interceptors = {ObservationGrpcServerInterceptor.class, ClientIpInterceptor.class})
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
  private final AccountNearbyCmdExe accountNearbyCmdExe;
  private final AccountSetDefaultAddressCmdExe accountSetDefaultAddressCmdExe;
  private final AccountModifyAddressByAddressIdCmdExe accountModifyAddressByAddressIdCmdExe;
  private final AccountDeleteAddressByIdCmdExe accountDeleteAddressByIdCmdExe;
  private final AccountSetDefaultSystemSettingsCmdExe accountSetDefaultSystemSettingsCmdExe;
  private final AccountDeleteSystemSettingsByIdCmdExe accountDeleteSystemSettingsByIdCmdExe;

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
    AccountFindAllSliceCmdExe accountFindAllSliceCmdExe, AccountConvertor accountConvertor,
    AccountNearbyCmdExe accountNearbyCmdExe,
    AccountSetDefaultAddressCmdExe accountSetDefaultAddressCmdExe,
    AccountModifyAddressByAddressIdCmdExe accountModifyAddressByAddressIdCmdExe,
    AccountDeleteAddressByIdCmdExe accountDeleteAddressByIdCmdExe,
    AccountSetDefaultSystemSettingsCmdExe accountSetDefaultSystemSettingsCmdExe,
    AccountDeleteSystemSettingsByIdCmdExe accountDeleteSystemSettingsByIdCmdExe) {
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
    this.accountNearbyCmdExe = accountNearbyCmdExe;
    this.accountSetDefaultAddressCmdExe = accountSetDefaultAddressCmdExe;
    this.accountModifyAddressByAddressIdCmdExe = accountModifyAddressByAddressIdCmdExe;
    this.accountDeleteAddressByIdCmdExe = accountDeleteAddressByIdCmdExe;
    this.accountSetDefaultSystemSettingsCmdExe = accountSetDefaultSystemSettingsCmdExe;
    this.accountDeleteSystemSettingsByIdCmdExe = accountDeleteSystemSettingsByIdCmdExe;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Long register(AccountRegisterCmd accountRegisterCmd) {
    return accountRegisterCmdExe.execute(accountRegisterCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AccountUpdatedDataDTO updateById(AccountUpdateByIdCmd accountUpdateByIdCmd) {
    return accountUpdateByIdCmdExe.execute(accountUpdateByIdCmd);
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
  public AccountCurrentLoginDTO queryCurrentLoginAccount() {
    return accountCurrentLoginQueryCmdExe.execute();
  }

  @Override
  public AccountOnlineStatisticsDTO onlineAccounts() {
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
  public AccountBasicInfoDTO getAccountBasicInfoById(Long id) {
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
  public Page<AccountFindAllDTO> findAll(AccountFindAllCmd accountFindAllCmd) {
    return accountFindAllCmdExe.execute(accountFindAllCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Slice<AccountFindAllSliceDTO> findAllSlice(AccountFindAllSliceCmd accountFindAllSliceCmd) {
    return accountFindAllSliceCmdExe.execute(accountFindAllSliceCmd);
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  @Transactional(rollbackFor = Exception.class)
  public void queryCurrentLoginAccount(Empty request,
    @NotNull StreamObserver<AccountCurrentLoginGrpcDTO> responseObserver) {
    AccountCurrentLoginDTO accountCurrentLoginDTO = accountCurrentLoginQueryCmdExe.execute();
    responseObserver.onNext(accountConvertor.toAccountCurrentLoginGrpcDTO(accountCurrentLoginDTO)
      .orElse(AccountCurrentLoginGrpcDTO.getDefaultInstance()));
    responseObserver.onCompleted();

  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public List<AccountNearbyDTO> nearby(double radiusInMeters) {
    return accountNearbyCmdExe.execute(radiusInMeters);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void setDefaultAddress(String addressId) {
    accountSetDefaultAddressCmdExe.execute(addressId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void modifyAddressByAddressId(
    AccountModifyAddressByAddressIdCmd accountModifyAddressByAddressIdCmd) {
    accountModifyAddressByAddressIdCmdExe.execute(accountModifyAddressByAddressIdCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteAddress(String addressId) {
    accountDeleteAddressByIdCmdExe.execute(addressId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void setDefaultSystemSettings(String systemSettingsId) {
    accountSetDefaultSystemSettingsCmdExe.execute(systemSettingsId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteSystemSettings(String systemSettingsId) {
    accountDeleteSystemSettingsByIdCmdExe.execute(systemSettingsId);
  }
}
