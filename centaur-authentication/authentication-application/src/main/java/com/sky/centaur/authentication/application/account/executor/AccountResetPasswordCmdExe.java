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

import com.sky.centaur.authentication.client.dto.AccountResetPasswordCmd;
import com.sky.centaur.authentication.client.dto.co.AccountResetPasswordCo;
import com.sky.centaur.authentication.domain.account.gateway.AccountGateway;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * 账户密码重置指令执行器
 *
 * @author 单开宇
 * @since 2024-04-09
 */
@Component
@Observed(name = "AccountResetPasswordCmdExe")
public class AccountResetPasswordCmdExe {

  @Resource
  private AccountGateway accountGateway;


  public AccountResetPasswordCo execute(@NotNull AccountResetPasswordCmd accountResetPasswordCmd) {
    accountGateway.resetPassword(accountResetPasswordCmd.getAccountResetPasswordCo().getId());
    return accountResetPasswordCmd.getAccountResetPasswordCo();
  }
}
