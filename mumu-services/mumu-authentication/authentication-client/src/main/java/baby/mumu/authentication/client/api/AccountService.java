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
package baby.mumu.authentication.client.api;

import baby.mumu.authentication.client.dto.AccountAddAddressCmd;
import baby.mumu.authentication.client.dto.AccountAddSystemSettingsCmd;
import baby.mumu.authentication.client.dto.AccountArchiveByIdCmd;
import baby.mumu.authentication.client.dto.AccountBasicInfoByIdCmd;
import baby.mumu.authentication.client.dto.AccountChangePasswordCmd;
import baby.mumu.authentication.client.dto.AccountDeleteCurrentCmd;
import baby.mumu.authentication.client.dto.AccountDisableCmd;
import baby.mumu.authentication.client.dto.AccountFindAllCmd;
import baby.mumu.authentication.client.dto.AccountFindAllSliceCmd;
import baby.mumu.authentication.client.dto.AccountModifySystemSettingsBySettingsIdCmd;
import baby.mumu.authentication.client.dto.AccountOfflineCmd;
import baby.mumu.authentication.client.dto.AccountPasswordVerifyCmd;
import baby.mumu.authentication.client.dto.AccountRecoverFromArchiveByIdCmd;
import baby.mumu.authentication.client.dto.AccountRegisterCmd;
import baby.mumu.authentication.client.dto.AccountResetPasswordCmd;
import baby.mumu.authentication.client.dto.AccountResetSystemSettingsBySettingsIdCmd;
import baby.mumu.authentication.client.dto.AccountUpdateByIdCmd;
import baby.mumu.authentication.client.dto.AccountUpdateRoleCmd;
import baby.mumu.authentication.client.dto.co.AccountBasicInfoCo;
import baby.mumu.authentication.client.dto.co.AccountCurrentLoginCo;
import baby.mumu.authentication.client.dto.co.AccountFindAllCo;
import baby.mumu.authentication.client.dto.co.AccountFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.AccountOnlineStatisticsCo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

/**
 * 账户功能API
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public interface AccountService {

  /**
   * 账户注册
   *
   * @param accountRegisterCmd 账户注册指令
   */
  void register(AccountRegisterCmd accountRegisterCmd);

  /**
   * 根据id更新账户基本信息
   *
   * @param accountUpdateByIdCmd 根据id更新账户基本信息指令
   */
  void updateById(AccountUpdateByIdCmd accountUpdateByIdCmd);

  /**
   * 根据id更新账户角色信息
   *
   * @param accountUpdateRoleCmd 根据id更新账户角色信息指令
   */
  void updateRoleById(AccountUpdateRoleCmd accountUpdateRoleCmd);

  /**
   * 禁用账户
   *
   * @param accountDisableCmd 禁用账户指令
   */
  void disable(AccountDisableCmd accountDisableCmd);

  /**
   * 退出登录
   */
  void logout();

  /**
   * 查询当前登录账户信息
   *
   * @return 当前登录账户信息
   */
  AccountCurrentLoginCo queryCurrentLoginAccount();

  /**
   * 查询当前在线账户数量
   *
   * @return 在线账户数量
   */
  AccountOnlineStatisticsCo onlineAccounts();

  /**
   * 重置密码
   *
   * @param accountResetPasswordCmd 重置密码指令
   */
  void resetPassword(AccountResetPasswordCmd accountResetPasswordCmd);

  /**
   * 重置系统设置
   */
  void resetSystemSettingsBySettingsId(
      AccountResetSystemSettingsBySettingsIdCmd accountResetSystemSettingsBySettingsIdCmd);

  /**
   * 修改系统设置
   */
  void modifySystemSettingsBySettingsId(
      AccountModifySystemSettingsBySettingsIdCmd accountModifySystemSettingsBySettingsIdCmd);

  /**
   * 添加系统设置
   */
  void addSystemSettings(
      AccountAddSystemSettingsCmd accountAddSystemSettingsCmd);

  /**
   * 删除当前账户
   *
   * @param accountDeleteCurrentCmd 账户删除指令
   */
  void deleteCurrentAccount(AccountDeleteCurrentCmd accountDeleteCurrentCmd);

  /**
   * 当前登录账户密码是否正确
   *
   * @param accountPasswordVerifyCmd 账户密码校验指令
   * @return 密码是否正确
   */
  boolean verifyPassword(AccountPasswordVerifyCmd accountPasswordVerifyCmd);

  /**
   * 修改账户密码
   *
   * @param accountChangePasswordCmd 修改账户密码指令
   */
  void changePassword(AccountChangePasswordCmd accountChangePasswordCmd);

  /**
   * 根据id归档账户
   *
   * @param accountArchiveByIdCmd 根据id归档账户指令
   */
  void archiveById(AccountArchiveByIdCmd accountArchiveByIdCmd);

  /**
   * 根据id查询账户基本信息
   *
   * @param accountBasicInfoByIdCmd 根据id查询账户基本信息指令
   */
  AccountBasicInfoCo getAccountBasicInfoById(AccountBasicInfoByIdCmd accountBasicInfoByIdCmd);

  /**
   * 通过id从归档中恢复
   *
   * @param accountRecoverFromArchiveByIdCmd 通过id从归档中恢复指令
   */
  void recoverFromArchiveById(
      AccountRecoverFromArchiveByIdCmd accountRecoverFromArchiveByIdCmd);

  /**
   * 账户添加地址
   *
   * @param accountAddAddressCmd 账户添加地址指令
   */
  void addAddress(AccountAddAddressCmd accountAddAddressCmd);

  /**
   * 下线账户
   *
   * @param accountOfflineCmd 下线账户指令
   */
  void offline(AccountOfflineCmd accountOfflineCmd);

  /**
   * 分页查询账户
   *
   * @param accountFindAllCmd 分页查询账户指令
   * @return 查询结果
   */
  Page<AccountFindAllCo> findAll(AccountFindAllCmd accountFindAllCmd);

  /**
   * 分页查询账户（不查询总数）
   *
   * @param accountFindAllSliceCmd 分页查询账户指令
   * @return 查询结果
   */
  Slice<AccountFindAllSliceCo> findAllSlice(AccountFindAllSliceCmd accountFindAllSliceCmd);
}
