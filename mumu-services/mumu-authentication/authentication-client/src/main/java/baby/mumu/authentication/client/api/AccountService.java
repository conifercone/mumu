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

import baby.mumu.authentication.client.cmds.AccountAddAddressCmd;
import baby.mumu.authentication.client.cmds.AccountAddSystemSettingsCmd;
import baby.mumu.authentication.client.cmds.AccountChangePasswordCmd;
import baby.mumu.authentication.client.cmds.AccountDeleteCurrentCmd;
import baby.mumu.authentication.client.cmds.AccountFindAllCmd;
import baby.mumu.authentication.client.cmds.AccountFindAllSliceCmd;
import baby.mumu.authentication.client.cmds.AccountModifySystemSettingsBySettingsIdCmd;
import baby.mumu.authentication.client.cmds.AccountPasswordVerifyCmd;
import baby.mumu.authentication.client.cmds.AccountRegisterCmd;
import baby.mumu.authentication.client.cmds.AccountUpdateByIdCmd;
import baby.mumu.authentication.client.cmds.AccountUpdateRoleCmd;
import baby.mumu.authentication.client.dto.AccountBasicInfoDTO;
import baby.mumu.authentication.client.dto.AccountCurrentLoginDTO;
import baby.mumu.authentication.client.dto.AccountFindAllDTO;
import baby.mumu.authentication.client.dto.AccountFindAllSliceDTO;
import baby.mumu.authentication.client.dto.AccountOnlineStatisticsDTO;
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
   * @param id 禁用账户指令
   */
  void disable(Long id);

  /**
   * 退出登录
   */
  void logout();

  /**
   * 查询当前登录账户信息
   *
   * @return 当前登录账户信息
   */
  AccountCurrentLoginDTO queryCurrentLoginAccount();

  /**
   * 查询当前在线账户数量
   *
   * @return 在线账户数量
   */
  AccountOnlineStatisticsDTO onlineAccounts();

  /**
   * 重置密码
   *
   * @param id 账户ID
   */
  void resetPassword(Long id);

  /**
   * 重置系统设置
   */
  void resetSystemSettingsBySettingsId(
    String systemSettingsId);

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
   * @param accountId 账户ID
   */
  void archiveById(Long accountId);

  /**
   * 根据id查询账户基本信息
   *
   * @param id 账户id
   */
  AccountBasicInfoDTO getAccountBasicInfoById(Long id);

  /**
   * 通过id从归档中恢复
   *
   * @param accountId 账户id
   */
  void recoverFromArchiveById(
    Long accountId);

  /**
   * 账户添加地址
   *
   * @param accountAddAddressCmd 账户添加地址指令
   */
  void addAddress(AccountAddAddressCmd accountAddAddressCmd);

  /**
   * 下线账户
   *
   * @param accountId 账户id
   */
  void offline(Long accountId);

  /**
   * 分页查询账户
   *
   * @param accountFindAllCmd 分页查询账户指令
   * @return 查询结果
   */
  Page<AccountFindAllDTO> findAll(AccountFindAllCmd accountFindAllCmd);

  /**
   * 分页查询账户（不查询总数）
   *
   * @param accountFindAllSliceCmd 分页查询账户指令
   * @return 查询结果
   */
  Slice<AccountFindAllSliceDTO> findAllSlice(AccountFindAllSliceCmd accountFindAllSliceCmd);
}
