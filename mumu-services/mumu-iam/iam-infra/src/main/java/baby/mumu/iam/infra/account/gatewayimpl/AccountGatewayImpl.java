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
import baby.mumu.basis.enums.AccountAvatarSourceEnum;
import baby.mumu.basis.event.OfflineSuccessEvent;
import baby.mumu.basis.exception.AccountAlreadyExistsException;
import baby.mumu.basis.exception.ApplicationException;
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
import baby.mumu.iam.infra.account.gatewayimpl.database.po.AccountArchivedPO;
import baby.mumu.iam.infra.account.gatewayimpl.database.po.AccountPO;
import baby.mumu.iam.infra.account.gatewayimpl.document.AccountAddressDocumentRepository;
import baby.mumu.iam.infra.account.gatewayimpl.document.AccountAvatarDocumentRepository;
import baby.mumu.iam.infra.account.gatewayimpl.document.AccountSystemSettingsDocumentRepository;
import baby.mumu.iam.infra.account.gatewayimpl.document.po.AccountAddressDocumentPO;
import baby.mumu.iam.infra.account.gatewayimpl.document.po.AccountAvatarDocumentPO;
import baby.mumu.iam.infra.relations.cache.AccountRoleCacheRepository;
import baby.mumu.iam.infra.relations.database.AccountRoleRepository;
import baby.mumu.iam.infra.token.gatewayimpl.cache.OidcIdTokenCacheRepository;
import baby.mumu.iam.infra.token.gatewayimpl.cache.PasswordTokenCacheRepository;
import baby.mumu.log.client.api.OperationLogGrpcService;
import baby.mumu.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import baby.mumu.storage.client.api.FileGrpcService;
import com.google.protobuf.Int64Value;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
  private final FileGrpcService fileGrpcService;

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
    AccountAvatarDocumentRepository accountAvatarDocumentRepository,
    FileGrpcService fileGrpcService) {
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
    this.fileGrpcService = fileGrpcService;
  }

  /**
   * {@inheritDoc}
   *
   * @return 账号ID
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public Long register(Account account) {
    AccountPO accountPO = accountConvertor.toAccountPO(account)
      .orElseThrow(() -> new ApplicationException(ResponseCode.INVALID_ACCOUNT_FORMAT));
    if (accountRepository.existsByIdOrUsernameOrEmail(account.getId(),
      account.getUsername(), account.getEmail())
      || accountArchivedRepository.existsByIdOrUsernameOrEmail(account.getId(),
      account.getUsername(), account.getEmail())) {
      operationLogGrpcService.syncSubmit(OperationLogSubmitGrpcCmd.newBuilder()
        .setContent("User registration")
        .setBizNo(account.getUsername())
        .setFail(ResponseCode.ACCOUNT_ALREADY_EXISTS.getMessage())
        .build());
      throw new AccountAlreadyExistsException(account.getUsername());
    }
    // 验证时区是否为有效时区类型
    if (StringUtils.isNotBlank(accountPO.getTimezone()) && !TimeUtils.isValidTimeZone(
      accountPO.getTimezone())) {
      throw new ApplicationException(ResponseCode.TIME_ZONE_IS_NOT_AVAILABLE);
    }
    accountPO.setPassword(passwordEncoder.encode(accountPO.getPassword()));
    AccountPO persisted = accountRepository.persist(accountPO);
    account.setId(persisted.getId());
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
    accountCacheRepository.deleteById(persisted.getId());
    accountRoleCacheRepository.deleteById(persisted.getId());
    operationLogGrpcService.syncSubmit(OperationLogSubmitGrpcCmd.newBuilder()
      .setContent("User registration")
      .setBizNo(account.getUsername())
      .setSuccess(String.format("User %s registration successfully", account.getUsername()))
      .build());
    return persisted.getId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public Optional<Account> findAccountByUsername(String username) {
    Optional<Account> cachedAccount = accountCacheRepository.findByUsername(username)
      .flatMap(accountConvertor::toEntity);
    if (cachedAccount.isPresent()) {
      return cachedAccount;
    }
    Optional<Account> dbAccount = accountRepository.findByUsername(username)
      .flatMap(accountConvertor::toEntity);
    dbAccount.flatMap(accountConvertor::toAccountCacheablePO)
      .ifPresent(accountCacheRepository::save);
    return dbAccount;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public Optional<Account> findAccountByEmail(String email) {
    Optional<Account> cached = accountCacheRepository.findByEmail(email)
      .flatMap(accountConvertor::toEntity);
    if (cached.isPresent()) {
      return cached;
    }
    Optional<Account> dbAccount = accountRepository.findByEmail(email)
      .flatMap(accountConvertor::toEntity);
    dbAccount.flatMap(accountConvertor::toAccountCacheablePO)
      .ifPresent(accountCacheRepository::save);
    return dbAccount;
  }

  /**
   * {@inheritDoc}
   *
   * @return 账号修改后数据
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Account> updateById(Account account) {
    if (account == null) {
      return Optional.empty();
    }
    Long loginAccountId = SecurityContextUtils.getLoginAccountId()
      .filter(id -> Objects.equals(id, account.getId()))
      .orElseThrow(() -> new ApplicationException(ResponseCode.UNAUTHORIZED));
    AccountPO accountPO = accountConvertor.toAccountPO(account)
      .orElseThrow(() -> new ApplicationException(ResponseCode.INVALID_ACCOUNT_FORMAT));
    AccountPO merged = accountRepository.merge(accountPO);
    accountCacheRepository.deleteById(loginAccountId);
    accountRoleCacheRepository.deleteById(loginAccountId);
    return accountConvertor.toEntity(merged);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateRoleById(Account account) {
    if (account == null) {
      return;
    }
    Long accountId = account.getId();
    if (!accountRepository.existsById(accountId)) {
      return;
    }
    Optional<AccountPO> accountPO = accountConvertor.toAccountPO(account);
    if (accountPO.isPresent()) {
      accountRoleRepository.deleteByAccountId(accountId);
      accountRoleRepository.persistAll(accountConvertor.toAccountRolePOS(account));
      accountRoleCacheRepository.deleteById(accountId);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void disable(Long accountId) {
    AccountPO accountPO = accountRepository.findById(accountId)
      .orElseThrow(() -> new ApplicationException(ResponseCode.ACCOUNT_DOES_NOT_EXIST));
    accountPO.setEnabled(false);
    accountRepository.merge(accountPO);
    passwordTokenCacheRepository.deleteById(accountId);
    accountCacheRepository.deleteById(accountId);
    accountRoleCacheRepository.deleteById(accountId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public Optional<Account> queryCurrentLoginAccount() {
    Optional<Long> optionalAccountId = SecurityContextUtils.getLoginAccountId();
    if (optionalAccountId.isEmpty()) {
      return Optional.empty();
    }
    Long accountId = optionalAccountId.get();
    // 优先查缓存
    Optional<Account> cachedAccount = accountCacheRepository.findById(accountId)
      .flatMap(accountConvertor::toEntity);
    if (cachedAccount.isPresent()) {
      return cachedAccount;
    }
    // 缓存未命中，查数据库
    Optional<Account> dbAccount = accountRepository.findById(accountId)
      .flatMap(accountConvertor::toEntity);
    // 写入缓存
    dbAccount.flatMap(accountConvertor::toAccountCacheablePO)
      .ifPresent(accountCacheRepository::save);
    return dbAccount;
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
    AccountPO accountPO = accountRepository.findById(accountId)
      .orElseThrow(() -> new ApplicationException(ResponseCode.ACCOUNT_DOES_NOT_EXIST));
    String initialPassword = extensionProperties.getAuthentication().getInitialPassword();
    if (StringUtils.isBlank(initialPassword)) {
      throw new ApplicationException(ResponseCode.THE_INITIAL_PASSWORD_CANNOT_BE_EMPTY);
    }
    accountPO.setPassword(passwordEncoder.encode(initialPassword));
    accountRepository.merge(accountPO);
    accountCacheRepository.deleteById(accountId);
    accountRoleCacheRepository.deleteById(accountId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  @DangerousOperation("删除当前账号")
  public void deleteCurrentAccount() {
    Long accountId = SecurityContextUtils.getLoginAccountId()
      .orElseThrow(() -> new ApplicationException(ResponseCode.UNAUTHORIZED));
    AccountPO accountPO = accountRepository.findById(accountId)
      .orElseThrow(() -> new ApplicationException(ResponseCode.ACCOUNT_DOES_NOT_EXIST));
    // 若账号还有余额，不允许删除
    if (accountPO.getBalance().isGreaterThan(Money.of(0, accountPO.getBalance().getCurrency()))) {
      throw new ApplicationException(ResponseCode.THE_ACCOUNT_HAS_AN_UNUSED_BALANCE);
    }
    // 删除数据库中的信息
    accountRepository.deleteById(accountId);
    // 删除账号地址信息
    accountAddressDocumentRepository.deleteByAccountId(accountId);
    // 删除账号系统设置信息
    accountSystemSettingsDocumentRepository.deleteByAccountId(accountId);

    // 如果账号存在上传头像则删除上传头像文件
    Optional<AccountAvatarDocumentPO> accountAvatarDocumentPOOptional = accountAvatarDocumentRepository.findByAccountId(
      accountId);
    if (accountAvatarDocumentPOOptional.isPresent()) {
      AccountAvatarDocumentPO accountAvatarDocumentPO = accountAvatarDocumentPOOptional.get();
      if (AccountAvatarSourceEnum.UPLOAD.equals(accountAvatarDocumentPO.getSource())) {
        fileGrpcService.syncDeleteByMetadataId(Int64Value.of(
          Long.parseLong(accountAvatarDocumentPO.getFileId())));
      }
      accountAvatarDocumentRepository.deleteByAccountId(accountId);
    }

    // 删除账号角色信息
    accountRoleRepository.deleteByAccountId(accountId);
    // 删除缓存
    passwordTokenCacheRepository.deleteById(accountId);
    accountCacheRepository.deleteById(accountId);
    accountRoleCacheRepository.deleteById(accountId);
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
    Long accountId = SecurityContextUtils.getLoginAccountId()
      .orElseThrow(() -> new ApplicationException(ResponseCode.UNAUTHORIZED));
    AccountPO accountPO = accountRepository.findById(accountId)
      .orElseThrow(() -> new ApplicationException(ResponseCode.ACCOUNT_DOES_NOT_EXIST));
    return passwordEncoder.matches(password, accountPO.getPassword());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void changePassword(String originalPassword, String newPassword) {
    Long accountId = SecurityContextUtils.getLoginAccountId()
      .orElseThrow(() -> new ApplicationException(ResponseCode.UNAUTHORIZED));
    AccountPO accountPO = accountRepository.findById(accountId)
      .orElseThrow(() -> new ApplicationException(ResponseCode.ACCOUNT_DOES_NOT_EXIST));
    if (!passwordEncoder.matches(originalPassword, accountPO.getPassword())) {
      throw new ApplicationException(ResponseCode.ACCOUNT_PASSWORD_IS_INCORRECT);
    }
    accountPO.setPassword(passwordEncoder.encode(newPassword));
    accountRepository.merge(accountPO);
    accountCacheRepository.deleteById(accountId);
    accountRoleCacheRepository.deleteById(accountId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  @DangerousOperation("归档ID为%0的账号")
  public void archiveById(Long accountId) {
    if (accountId == null) {
      return;
    }
    AccountPO accountPO = accountRepository.findById(accountId)
      .orElseThrow(() -> new ApplicationException(ResponseCode.ACCOUNT_DOES_NOT_EXIST));
    AccountArchivedPO archivedPO = accountConvertor.toAccountArchivedPO(accountPO)
      .orElseThrow(
        () -> new ApplicationException(ResponseCode.ACCOUNT_CONVERSION_TO_ARCHIVED_FAILED));
    // 禁止归档有余额的账号
    if (archivedPO.getBalance().isGreaterThan(Money.of(0, archivedPO.getBalance().getCurrency()))) {
      throw new ApplicationException(ResponseCode.THE_ACCOUNT_HAS_AN_UNUSED_BALANCE);
    }
    archivedPO.setArchived(true);
    accountArchivedRepository.persist(archivedPO);
    // 删除主表和缓存
    accountRepository.deleteById(accountId);
    accountCacheRepository.deleteById(accountId);
    accountRoleCacheRepository.deleteById(accountId);
    // 安排归档后的延迟清理任务
    GlobalProperties global = extensionProperties.getGlobal();
    Instant triggerTime = Instant.now()
      .plus(global.getArchiveDeletionPeriod(), global.getArchiveDeletionPeriodUnit());
    jobScheduler.schedule(triggerTime, () -> deleteArchivedDataJob(accountId));
  }

  @Job(name = "删除ID为：%0 的账号归档数据")
  @DangerousOperation("删除ID为%0的账号归档数据定时任务")
  @Transactional(rollbackFor = Exception.class)
  public void deleteArchivedDataJob(Long id) {
    if (id == null) {
      return;
    }
    accountArchivedRepository.deleteById(id);
    accountAddressDocumentRepository.deleteByAccountId(id);
    accountRoleRepository.deleteByAccountId(id);
    accountCacheRepository.deleteById(id);
    accountRoleCacheRepository.deleteById(id);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(Long accountId) {
    if (accountId == null) {
      return;
    }
    AccountArchivedPO archivedPO = accountArchivedRepository.findById(accountId)
      .orElseThrow(() -> new ApplicationException(ResponseCode.ACCOUNT_ARCHIVE_NOT_FOUND));
    AccountPO accountPO = accountConvertor.toAccountPO(archivedPO)
      .orElseThrow(
        () -> new ApplicationException(ResponseCode.ACCOUNT_CONVERSION_TO_ARCHIVED_FAILED));
    accountPO.setArchived(false);
    accountArchivedRepository.deleteById(accountId);
    accountRepository.persist(accountPO);
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
    Optional<Long> optionalAccountId = SecurityContextUtils.getLoginAccountId();
    if (optionalAccountId.isEmpty()) {
      return;
    }
    Long accountId = optionalAccountId.get();
    // 清除缓存
    passwordTokenCacheRepository.deleteById(accountId);
    oidcIdTokenCacheRepository.deleteById(accountId);
    accountCacheRepository.deleteById(accountId);
    accountRoleCacheRepository.deleteById(accountId);
    // 发布登出成功事件
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    applicationEventPublisher.publishEvent(new LogoutSuccessEvent(authentication));
    // 提交操作日志（如果有账号名）
    SecurityContextUtils.getLoginAccountName().ifPresent(accountName -> {
      OperationLogSubmitGrpcCmd cmd = OperationLogSubmitGrpcCmd.newBuilder()
        .setContent("User logout")
        .setBizNo(accountName)
        .setSuccess(String.format("User %s successfully logged out", accountName))
        .build();
      operationLogGrpcService.syncSubmit(cmd);
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
      accountConvertor.toAccountPO(account).orElseGet(AccountPO::new),
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
      accountConvertor.toAccountPO(account).orElseGet(AccountPO::new),
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
