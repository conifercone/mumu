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
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.basis.tools.BeanUtil;
import com.sky.centaur.basis.tools.SecurityContextUtil;
import com.sky.centaur.extension.distributed.lock.DistributedLock;
import com.sky.centaur.log.client.api.OperationLogGrpcService;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCo;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.Resource;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

  @Resource
  private DistributedLock distributedLock;

  @Override
  @Transactional
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
    accountRepository.persist(dataObject);
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
  @Transactional(readOnly = true)
  public @Nullable Account findAccountByUsername(String username) {
    return Optional.ofNullable(accountRepository.findAccountDoByUsername(username))
        .map(AccountConvertor::toEntity).orElse(null);
  }

  @Override
  @Transactional
  public void updateById(@NotNull Account account) {
    if (SecurityContextUtil.getLoginAccountId() != null && Objects.equals(
        SecurityContextUtil.getLoginAccountId(), account.getId())) {
      Optional<AccountDo> accountDoOptional = accountRepository.findById(account.getId());
      if (accountDoOptional.isPresent()) {
        distributedLock.lock();
        AccountDo accountDoSource = AccountConvertor.toDataObject(account);
        AccountDo accountDoTarget = accountDoOptional.get();
        BeanUtil.jpaUpdate(accountDoSource, accountDoTarget);
        accountRepository.merge(accountDoTarget);
        distributedLock.unlock();
      } else {
        throw new CentaurException(ResultCode.DATA_DOES_NOT_EXIST);
      }
    } else {
      throw new CentaurException(ResultCode.UNAUTHORIZED);
    }
  }
}
