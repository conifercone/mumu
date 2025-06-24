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

package baby.mumu.iam.infra.account.gatewayimpl;

import baby.mumu.basis.annotations.DangerousOperation;
import baby.mumu.basis.event.OfflineSuccessEvent;
import baby.mumu.basis.exception.AccountAlreadyExistsException;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.kotlin.tools.SecurityContextUtils;
import baby.mumu.basis.kotlin.tools.TimeUtils;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.GlobalProperties;
import baby.mumu.iam.domain.account.Account;
import baby.mumu.iam.domain.account.AccountAddress;
import baby.mumu.iam.domain.account.AccountSystemSettings;
import baby.mumu.iam.domain.account.gateway.AccountGateway;
import baby.mumu.iam.domain.role.Role;
import baby.mumu.iam.infra.account.convertor.AccountConvertor;
import baby.mumu.iam.infra.account.gatewayimpl.cache.AccountCacheRepository;
import baby.mumu.iam.infra.account.gatewayimpl.database.AccountArchivedRepository;
import baby.mumu.iam.infra.account.gatewayimpl.database.AccountRepository;
import baby.mumu.iam.infra.account.gatewayimpl.database.po.AccountPO;
import baby.mumu.iam.infra.account.gatewayimpl.document.AccountAddressDocumentRepository;
import baby.mumu.iam.infra.account.gatewayimpl.document.AccountAvatarDocumentRepository;
import baby.mumu.iam.infra.account.gatewayimpl.document.AccountSystemSettingsDocumentRepository;
import baby.mumu.iam.infra.account.gatewayimpl.document.po.AccountAddressDocumentPO;
import baby.mumu.iam.infra.relations.cache.AccountRoleCacheRepository;
import baby.mumu.iam.infra.relations.database.AccountRoleRepository;
import baby.mumu.iam.infra.token.gatewayimpl.cache.OidcIdTokenCacheRepository;
import baby.mumu.iam.infra.token.gatewayimpl.cache.PasswordTokenCacheRepository;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "AccountGatewayImpl")
public class AccountGatewayImpl implements AccountGateway {

  private final AccountRepository accountRepository;
  private final PasswordTokenCacheRepository passwordTokenCacheRepository;
  private final PasswordEncoder passwordEncoder;
  private final OperationLogGrpcService operationLogGrpcService;
  private final ExtensionProperties extensionProperties;
  private final AccountConvertor accountConvertor;
  private final AccountArchivedRepository accountArchivedRepository;
  private final AccountAddressDocumentRepository accountAddressDocumentRepository;
  private final JobScheduler jobScheduler;
  private final AccountRoleRepository accountRoleRepository;
  private final AccountCacheRepository accountCacheRepository;
  private final AccountSystemSettingsDocumentRepository accountSystemSettingsDocumentRepository;
  private final OidcIdTokenCacheRepository oidcIdTokenCacheRepository;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final AccountRoleCacheRepository accountRoleCacheRepository;
  private final AccountAvatarDocumentRepository accountAvatarDocumentRepository;

