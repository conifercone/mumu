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
package com.sky.centaur.authentication.client.api;

import com.sky.centaur.authentication.client.dto.AccountDisableCmd;
import com.sky.centaur.authentication.client.dto.AccountRegisterCmd;
import com.sky.centaur.authentication.client.dto.AccountResetPasswordCmd;
import com.sky.centaur.authentication.client.dto.AccountUpdateByIdCmd;
import com.sky.centaur.authentication.client.dto.AccountUpdateRoleCmd;
import com.sky.centaur.authentication.client.dto.co.AccountCurrentLoginQueryCo;
import com.sky.centaur.authentication.client.dto.co.AccountOnlineStatisticsCo;

/**
 * 账户功能API
 *
 * @author kaiyu.shan
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
   * 查询当前登录账户信息
   *
   * @return 当前登录账户信息
   */
  AccountCurrentLoginQueryCo queryCurrentLoginAccount();

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
   * 删除当前账户
   */
  void deleteCurrentAccount();
}
