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

import com.sky.centaur.authentication.client.dto.co.AccountCurrentLoginQueryCo;
import com.sky.centaur.authentication.domain.account.Account;
import com.sky.centaur.authentication.domain.account.gateway.AccountGateway;
import com.sky.centaur.authentication.infrastructure.account.convertor.AccountConvertor;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 查询当前登录账户信息指令执行器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
@Observed(name = "AccountCurrentLoginQueryCmdExe")
public class AccountCurrentLoginQueryCmdExe {

  @Resource
  private AccountGateway accountGateway;


  public AccountCurrentLoginQueryCo execute() {
    Optional<Account> optionalAccount = accountGateway.queryCurrentLoginAccount();
    if (optionalAccount.isPresent()) {
      return AccountConvertor.toCurrentLoginQueryCo(optionalAccount.get());
    }
    throw new CentaurException(ResultCode.ACCOUNT_DOES_NOT_EXIST);
  }
}