  @Autowired
  public AccountGatewayImpl(AccountRepository accountRepository,
    PasswordTokenCacheRepository passwordTokenCacheRepository,
    PasswordEncoder passwordEncoder,
    OperationLogGrpcService operationLogGrpcService,
    ExtensionProperties extensionProperties, AccountConvertor accountConvertor,
    AccountArchivedRepository accountArchivedRepository,
    AccountAddressDocumentRepository accountAddressDocumentRepository, JobScheduler jobScheduler,
    AccountRoleRepository accountRoleRepository,
    AccountCacheRepository accountCacheRepository,
    AccountSystemSettingsDocumentRepository accountSystemSettingsDocumentRepository,
    OidcIdTokenCacheRepository oidcIdTokenCacheRepository,
    ApplicationEventPublisher applicationEventPublisher,
    AccountRoleCacheRepository accountRoleCacheRepository,
    AccountAvatarDocumentRepository accountAvatarDocumentRepository) {
    this.accountRepository = accountRepository;
    this.passwordTokenCacheRepository = passwordTokenCacheRepository;
    this.passwordEncoder = passwordEncoder;
    this.operationLogGrpcService = operationLogGrpcService;
    this.extensionProperties = extensionProperties;
    this.accountConvertor = accountConvertor;
    this.accountArchivedRepository = accountArchivedRepository;
    this.accountAddressDocumentRepository = accountAddressDocumentRepository;
    this.jobScheduler = jobScheduler;
    this.accountRoleRepository = accountRoleRepository;
    this.accountCacheRepository = accountCacheRepository;
    this.accountSystemSettingsDocumentRepository = accountSystemSettingsDocumentRepository;
    this.oidcIdTokenCacheRepository = oidcIdTokenCacheRepository;
    this.applicationEventPublisher = applicationEventPublisher;
    this.accountRoleCacheRepository = accountRoleCacheRepository;
    this.accountAvatarDocumentRepository = accountAvatarDocumentRepository;
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
            .flatMap(accountAddress -> accountConvertor.toAccountAddressDocumentPO(accountAddress)
              .stream())
            .collect(
              Collectors.toList())).filter(CollectionUtils::isNotEmpty)
        .ifPresent(accountAddressDocumentRepository::saveAll);
      Optional.ofNullable(account.getAvatar())
        .flatMap(accountConvertor::toAccountAvatarDocumentPO)
        .ifPresent(accountAvatarDocumentRepository::save);
      accountRoleRepository.persistAll(accountConvertor.toAccountRolePOS(account));
      accountCacheRepository.deleteById(account.getId());
      accountRoleCacheRepository.deleteById(account.getId());
      operationLogGrpcService.syncSubmit(OperationLogSubmitGrpcCmd.newBuilder()
        .setContent("User registration")
        .setBizNo(account.getUsername())
        .setSuccess(String.format("User %s registration successfully", account.getUsername()))
        .build());
    }, () -> {
      operationLogGrpcService.syncSubmit(OperationLogSubmitGrpcCmd.newBuilder()
        .setContent("User registration")
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
    return accountCacheRepository.findByUsername(username).flatMap(accountConvertor::toEntity)
      .or(() -> {
        Optional<Account> account = accountRepository.findByUsername(username)
          .flatMap(accountConvertor::toEntity);
        account.flatMap(accountConvertor::toAccountCacheablePO)
          .ifPresent(accountCacheRepository::save);
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
    return accountCacheRepository.findByEmail(email).flatMap(accountConvertor::toEntity).or(() -> {
      Optional<Account> account = accountRepository.findByEmail(email)
        .flatMap(accountConvertor::toEntity);
      account.flatMap(accountConvertor::toAccountCacheablePO)
        .ifPresent(accountCacheRepository::save);
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
            accountCacheRepository.deleteById(accountId);
            accountRoleCacheRepository.deleteById(accountId);
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
        accountRoleCacheRepository.deleteById(accountId);
      }));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void disable(Long accountId) {
    accountRepository.findById(accountId).ifPresentOrElse((accountPO) -> {
      accountPO.setEnabled(false);
      accountRepository.merge(accountPO);
      passwordTokenCacheRepository.deleteById(accountId);
      accountCacheRepository.deleteById(accountId);
      accountRoleCacheRepository.deleteById(accountId);
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
    return SecurityContextUtils.getLoginAccountId().flatMap(accountCacheRepository::findById)
      .flatMap(accountConvertor::toEntity).or(() -> {
        Optional<Account> account = accountRepository.findById(
            SecurityContextUtils.getLoginAccountId().get())
          .flatMap(accountConvertor::toEntity);
        account.flatMap(accountConvertor::toAccountCacheablePO)
          .ifPresent(accountCacheRepository::save);
        return account;
      });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  public long onlineAccounts() {
    return passwordTokenCacheRepository.count();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void resetPassword(Long accountId) {
    accountRepository.findById(accountId).ifPresentOrElse((accountPO) -> {
      String initialPassword = extensionProperties.getAuthentication().getInitialPassword();
      Assert.isTrue(StringUtils.isNotBlank(initialPassword),
        ResponseCode.THE_INITIAL_PASSWORD_CANNOT_BE_EMPTY.getMessage());
      accountPO.setPassword(passwordEncoder.encode(initialPassword));
      accountRepository.merge(accountPO);
      accountCacheRepository.deleteById(accountPO.getId());
      accountRoleCacheRepository.deleteById(accountPO.getId());
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
  @DangerousOperation("删除当前账号")
  public void deleteCurrentAccount() {
    SecurityContextUtils.getLoginAccountId().flatMap(accountRepository::findById)
      .ifPresentOrElse(accountPO -> {
        // 账号存在未使用的余额时不允许删除
        if (accountPO.getBalance()
          .isGreaterThan(Money.of(0, accountPO.getBalance().getCurrency()))) {
          throw new MuMuException(ResponseCode.THE_ACCOUNT_HAS_AN_UNUSED_BALANCE);
        }
        // 删除账号基本信息
        accountRepository.deleteById(accountPO.getId());
        // 删除账号地址信息
        accountAddressDocumentRepository.deleteByAccountId(accountPO.getId());
        // 删除账号系统设置
        accountSystemSettingsDocumentRepository.deleteByAccountId(accountPO.getId());
        // 删除账号头像信息，头像如果存在关联文件需要同步删除文件
        accountAvatarDocumentRepository.deleteByAccountId(accountPO.getId());
        // 删除账号令牌缓存
        passwordTokenCacheRepository.deleteById(accountPO.getId());
        // 删除账号角色关联信息
        accountRoleRepository.deleteByAccountId(accountPO.getId());
        // 删除账号缓存
        accountCacheRepository.deleteById(accountPO.getId());
        // 删除账号角色缓存
        accountRoleCacheRepository.deleteById(accountPO.getId());
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
      accountCacheRepository.deleteById(newAccountPO.getId());
      accountRoleCacheRepository.deleteById(newAccountPO.getId());
    } else {
      throw new MuMuException(ResponseCode.ACCOUNT_PASSWORD_IS_INCORRECT);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @DangerousOperation("根据ID归档账号ID为%0的账号")
  public void archiveById(Long accountId) {
    Optional.ofNullable(accountId).flatMap(accountRepository::findById)
      .flatMap(accountConvertor::toArchivedPO).ifPresent(accountArchivedPO -> {
        if (accountArchivedPO.getBalance()
          .isGreaterThan(Money.of(0, accountArchivedPO.getBalance().getCurrency()))) {
          throw new MuMuException(ResponseCode.THE_ACCOUNT_HAS_AN_UNUSED_BALANCE);
        }
        // noinspection DuplicatedCode
        accountArchivedPO.setArchived(true);
        accountArchivedRepository.persist(accountArchivedPO);
        accountRepository.deleteById(accountArchivedPO.getId());
        accountCacheRepository.deleteById(accountArchivedPO.getId());
        accountRoleCacheRepository.deleteById(accountArchivedPO.getId());
        GlobalProperties global = extensionProperties.getGlobal();
        jobScheduler.schedule(Instant.now()
            .plus(global.getArchiveDeletionPeriod(), global.getArchiveDeletionPeriodUnit()),
          () -> deleteArchivedDataJob(accountArchivedPO.getId()));
      });
  }

  @Job(name = "删除ID为：%0 的账号归档数据")
  @DangerousOperation("根据ID删除ID为%0的账号归档数据定时任务")
  @Transactional(rollbackFor = Exception.class)
  public void deleteArchivedDataJob(Long id) {
    Optional.ofNullable(id).ifPresent(accountIdNonNull -> {
      accountArchivedRepository.deleteById(accountIdNonNull);
      accountAddressDocumentRepository.deleteByAccountId(accountIdNonNull);
      accountRoleRepository.deleteByAccountId(accountIdNonNull);
      accountCacheRepository.deleteById(accountIdNonNull);
      accountRoleCacheRepository.deleteById(accountIdNonNull);
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(Long accountId) {
    Optional.ofNullable(accountId).flatMap(accountArchivedRepository::findById)
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
        .flatMap(accountConvertor::toAccountAddressDocumentPO)
        .ifPresent(
          accountAddressPO -> accountRepository.findById(accountId).ifPresent(_ -> {
            accountAddressPO.setAccountId(accountId);
            accountAddressDocumentRepository.save(accountAddressPO);
            accountCacheRepository.deleteById(accountId);
            accountRoleCacheRepository.deleteById(accountId);
          })));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Optional<Account> getAccountBasicInfoById(Long accountId) {
    return Optional.ofNullable(accountId).flatMap(accountCacheRepository::findById)
      .flatMap(accountConvertor::toEntity).or(() -> {
        Optional<Account> account = accountRepository.findById(accountId)
          .flatMap(accountConvertor::toBasicInfoEntity);
        account.flatMap(accountConvertor::toAccountCacheablePO)
          .ifPresent(accountCacheRepository::save);
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
      .flatMap(_ -> accountSystemSettingsDocumentRepository.findById(systemSettingsId))
      .filter(accountSystemSettingsMongodbPO -> SecurityContextUtils.getLoginAccountId().get()
        .equals(accountSystemSettingsMongodbPO.getAccountId()))
      .flatMap(
        accountConvertor::resetAccountSystemSettingsDocumentPO)
      .ifPresent(accountSystemSettingsMongodbPO -> {
        accountSystemSettingsDocumentRepository.save(accountSystemSettingsMongodbPO);
        accountCacheRepository.deleteById(accountSystemSettingsMongodbPO.getAccountId());
      });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void modifySystemSettings(AccountSystemSettings accountSystemSettings) {
    SecurityContextUtils.getLoginAccountId()
      .flatMap(_ -> accountSystemSettingsDocumentRepository.findById(
        accountSystemSettings.getId()))
      .filter(accountSystemSettingsMongodbPO -> SecurityContextUtils.getLoginAccountId().get()
        .equals(accountSystemSettingsMongodbPO.getAccountId()))
      .flatMap(_ -> accountConvertor.toAccountSystemSettingsDocumentPO(
        accountSystemSettings)).ifPresent(accountSystemSettingsMongodbPO -> {
        accountSystemSettingsDocumentRepository.save(accountSystemSettingsMongodbPO);
        accountCacheRepository.deleteById(accountSystemSettingsMongodbPO.getAccountId());
      });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void modifyAddress(AccountAddress accountAddress) {
    SecurityContextUtils.getLoginAccountId()
      .flatMap(_ -> accountAddressDocumentRepository.findById(
        accountAddress.getId()))
      .filter(accountAddressMongodbPO -> SecurityContextUtils.getLoginAccountId().get()
        .equals(accountAddressMongodbPO.getAccountId()))
      .flatMap(_ -> accountConvertor.toAccountAddressDocumentPO(
        accountAddress)).ifPresent(accountAddressMongodbPO -> {
        accountAddressDocumentRepository.save(accountAddressMongodbPO);
        accountCacheRepository.deleteById(accountAddressMongodbPO.getAccountId());
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
        .flatMap(accountConvertor::toAccountSystemSettingsDocumentPO)
        .filter(
          accountSystemSettingsMongodbPO -> !accountSystemSettingsDocumentRepository.existsByAccountIdAndProfile(
            accountId, accountSystemSettingsMongodbPO.getProfile()))
        .ifPresent(
          accountSystemSettingsMongodbPO -> {
            accountSystemSettingsMongodbPO.setAccountId(accountId);
            accountSystemSettingsDocumentRepository.save(accountSystemSettingsMongodbPO);
            accountCacheRepository.deleteById(accountSystemSettingsMongodbPO.getAccountId());
          }));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void logout() {
    SecurityContextUtils.getLoginAccountId().ifPresent(accountId -> {
      passwordTokenCacheRepository.deleteById(accountId);
      oidcIdTokenCacheRepository.deleteById(accountId);
      accountCacheRepository.deleteById(accountId);
      accountRoleCacheRepository.deleteById(accountId);
      applicationEventPublisher.publishEvent(new LogoutSuccessEvent(
        SecurityContextHolder.getContext().getAuthentication()));
      SecurityContextUtils.getLoginAccountName().ifPresent(
        accountName -> operationLogGrpcService.syncSubmit(OperationLogSubmitGrpcCmd.newBuilder()
          .setContent("User logout")
          .setBizNo(accountName)
          .setSuccess(String.format("User %s successfully logged out", accountName))
          .build()));
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @DangerousOperation("强制ID为%0的用户下线")
  public void offline(Long accountId) {
    Optional.ofNullable(accountId).ifPresent(accountIdNotNull -> {
      passwordTokenCacheRepository.findById(accountIdNotNull)
        .ifPresent(
          passwordTokenCacheablePO -> applicationEventPublisher.publishEvent(
            new OfflineSuccessEvent(
              this, passwordTokenCacheablePO.getTokenValue())));
      passwordTokenCacheRepository.deleteById(accountIdNotNull);
      oidcIdTokenCacheRepository.deleteById(accountIdNotNull);
      accountCacheRepository.deleteById(accountIdNotNull);
      accountRoleCacheRepository.deleteById(accountIdNotNull);
      SecurityContextUtils.getLoginAccountName().ifPresent(
        accountName -> operationLogGrpcService.syncSubmit(OperationLogSubmitGrpcCmd.newBuilder()
          .setContent("User offline")
          .setBizNo(accountName)
          .setSuccess(String.format("User %s successfully offline", accountName))
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
        accountAddressDocumentRepository.findByAccountId(accountId).stream()
          .filter(AccountAddressDocumentPO::isDefaultAddress).findAny()
      ).filter(accountAddressMongodbPO -> accountAddressMongodbPO.getLocation() != null)
      .map(accountAddressMongodbPO -> {
        List<AccountAddressDocumentPO> byLocationWithin = accountAddressDocumentRepository.findByLocationWithin(
          accountAddressMongodbPO.getLocation().getX(),
          accountAddressMongodbPO.getLocation().getY(), radiusInMeters / 6378137);
        return accountRepository.findAllById(byLocationWithin.stream().filter(
            accountAddressMongodbPOProbablyTheCurrentUser -> !Objects.equals(
              accountAddressMongodbPO.getAccountId(),
              accountAddressMongodbPOProbablyTheCurrentUser.getAccountId())).collect(
            Collectors.toMap(AccountAddressDocumentPO::getAccountId,
              AccountAddressDocumentPO::getAccountId,
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
          accountAddressDocumentRepository.saveAll(
            accountAddressDocumentRepository.findByAccountId(accountId).stream()
              .filter(accountAddressMongodbPO -> !addressId.equals(
                accountAddressMongodbPO.getId()))
              .peek(accountAddressMongodbPO -> accountAddressMongodbPO.setDefaultAddress(false))
              .toList());
          return accountAddressDocumentRepository.findById(addressId)
            .filter(
              accountAddressMongodbPO -> accountId.equals(accountAddressMongodbPO.getAccountId()));
        }
      ).ifPresent(accountAddressMongodbPO -> {
        accountAddressMongodbPO.setDefaultAddress(true);
        accountAddressDocumentRepository.save(accountAddressMongodbPO);
        accountCacheRepository.deleteById(accountAddressMongodbPO.getAccountId());
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
          accountSystemSettingsDocumentRepository.saveAll(
            accountSystemSettingsDocumentRepository.findByAccountId(accountId).stream()
              .filter(accountSystemSettingsMongodbPO -> !systemSettingsId.equals(
                accountSystemSettingsMongodbPO.getId()))
              .peek(
                accountSystemSettingsMongodbPO -> accountSystemSettingsMongodbPO.setDefaultSystemSettings(
                  false))
              .toList());
          return accountSystemSettingsDocumentRepository.findById(systemSettingsId)
            .filter(accountSystemSettingsMongodbPO -> accountId.equals(
              accountSystemSettingsMongodbPO.getAccountId()));
        }
      ).ifPresent(accountSystemSettingsMongodbPO -> {
        accountSystemSettingsMongodbPO.setDefaultSystemSettings(true);
        accountSystemSettingsDocumentRepository.save(accountSystemSettingsMongodbPO);
        accountCacheRepository.deleteById(accountSystemSettingsMongodbPO.getAccountId());
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
        .flatMap(accountId -> accountAddressDocumentRepository.findById(addressId).filter(
          accountAddressMongodbPO -> accountId.equals(accountAddressMongodbPO.getAccountId())))
        .filter(accountAddressMongodbPO -> !accountAddressMongodbPO.isDefaultAddress())
        .ifPresent(accountAddressMongodbPO -> {
          accountAddressDocumentRepository.deleteById(addressId);
          accountCacheRepository.deleteById(accountAddressMongodbPO.getAccountId());
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
          accountId -> accountSystemSettingsDocumentRepository.findById(systemSettingsId).filter(
            accountSystemSettingsMongodbPO -> accountId.equals(
              accountSystemSettingsMongodbPO.getAccountId())))
        .filter(
          accountSystemSettingsMongodbPO -> !accountSystemSettingsMongodbPO.isDefaultSystemSettings())
        .ifPresent(
          accountSystemSettingsMongodbPO -> {
            accountSystemSettingsDocumentRepository.deleteById(systemSettingsId);
            accountCacheRepository.deleteById(accountSystemSettingsMongodbPO.getAccountId());
          });
    }
  }
}
