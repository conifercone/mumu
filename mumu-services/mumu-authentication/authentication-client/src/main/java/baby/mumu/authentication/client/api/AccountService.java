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
package baby.mumu.authentication.client.api;

import baby.mumu.authentication.client.cmds.AccountAddAddressCmd;
import baby.mumu.authentication.client.cmds.AccountAddSystemSettingsCmd;
import baby.mumu.authentication.client.cmds.AccountChangePasswordCmd;
import baby.mumu.authentication.client.cmds.AccountDeleteCurrentCmd;
import baby.mumu.authentication.client.cmds.AccountFindAllCmd;
import baby.mumu.authentication.client.cmds.AccountFindAllSliceCmd;
import baby.mumu.authentication.client.cmds.AccountModifyAddressByAddressIdCmd;
import baby.mumu.authentication.client.cmds.AccountModifySystemSettingsBySettingsIdCmd;
import baby.mumu.authentication.client.cmds.AccountPasswordVerifyCmd;
import baby.mumu.authentication.client.cmds.AccountRegisterCmd;
import baby.mumu.authentication.client.cmds.AccountUpdateByIdCmd;
import baby.mumu.authentication.client.cmds.AccountUpdateRoleCmd;
import baby.mumu.authentication.client.dto.AccountBasicInfoDTO;
import baby.mumu.authentication.client.dto.AccountCurrentLoginDTO;
import baby.mumu.authentication.client.dto.AccountFindAllDTO;
import baby.mumu.authentication.client.dto.AccountFindAllSliceDTO;
import baby.mumu.authentication.client.dto.AccountNearbyDTO;
import baby.mumu.authentication.client.dto.AccountOnlineStatisticsDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

/**
 * 账号功能API
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public interface AccountService {

  /**
   * 账号注册
   *
   * @param accountRegisterCmd 账号注册指令
   */
  void register(AccountRegisterCmd accountRegisterCmd);

  /**
   * 根据id更新账号基本信息
   *
   * @param accountUpdateByIdCmd 根据id更新账号基本信息指令
   */
  void updateById(AccountUpdateByIdCmd accountUpdateByIdCmd);

  /**
   * 根据id更新账号角色信息
   *
   * @param accountUpdateRoleCmd 根据id更新账号角色信息指令
   */
  void updateRoleById(AccountUpdateRoleCmd accountUpdateRoleCmd);

  /**
   * 禁用账号
   *
   * @param id 禁用账号指令
   */
  void disable(Long id);

  /**
   * 退出登录
   */
  void logout();

  /**
   * 查询当前登录账号信息
   *
   * @return 当前登录账号信息
   */
  AccountCurrentLoginDTO queryCurrentLoginAccount();

  /**
   * 查询当前在线账号数量
   *
   * @return 在线账号数量
   */
  AccountOnlineStatisticsDTO onlineAccounts();

  /**
   * 重置密码
   *
   * @param id 账号ID
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
   * 删除当前账号
   *
   * @param accountDeleteCurrentCmd 账号删除指令
   */
  void deleteCurrentAccount(AccountDeleteCurrentCmd accountDeleteCurrentCmd);

  /**
   * 当前登录账号密码是否正确
   *
   * @param accountPasswordVerifyCmd 账号密码校验指令
   * @return 密码是否正确
   */
  boolean verifyPassword(AccountPasswordVerifyCmd accountPasswordVerifyCmd);

  /**
   * 修改账号密码
   *
   * @param accountChangePasswordCmd 修改账号密码指令
   */
  void changePassword(AccountChangePasswordCmd accountChangePasswordCmd);

  /**
   * 根据id归档账号
   *
   * @param accountId 账号ID
   */
  void archiveById(Long accountId);

  /**
   * 根据id查询账号基本信息
   *
   * @param id 账号id
   */
  AccountBasicInfoDTO getAccountBasicInfoById(Long id);

  /**
   * 通过id从归档中恢复
   *
   * @param accountId 账号id
   */
  void recoverFromArchiveById(
    Long accountId);

  /**
   * 账号添加地址
   *
   * @param accountAddAddressCmd 账号添加地址指令
   */
  void addAddress(AccountAddAddressCmd accountAddAddressCmd);

  /**
   * 下线账号
   *
   * @param accountId 账号id
   */
  void offline(Long accountId);

  /**
   * 分页查询账号
   *
   * @param accountFindAllCmd 分页查询账号指令
   * @return 查询结果
   */
  Page<AccountFindAllDTO> findAll(AccountFindAllCmd accountFindAllCmd);

  /**
   * 分页查询账号（不查询总数）
   *
   * @param accountFindAllSliceCmd 分页查询账号指令
   * @return 查询结果
   */
  Slice<AccountFindAllSliceDTO> findAllSlice(AccountFindAllSliceCmd accountFindAllSliceCmd);

  /**
   * 附近的账号
   *
   * @param radiusInMeters 半径（米）
   * @return 附近的账号
   */
  List<AccountNearbyDTO> nearby(double radiusInMeters);

  /**
   * 设置默认地址
   *
   * @param addressId 地址ID
   */
  void setDefaultAddress(String addressId);

  /**
   * 设置默认系统设置
   *
   * @param systemSettingsId 系统设置ID
   */
  void setDefaultSystemSettings(String systemSettingsId);

  /**
   * 删除指定系统设置
   *
   * @param systemSettingsId 系统设置ID
   */
  void deleteSystemSettings(String systemSettingsId);

  /**
   * 修改账号地址
   */
  void modifyAddressByAddressId(
    AccountModifyAddressByAddressIdCmd accountModifyAddressByAddressIdCmd);

  /**
   * 删除指定账号地址
   *
   * @param addressId 地址ID
   */
  void deleteAddress(String addressId);
}
