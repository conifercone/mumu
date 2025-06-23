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

package baby.mumu.authentication.domain.account.gateway;

import baby.mumu.authentication.domain.account.Account;
import baby.mumu.authentication.domain.account.AccountAddress;
import baby.mumu.authentication.domain.account.AccountSystemSettings;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

/**
 * 用户领域网关
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
public interface AccountGateway {

  /**
   * 账号注册
   *
   * @param account 账号信息
   */
  void register(Account account);

  /**
   * 根据用户名查找账号
   *
   * @param username 用户名
   * @return 账号信息
   */
  Optional<Account> findAccountByUsername(String username);

  /**
   * 根据邮箱查找账号
   *
   * @param email 邮箱
   * @return 账号信息
   */
  Optional<Account> findAccountByEmail(String email);

  /**
   * 根据id更新账号信息
   *
   * @param account 目标账号信息
   */
  void updateById(Account account);

  /**
   * 根据id更新账号角色
   *
   * @param account 目标账号信息
   */
  void updateRoleById(Account account);

  /**
   * 禁用账号
   *
   * @param accountId 账号id
   */
  void disable(Long accountId);

  /**
   * 查询当前登录账号
   *
   * @return 当前登录账号信息
   */
  Optional<Account> queryCurrentLoginAccount();

  /**
   * 在线账号数量
   *
   * @return 在线账号数量
   */
  long onlineAccounts();

  /**
   * 重置密码
   *
   * @param accountId 账号id
   */
  void resetPassword(Long accountId);

  /**
   * 删除当前账号
   */
  void deleteCurrentAccount();

  /**
   * 查询所有正在使用指定角色的账号
   *
   * @param roleId 角色id
   * @return 正在使用指定角色的账号
   */
  List<Account> findAllAccountByRoleId(Long roleId);

  /**
   * 当前登录账号密码是否正确
   *
   * @param password 账号密码
   * @return 密码是否正确
   */
  boolean verifyPassword(String password);

  /**
   * 修改账号密码
   *
   * @param originalPassword 原始密码
   * @param newPassword      新密码
   */
  void changePassword(String originalPassword, String newPassword);

  /**
   * 根据id归档账号
   *
   * @param accountId 账号id
   */
  void archiveById(Long accountId);

  /**
   * 通过id从归档中恢复
   *
   * @param accountId 账号id
   */
  void recoverFromArchiveById(Long accountId);

  /**
   * 账号添加地址
   *
   * @param accountAddress 账号地址
   */
  void addAddress(AccountAddress accountAddress);

  /**
   * 获取账号基本信息（不包含账号角色及角色权限信息）
   *
   * @param accountId 账号ID
   * @return 账号基本信息
   */
  Optional<Account> getAccountBasicInfoById(Long accountId);

  /**
   * 根据系统设置ID重置系统设置
   */
  void resetSystemSettingsById(String systemSettingsId);

  /**
   * 修改系统设置
   */
  void modifySystemSettings(AccountSystemSettings accountSystemSettings);

  /**
   * 修改账号地址
   */
  void modifyAddress(AccountAddress accountAddress);

  /**
   * 账号添加系统设置
   *
   * @param accountSystemSettings 账号系统设置
   */
  void addSystemSettings(AccountSystemSettings accountSystemSettings);

  /**
   * 退出登录
   */
  void logout();

  /**
   * 根据账号id下线账号
   *
   * @param accountId 账号ID
   */
  void offline(Long accountId);

  /**
   * 分页获取账号
   *
   * @param account  查询条件
   * @param current  页码
   * @param pageSize 每页数量
   * @return 查询结果
   */
  Page<Account> findAll(Account account, int current, int pageSize);

  /**
   * 分页获取账号(不查询总数)
   *
   * @param account  查询条件
   * @param current  页码
   * @param pageSize 每页数量
   * @return 查询结果
   */
  Slice<Account> findAllSlice(Account account, int current, int pageSize);

  /**
   * 附近的账号
   *
   * @param radiusInMeters 半径（米）
   * @return 附近的账号
   */
  List<Account> nearby(double radiusInMeters);

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
   * 删除指定地址
   *
   * @param addressId 地址ID
   */
  void deleteAddress(String addressId);

  /**
   * 删除指定系统设置
   *
   * @param systemSettingsId 系统设置ID
   */
  void deleteSystemSettings(String systemSettingsId);
}
