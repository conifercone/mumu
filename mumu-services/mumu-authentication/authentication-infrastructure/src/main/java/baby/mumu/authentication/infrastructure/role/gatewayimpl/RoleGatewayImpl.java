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
package baby.mumu.authentication.infrastructure.role.gatewayimpl;

import baby.mumu.authentication.domain.account.Account;
import baby.mumu.authentication.domain.account.gateway.AccountGateway;
import baby.mumu.authentication.domain.authority.Authority;
import baby.mumu.authentication.domain.role.Role;
import baby.mumu.authentication.domain.role.gateway.RoleGateway;
import baby.mumu.authentication.infrastructure.relations.database.RoleAuthorityDo;
import baby.mumu.authentication.infrastructure.relations.database.RoleAuthorityRepository;
import baby.mumu.authentication.infrastructure.relations.redis.RoleAuthorityRedisRepository;
import baby.mumu.authentication.infrastructure.role.convertor.RoleConvertor;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.RoleArchivedRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.RoleRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleArchivedDo;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleDo;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.redis.RoleRedisRepository;
import baby.mumu.basis.annotations.DangerousOperation;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.GlobalProperties;
import baby.mumu.extension.distributed.lock.DistributedLock;
import io.micrometer.observation.annotation.Observed;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色领域网关实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "RoleGatewayImpl")
public class RoleGatewayImpl implements RoleGateway {

  private final RoleRepository roleRepository;
  private final DistributedLock distributedLock;
  private final AccountGateway accountGateway;
  private final RoleConvertor roleConvertor;
  private final RoleArchivedRepository roleArchivedRepository;
  private final JobScheduler jobScheduler;
  private final ExtensionProperties extensionProperties;
  private final RoleAuthorityRepository roleAuthorityRepository;
  private final RoleRedisRepository roleRedisRepository;
  private final RoleAuthorityRedisRepository roleAuthorityRedisRepository;

