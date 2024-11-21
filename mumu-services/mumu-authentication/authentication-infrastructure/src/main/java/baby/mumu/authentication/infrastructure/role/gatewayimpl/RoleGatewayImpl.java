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
import baby.mumu.authentication.domain.permission.Permission;
import baby.mumu.authentication.domain.role.Role;
import baby.mumu.authentication.domain.role.gateway.RoleGateway;
import baby.mumu.authentication.infrastructure.relations.database.RolePathsDo;
import baby.mumu.authentication.infrastructure.relations.database.RolePathsDoId;
import baby.mumu.authentication.infrastructure.relations.database.RolePathsRepository;
import baby.mumu.authentication.infrastructure.relations.database.RolePermissionDo;
import baby.mumu.authentication.infrastructure.relations.database.RolePermissionRepository;
import baby.mumu.authentication.infrastructure.relations.redis.RolePermissionRedisRepository;
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
import java.util.Set;
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
  private final RolePermissionRepository rolePermissionRepository;
  private final RoleRedisRepository roleRedisRepository;
  private final RolePermissionRedisRepository rolePermissionRedisRepository;
  private final RolePathsRepository rolePathsRepository;

  public RoleGatewayImpl(RoleRepository roleRepository,
    ObjectProvider<DistributedLock> distributedLockObjectProvider,
    AccountGateway accountGateway, RoleConvertor roleConvertor,
    RoleArchivedRepository roleArchivedRepository, JobScheduler jobScheduler,
    ExtensionProperties extensionProperties,
    RolePermissionRepository rolePermissionRepository,
    RoleRedisRepository roleRedisRepository,
    RolePermissionRedisRepository rolePermissionRedisRepository,
    RolePathsRepository rolePathsRepository) {
    this.roleRepository = roleRepository;
    this.accountGateway = accountGateway;
    this.distributedLock = distributedLockObjectProvider.getIfAvailable();
    this.roleConvertor = roleConvertor;
    this.roleArchivedRepository = roleArchivedRepository;
    this.jobScheduler = jobScheduler;
    this.extensionProperties = extensionProperties;
    this.rolePermissionRepository = rolePermissionRepository;
    this.roleRedisRepository = roleRedisRepository;
    this.rolePermissionRedisRepository = rolePermissionRedisRepository;
    this.rolePathsRepository = rolePathsRepository;
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
        rolePathsRepository.persist(
          new RolePathsDo(new RolePathsDoId(roleDo.getId(), roleDo.getId(), 0L), roleDo, roleDo));
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
      List<RolePermissionDo> rolePermissionDos = roleConvertor.toRolePermissionDos(role);
      if (CollectionUtils.isNotEmpty(rolePermissionDos)) {
        rolePermissionRepository.persistAll(rolePermissionDos);
        rolePermissionRedisRepository.deleteById(roleNonNull.getId());
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
      rolePermissionRepository.deleteByRoleId(roleId);
      roleRepository.deleteById(roleId);
      rolePathsRepository.deleteAllPathsByRoleId(roleId);
      roleArchivedRepository.deleteById(roleId);
      roleRedisRepository.deleteById(roleId);
      rolePermissionRedisRepository.deleteById(roleId);
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
        rolePermissionRepository.deleteByRoleId(roleDomain.getId());
        saveRoleAuthorityRelationsData(roleDomain);
        roleRedisRepository.deleteById(roleDomain.getId());
        rolePermissionRedisRepository.deleteById(roleDomain.getId());
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
          roleEntity.getPermissions()))
        .map(authorities -> authorities.stream().map(Permission::getId).collect(
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
          roleEntity.getPermissions()))
        .map(authorities -> authorities.stream().map(Permission::getId).collect(
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
          roleEntity.getPermissions()))
        .map(authorities -> authorities.stream().map(Permission::getId).collect(
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
          roleEntity.getPermissions()))
        .map(authorities -> authorities.stream().map(Permission::getId).collect(
          Collectors.toList())).orElse(null), pageRequest);
    return new PageImpl<>(roleArchivedDoPage.getContent().stream()
      .flatMap(roleArchivedDo -> roleConvertor.toEntity(roleArchivedDo).stream())
      .toList(), pageRequest, roleArchivedDoPage.getTotalElements());
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public List<Role> findAllContainPermission(Long permissionId) {
    return rolePermissionRepository.findByPermissionId(permissionId).stream()
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
        rolePermissionRedisRepository.deleteById(roleArchivedDo.getId());
        GlobalProperties global = extensionProperties.getGlobal();
        jobScheduler.schedule(Instant.now()
            .plus(global.getArchiveDeletionPeriod(), global.getArchiveDeletionPeriodUnit()),
          () -> deleteArchivedDataJob(roleArchivedDo.getId()));
      });
  }

  @Job(name = "删除ID为：%0 的角色归档数据")
  @DangerousOperation("根据ID删除ID为%0的角色归档数据定时任务")
  @Transactional(rollbackFor = Exception.class)
  public void deleteArchivedDataJob(Long id) {
    Optional.ofNullable(id)
      .filter(roleId -> accountGateway.findAllAccountByRoleId(roleId).isEmpty())
      .ifPresent(roleIdNotNull -> {
        roleArchivedRepository.deleteById(roleIdNotNull);
        rolePathsRepository.deleteAllPathsByRoleId(roleIdNotNull);
        rolePermissionRepository.deleteByRoleId(roleIdNotNull);
        roleRedisRepository.deleteById(roleIdNotNull);
        rolePermissionRedisRepository.deleteById(roleIdNotNull);
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
        rolePermissionRedisRepository.deleteById(roleDo.getId());
      });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void addAncestor(Long descendantId, Long ancestorId) {
    Optional<RoleDo> ancestorRoleDoOptional = roleRepository.findById(ancestorId);
    Optional<RoleDo> descendantRoleDoOptional = roleRepository.findById(
      descendantId);
    if (ancestorRoleDoOptional.isPresent() && descendantRoleDoOptional.isPresent()) {
      // 后代角色
      RoleDo descendantRoleDo = descendantRoleDoOptional.get();
      // 为节点添加从所有祖先到自身的路径
      List<RolePathsDo> ancestorRoles = rolePathsRepository.findByDescendantId(
        ancestorId);
      // 成环检测
      Set<Long> ancestorIds = ancestorRoles.stream()
        .map(rolePathsDo -> rolePathsDo.getId().getAncestorId())
        .collect(
          Collectors.toSet());
      if (ancestorIds.contains(descendantId)) {
        throw new MuMuException(ResponseCode.ROLE_CYCLE);
      }
      if (rolePathsRepository.existsById(
        new RolePathsDoId(ancestorId, descendantId, 1L))) {
        throw new MuMuException(ResponseCode.ROLE_PATH_ALREADY_EXISTS);
      }
      List<RolePathsDo> rolePathsDos = ancestorRoles.stream()
        .map(rolePathsDo -> new RolePathsDo(
          new RolePathsDoId(rolePathsDo.getId().getAncestorId(), descendantId,
            rolePathsDo.getId().getDepth() + 1),
          rolePathsDo.getAncestor(), descendantRoleDo))
        .filter(
          rolePathsDo -> !rolePathsRepository.existsById(rolePathsDo.getId())
        )
        .collect(
          Collectors.toList());
      rolePathsRepository.persistAll(rolePathsDos);
      roleRedisRepository.deleteById(ancestorId);
      roleRedisRepository.deleteById(descendantId);
    }
  }

  @Override
  public Page<Role> findRootRoles(int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<RolePathsDo> repositoryAll = rolePathsRepository.findRootRoles(
      pageRequest);
    List<Role> roles = repositoryAll.getContent().stream().flatMap(
        rolePathsDo -> findById(rolePathsDo.getId().getAncestorId()).stream())
      .collect(Collectors.toList());
    return new PageImpl<>(roles, pageRequest, repositoryAll.getTotalElements());
  }

  @Override
  public Page<Role> findDirectRoles(Long ancestorId, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<RolePathsDo> repositoryAll = rolePathsRepository.findDirectRoles(
      ancestorId,
      pageRequest);
    List<Role> roles = repositoryAll.getContent().stream().flatMap(
        rolePathsDo -> findById(rolePathsDo.getId().getDescendantId()).stream())
      .collect(Collectors.toList());
    return new PageImpl<>(roles, pageRequest, repositoryAll.getTotalElements());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deletePath(Long ancestorId, Long descendantId) {
    if (rolePathsRepository.existsDescendantRoles(descendantId)) {
      throw new MuMuException(ResponseCode.DESCENDANT_ROLE_HAS_DESCENDANT_ROLE);
    }
    rolePathsRepository.deleteById(new RolePathsDoId(ancestorId, descendantId, 1L));
    rolePathsRepository.deleteUnreachableData();
    roleRedisRepository.deleteById(ancestorId);
    roleRedisRepository.deleteById(descendantId);
  }

  @Override
  public Optional<Role> findById(Long id) {
    return Optional.ofNullable(id).flatMap(roleRedisRepository::findById).flatMap(
      roleConvertor::toEntity).or(() -> {
      Optional<Role> role = roleRepository.findById(id)
        .flatMap(roleConvertor::toEntity);
      role.flatMap(roleConvertor::toRoleRedisDo)
        .ifPresent(roleRedisRepository::save);
      return role;
    });
  }
}
