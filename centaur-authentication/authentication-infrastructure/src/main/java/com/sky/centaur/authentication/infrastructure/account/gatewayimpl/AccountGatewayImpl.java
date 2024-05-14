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
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountNodeDo;
import com.sky.centaur.authentication.infrastructure.token.redis.TokenRepository;
import com.sky.centaur.basis.constants.BeanNameConstant;
import com.sky.centaur.basis.exception.AccountAlreadyExistsException;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.basis.tools.SecurityContextUtil;
import com.sky.centaur.extension.distributed.lock.DistributedLock;
import com.sky.centaur.log.client.api.OperationLogGrpcService;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCo;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.Resource;
import java.util.Objects;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户领域网关实现
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
@Observed(name = "AccountGatewayImpl")
public class AccountGatewayImpl implements AccountGateway {

  @Resource
  private AccountRepository accountRepository;

  @Resource
  private TokenRepository tokenRepository;

  @Resource
  private AccountNodeRepository accountNodeRepository;

  @Resource
  private PasswordEncoder passwordEncoder;

  @Resource
  private OperationLogGrpcService operationLogGrpcService;

  @Resource
  private DistributedLock distributedLock;

  public static final String PASSWORD_AFTER_RESET = "123456";

  @Override
  @Transactional(transactionManager = BeanNameConstant.DEFAULT_TRANSACTION_MANAGER_BEAN_NAME)
  @API(status = Status.STABLE, since = "1.0.0")
  public void register(Account account) {
    AccountDo dataObject = AccountConvertor.toDataObject(account);
    // 密码加密
    dataObject.setPassword(passwordEncoder.encode(dataObject.getPassword()));
    if (findAccountByUsername(dataObject.getUsername()).isPresent() || findAccountByEmail(
        dataObject.getEmail()).isPresent()) {
      operationLogGrpcService.submit(OperationLogSubmitGrpcCmd.newBuilder()
          .setOperationLogSubmitCo(
              OperationLogSubmitGrpcCo.newBuilder().setContent("用户注册")
                  .setBizNo(account.getUsername())
                  .setFail(ResultCode.ACCOUNT_ALREADY_EXISTS.getResultMsg()).build())
          .build());
      throw new AccountAlreadyExistsException(dataObject.getUsername());
    }
    accountRepository.persist(dataObject);
    accountNodeRegister(
        AccountConvertor.toNodeDataObject(account));
    operationLogGrpcService.submit(OperationLogSubmitGrpcCmd.newBuilder()
        .setOperationLogSubmitCo(
            OperationLogSubmitGrpcCo.newBuilder().setContent("用户注册")
                .setBizNo(account.getUsername())
                .setSuccess(String.format("%s注册成功", account.getUsername())).build())
        .build());
  }

  @Transactional(transactionManager = BeanNameConstant.NEO4J_TRANSACTION_MANAGER_BEAN_NAME)
  protected void accountNodeRegister(AccountNodeDo accountNodeDo) {
    accountNodeRepository.save(accountNodeDo);
  }

  @Override
  @Transactional(readOnly = true, transactionManager = BeanNameConstant.DEFAULT_TRANSACTION_MANAGER_BEAN_NAME)
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> findAccountByUsername(String username) {
    return Optional.ofNullable(accountRepository.findAccountDoByUsername(username))
        .map(AccountConvertor::toEntity);
  }

  @Override
  @Transactional(readOnly = true, transactionManager = BeanNameConstant.DEFAULT_TRANSACTION_MANAGER_BEAN_NAME)
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> findAccountByEmail(String email) {
    return Optional.ofNullable(accountRepository.findAccountDoByEmail(email))
        .map(AccountConvertor::toEntity);
  }

  @Override
  @Transactional(transactionManager = BeanNameConstant.DEFAULT_TRANSACTION_MANAGER_BEAN_NAME)
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateById(@NotNull Account account) {
    if (SecurityContextUtil.getLoginAccountId() != null && Objects.equals(
        SecurityContextUtil.getLoginAccountId(), account.getId())) {
      distributedLock.lock();
      try {
        AccountDo accountDoSource = AccountConvertor.toDataObject(account);
        accountRepository.merge(accountDoSource);
      } catch (Exception e) {
        throw new RuntimeException(e);
      } finally {
        distributedLock.unlock();
      }
    } else {
      throw new CentaurException(ResultCode.UNAUTHORIZED);
    }
  }

  @Override
  @Transactional(transactionManager = BeanNameConstant.DEFAULT_TRANSACTION_MANAGER_BEAN_NAME)
  @API(status = Status.STABLE, since = "1.0.0")
  public void disable(Long id) {
    Optional<AccountDo> accountDoOptional = accountRepository.findById(id);
    if (accountDoOptional.isPresent()) {
      AccountDo accountDo = accountDoOptional.get();
      accountDo.setEnabled(false);
      accountRepository.merge(accountDo);
    } else {
      throw new CentaurException(ResultCode.ACCOUNT_DOES_NOT_EXIST);
    }
  }

  @Override
  @Transactional(readOnly = true, transactionManager = BeanNameConstant.DEFAULT_TRANSACTION_MANAGER_BEAN_NAME)
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> queryCurrentLoginAccount() {
    Long loginAccountId = SecurityContextUtil.getLoginAccountId();
    if (loginAccountId == null) {
      throw new CentaurException(ResultCode.UNAUTHORIZED);
    } else {
      return accountRepository.findById(loginAccountId).map(AccountConvertor::toEntity);
    }
  }

  @Override
  @Transactional(readOnly = true, transactionManager = BeanNameConstant.DEFAULT_TRANSACTION_MANAGER_BEAN_NAME)
  @API(status = Status.STABLE, since = "1.0.0")
  public long onlineAccounts() {
    return tokenRepository.count();
  }

  @Override
  @Transactional(transactionManager = BeanNameConstant.DEFAULT_TRANSACTION_MANAGER_BEAN_NAME)
  @API(status = Status.STABLE, since = "1.0.0")
  public void resetPassword(Long id) {
    Optional<AccountDo> accountDoOptional = accountRepository.findById(id);
    if (accountDoOptional.isPresent()) {
      AccountDo accountDo = accountDoOptional.get();
      accountDo.setPassword(passwordEncoder.encode(PASSWORD_AFTER_RESET));
      accountRepository.merge(accountDo);
    } else {
      throw new CentaurException(ResultCode.ACCOUNT_DOES_NOT_EXIST);
    }
  }
}
