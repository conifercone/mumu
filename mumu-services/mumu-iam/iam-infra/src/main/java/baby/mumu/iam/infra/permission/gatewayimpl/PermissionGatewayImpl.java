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

package baby.mumu.iam.infra.permission.gatewayimpl;

import baby.mumu.basis.annotations.DangerousOperation;
import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.GlobalProperties;
import baby.mumu.iam.domain.permission.Permission;
import baby.mumu.iam.domain.permission.gateway.PermissionGateway;
import baby.mumu.iam.domain.role.Role;
import baby.mumu.iam.domain.role.gateway.RoleGateway;
import baby.mumu.iam.infra.permission.convertor.PermissionConvertor;
import baby.mumu.iam.infra.permission.gatewayimpl.cache.PermissionCacheRepository;
import baby.mumu.iam.infra.permission.gatewayimpl.database.PermissionArchivedRepository;
import baby.mumu.iam.infra.permission.gatewayimpl.database.PermissionRepository;
import baby.mumu.iam.infra.permission.gatewayimpl.database.po.PermissionArchivedPO;
import baby.mumu.iam.infra.permission.gatewayimpl.database.po.PermissionPO;
import baby.mumu.iam.infra.relations.cache.RolePermissionCacheRepository;
import baby.mumu.iam.infra.relations.database.PermissionPathPO;
import baby.mumu.iam.infra.relations.database.PermissionPathPOId;
import baby.mumu.iam.infra.relations.database.PermissionPathRepository;
import io.micrometer.observation.annotation.Observed;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 权限领域网关实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "PermissionGatewayImpl")
public class PermissionGatewayImpl implements PermissionGateway {


  private final PermissionRepository permissionRepository;
  private final RoleGateway roleGateway;
  private final PermissionConvertor permissionConvertor;
  private final PermissionArchivedRepository permissionArchivedRepository;
  private final JobScheduler jobScheduler;
  private final ExtensionProperties extensionProperties;
  private final PermissionCacheRepository permissionCacheRepository;
  private final PermissionPathRepository permissionPathRepository;
  private final RolePermissionCacheRepository rolePermissionCacheRepository;

