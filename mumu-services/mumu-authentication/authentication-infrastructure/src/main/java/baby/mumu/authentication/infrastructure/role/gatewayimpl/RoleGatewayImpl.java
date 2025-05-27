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
package baby.mumu.authentication.infrastructure.role.gatewayimpl;

import baby.mumu.authentication.domain.account.Account;
import baby.mumu.authentication.domain.account.gateway.AccountGateway;
import baby.mumu.authentication.domain.permission.Permission;
import baby.mumu.authentication.domain.role.Role;
import baby.mumu.authentication.domain.role.gateway.RoleGateway;
import baby.mumu.authentication.infrastructure.relations.cache.RolePermissionCacheRepository;
import baby.mumu.authentication.infrastructure.relations.database.RolePathPO;
import baby.mumu.authentication.infrastructure.relations.database.RolePathPOId;
import baby.mumu.authentication.infrastructure.relations.database.RolePathRepository;
import baby.mumu.authentication.infrastructure.relations.database.RolePermissionPO;
import baby.mumu.authentication.infrastructure.relations.database.RolePermissionRepository;
import baby.mumu.authentication.infrastructure.role.convertor.RoleConvertor;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.cache.RoleCacheRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.RoleArchivedRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.RoleRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.po.RoleArchivedPO;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.po.RolePO;
import baby.mumu.basis.annotations.DangerousOperation;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.GlobalProperties;
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
  private final AccountGateway accountGateway;
  private final RoleConvertor roleConvertor;
  private final RoleArchivedRepository roleArchivedRepository;
  private final JobScheduler jobScheduler;
  private final ExtensionProperties extensionProperties;
  private final RolePermissionRepository rolePermissionRepository;
  private final RoleCacheRepository roleCacheRepository;
  private final RolePermissionCacheRepository rolePermissionCacheRepository;
  private final RolePathRepository rolePathRepository;

  public RoleGatewayImpl(RoleRepository roleRepository,
    AccountGateway accountGateway, RoleConvertor roleConvertor,
    RoleArchivedRepository roleArchivedRepository, JobScheduler jobScheduler,
    ExtensionProperties extensionProperties,
    RolePermissionRepository rolePermissionRepository,
    RoleCacheRepository roleCacheRepository,
    RolePermissionCacheRepository rolePermissionCacheRepository,
    RolePathRepository rolePathRepository) {
    this.roleRepository = roleRepository;
    this.accountGateway = accountGateway;
    this.roleConvertor = roleConvertor;
    this.roleArchivedRepository = roleArchivedRepository;
    this.jobScheduler = jobScheduler;
    this.extensionProperties = extensionProperties;
    this.rolePermissionRepository = rolePermissionRepository;
    this.roleCacheRepository = roleCacheRepository;
    this.rolePermissionCacheRepository = rolePermissionCacheRepository;
    this.rolePathRepository = rolePathRepository;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void add(Role role) {
    //保存角色数据
    Optional.ofNullable(role).flatMap(roleConvertor::toPO)
      .filter(rolePO -> !roleRepository.existsByIdOrCode(rolePO.getId(), rolePO.getCode())
        && !roleArchivedRepository.existsByIdOrCode(rolePO.getId(), rolePO.getCode()))
      .ifPresentOrElse(rolePO -> {
        roleRepository.persist(rolePO);
        saveRoleAuthorityRelationsData(role);
        Optional.ofNullable(role).ifPresent(roleNonNull -> roleNonNull.setId(rolePO.getId()));
        rolePathRepository.persist(
          new RolePathPO(new RolePathPOId(rolePO.getId(), rolePO.getId(), 0L), rolePO, rolePO));
        roleCacheRepository.deleteById(rolePO.getId());
      }, () -> {
        throw new MuMuException(ResponseCode.ROLE_CODE_OR_ID_ALREADY_EXISTS);
      });
  }

  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "2.1.0")
  protected void saveRoleAuthorityRelationsData(Role role) {
    //保存角色权限关系数据（如果存在关系）
    Optional.ofNullable(role).ifPresent(roleNonNull -> {
      List<RolePermissionPO> rolePermissionPOS = roleConvertor.toRolePermissionPOS(role);
      if (CollectionUtils.isNotEmpty(rolePermissionPOS)) {
        rolePermissionRepository.persistAll(rolePermissionPOS);
        rolePermissionCacheRepository.deleteById(roleNonNull.getId());
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
      rolePathRepository.deleteAllPathsByRoleId(roleId);
      roleArchivedRepository.deleteById(roleId);
      roleCacheRepository.deleteById(roleId);
      rolePermissionCacheRepository.deleteById(roleId);
    });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "2.4.0")
  @DangerousOperation("删除code为%0的角色")
  public void deleteByCode(String code) {
    Optional.ofNullable(code).flatMap(roleRepository::findByCode).map(RolePO::getId)
      .ifPresent(this::deleteById);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateById(Role role) {
    Optional.ofNullable(role).ifPresent(roleDomain -> {
      roleConvertor.toPO(roleDomain).ifPresent(roleRepository::merge);
      //删除权限关系数据重新添加
      rolePermissionRepository.deleteByRoleId(roleDomain.getId());
      saveRoleAuthorityRelationsData(roleDomain);
      roleCacheRepository.deleteById(roleDomain.getId());
      rolePermissionCacheRepository.deleteById(roleDomain.getId());
    });
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public Page<Role> findAll(Role role, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<RolePO> rolePOPage = roleRepository.findAllPage(
      roleConvertor.toPO(role).orElseGet(RolePO::new),
      Optional.ofNullable(role).flatMap(roleEntity -> Optional.ofNullable(
          roleEntity.getPermissions()))
        .map(authorities -> authorities.stream().map(Permission::getId).collect(
          Collectors.toList())).orElse(null), pageRequest);
    return new PageImpl<>(rolePOPage.getContent().stream()
      .flatMap(rolePO -> roleConvertor.toEntity(rolePO).stream())
      .toList(), pageRequest, rolePOPage.getTotalElements());
  }

  @Override
  @API(status = Status.STABLE, since = "2.2.0")
  @Transactional(rollbackFor = Exception.class)
  public Slice<Role> findAllSlice(Role role, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Slice<RolePO> rolePOSlice = roleRepository.findAllSlice(
      roleConvertor.toPO(role).orElseGet(RolePO::new),
      Optional.ofNullable(role).flatMap(roleEntity -> Optional.ofNullable(
          roleEntity.getPermissions()))
        .map(authorities -> authorities.stream().map(Permission::getId).collect(
          Collectors.toList())).orElse(null), pageRequest);
    return new SliceImpl<>(rolePOSlice.getContent().stream()
      .flatMap(roleDataObject -> roleConvertor.toEntity(roleDataObject).stream())
      .toList(), pageRequest, rolePOSlice.hasNext());
  }

  @Override
  @API(status = Status.STABLE, since = "2.2.0")
  @Transactional(rollbackFor = Exception.class)
  public Slice<Role> findArchivedAllSlice(Role role, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Slice<RoleArchivedPO> roleArchivedPOS = roleArchivedRepository.findAllSlice(
      roleConvertor.toArchivedPO(role).orElseGet(RoleArchivedPO::new),
      Optional.ofNullable(role).flatMap(roleEntity -> Optional.ofNullable(
          roleEntity.getPermissions()))
        .map(authorities -> authorities.stream().map(Permission::getId).collect(
          Collectors.toList())).orElse(null), pageRequest);
    return new SliceImpl<>(roleArchivedPOS.getContent().stream()
      .flatMap(roleArchivedPO -> roleConvertor.toEntity(roleArchivedPO).stream())
      .toList(), pageRequest, roleArchivedPOS.hasNext());
  }

  @Override
  @API(status = Status.STABLE, since = "2.2.0")
  @Transactional(rollbackFor = Exception.class)
  public Page<Role> findArchivedAll(Role role, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<RoleArchivedPO> roleArchivedPOPage = roleArchivedRepository.findAllPage(
      roleConvertor.toArchivedPO(role).orElseGet(RoleArchivedPO::new),
      Optional.ofNullable(role).flatMap(roleEntity -> Optional.ofNullable(
          roleEntity.getPermissions()))
        .map(authorities -> authorities.stream().map(Permission::getId).collect(
          Collectors.toList())).orElse(null), pageRequest);
    return new PageImpl<>(roleArchivedPOPage.getContent().stream()
      .flatMap(roleArchivedPO -> roleConvertor.toEntity(roleArchivedPO).stream())
      .toList(), pageRequest, roleArchivedPOPage.getTotalElements());
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  @Transactional(rollbackFor = Exception.class)
  public List<Role> findAllContainPermission(Long permissionId) {
    return rolePermissionRepository.findByPermissionId(permissionId).stream()
      .flatMap(rolePermissionPO -> roleConvertor.toEntity(rolePermissionPO.getRole()).stream())
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
    Optional.ofNullable(id).flatMap(roleRepository::findById)
      .flatMap(roleConvertor::toArchivedPO).ifPresent(roleArchivedPO -> {
        //noinspection DuplicatedCode
        roleArchivedPO.setArchived(true);
        roleArchivedRepository.persist(roleArchivedPO);
        roleRepository.deleteById(roleArchivedPO.getId());
        roleCacheRepository.deleteById(roleArchivedPO.getId());
        rolePermissionCacheRepository.deleteById(roleArchivedPO.getId());
        GlobalProperties global = extensionProperties.getGlobal();
        jobScheduler.schedule(Instant.now()
            .plus(global.getArchiveDeletionPeriod(), global.getArchiveDeletionPeriodUnit()),
          () -> deleteArchivedDataJob(roleArchivedPO.getId()));
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
        rolePathRepository.deleteAllPathsByRoleId(roleIdNotNull);
        rolePermissionRepository.deleteByRoleId(roleIdNotNull);
        roleCacheRepository.deleteById(roleIdNotNull);
        rolePermissionCacheRepository.deleteById(roleIdNotNull);
      });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(Long id) {
    Optional.ofNullable(id).flatMap(roleArchivedRepository::findById)
      .flatMap(roleConvertor::toPO).ifPresent(rolePO -> {
        rolePO.setArchived(false);
        roleArchivedRepository.deleteById(rolePO.getId());
        roleRepository.persist(rolePO);
        roleCacheRepository.deleteById(rolePO.getId());
        rolePermissionCacheRepository.deleteById(rolePO.getId());
      });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void addAncestor(Long descendantId, Long ancestorId) {
    Optional<RolePO> ancestorRolePOOptional = roleRepository.findById(ancestorId);
    Optional<RolePO> descendantRolePOOptional = roleRepository.findById(
      descendantId);
    if (ancestorRolePOOptional.isPresent() && descendantRolePOOptional.isPresent()) {
      // 后代角色
      RolePO descendantRolePO = descendantRolePOOptional.get();
      // 为节点添加从所有祖先到自身的路径
      List<RolePathPO> ancestorRoles = rolePathRepository.findByDescendantId(
        ancestorId);
      // 成环检测
      Set<Long> ancestorIds = ancestorRoles.stream()
        .map(rolePathPO -> rolePathPO.getId().getAncestorId())
        .collect(
          Collectors.toSet());
      if (ancestorIds.contains(descendantId)) {
        throw new MuMuException(ResponseCode.ROLE_CYCLE);
      }
      if (rolePathRepository.existsById(
        new RolePathPOId(ancestorId, descendantId, 1L))) {
        throw new MuMuException(ResponseCode.ROLE_PATH_ALREADY_EXISTS);
      }
      List<RolePathPO> rolePathPOS = ancestorRoles.stream()
        .map(rolePathPO -> new RolePathPO(
          new RolePathPOId(rolePathPO.getId().getAncestorId(), descendantId,
            rolePathPO.getId().getDepth() + 1),
          rolePathPO.getAncestor(), descendantRolePO))
        .filter(
          rolePathPO -> !rolePathRepository.existsById(rolePathPO.getId())
        )
        .collect(
          Collectors.toList());
      rolePathRepository.persistAll(rolePathPOS);
      roleCacheRepository.deleteById(ancestorId);
      roleCacheRepository.deleteById(descendantId);
    }
  }

  @Override
  public Page<Role> findRootRoles(int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<RolePathPO> repositoryAll = rolePathRepository.findRootRoles(
      pageRequest);
    List<Role> roles = repositoryAll.getContent().stream().flatMap(
        rolePathPO -> findById(rolePathPO.getId().getAncestorId()).stream())
      .collect(Collectors.toList());
    return new PageImpl<>(roles, pageRequest, repositoryAll.getTotalElements());
  }

  @Override
  public Page<Role> findDirectRoles(Long ancestorId, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<RolePathPO> repositoryAll = rolePathRepository.findDirectRoles(
      ancestorId,
      pageRequest);
    List<Role> roles = repositoryAll.getContent().stream().flatMap(
        rolePathPO -> findById(rolePathPO.getId().getDescendantId()).stream())
      .collect(Collectors.toList());
    return new PageImpl<>(roles, pageRequest, repositoryAll.getTotalElements());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deletePath(Long ancestorId, Long descendantId) {
    if (rolePathRepository.existsDescendantRoles(descendantId)) {
      throw new MuMuException(ResponseCode.DESCENDANT_ROLE_HAS_DESCENDANT_ROLE);
    }
    rolePathRepository.deleteById(new RolePathPOId(ancestorId, descendantId, 1L));
    rolePathRepository.deleteUnreachableData();
    roleCacheRepository.deleteById(ancestorId);
    roleCacheRepository.deleteById(descendantId);
  }

  @Override
  public Optional<Role> findById(Long id) {
    return Optional.ofNullable(id).flatMap(roleCacheRepository::findById).flatMap(
      roleConvertor::toEntity).or(() -> roleRepository.findById(id)
      .flatMap(roleConvertor::toEntity).map(entity -> {
        roleConvertor.toRoleCacheablePO(entity).ifPresent(roleCacheRepository::save);
        return entity;
      }));
  }

  @Override
  public Optional<Role> findByCode(String code) {
    return Optional.ofNullable(code).flatMap(roleCacheRepository::findByCode).flatMap(
      roleConvertor::toEntity).or(() -> roleRepository.findByCode(code)
      .flatMap(roleConvertor::toEntity).map(entity -> {
        roleConvertor.toRoleCacheablePO(entity).ifPresent(roleCacheRepository::save);
        return entity;
      }));
  }
}