  public RoleGatewayImpl(RoleRepository roleRepository,
    ObjectProvider<DistributedLock> distributedLockObjectProvider,
    AccountGateway accountGateway, RoleConvertor roleConvertor,
    RoleArchivedRepository roleArchivedRepository, JobScheduler jobScheduler,
    ExtensionProperties extensionProperties,
    RoleAuthorityRepository roleAuthorityRepository,
    RoleRedisRepository roleRedisRepository,
    RoleAuthorityRedisRepository roleAuthorityRedisRepository) {
    this.roleRepository = roleRepository;
    this.accountGateway = accountGateway;
    this.distributedLock = distributedLockObjectProvider.getIfAvailable();
    this.roleConvertor = roleConvertor;
    this.roleArchivedRepository = roleArchivedRepository;
    this.jobScheduler = jobScheduler;
    this.extensionProperties = extensionProperties;
    this.roleAuthorityRepository = roleAuthorityRepository;
    this.roleRedisRepository = roleRedisRepository;
    this.roleAuthorityRedisRepository = roleAuthorityRedisRepository;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void add(Role role) {
    //保存角色数据
    Optional.ofNullable(role).flatMap(roleConvertor::toDataObject)
      .filter(roleDo -> !roleRepository.existsByIdOrCode(roleDo.getId(), roleDo.getCode())
        && !roleArchivedRepository.existsByIdOrCode(roleDo.getId(), roleDo.getCode()))
      .ifPresentOrElse(roleDo -> {
        roleRepository.persist(roleDo);
        roleRedisRepository.deleteById(roleDo.getId());
      }, () -> {
        throw new MuMuException(ResponseCode.ROLE_CODE_OR_ID_ALREADY_EXISTS);
      });
    saveRoleAuthorityRelationsData(role);
  }

  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "2.1.0")
  protected void saveRoleAuthorityRelationsData(Role role) {
    //保存角色权限关系数据（如果存在关系）
    Optional.ofNullable(role).ifPresent(roleNonNull -> {
      List<RoleAuthorityDo> roleAuthorityDos = roleConvertor.toRoleAuthorityDos(role);
      if (CollectionUtils.isNotEmpty(roleAuthorityDos)) {
        roleAuthorityRepository.persistAll(roleAuthorityDos);
        roleAuthorityRedisRepository.deleteById(roleNonNull.getId());
      }
    });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  @DangerousOperation("删除ID为%0的角色")
  public void deleteById(Long id) {
    Optional.ofNullable(id).ifPresent(roleId -> {
      List<Account> allAccountByRoleId = accountGateway.findAllAccountByRoleId(roleId);
      if (CollectionUtils.isNotEmpty(allAccountByRoleId)) {
        throw new MuMuException(ResponseCode.ROLE_IS_IN_USE_AND_CANNOT_BE_REMOVED,
          allAccountByRoleId.stream().map(Account::getUsername).toList());
      }
      roleAuthorityRepository.deleteByRoleId(roleId);
      roleRepository.deleteById(roleId);
      roleArchivedRepository.deleteById(roleId);
      roleRedisRepository.deleteById(roleId);
      roleAuthorityRedisRepository.deleteById(roleId);
    });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateById(Role role) {
    Optional.ofNullable(role).ifPresent(roleDomain -> {
      Optional.ofNullable(distributedLock).ifPresent(DistributedLock::lock);
      try {
        roleConvertor.toDataObject(roleDomain).ifPresent(roleRepository::merge);
        //删除权限关系数据重新添加
        roleAuthorityRepository.deleteByRoleId(roleDomain.getId());
        saveRoleAuthorityRelationsData(roleDomain);
        roleRedisRepository.deleteById(roleDomain.getId());
        roleAuthorityRedisRepository.deleteById(roleDomain.getId());
      } finally {
        Optional.ofNullable(distributedLock).ifPresent(DistributedLock::unlock);
      }
    });
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public Page<Role> findAll(Role role, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<RoleDo> roleDoPage = roleRepository.findAllPage(
      roleConvertor.toDataObject(role).orElseGet(RoleDo::new),
      Optional.ofNullable(role).flatMap(roleEntity -> Optional.ofNullable(
          roleEntity.getAuthorities()))
        .map(authorities -> authorities.stream().map(Authority::getId).collect(
          Collectors.toList())).orElse(null), pageRequest);
    return new PageImpl<>(roleDoPage.getContent().stream()
      .flatMap(roleDo -> roleConvertor.toEntity(roleDo).stream())
      .toList(), pageRequest, roleDoPage.getTotalElements());
  }

  @Override
  @API(status = Status.STABLE, since = "2.2.0")
  @Transactional(rollbackFor = Exception.class)
  public Slice<Role> findAllSlice(Role role, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Slice<RoleDo> roleDoSlice = roleRepository.findAllSlice(
      roleConvertor.toDataObject(role).orElseGet(RoleDo::new),
      Optional.ofNullable(role).flatMap(roleEntity -> Optional.ofNullable(
          roleEntity.getAuthorities()))
        .map(authorities -> authorities.stream().map(Authority::getId).collect(
          Collectors.toList())).orElse(null), pageRequest);
    return new SliceImpl<>(roleDoSlice.getContent().stream()
      .flatMap(roleDataObject -> roleConvertor.toEntity(roleDataObject).stream())
      .toList(), pageRequest, roleDoSlice.hasNext());
  }

  @Override
  @API(status = Status.STABLE, since = "2.2.0")
  @Transactional(rollbackFor = Exception.class)
  public Slice<Role> findArchivedAllSlice(Role role, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Slice<RoleArchivedDo> roleArchivedDos = roleArchivedRepository.findAllSlice(
      roleConvertor.toArchivedDo(role).orElseGet(RoleArchivedDo::new),
      Optional.ofNullable(role).flatMap(roleEntity -> Optional.ofNullable(
          roleEntity.getAuthorities()))
        .map(authorities -> authorities.stream().map(Authority::getId).collect(
          Collectors.toList())).orElse(null), pageRequest);
    return new SliceImpl<>(roleArchivedDos.getContent().stream()
      .flatMap(roleArchivedDo -> roleConvertor.toEntity(roleArchivedDo).stream())
      .toList(), pageRequest, roleArchivedDos.hasNext());
  }

  @Override
  @API(status = Status.STABLE, since = "2.2.0")
  @Transactional(rollbackFor = Exception.class)
  public Page<Role> findArchivedAll(Role role, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<RoleArchivedDo> roleArchivedDoPage = roleArchivedRepository.findAllPage(
      roleConvertor.toArchivedDo(role).orElseGet(RoleArchivedDo::new),
      Optional.ofNullable(role).flatMap(roleEntity -> Optional.ofNullable(
          roleEntity.getAuthorities()))
        .map(authorities -> authorities.stream().map(Authority::getId).collect(
          Collectors.toList())).orElse(null), pageRequest);
    return new PageImpl<>(roleArchivedDoPage.getContent().stream()
      .flatMap(roleArchivedDo -> roleConvertor.toEntity(roleArchivedDo).stream())
      .toList(), pageRequest, roleArchivedDoPage.getTotalElements());
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public List<Role> findAllContainAuthority(Long authorityId) {
    return roleAuthorityRepository.findByAuthorityId(authorityId).stream()
      .flatMap(roleAuthorityDo -> roleConvertor.toEntity(roleAuthorityDo.getRole()).stream())
      .toList();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @DangerousOperation("根据ID归档ID为%0的角色")
  public void archiveById(Long id) {
    List<Account> allAccountByRoleId = accountGateway.findAllAccountByRoleId(id);
    if (CollectionUtils.isNotEmpty(allAccountByRoleId)) {
      throw new MuMuException(ResponseCode.ROLE_IS_IN_USE_AND_CANNOT_BE_ARCHIVE,
        allAccountByRoleId.stream().map(Account::getUsername).toList());
    }
    //noinspection DuplicatedCode
    Optional.ofNullable(id).flatMap(roleRepository::findById)
      .flatMap(roleConvertor::toArchivedDo).ifPresent(roleArchivedDo -> {
        roleArchivedDo.setArchived(true);
        roleArchivedRepository.persist(roleArchivedDo);
        roleRepository.deleteById(roleArchivedDo.getId());
        roleRedisRepository.deleteById(roleArchivedDo.getId());
        roleAuthorityRedisRepository.deleteById(roleArchivedDo.getId());
        GlobalProperties global = extensionProperties.getGlobal();
        jobScheduler.schedule(Instant.now()
            .plus(global.getArchiveDeletionPeriod(), global.getArchiveDeletionPeriodUnit()),
          () -> deleteArchivedDataJob(roleArchivedDo.getId()));
      });
  }

  @Job(name = "删除ID为：%0 的角色归档数据")
  @DangerousOperation("根据ID删除ID为%0的角色归档数据定时任务")
  public void deleteArchivedDataJob(Long id) {
    Optional.ofNullable(id)
      .filter(roleId -> accountGateway.findAllAccountByRoleId(roleId).isEmpty())
      .ifPresent(roleIdNotNull -> {
        roleArchivedRepository.deleteById(roleIdNotNull);
        roleAuthorityRepository.deleteByRoleId(roleIdNotNull);
        roleRedisRepository.deleteById(roleIdNotNull);
        roleAuthorityRedisRepository.deleteById(roleIdNotNull);
      });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(Long id) {
    Optional.ofNullable(id).flatMap(roleArchivedRepository::findById)
      .flatMap(roleConvertor::toDataObject).ifPresent(roleDo -> {
        roleDo.setArchived(false);
        roleArchivedRepository.deleteById(roleDo.getId());
        roleRepository.persist(roleDo);
        roleRedisRepository.deleteById(roleDo.getId());
        roleAuthorityRedisRepository.deleteById(roleDo.getId());
      });
  }
}
