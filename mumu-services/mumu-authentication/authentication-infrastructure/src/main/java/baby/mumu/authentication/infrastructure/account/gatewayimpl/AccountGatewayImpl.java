/*
 * Copyright (c) 2024-2025, the original author or authors.
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
import baby.mumu.authentication.domain.account.AccountSystemSettings;
import baby.mumu.authentication.domain.account.gateway.AccountGateway;
import baby.mumu.authentication.domain.role.Role;
import baby.mumu.authentication.infrastructure.account.convertor.AccountConvertor;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.cache.AccountRedisRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.AccountArchivedRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.AccountRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.po.AccountPO;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.document.AccountAddressMongodbRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.document.AccountSystemSettingsMongodbRepository;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.document.po.AccountAddressMongodbPO;
import baby.mumu.authentication.infrastructure.relations.cache.AccountRoleRedisRepository;
import baby.mumu.authentication.infrastructure.relations.database.AccountRoleRepository;
import baby.mumu.authentication.infrastructure.token.gatewayimpl.cache.OidcIdTokenRepository;
import baby.mumu.authentication.infrastructure.token.gatewayimpl.cache.PasswordTokenRepository;
import baby.mumu.basis.annotations.DangerousOperation;
import baby.mumu.basis.event.OfflineSuccessEvent;
import baby.mumu.basis.exception.AccountAlreadyExistsException;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.kotlin.tools.SecurityContextUtils;
import baby.mumu.basis.kotlin.tools.TimeUtils;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.GlobalProperties;
import baby.mumu.log.client.api.OperationLogGrpcService;
import baby.mumu.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import io.micrometer.observation.annotation.Observed;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.javamoney.moneta.Money;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 用户领域网关实现
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "AccountGatewayImpl")
public class AccountGatewayImpl implements AccountGateway {

  private final AccountRepository accountRepository;
  private final PasswordTokenRepository passwordTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final OperationLogGrpcService operationLogGrpcService;
  private final ExtensionProperties extensionProperties;
  private final AccountConvertor accountConvertor;
  private final AccountArchivedRepository accountArchivedRepository;
  private final AccountAddressMongodbRepository accountAddressMongodbRepository;
  private final JobScheduler jobScheduler;
  private final AccountRoleRepository accountRoleRepository;
  private final AccountRedisRepository accountRedisRepository;
  private final AccountSystemSettingsMongodbRepository accountSystemSettingsMongodbRepository;
  private final OidcIdTokenRepository oidcIdTokenRepository;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final AccountRoleRedisRepository accountRoleRedisRepository;

  @Autowired
  public AccountGatewayImpl(AccountRepository accountRepository,
    PasswordTokenRepository passwordTokenRepository,
    PasswordEncoder passwordEncoder,
    OperationLogGrpcService operationLogGrpcService,
    ExtensionProperties extensionProperties, AccountConvertor accountConvertor,
    AccountArchivedRepository accountArchivedRepository,
    AccountAddressMongodbRepository accountAddressMongodbRepository, JobScheduler jobScheduler,
    AccountRoleRepository accountRoleRepository,
    AccountRedisRepository accountRedisRepository,
    AccountSystemSettingsMongodbRepository accountSystemSettingsMongodbRepository,
    OidcIdTokenRepository oidcIdTokenRepository,
    ApplicationEventPublisher applicationEventPublisher,
    AccountRoleRedisRepository accountRoleRedisRepository) {
    this.accountRepository = accountRepository;
    this.passwordTokenRepository = passwordTokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.operationLogGrpcService = operationLogGrpcService;
    this.extensionProperties = extensionProperties;
    this.accountConvertor = accountConvertor;
    this.accountArchivedRepository = accountArchivedRepository;
    this.accountAddressMongodbRepository = accountAddressMongodbRepository;
    this.jobScheduler = jobScheduler;
    this.accountRoleRepository = accountRoleRepository;
    this.accountRedisRepository = accountRedisRepository;
    this.accountSystemSettingsMongodbRepository = accountSystemSettingsMongodbRepository;
    this.oidcIdTokenRepository = oidcIdTokenRepository;
    this.applicationEventPublisher = applicationEventPublisher;
    this.accountRoleRedisRepository = accountRoleRedisRepository;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void register(Account account) {
    accountConvertor.toPO(account).filter(
      _ -> !accountRepository.existsByIdOrUsernameOrEmail(account.getId(),
        account.getUsername(), account.getEmail())
        && !accountArchivedRepository.existsByIdOrUsernameOrEmail(account.getId(),
        account.getUsername(), account.getEmail())).ifPresentOrElse(accountPO -> {
      // 验证时区是否为有效时区类型
      if (StringUtils.isNotBlank(accountPO.getTimezone()) && !TimeUtils.isValidTimeZone(
        accountPO.getTimezone())) {
        throw new MuMuException(ResponseCode.TIME_ZONE_IS_NOT_AVAILABLE);
      }
      accountPO.setPassword(passwordEncoder.encode(accountPO.getPassword()));
      accountRepository.persist(accountPO);
      account.setId(accountPO.getId());
      Optional.ofNullable(account.getAddresses()).filter(CollectionUtils::isNotEmpty).map(
          accountAddresses -> accountAddresses.stream()
            .flatMap(accountAddress -> accountConvertor.toAccountAddressPO(accountAddress)
              .stream())
            .collect(
              Collectors.toList())).filter(CollectionUtils::isNotEmpty)
        .ifPresent(accountAddressMongodbRepository::saveAll);
      accountRoleRepository.persistAll(accountConvertor.toAccountRolePOS(account));
      accountRedisRepository.deleteById(account.getId());
      accountRoleRedisRepository.deleteById(account.getId());
      operationLogGrpcService.syncSubmit(OperationLogSubmitGrpcCmd.newBuilder()
        .setContent("用户注册")
        .setBizNo(account.getUsername())
        .setSuccess(String.format("%s注册成功", account.getUsername()))
        .build());
    }, () -> {
      operationLogGrpcService.syncSubmit(OperationLogSubmitGrpcCmd.newBuilder()
        .setContent("用户注册")
        .setBizNo(account.getUsername())
        .setFail(ResponseCode.ACCOUNT_ALREADY_EXISTS.getMessage())
        .build());
      throw new AccountAlreadyExistsException(account.getUsername());
    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public Optional<Account> findAccountByUsername(String username) {
    return accountRedisRepository.findByUsername(username).flatMap(accountConvertor::toEntity)
      .or(() -> {
        Optional<Account> account = accountRepository.findByUsername(username)
          .flatMap(accountConvertor::toEntity);
        account.flatMap(accountConvertor::toAccountRedisPO)
          .ifPresent(accountRedisRepository::save);
        return account;
      });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public Optional<Account> findAccountByEmail(String email) {
    return accountRedisRepository.findByEmail(email).flatMap(accountConvertor::toEntity).or(() -> {
      Optional<Account> account = accountRepository.findByEmail(email)
        .flatMap(accountConvertor::toEntity);
      account.flatMap(accountConvertor::toAccountRedisPO).ifPresent(accountRedisRepository::save);
      return account;
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateById(Account account) {
    Optional.ofNullable(account)
      .ifPresent(accountNotNull -> SecurityContextUtils.getLoginAccountId()
        .filter(res -> Objects.equals(res, accountNotNull.getId()))
        .ifPresentOrElse((accountId) -> accountConvertor.toPO(accountNotNull)
          .ifPresent(accountPO -> {
            accountRepository.merge(accountPO);
            accountRedisRepository.deleteById(accountId);
            accountRoleRedisRepository.deleteById(accountId);
          }), () -> {
          throw new MuMuException(ResponseCode.UNAUTHORIZED);
        }));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateRoleById(Account account) {
    Optional.ofNullable(account).map(Account::getId).filter(accountRepository::existsById)
      .ifPresent(accountId -> accountConvertor.toPO(account).ifPresent(_ -> {
        accountRoleRepository.deleteByAccountId(accountId);
        accountRoleRepository.persistAll(accountConvertor.toAccountRolePOS(account));
        accountRoleRedisRepository.deleteById(accountId);
      }));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void disable(Long id) {
    accountRepository.findById(id).ifPresentOrElse((accountPO) -> {
      accountPO.setEnabled(false);
      accountRepository.merge(accountPO);
      passwordTokenRepository.deleteById(id);
      accountRedisRepository.deleteById(id);
      accountRoleRedisRepository.deleteById(id);
    }, () -> {
      throw new MuMuException(ResponseCode.ACCOUNT_DOES_NOT_EXIST);
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public Optional<Account> queryCurrentLoginAccount() {
    return SecurityContextUtils.getLoginAccountId().flatMap(accountRedisRepository::findById)
      .flatMap(accountConvertor::toEntity).or(() -> {
        Optional<Account> account = accountRepository.findById(
            SecurityContextUtils.getLoginAccountId().get())
          .flatMap(accountConvertor::toEntity);
        account.flatMap(accountConvertor::toAccountRedisPO)
          .ifPresent(accountRedisRepository::save);
        return account;
      });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  public long onlineAccounts() {
    return passwordTokenRepository.count();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void resetPassword(Long id) {
    accountRepository.findById(id).ifPresentOrElse((accountPO) -> {
      String initialPassword = extensionProperties.getAuthentication().getInitialPassword();
      Assert.isTrue(StringUtils.isNotBlank(initialPassword),
        ResponseCode.THE_INITIAL_PASSWORD_CANNOT_BE_EMPTY.getMessage());
      accountPO.setPassword(passwordEncoder.encode(initialPassword));
      accountRepository.merge(accountPO);
      accountRedisRepository.deleteById(accountPO.getId());
      accountRoleRedisRepository.deleteById(accountPO.getId());
    }, () -> {
      throw new MuMuException(ResponseCode.ACCOUNT_DOES_NOT_EXIST);
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  @DangerousOperation("删除当前账户")
  public void deleteCurrentAccount() {
    SecurityContextUtils.getLoginAccountId().flatMap(accountRepository::findById)
      .ifPresentOrElse(accountPO -> {
        if (accountPO.getBalance()
          .isGreaterThan(Money.of(0, accountPO.getBalance().getCurrency()))) {
          throw new MuMuException(ResponseCode.THE_ACCOUNT_HAS_AN_UNUSED_BALANCE);
        }
        accountRepository.deleteById(accountPO.getId());
        accountAddressMongodbRepository.deleteByUserId(accountPO.getId());
        passwordTokenRepository.deleteById(accountPO.getId());
        accountRoleRepository.deleteByAccountId(accountPO.getId());
        accountRedisRepository.deleteById(accountPO.getId());
        accountRoleRedisRepository.deleteById(accountPO.getId());
      }, () -> {
        throw new MuMuException(ResponseCode.UNAUTHORIZED);
      });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public List<Account> findAllAccountByRoleId(Long roleId) {
    return Optional.ofNullable(roleId)
      .map(roleIdNonNull -> accountRoleRepository.findByRoleId(roleIdNonNull).stream()
        .flatMap(
          accountRolePO -> accountConvertor.toEntity(accountRolePO.getAccount()).stream())
        .collect(Collectors.toList())).orElse(new ArrayList<>());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public boolean verifyPassword(String password) {
    return SecurityContextUtils.getLoginAccountId()
      .map(accountId -> accountRepository.findById(accountId)
        .map(accountPO -> passwordEncoder.matches(password, accountPO.getPassword()))
        .orElseThrow(() -> new MuMuException(ResponseCode.ACCOUNT_DOES_NOT_EXIST)))
      .orElseThrow(() -> new MuMuException(ResponseCode.UNAUTHORIZED));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void changePassword(String originalPassword, String newPassword) {
    if (verifyPassword(originalPassword)) {
      AccountPO newAccountPO = SecurityContextUtils.getLoginAccountId()
        .map(accountId -> accountRepository.findById(accountId)
          .stream()
          .peek(accountPO -> accountPO.setPassword(passwordEncoder.encode(newPassword)))
          .findAny().orElseThrow(() -> new MuMuException(ResponseCode.ACCOUNT_DOES_NOT_EXIST)))
        .orElseThrow(() -> new MuMuException(ResponseCode.UNAUTHORIZED));
      accountRepository.merge(newAccountPO);
      accountRedisRepository.deleteById(newAccountPO.getId());
      accountRoleRedisRepository.deleteById(newAccountPO.getId());
    } else {
      throw new MuMuException(ResponseCode.ACCOUNT_PASSWORD_IS_INCORRECT);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @DangerousOperation("根据ID归档账户ID为%0的账户")
  public void archiveById(Long id) {
    Optional.ofNullable(id).flatMap(accountRepository::findById)
      .flatMap(accountConvertor::toArchivedPO).ifPresent(accountArchivedPO -> {
        if (accountArchivedPO.getBalance()
          .isGreaterThan(Money.of(0, accountArchivedPO.getBalance().getCurrency()))) {
          throw new MuMuException(ResponseCode.THE_ACCOUNT_HAS_AN_UNUSED_BALANCE);
        }
        //noinspection DuplicatedCode
        accountArchivedPO.setArchived(true);
        accountArchivedRepository.persist(accountArchivedPO);
        accountRepository.deleteById(accountArchivedPO.getId());
        accountRedisRepository.deleteById(accountArchivedPO.getId());
        accountRoleRedisRepository.deleteById(accountArchivedPO.getId());
        GlobalProperties global = extensionProperties.getGlobal();
        jobScheduler.schedule(Instant.now()
            .plus(global.getArchiveDeletionPeriod(), global.getArchiveDeletionPeriodUnit()),
          () -> deleteArchivedDataJob(accountArchivedPO.getId()));
      });
  }

  @Job(name = "删除ID为：%0 的账户归档数据")
  @DangerousOperation("根据ID删除ID为%0的账户归档数据定时任务")
  @Transactional(rollbackFor = Exception.class)
  public void deleteArchivedDataJob(Long id) {
    Optional.ofNullable(id).ifPresent(accountIdNonNull -> {
      accountArchivedRepository.deleteById(accountIdNonNull);
      accountAddressMongodbRepository.deleteByUserId(accountIdNonNull);
      accountRoleRepository.deleteByAccountId(accountIdNonNull);
      accountRedisRepository.deleteById(accountIdNonNull);
      accountRoleRedisRepository.deleteById(accountIdNonNull);
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(Long id) {
    Optional.ofNullable(id).flatMap(accountArchivedRepository::findById)
      .flatMap(accountConvertor::toPO).ifPresent(accountPO -> {
        accountPO.setArchived(false);
        accountArchivedRepository.deleteById(accountPO.getId());
        accountRepository.persist(accountPO);
      });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void addAddress(AccountAddress accountAddress) {
    SecurityContextUtils.getLoginAccountId().ifPresent(
      accountId -> Optional.ofNullable(accountAddress)
        .flatMap(accountConvertor::toAccountAddressPO)
        .ifPresent(
          accountAddressPO -> accountRepository.findById(accountId).ifPresent(_ -> {
            accountAddressPO.setUserId(accountId);
            accountAddressMongodbRepository.save(accountAddressPO);
            accountRedisRepository.deleteById(accountId);
            accountRoleRedisRepository.deleteById(accountId);
          })));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Optional<Account> getAccountBasicInfoById(Long id) {
    return Optional.ofNullable(id).flatMap(accountRedisRepository::findById)
      .flatMap(accountConvertor::toEntity).or(() -> {
        Optional<Account> account = accountRepository.findById(id)
          .flatMap(accountConvertor::toBasicInfoEntity);
        account.flatMap(accountConvertor::toAccountRedisPO)
          .ifPresent(accountRedisRepository::save);
        return account;
      });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void resetSystemSettingsById(String systemSettingsId) {
    SecurityContextUtils.getLoginAccountId()
      .flatMap(_ -> accountSystemSettingsMongodbRepository.findById(systemSettingsId))
      .filter(accountSystemSettingsMongodbPO -> SecurityContextUtils.getLoginAccountId().get()
        .equals(accountSystemSettingsMongodbPO.getUserId()))
      .flatMap(
        accountConvertor::resetAccountSystemSettingMongodbPO)
      .ifPresent(accountSystemSettingsMongodbPO -> {
        accountSystemSettingsMongodbRepository.save(accountSystemSettingsMongodbPO);
        accountRedisRepository.deleteById(accountSystemSettingsMongodbPO.getUserId());
      });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void modifySystemSettings(AccountSystemSettings accountSystemSettings) {
    SecurityContextUtils.getLoginAccountId()
      .flatMap(_ -> accountSystemSettingsMongodbRepository.findById(
        accountSystemSettings.getId()))
      .filter(accountSystemSettingsMongodbPO -> SecurityContextUtils.getLoginAccountId().get()
        .equals(accountSystemSettingsMongodbPO.getUserId()))
      .flatMap(_ -> accountConvertor.toAccountSystemSettingMongodbPO(
        accountSystemSettings)).ifPresent(accountSystemSettingsMongodbPO -> {
        accountSystemSettingsMongodbRepository.save(accountSystemSettingsMongodbPO);
        accountRedisRepository.deleteById(accountSystemSettingsMongodbPO.getUserId());
      });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void modifyAddress(AccountAddress accountAddress) {
    SecurityContextUtils.getLoginAccountId()
      .flatMap(_ -> accountAddressMongodbRepository.findById(
        accountAddress.getId()))
      .filter(accountAddressMongodbPO -> SecurityContextUtils.getLoginAccountId().get()
        .equals(accountAddressMongodbPO.getUserId()))
      .flatMap(_ -> accountConvertor.toAccountAddressPO(
        accountAddress)).ifPresent(accountAddressMongodbPO -> {
        accountAddressMongodbRepository.save(accountAddressMongodbPO);
        accountRedisRepository.deleteById(accountAddressMongodbPO.getUserId());
      });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void addSystemSettings(AccountSystemSettings accountSystemSettings) {
    SecurityContextUtils.getLoginAccountId().ifPresent(
      accountId -> Optional.ofNullable(accountSystemSettings)
        .flatMap(accountConvertor::toAccountSystemSettingMongodbPO)
        .filter(
          accountSystemSettingsMongodbPO -> !accountSystemSettingsMongodbRepository.existsByUserIdAndProfile(
            accountId, accountSystemSettingsMongodbPO.getProfile()))
        .ifPresent(
          accountSystemSettingsMongodbPO -> {
            accountSystemSettingsMongodbPO.setUserId(accountId);
            accountSystemSettingsMongodbRepository.save(accountSystemSettingsMongodbPO);
            accountRedisRepository.deleteById(accountSystemSettingsMongodbPO.getUserId());
          }));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void logout() {
    SecurityContextUtils.getLoginAccountId().ifPresent(accountId -> {
      passwordTokenRepository.deleteById(accountId);
      oidcIdTokenRepository.deleteById(accountId);
      accountRedisRepository.deleteById(accountId);
      accountRoleRedisRepository.deleteById(accountId);
      applicationEventPublisher.publishEvent(new LogoutSuccessEvent(
        SecurityContextHolder.getContext().getAuthentication()));
      SecurityContextUtils.getLoginAccountName().ifPresent(
        accountName -> operationLogGrpcService.syncSubmit(OperationLogSubmitGrpcCmd.newBuilder()
          .setContent("用户退出登录")
          .setBizNo(accountName)
          .setSuccess(String.format("用户%s成功退出登录", accountName))
          .build()));
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @DangerousOperation("强制ID为%0的用户下线")
  public void offline(Long id) {
    Optional.ofNullable(id).ifPresent(accountId -> {
      passwordTokenRepository.findById(accountId)
        .ifPresent(
          passwordTokenRedisPO -> applicationEventPublisher.publishEvent(new OfflineSuccessEvent(
            this, passwordTokenRedisPO.getTokenValue())));
      passwordTokenRepository.deleteById(accountId);
      oidcIdTokenRepository.deleteById(accountId);
      accountRedisRepository.deleteById(accountId);
      accountRoleRedisRepository.deleteById(accountId);
      SecurityContextUtils.getLoginAccountName().ifPresent(
        accountName -> operationLogGrpcService.syncSubmit(OperationLogSubmitGrpcCmd.newBuilder()
          .setContent("用户下线")
          .setBizNo(accountName)
          .setSuccess(String.format("用户%s成功下线", accountName))
          .build()));
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Page<Account> findAll(Account account, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<AccountPO> accountPOS = accountRepository.findAllPage(
      accountConvertor.toPO(account).orElseGet(AccountPO::new),
      Optional.ofNullable(account).flatMap(accountEntity -> Optional.ofNullable(
          accountEntity.getRoles()))
        .map(roles -> roles.stream().map(Role::getId).collect(
          Collectors.toList())).orElse(null), pageRequest);
    return new PageImpl<>(accountPOS.getContent().stream()
      .flatMap(accountPO -> accountConvertor.toEntity(accountPO).stream())
      .toList(), pageRequest, accountPOS.getTotalElements());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Slice<Account> findAllSlice(Account account, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Slice<AccountPO> accountPOS = accountRepository.findAllSlice(
      accountConvertor.toPO(account).orElseGet(AccountPO::new),
      Optional.ofNullable(account).flatMap(accountEntity -> Optional.ofNullable(
          accountEntity.getRoles()))
        .map(roles -> roles.stream().map(Role::getId).collect(
          Collectors.toList())).orElse(null), pageRequest);
    return new SliceImpl<>(accountPOS.getContent().stream()
      .flatMap(accountPO -> accountConvertor.toEntity(accountPO).stream())
      .toList(), pageRequest, accountPOS.hasNext());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public List<Account> nearby(double radiusInMeters) {
    return SecurityContextUtils.getLoginAccountId().flatMap(accountId ->
        accountAddressMongodbRepository.findByUserId(accountId).stream()
          .filter(AccountAddressMongodbPO::isDefaultAddress).findAny()
      ).filter(accountAddressMongodbPO -> accountAddressMongodbPO.getLocation() != null)
      .map(accountAddressMongodbPO -> {
        List<AccountAddressMongodbPO> byLocationWithin = accountAddressMongodbRepository.findByLocationWithin(
          accountAddressMongodbPO.getLocation().getX(),
          accountAddressMongodbPO.getLocation().getY(), radiusInMeters / 6378137);
        return accountRepository.findAllById(byLocationWithin.stream().filter(
            accountAddressMongodbPOProbablyTheCurrentUser -> !Objects.equals(
              accountAddressMongodbPO.getUserId(),
              accountAddressMongodbPOProbablyTheCurrentUser.getUserId())).collect(
            Collectors.toMap(AccountAddressMongodbPO::getUserId, AccountAddressMongodbPO::getUserId,
              (existing, _) -> existing)).values()).stream()
          .flatMap(accountPO -> accountConvertor.toBasicInfoEntity(accountPO).stream())
          .collect(Collectors.toList());
      }).orElse(new ArrayList<>());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void setDefaultAddress(String addressId) {
    SecurityContextUtils.getLoginAccountId().filter(_ -> StringUtils.isNotBlank(addressId))
      .flatMap(accountId -> {
          accountAddressMongodbRepository.saveAll(
            accountAddressMongodbRepository.findByUserId(accountId).stream()
              .filter(accountAddressMongodbPO -> !addressId.equals(
                accountAddressMongodbPO.getId()))
              .peek(accountAddressMongodbPO -> accountAddressMongodbPO.setDefaultAddress(false))
              .toList());
          return accountAddressMongodbRepository.findById(addressId)
            .filter(accountAddressMongodbPO -> accountId.equals(accountAddressMongodbPO.getUserId()));
        }
      ).ifPresent(accountAddressMongodbPO -> {
        accountAddressMongodbPO.setDefaultAddress(true);
        accountAddressMongodbRepository.save(accountAddressMongodbPO);
        accountRedisRepository.deleteById(accountAddressMongodbPO.getUserId());
      });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void setDefaultSystemSettings(String systemSettingsId) {
    SecurityContextUtils.getLoginAccountId().filter(_ -> StringUtils.isNotBlank(systemSettingsId))
      .flatMap(accountId -> {
          accountSystemSettingsMongodbRepository.saveAll(
            accountSystemSettingsMongodbRepository.findByUserId(accountId).stream()
              .filter(accountSystemSettingsMongodbPO -> !systemSettingsId.equals(
                accountSystemSettingsMongodbPO.getId()))
              .peek(
                accountSystemSettingsMongodbPO -> accountSystemSettingsMongodbPO.setDefaultSystemSettings(
                  false))
              .toList());
          return accountSystemSettingsMongodbRepository.findById(systemSettingsId)
            .filter(accountSystemSettingsMongodbPO -> accountId.equals(
              accountSystemSettingsMongodbPO.getUserId()));
        }
      ).ifPresent(accountSystemSettingsMongodbPO -> {
        accountSystemSettingsMongodbPO.setDefaultSystemSettings(true);
        accountSystemSettingsMongodbRepository.save(accountSystemSettingsMongodbPO);
        accountRedisRepository.deleteById(accountSystemSettingsMongodbPO.getUserId());
      });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteAddress(String addressId) {
    if (StringUtils.isNotBlank(addressId)) {
      SecurityContextUtils.getLoginAccountId()
        .flatMap(accountId -> accountAddressMongodbRepository.findById(addressId).filter(
          accountAddressMongodbPO -> accountId.equals(accountAddressMongodbPO.getUserId())))
        .filter(accountAddressMongodbPO -> !accountAddressMongodbPO.isDefaultAddress())
        .ifPresent(accountAddressMongodbPO -> {
          accountAddressMongodbRepository.deleteById(addressId);
          accountRedisRepository.deleteById(accountAddressMongodbPO.getUserId());
        });
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteSystemSettings(String systemSettingsId) {
    if (StringUtils.isNotBlank(systemSettingsId)) {
      SecurityContextUtils.getLoginAccountId()
        .flatMap(
          accountId -> accountSystemSettingsMongodbRepository.findById(systemSettingsId).filter(
            accountSystemSettingsMongodbPO -> accountId.equals(
              accountSystemSettingsMongodbPO.getUserId())))
        .filter(
          accountSystemSettingsMongodbPO -> !accountSystemSettingsMongodbPO.isDefaultSystemSettings())
        .ifPresent(
          accountSystemSettingsMongodbPO -> {
            accountSystemSettingsMongodbRepository.deleteById(systemSettingsId);
            accountRedisRepository.deleteById(accountSystemSettingsMongodbPO.getUserId());
          });
    }
  }
}
