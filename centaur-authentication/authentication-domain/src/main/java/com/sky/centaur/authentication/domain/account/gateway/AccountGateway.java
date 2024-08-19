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
package com.sky.centaur.authentication.domain.account.gateway;

import com.sky.centaur.authentication.domain.account.Account;
import java.util.Optional;
import org.springframework.data.domain.Page;

/**
 * 用户领域网关
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public interface AccountGateway {

  /**
   * 账户注册
   *
   * @param account 账户信息
   */
  void register(Account account);

  /**
   * 根据用户名查找账户
   *
   * @param username 用户名
   * @return 账户信息
   */
  Optional<Account> findAccountByUsername(String username);

  /**
   * 根据邮箱查找账户
   *
   * @param email 邮箱
   * @return 账户信息
   */
  Optional<Account> findAccountByEmail(String email);

  /**
   * 根据id更新账户信息
   *
   * @param account 目标账户信息
   */
  void updateById(Account account);

  /**
   * 根据id更新账户角色
   *
   * @param account 目标账户信息
   */
  void updateRoleById(Account account);

  /**
   * 禁用账户
   *
   * @param id 账户id
   */
  void disable(Long id);

  /**
   * 查询当前登录账户
   *
   * @return 当前登录账户信息
   */
  Optional<Account> queryCurrentLoginAccount();

  /**
   * 在线账户数量
   *
   * @return 在线账户数量
   */
  long onlineAccounts();

  /**
   * 重置密码
   *
   * @param id 账户id
   */
  void resetPassword(Long id);

  /**
   * 删除当前账户
   */
  void deleteCurrentAccount();

  /**
   * 查询所有正在使用指定角色的账户
   *
   * @param roleId   角色id
   * @param pageNo   当前页
   * @param pageSize 每页数量
   * @return 正在使用指定角色的账户
   */
  Page<Account> findAllAccountByRoleId(Long roleId, int pageNo, int pageSize);

  /**
   * 当前登录账户密码是否正确
   *
   * @param password 账户密码
   * @return 密码是否正确
   */
  boolean verifyPassword(String password);

  /**
   * 修改账户密码
   *
   * @param originalPassword 原始密码
   * @param newPassword      新密码
   */
  void changePassword(String originalPassword, String newPassword);

  /**
   * 根据id归档账户
   *
   * @param id 账户id
   */
  void archiveById(Long id);

  /**
   * 通过id从归档中恢复
   *
   * @param id 账户id
   */
  void recoverFromArchiveById(Long id);
}
