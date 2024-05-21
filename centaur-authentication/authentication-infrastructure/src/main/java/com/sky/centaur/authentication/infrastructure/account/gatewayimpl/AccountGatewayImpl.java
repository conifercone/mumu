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
import com.sky.centaur.authentication.infrastructure.token.redis.TokenRepository;
import com.sky.centaur.basis.exception.AccountAlreadyExistsException;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.basis.tools.SecurityContextUtil;
import com.sky.centaur.extension.distributed.lock.DistributedLock;
import com.sky.centaur.log.client.api.OperationLogGrpcService;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCo;
import io.micrometer.observation.annotation.Observed;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
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

  private final AccountRepository accountRepository;

  private final TokenRepository tokenRepository;

  private final PasswordEncoder passwordEncoder;

  private final OperationLogGrpcService operationLogGrpcService;

  private final DistributedLock distributedLock;

  public static final String PASSWORD_AFTER_RESET = "123456";

  @Autowired
  public AccountGatewayImpl(AccountRepository accountRepository, TokenRepository tokenRepository,
      PasswordEncoder passwordEncoder,
      OperationLogGrpcService operationLogGrpcService,
      ObjectProvider<DistributedLock> distributedLockObjectProvider) {
    this.accountRepository = accountRepository;
    this.tokenRepository = tokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.operationLogGrpcService = operationLogGrpcService;
    this.distributedLock = distributedLockObjectProvider.getIfAvailable();
  }

  @Override
  @Transactional
  @API(status = Status.STABLE, since = "1.0.0")
  public void register(Account account) {
    Consumer<Account> accountAlreadyExistsConsumer = (existingAccount) -> {
      operationLogGrpcService.submit(OperationLogSubmitGrpcCmd.newBuilder()
          .setOperationLogSubmitCo(
              OperationLogSubmitGrpcCo.newBuilder().setContent("用户注册")
                  .setBizNo(existingAccount.getUsername())
                  .setFail(ResultCode.ACCOUNT_ALREADY_EXISTS.getResultMsg()).build())
          .build());
      throw new AccountAlreadyExistsException(existingAccount.getUsername());
    };
    AccountDo dataObject = AccountConvertor.toDataObject(account);
    // 密码加密
    dataObject.setPassword(passwordEncoder.encode(dataObject.getPassword()));
    findAccountByUsername(dataObject.getUsername()).ifPresentOrElse(accountAlreadyExistsConsumer,
        () -> findAccountByEmail(dataObject.getEmail()).ifPresentOrElse(
            accountAlreadyExistsConsumer, () -> {
              accountRepository.persist(dataObject);
              operationLogGrpcService.submit(OperationLogSubmitGrpcCmd.newBuilder()
                  .setOperationLogSubmitCo(
                      OperationLogSubmitGrpcCo.newBuilder().setContent("用户注册")
                          .setBizNo(account.getUsername())
                          .setSuccess(String.format("%s注册成功", account.getUsername())).build())
                  .build());
            }));
  }

  @Override
  @Transactional
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> findAccountByUsername(String username) {
    return accountRepository.findAccountDoByUsername(username)
        .map(AccountConvertor::toEntity);
  }

  @Override
  @Transactional
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> findAccountByEmail(String email) {
    return accountRepository.findAccountDoByEmail(email)
        .map(AccountConvertor::toEntity);
  }

  @Override
  @Transactional
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateById(@NotNull Account account) {
    SecurityContextUtil.getLoginAccountId()
        .filter(res -> Objects.equals(res, account.getId()))
        .ifPresentOrElse((accountId) -> {
          distributedLock.lock();
          try {
            AccountDo accountDoSource = AccountConvertor.toDataObject(account);
            accountRepository.merge(accountDoSource);
          } catch (Exception e) {
            throw new RuntimeException(e);
          } finally {
            distributedLock.unlock();
          }
        }, () -> {
          throw new CentaurException(ResultCode.UNAUTHORIZED);
        });
  }

  @Override
  @Transactional
  @API(status = Status.STABLE, since = "1.0.0")
  public void disable(Long id) {
    accountRepository.findById(id).ifPresentOrElse((accountDo) -> {
      accountDo.setEnabled(false);
      accountRepository.merge(accountDo);
    }, () -> {
      throw new CentaurException(ResultCode.ACCOUNT_DOES_NOT_EXIST);
    });
  }

  @Override
  @Transactional
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> queryCurrentLoginAccount() {
    return SecurityContextUtil.getLoginAccountId().map(
            loginAccountId -> accountRepository.findById(loginAccountId)
                .map(AccountConvertor::toEntity))
        .orElseThrow(() -> new CentaurException(ResultCode.UNAUTHORIZED));
  }

  @Override
  @Transactional
  @API(status = Status.STABLE, since = "1.0.0")
  public long onlineAccounts() {
    return tokenRepository.count();
  }

  @Override
  @Transactional
  @API(status = Status.STABLE, since = "1.0.0")
  public void resetPassword(Long id) {
    accountRepository.findById(id).ifPresentOrElse((accountDo) -> {
      accountDo.setPassword(passwordEncoder.encode(PASSWORD_AFTER_RESET));
      accountRepository.merge(accountDo);
    }, () -> {
      throw new CentaurException(ResultCode.ACCOUNT_DOES_NOT_EXIST);
    });
  }
}
