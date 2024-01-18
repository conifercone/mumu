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
package com.sky.centaur.authentication.infrastructure.account.gatewayimpl;

import com.sky.centaur.authentication.domain.account.Account;
import com.sky.centaur.authentication.domain.account.gateway.AccountGateway;
import com.sky.centaur.authentication.infrastructure.account.convertor.AccountConvertor;
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.AccountRepository;
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountDo;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
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

  @Resource
  private PasswordEncoder passwordEncoder;

  @Override
  public Account register(Account account) {
    AccountDo dataObject = AccountConvertor.toDataObject(account);
    // 密码加密
    dataObject.setPassword(passwordEncoder.encode(dataObject.getPassword()));
    AccountDo save = accountRepository.save(dataObject);
    return AccountConvertor.toEntity(save);
  }

  @Override
  public Account findAccountByUsername(String username) {
    return AccountConvertor.toEntity(accountRepository.findAccountDoByUsername(username));
  }
}
