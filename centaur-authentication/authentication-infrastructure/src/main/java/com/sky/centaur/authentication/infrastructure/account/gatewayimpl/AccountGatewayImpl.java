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
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.AccountNodeRepository;
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.AccountRepository;
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountDo;
import com.sky.centaur.basis.exception.AccountAlreadyExistsException;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.log.client.api.OperationLogGrpcService;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCo;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 用户领域网关实现
 *
 * @author 单开宇
 * @since 2024-01-16
 */
@Component
@Observed(name = "AccountGatewayImpl")
public class AccountGatewayImpl implements AccountGateway {

  @Resource
  private AccountRepository accountRepository;

  @Resource
  private AccountNodeRepository accountNodeRepository;

  @Resource
  private PasswordEncoder passwordEncoder;

  @Resource
  private OperationLogGrpcService operationLogGrpcService;

  @Override
  public void register(Account account) {
    AccountDo dataObject = AccountConvertor.toDataObject(account);
    // 密码加密
    dataObject.setPassword(passwordEncoder.encode(dataObject.getPassword()));
    AccountDo accountDoByUsername = accountRepository.findAccountDoByUsername(
        dataObject.getUsername());
    if (accountDoByUsername != null) {
      operationLogGrpcService.submit(OperationLogSubmitGrpcCmd.newBuilder()
          .setOperationLogSubmitCo(
              OperationLogSubmitGrpcCo.newBuilder().setContent("用户注册")
                  .setBizNo(account.getUsername())
                  .setFail(ResultCode.ACCOUNT_ALREADY_EXISTS.getResultMsg()).build())
          .build());
      throw new AccountAlreadyExistsException(dataObject.getUsername());
    }
    accountRepository.save(dataObject);
    accountNodeRepository.save(
        AccountConvertor.toNodeDataObject(account));
    operationLogGrpcService.submit(OperationLogSubmitGrpcCmd.newBuilder()
        .setOperationLogSubmitCo(
            OperationLogSubmitGrpcCo.newBuilder().setContent("用户注册")
                .setBizNo(account.getUsername())
                .setSuccess(String.format("%s注册成功", account.getUsername())).build())
        .build());
  }

  @Override
  public @Nullable Account findAccountByUsername(String username) {
    return Optional.ofNullable(accountRepository.findAccountDoByUsername(username))
        .map(AccountConvertor::toEntity).orElse(null);
  }
}
