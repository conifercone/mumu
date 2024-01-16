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
package com.sky.centaur.authentication.infrastructure.gatewayimpl;

import com.sky.centaur.authentication.domain.account.Account;
import com.sky.centaur.authentication.domain.gateway.AccountGateway;
import com.sky.centaur.authentication.infrastructure.convertor.AccountConvertor;
import com.sky.centaur.authentication.infrastructure.gatewayimpl.database.AccountRepository;
import com.sky.centaur.authentication.infrastructure.gatewayimpl.database.dataobject.AccountDo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 用户领域网关实现
 *
 * @author 单开宇
 * @since 2024-01-16
 */
@Component
public class AccountGatewayImpl implements AccountGateway {

  @Resource
  private AccountRepository accountRepository;

  @Override
  public Account register(Account account) {
    AccountDo save = accountRepository.save(AccountConvertor.toDataObject(account));
    return AccountConvertor.toEntity(save);
  }

  @Override
  public Account findAccountByUsername(String username) {
    return AccountConvertor.toEntity(accountRepository.findAccountDoByUsername(username));
  }
}
