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
import com.sky.centaur.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountDo_;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleDo_;
import com.sky.centaur.authentication.infrastructure.token.redis.TokenRepository;
import com.sky.centaur.basis.exception.AccountAlreadyExistsException;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.kotlin.tools.SecurityContextUtil;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.extension.ExtensionProperties;
import com.sky.centaur.extension.distributed.lock.DistributedLock;
import com.sky.centaur.log.client.api.OperationLogGrpcService;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCo;
import io.micrometer.observation.annotation.Observed;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

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

  private final ExtensionProperties extensionProperties;

  @Autowired
  public AccountGatewayImpl(AccountRepository accountRepository, TokenRepository tokenRepository,
      PasswordEncoder passwordEncoder,
      OperationLogGrpcService operationLogGrpcService,
      ObjectProvider<DistributedLock> distributedLockObjectProvider,
      ExtensionProperties extensionProperties) {
    this.accountRepository = accountRepository;
    this.tokenRepository = tokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.operationLogGrpcService = operationLogGrpcService;
    this.distributedLock = distributedLockObjectProvider.getIfAvailable();
    this.extensionProperties = extensionProperties;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
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
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public Optional<Account> findAccountByUsername(String username) {
    return accountRepository.findAccountDoByUsername(username)
        .map(AccountConvertor::toEntity);
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public Optional<Account> findAccountByEmail(String email) {
    return accountRepository.findAccountDoByEmail(email)
        .map(AccountConvertor::toEntity);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateById(@NotNull Account account) {
    SecurityContextUtil.getLoginAccountId()
        .filter(res -> Objects.equals(res, account.getId()))
        .ifPresentOrElse((accountId) -> {
          Optional.ofNullable(distributedLock).ifPresent(DistributedLock::lock);
          try {
            AccountDo accountDoSource = AccountConvertor.toDataObject(account);
            accountRepository.merge(accountDoSource);
          } finally {
            Optional.ofNullable(distributedLock).ifPresent(DistributedLock::unlock);
          }
        }, () -> {
          throw new CentaurException(ResultCode.UNAUTHORIZED);
        });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateRoleById(Account account) {
    Optional.ofNullable(distributedLock).ifPresent(DistributedLock::lock);
    try {
      AccountDo accountDoSource = AccountConvertor.toDataObject(account);
      accountRepository.merge(accountDoSource);
    } finally {
      Optional.ofNullable(distributedLock).ifPresent(DistributedLock::unlock);
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void disable(Long id) {
    Optional.ofNullable(distributedLock).ifPresent(DistributedLock::lock);
    try {
      accountRepository.findById(id).ifPresentOrElse((accountDo) -> {
        accountDo.setEnabled(false);
        accountRepository.merge(accountDo);
      }, () -> {
        throw new CentaurException(ResultCode.ACCOUNT_DOES_NOT_EXIST);
      });
    } finally {
      Optional.ofNullable(distributedLock).ifPresent(DistributedLock::unlock);
    }
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public Optional<Account> queryCurrentLoginAccount() {
    return SecurityContextUtil.getLoginAccountId().map(
            loginAccountId -> accountRepository.findById(loginAccountId)
                .map(AccountConvertor::toEntity))
        .orElseThrow(() -> new CentaurException(ResultCode.UNAUTHORIZED));
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  public long onlineAccounts() {
    return tokenRepository.count();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void resetPassword(Long id) {
    accountRepository.findById(id).ifPresentOrElse((accountDo) -> {
      String initialPassword = extensionProperties.getAuthentication().getInitialPassword();
      Assert.isTrue(StringUtils.hasText(initialPassword),
          "the initial password cannot be empty,please check if the setting centaur.extension.authentication.initial-password");
      accountDo.setPassword(passwordEncoder.encode(initialPassword));
      accountRepository.merge(accountDo);
    }, () -> {
      throw new CentaurException(ResultCode.ACCOUNT_DOES_NOT_EXIST);
    });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void deleteCurrentAccount() {
    SecurityContextUtil.getLoginAccountId().ifPresentOrElse(accountId -> {
      Optional.ofNullable(distributedLock).ifPresent(DistributedLock::lock);
      try {
        accountRepository.deleteById(accountId);
      } finally {
        Optional.ofNullable(distributedLock).ifPresent(DistributedLock::unlock);
      }
    }, () -> {
      throw new CentaurException(ResultCode.UNAUTHORIZED);
    });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public Page<Account> findAllAccountByRoleId(Long roleId, int pageNo, int pageSize) {
    Specification<AccountDo> accountDoSpecification = (root, query, cb) -> {
      List<Predicate> predicateList = new ArrayList<>();
      Optional.ofNullable(roleId)
          .ifPresent(
              id -> predicateList.add(cb.equal(root.get(AccountDo_.role).get(RoleDo_.id), id)));
      return query.orderBy(cb.desc(root.get(AccountDo_.creationTime)))
          .where(predicateList.toArray(new Predicate[0]))
          .getRestriction();
    };
    return getAccounts(pageNo, pageSize, accountDoSpecification);
  }

  @NotNull
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  protected Page<Account> getAccounts(int pageNo, int pageSize,
      Specification<AccountDo> accountDoSpecification) {
    PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
    Page<AccountDo> repositoryAll = accountRepository.findAll(accountDoSpecification,
        pageRequest);
    List<Account> accounts = repositoryAll.getContent().stream()
        .map(AccountConvertor::toEntity)
        .toList();
    return new PageImpl<>(accounts, pageRequest, repositoryAll.getTotalElements());
  }
}