  @Autowired
  public PermissionGatewayImpl(PermissionRepository permissionRepository,
    RoleGateway roleGateway,
    PermissionConvertor permissionConvertor,
    PermissionArchivedRepository permissionArchivedRepository, JobScheduler jobScheduler,
    ExtensionProperties extensionProperties, PermissionCacheRepository permissionCacheRepository,
    PermissionPathRepository permissionPathRepository,
    RolePermissionCacheRepository rolePermissionCacheRepository) {
    this.permissionRepository = permissionRepository;
    this.roleGateway = roleGateway;
    this.permissionConvertor = permissionConvertor;
    this.permissionArchivedRepository = permissionArchivedRepository;
    this.jobScheduler = jobScheduler;
    this.extensionProperties = extensionProperties;
    this.permissionCacheRepository = permissionCacheRepository;
    this.permissionPathRepository = permissionPathRepository;
    this.rolePermissionCacheRepository = rolePermissionCacheRepository;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public Long add(Permission permission) {
    PermissionPO permissionPO = permissionConvertor.toPermissionPO(permission)
      .orElseThrow(() -> new ApplicationException(ResponseCode.INVALID_PERMISSION_FORMAT));
    if (permissionRepository.existsByIdOrCode(permissionPO.getId(),
      permissionPO.getCode()) || permissionArchivedRepository.existsByIdOrCode(
      permissionPO.getId(),
      permissionPO.getCode())) {
      throw new ApplicationException(ResponseCode.PERMISSION_CODE_OR_ID_ALREADY_EXISTS);
    }
    PermissionPO persisted = permissionRepository.persist(permissionPO);
    permissionPathRepository.persist(
      new PermissionPathPO(
        new PermissionPathPOId(persisted.getId(), persisted.getId(), 0L),
        permissionPO, permissionPO));
    permissionCacheRepository.deleteById(persisted.getId());
    return persisted.getId();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  @DangerousOperation("根据ID删除ID为%0的权限数据")
  public void deleteById(Long id) {
    Optional.ofNullable(id).ifPresent(permissionId -> {
      if (permissionRepository.existsById(permissionId)) {
        List<Role> roles = roleGateway.findAllContainPermission(permissionId);
        if (CollectionUtils.isNotEmpty(roles)) {
          throw new ApplicationException(ResponseCode.PERMISSION_IS_IN_USE_AND_CANNOT_BE_REMOVED,
            roles.stream().map(Role::getCode).toList());
        }
        permissionRepository.deleteById(permissionId);
        permissionPathRepository.deleteAllPathsByPermissionId(permissionId);
        permissionArchivedRepository.deleteById(permissionId);
        permissionCacheRepository.deleteById(permissionId);
        rolePermissionCacheRepository.deleteByPermissionIdsContaining(permissionId);
      } else {
        throw new ApplicationException(ResponseCode.PERMISSION_DOES_NOT_EXIST);
      }
    });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Permission> updateById(Permission permission) {
    PermissionPO permissionPO = permissionConvertor.toPermissionPO(permission)
      .orElseThrow(() -> new ApplicationException(ResponseCode.INVALID_PERMISSION_FORMAT));
    if (permission.getId() == null) {
      return Optional.empty();
    }
    // 判断权限是否存在
    if (permissionRepository.findById(permission.getId()).isEmpty()) {
      throw new ApplicationException(ResponseCode.PERMISSION_DOES_NOT_EXIST);
    }

    PermissionPO merged = permissionRepository.merge(permissionPO);
    permissionCacheRepository.deleteById(permissionPO.getId());
    return permissionConvertor.toEntity(merged);
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  public Page<Permission> findAll(Permission permission, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<PermissionPO> repositoryAll = permissionRepository.findAllPage(
      permissionConvertor.toPermissionPO(permission).orElseGet(PermissionPO::new),
      pageRequest);
    List<Permission> permissions = permissionConvertor.toEntities(repositoryAll.getContent());
    return new PageImpl<>(permissions, pageRequest, repositoryAll.getTotalElements());
  }

  @Override
  @API(status = Status.STABLE, since = "2.2.0")
  public Slice<Permission> findAllSlice(Permission permission, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Slice<PermissionPO> permissionPOSlice = permissionRepository.findAllSlice(
      permissionConvertor.toPermissionPO(permission).orElseGet(PermissionPO::new), pageRequest);
    List<Permission> permissions = permissionConvertor.toEntities(permissionPOSlice.getContent());
    return new SliceImpl<>(permissions, pageRequest, permissionPOSlice.hasNext());
  }

  @Override
  @API(status = Status.STABLE, since = "2.2.0")
  public Slice<Permission> findArchivedAllSlice(Permission permission, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Slice<PermissionArchivedPO> permissionArchivedPOS = permissionArchivedRepository.findAllSlice(
      permissionConvertor.toPermissionArchivedPO(permission).orElseGet(PermissionArchivedPO::new),
      pageRequest);
    List<Permission> entitiesFromArchivedPO = permissionConvertor.toEntitiesFromArchivedPO(
      permissionArchivedPOS.getContent());
    return new SliceImpl<>(entitiesFromArchivedPO, pageRequest, permissionArchivedPOS.hasNext());
  }

  @Override
  @API(status = Status.STABLE, since = "2.0.0")
  public Page<Permission> findArchivedAll(Permission permission, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<PermissionArchivedPO> repositoryAll = permissionArchivedRepository.findAllPage(
      permissionConvertor.toPermissionArchivedPO(permission).orElseGet(PermissionArchivedPO::new),
      pageRequest);
    List<Permission> entitiesFromArchivedPO = permissionConvertor.toEntitiesFromArchivedPO(
      repositoryAll.getContent());
    return new PageImpl<>(entitiesFromArchivedPO, pageRequest, repositoryAll.getTotalElements());
  }

  @Override
  public Optional<Permission> findById(Long id) {
    return Optional.ofNullable(id).flatMap(permissionCacheRepository::findById).flatMap(
      permissionConvertor::toEntity).or(() ->
      permissionRepository.findById(id)
        .flatMap(permissionConvertor::toEntity)
        .map(permission -> {
          permissionConvertor.toPermissionCacheablePO(permission)
            .ifPresent(permissionCacheRepository::save);
          return permission;
        })
    );
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @DangerousOperation("根据ID归档ID为%0的权限")
  public void archiveById(Long id) {
    List<Role> authorities = roleGateway.findAllContainPermission(id);
    if (CollectionUtils.isNotEmpty(authorities)) {
      throw new ApplicationException(ResponseCode.PERMISSION_IS_IN_USE_AND_CANNOT_BE_ARCHIVE,
        authorities.stream().map(Role::getCode).toList());
    }
    // noinspection DuplicatedCode
    Optional.ofNullable(id).flatMap(permissionRepository::findById)
      .flatMap(permissionConvertor::toPermissionArchivedPO).ifPresent(permissionArchivedPO -> {
        permissionArchivedPO.setArchived(true);
        permissionArchivedRepository.persist(permissionArchivedPO);
        permissionRepository.deleteById(permissionArchivedPO.getId());
        permissionCacheRepository.deleteById(permissionArchivedPO.getId());
        GlobalProperties global = extensionProperties.getGlobal();
        jobScheduler.schedule(Instant.now()
            .plus(global.getArchiveDeletionPeriod(), global.getArchiveDeletionPeriodUnit()),
          () -> deleteArchivedDataJob(permissionArchivedPO.getId()));
      });
  }

  @Job(name = "删除ID为：%0 的权限归档数据")
  @DangerousOperation("根据ID删除ID为%0的权限归档数据定时任务")
  @Transactional(rollbackFor = Exception.class)
  public void deleteArchivedDataJob(Long id) {
    Optional.ofNullable(id)
      .filter(permissionId -> roleGateway.findAllContainPermission(permissionId).isEmpty())
      .ifPresent(permissionId -> {
        permissionArchivedRepository.deleteById(permissionId);
        permissionPathRepository.deleteAllPathsByPermissionId(permissionId);
        permissionCacheRepository.deleteById(permissionId);
      });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(Long id) {
    Optional.ofNullable(id).flatMap(permissionArchivedRepository::findById)
      .flatMap(permissionConvertor::toPermissionPO).ifPresent(permissionPO -> {
        permissionPO.setArchived(false);
        permissionArchivedRepository.deleteById(permissionPO.getId());
        permissionRepository.persist(permissionPO);
        permissionCacheRepository.deleteById(permissionPO.getId());
      });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void addDescendant(Long ancestorId, Long descendantId) {
    Optional<PermissionPO> ancestorPermissionPOOptional = permissionRepository.findById(ancestorId);
    Optional<PermissionPO> descendantPermissionPOOptional = permissionRepository.findById(
      descendantId);
    if (ancestorPermissionPOOptional.isPresent() && descendantPermissionPOOptional.isPresent()) {
      // 后代权限
      PermissionPO descendantPermissionPO = descendantPermissionPOOptional.get();
      // 为节点添加从所有祖先到自身的路径
      List<PermissionPathPO> ancestorAuthorities = permissionPathRepository.findByDescendantId(
        ancestorId);
      // 成环检测
      Set<Long> ancestorIds = ancestorAuthorities.stream()
        .map(permissionPathPO -> permissionPathPO.getId().getAncestorId())
        .collect(
          Collectors.toSet());
      if (ancestorIds.contains(descendantId)) {
        throw new ApplicationException(ResponseCode.PERMISSION_CYCLE);
      }
      if (permissionPathRepository.existsById(
        new PermissionPathPOId(ancestorId, descendantId, 1L))) {
        throw new ApplicationException(ResponseCode.PERMISSION_PATH_ALREADY_EXISTS);
      }
      List<PermissionPathPO> permissionPathPOS = ancestorAuthorities.stream()
        .map(ancestorPermission -> new PermissionPathPO(
          new PermissionPathPOId(ancestorPermission.getId().getAncestorId(), descendantId,
            ancestorPermission.getId().getDepth() + 1),
          ancestorPermission.getAncestor(), descendantPermissionPO))
        .filter(
          permissionPathPO -> !permissionPathRepository.existsById(permissionPathPO.getId())
        )
        .collect(
          Collectors.toList());
      permissionPathRepository.persistAll(permissionPathPOS);
      permissionCacheRepository.deleteById(ancestorId);
      permissionCacheRepository.deleteById(descendantId);
    }
  }

  @Override
  public Page<Permission> findRootPermissions(int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<PermissionPathPO> repositoryAll = permissionPathRepository.findRootPermissions(
      pageRequest);
    List<Permission> authorities = repositoryAll.getContent().stream().flatMap(
        permissionPathPO -> findById(permissionPathPO.getId().getAncestorId()).stream())
      .collect(Collectors.toList());
    return new PageImpl<>(authorities, pageRequest, repositoryAll.getTotalElements());
  }

  @Override
  public Page<Permission> findDirectPermissions(Long ancestorId, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<PermissionPathPO> repositoryAll = permissionPathRepository.findDirectPermissions(
      ancestorId,
      pageRequest);
    List<Permission> authorities = repositoryAll.getContent().stream().flatMap(
        permissionPathPO -> findById(permissionPathPO.getId().getDescendantId()).stream())
      .collect(Collectors.toList());
    return new PageImpl<>(authorities, pageRequest, repositoryAll.getTotalElements());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deletePath(Long ancestorId, Long descendantId) {
    if (permissionPathRepository.existsDescendantPermissions(descendantId)) {
      throw new ApplicationException(ResponseCode.DESCENDANT_PERMISSION_HAS_DESCENDANT_PERMISSION);
    }
    permissionPathRepository.deleteById(new PermissionPathPOId(ancestorId, descendantId, 1L));
    permissionPathRepository.deleteUnreachableData();
    permissionCacheRepository.deleteById(ancestorId);
    permissionCacheRepository.deleteById(descendantId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "2.4.0")
  @DangerousOperation("根据Code删除Code为%0的权限数据")
  public void deleteByCode(String code) {
    if (StringUtils.isNotBlank(code)) {
      PermissionPO permissionPO = permissionRepository.findByCode(code)
        .orElseThrow(() -> new ApplicationException(ResponseCode.PERMISSION_DOES_NOT_EXIST));
      this.deleteById(permissionPO.getId());
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Stream<Permission> findAll() {
    return permissionRepository.findAll()
      .flatMap(
        permissionPO -> permissionConvertor.toEntityDoNotJudgeHasDescendant(permissionPO).stream());
  }

  @Override
  @Transactional(readOnly = true)
  public Stream<Permission> findAllIncludePath() {
    return permissionRepository.findAll()
      .flatMap(
        permissionPO -> permissionConvertor.toEntity(permissionPO).stream());
  }

  @Override
  public Optional<Permission> findByCode(String code) {
    return Optional.ofNullable(code).flatMap(permissionCacheRepository::findByCode).flatMap(
      permissionConvertor::toEntity).or(() -> permissionRepository.findByCode(code)
      .flatMap(permissionConvertor::toEntity)
      .map(entity -> {
        permissionConvertor.toPermissionCacheablePO(entity)
          .ifPresent(permissionCacheRepository::save);
        return entity;
      }));
  }
}
