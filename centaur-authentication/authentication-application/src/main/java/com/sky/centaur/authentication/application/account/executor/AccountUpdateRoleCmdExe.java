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
package com.sky.centaur.authentication.application.account.executor;

import com.sky.centaur.authentication.client.dto.AccountUpdateRoleCmd;
import com.sky.centaur.authentication.client.dto.co.AccountUpdateRoleCo;
import com.sky.centaur.authentication.domain.account.Account;
import com.sky.centaur.authentication.domain.account.gateway.AccountGateway;
import com.sky.centaur.authentication.infrastructure.account.convertor.AccountConvertor;
import io.micrometer.observation.annotation.Observed;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 账户更新角色指令执行器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
@Observed(name = "AccountUpdateRoleCmdExe")
public class AccountUpdateRoleCmdExe {

  private final AccountGateway accountGateway;

  @Autowired
  public AccountUpdateRoleCmdExe(AccountGateway accountGateway) {
    this.accountGateway = accountGateway;
  }

  public AccountUpdateRoleCo execute(@NotNull AccountUpdateRoleCmd accountUpdateRoleCmd) {
    Account entity = AccountConvertor.toEntity(accountUpdateRoleCmd.getAccountUpdateRoleCo());
    accountGateway.updateRoleById(entity);
    return accountUpdateRoleCmd.getAccountUpdateRoleCo();
  }
}
