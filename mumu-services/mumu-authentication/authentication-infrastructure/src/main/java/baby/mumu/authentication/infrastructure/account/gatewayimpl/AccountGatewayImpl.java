/*
 * Copyright (c) 2024-2024, the original author or authors.
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
package baby.mumu.authentication.infrastructure.account.gatewayimpl;

import baby.mumu.authentication.domain.account.Account;
import baby.mumu.authentication.domain.account.AccountAddress;
import baby.mumu.authentication.domain.account.gateway.AccountGateway;
import baby.mumu.authentication.infrastructure.account.convertor.AccountConvertor;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.AccountAddressRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.AccountArchivedRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.AccountRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountAddressDo;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.dataobject.AccountDo;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.redis.AccountRedisRepository;
import baby.mumu.authentication.infrastructure.relations.database.AccountRoleRepository;
import baby.mumu.authentication.infrastructure.token.gatewayimpl.redis.RefreshTokenRepository;
import baby.mumu.authentication.infrastructure.token.gatewayimpl.redis.TokenRepository;
import baby.mumu.basis.annotations.DangerousOperation;
import baby.mumu.basis.exception.AccountAlreadyExistsException;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.kotlin.tools.SecurityContextUtil;
import baby.mumu.basis.response.ResultCode;
import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.GlobalProperties;
import baby.mumu.extension.distributed.lock.DistributedLock;
import baby.mumu.log.client.api.OperationLogGrpcService;
import baby.mumu.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import baby.mumu.log.client.api.grpc.OperationLogSubmitGrpcCo;
import io.micrometer.observation.annotation.Observed;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.NotNull;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 用户领域网关实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "AccountGatewayImpl")
public class AccountGatewayImpl implements AccountGateway {

  private final AccountRepository accountRepository;
  private final TokenRepository tokenRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final OperationLogGrpcService operationLogGrpcService;
  private final DistributedLock distributedLock;
  private final ExtensionProperties extensionProperties;
  private final AccountConvertor accountConvertor;
  private final AccountArchivedRepository accountArchivedRepository;
  private final AccountAddressRepository accountAddressRepository;
  private final JobScheduler jobScheduler;
  private final AccountRoleRepository accountRoleRepository;
  private final AccountRedisRepository accountRedisRepository;

  @Autowired
  public AccountGatewayImpl(AccountRepository accountRepository, TokenRepository tokenRepository,
      RefreshTokenRepository refreshTokenRepository,
      PasswordEncoder passwordEncoder,
      OperationLogGrpcService operationLogGrpcService,
      ObjectProvider<DistributedLock> distributedLockObjectProvider,
      ExtensionProperties extensionProperties, AccountConvertor accountConvertor,
      AccountArchivedRepository accountArchivedRepository,
      AccountAddressRepository accountAddressRepository, JobScheduler jobScheduler,
      AccountRoleRepository accountRoleRepository,
      AccountRedisRepository accountRedisRepository) {
    this.accountRepository = accountRepository;
    this.tokenRepository = tokenRepository;
    this.refreshTokenRepository = refreshTokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.operationLogGrpcService = operationLogGrpcService;
    this.distributedLock = distributedLockObjectProvider.getIfAvailable();
    this.extensionProperties = extensionProperties;
    this.accountConvertor = accountConvertor;
    this.accountArchivedRepository = accountArchivedRepository;
    this.accountAddressRepository = accountAddressRepository;
    this.jobScheduler = jobScheduler;
    this.accountRoleRepository = accountRoleRepository;
    this.accountRedisRepository = accountRedisRepository;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void register(Account account) {
    Runnable accountAlreadyExistsRunnable = () -> {
      operationLogGrpcService.syncSubmit(OperationLogSubmitGrpcCmd.newBuilder()
          .setOperationLogSubmitCo(
              OperationLogSubmitGrpcCo.newBuilder().setContent("用户注册")
                  .setBizNo(account.getUsername())
                  .setFail(ResultCode.ACCOUNT_ALREADY_EXISTS.getResultMsg()).build())
          .build());
      throw new AccountAlreadyExistsException(account.getUsername());
    };
    accountConvertor.toDataObject(account).filter(
        accountDo -> !accountRepository.existsByIdOrUsernameOrEmail(account.getId(),
            account.getUsername(), account.getEmail())
            && !accountArchivedRepository.existsByIdOrUsernameOrEmail(account.getId(),
            account.getUsername(), account.getEmail())).ifPresentOrElse(dataObject -> {
      if (StringUtils.isNotBlank(dataObject.getTimezone())) {
        try {
          //noinspection ResultOfMethodCallIgnored
          ZoneId.of(dataObject.getTimezone());
        } catch (Exception e) {
          throw new MuMuException(ResultCode.TIME_ZONE_IS_NOT_AVAILABLE);
        }
      }
      dataObject.setPassword(passwordEncoder.encode(dataObject.getPassword()));
      accountRepository.persist(dataObject);
      accountRedisRepository.deleteById(account.getId());
      Optional.ofNullable(account.getAddresses()).filter(CollectionUtils::isNotEmpty).map(
              accountAddresses -> accountAddresses.stream()
                  .flatMap(accountAddress -> accountConvertor.toAccountAddressDo(accountAddress)
                      .stream())
                  .collect(
                      Collectors.toList())).filter(CollectionUtils::isNotEmpty)
          .ifPresent(accountAddressRepository::persistAll);
      accountRoleRepository.persistAll(accountConvertor.toAccountRoleDos(account));
      operationLogGrpcService.syncSubmit(OperationLogSubmitGrpcCmd.newBuilder()
          .setOperationLogSubmitCo(
              OperationLogSubmitGrpcCo.newBuilder().setContent("用户注册")
                  .setBizNo(account.getUsername())
                  .setSuccess(String.format("%s注册成功", account.getUsername())).build())
          .build());
    }, accountAlreadyExistsRunnable);

  }

  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public Optional<Account> findAccountByUsername(String username) {
    return accountRepository.findAccountDoByUsername(username)
        .flatMap(accountConvertor::toEntity);
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public Optional<Account> findAccountByEmail(String email) {
    return accountRepository.findAccountDoByEmail(email)
        .flatMap(accountConvertor::toEntity);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateById(@NotNull Account account) {
    SecurityContextUtil.getLoginAccountId()
        .filter(res -> Objects.equals(res, account.getId()))
        .ifPresentOrElse((accountId) -> accountConvertor.toDataObject(account)
            .ifPresent(accountDo -> {
              Optional.ofNullable(account.getAddresses()).filter(CollectionUtils::isNotEmpty)
                  .ifPresent(accountAddresses -> {
                    List<AccountAddressDo> accountAddressDos = accountAddresses.stream().flatMap(
                            accountAddress -> accountConvertor.toAccountAddressDo(accountAddress)
                                .stream())
                        .collect(Collectors.toList());
                    accountAddressRepository.mergeAll(accountAddressDos);
                  });
              accountRepository.merge(accountDo);
              accountRedisRepository.deleteById(accountId);
            }), () -> {
          throw new MuMuException(ResultCode.UNAUTHORIZED);
        });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateRoleById(Account account) {
    accountConvertor.toDataObject(account).ifPresent(accountDo -> {
      Optional.ofNullable(distributedLock).ifPresent(DistributedLock::lock);
      try {
        accountRoleRepository.deleteByAccountId(account.getId());
        accountRoleRepository.persistAll(accountConvertor.toAccountRoleDos(account));
      } finally {
        Optional.ofNullable(distributedLock).ifPresent(DistributedLock::unlock);
      }
    });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void disable(Long id) {
    accountRepository.findById(id).ifPresentOrElse((accountDo) -> {
      accountDo.setEnabled(false);
      accountRepository.merge(accountDo);
      tokenRepository.deleteById(id);
      refreshTokenRepository.deleteById(id);
      accountRedisRepository.deleteById(id);
    }, () -> {
      throw new MuMuException(ResultCode.ACCOUNT_DOES_NOT_EXIST);
    });
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public Optional<Account> queryCurrentLoginAccount() {
    return SecurityContextUtil.getLoginAccountId().map(
            loginAccountId -> accountRepository.findById(loginAccountId)
                .flatMap(accountConvertor::toEntity))
        .orElseThrow(() -> new MuMuException(ResultCode.UNAUTHORIZED));
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
      Assert.isTrue(StringUtils.isNotBlank(initialPassword),
          ResultCode.THE_INITIAL_PASSWORD_CANNOT_BE_EMPTY.getResultMsg());
      accountDo.setPassword(passwordEncoder.encode(initialPassword));
      accountRepository.merge(accountDo);
      accountRedisRepository.deleteById(accountDo.getId());
    }, () -> {
      throw new MuMuException(ResultCode.ACCOUNT_DOES_NOT_EXIST);
    });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  @DangerousOperation("删除当前账户")
  public void deleteCurrentAccount() {
    SecurityContextUtil.getLoginAccountId().ifPresentOrElse(accountId -> {
      accountRepository.deleteById(accountId);
      accountAddressRepository.deleteByUserId(accountId);
      tokenRepository.deleteById(accountId);
      refreshTokenRepository.deleteById(accountId);
      accountRoleRepository.deleteByAccountId(accountId);
      accountRedisRepository.deleteById(accountId);
    }, () -> {
      throw new MuMuException(ResultCode.UNAUTHORIZED);
    });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public List<Account> findAllAccountByRoleId(Long roleId) {
    return Optional.ofNullable(roleId)
        .map(roleIdNonNull -> accountRoleRepository.findByRoleId(roleIdNonNull).stream()
            .flatMap(
                accountRoleDo -> accountConvertor.toEntity(accountRoleDo.getAccount()).stream())
            .collect(Collectors.toList())).orElse(new ArrayList<>());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public boolean verifyPassword(String password) {
    return SecurityContextUtil.getLoginAccountId()
        .map(accountId -> accountRepository.findById(accountId)
            .map(accountDo -> passwordEncoder.matches(password, accountDo.getPassword()))
            .orElseThrow(() -> new MuMuException(ResultCode.ACCOUNT_DOES_NOT_EXIST)))
        .orElseThrow(() -> new MuMuException(ResultCode.UNAUTHORIZED));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void changePassword(String originalPassword, String newPassword) {
    if (verifyPassword(originalPassword)) {
      AccountDo newAccountDo = SecurityContextUtil.getLoginAccountId()
          .map(accountId -> accountRepository.findById(accountId)
              .stream()
              .peek(accountDo -> accountDo.setPassword(passwordEncoder.encode(newPassword)))
              .findAny().orElseThrow(() -> new MuMuException(ResultCode.ACCOUNT_DOES_NOT_EXIST)))
          .orElseThrow(() -> new MuMuException(ResultCode.UNAUTHORIZED));
      accountRepository.merge(newAccountDo);
      accountRedisRepository.deleteById(newAccountDo.getId());
    } else {
      throw new MuMuException(ResultCode.ACCOUNT_PASSWORD_IS_INCORRECT);
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @DangerousOperation("根据ID归档账户ID为%0的账户")
  public void archiveById(Long id) {
    //noinspection DuplicatedCode
    Optional.ofNullable(id).flatMap(accountRepository::findById)
        .flatMap(accountConvertor::toArchivedDo).ifPresent(accountArchivedDo -> {
          accountArchivedDo.setArchived(true);
          accountArchivedRepository.persist(accountArchivedDo);
          accountRepository.deleteById(accountArchivedDo.getId());
          accountRedisRepository.deleteById(accountArchivedDo.getId());
          GlobalProperties global = extensionProperties.getGlobal();
          jobScheduler.schedule(Instant.now()
                  .plus(global.getArchiveDeletionPeriod(), global.getArchiveDeletionPeriodUnit()),
              () -> deleteArchivedDataJob(accountArchivedDo.getId()));
        });
  }

  @Job(name = "删除ID为：%0 的账户归档数据")
  @DangerousOperation("根据ID删除ID为%0的账户归档数据定时任务")
  public void deleteArchivedDataJob(Long id) {
    Optional.ofNullable(id).ifPresent(accountIdNonNull -> {
      accountArchivedRepository.deleteById(accountIdNonNull);
      accountAddressRepository.deleteByUserId(accountIdNonNull);
      accountRoleRepository.deleteByAccountId(accountIdNonNull);
    });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(Long id) {
    Optional.ofNullable(id).flatMap(accountArchivedRepository::findById)
        .flatMap(accountConvertor::toDataObject).ifPresent(accountDo -> {
          accountDo.setArchived(false);
          accountArchivedRepository.deleteById(accountDo.getId());
          accountRepository.persist(accountDo);
        });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void addAddress(AccountAddress accountAddress) {
    SecurityContextUtil.getLoginAccountId().ifPresent(
        accountId -> Optional.ofNullable(accountAddress)
            .flatMap(accountConvertor::toAccountAddressDo)
            .ifPresent(
                accountAddressDo -> accountRepository.findById(accountId).ifPresent(accountDo -> {
                  accountAddressDo.setUserId(accountId);
                  accountAddressRepository.persist(accountAddressDo);
                  accountRedisRepository.deleteById(accountId);
                })));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Optional<Account> getAccountBasicInfoById(Long id) {
    return Optional.ofNullable(id).flatMap(accountRedisRepository::findById)
        .flatMap(accountConvertor::toEntity).or(() -> {
          Optional<Account> account = accountRepository.findById(id)
              .flatMap(accountConvertor::toBasicInfoEntity);
          account.flatMap(accountConvertor::toAccountRedisDo)
              .ifPresent(accountRedisRepository::save);
          return account;
        });
  }
}
