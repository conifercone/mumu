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
package baby.mumu.authentication.infrastructure.permission.gatewayimpl;

import baby.mumu.authentication.domain.permission.Permission;
import baby.mumu.authentication.domain.permission.gateway.PermissionGateway;
import baby.mumu.authentication.domain.role.Role;
import baby.mumu.authentication.domain.role.gateway.RoleGateway;
import baby.mumu.authentication.infrastructure.permission.convertor.PermissionConvertor;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.PermissionArchivedRepository;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.PermissionRepository;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.po.PermissionArchivedPO;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.po.PermissionPO;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.redis.PermissionRedisRepository;
import baby.mumu.authentication.infrastructure.relations.database.PermissionPathsPO;
import baby.mumu.authentication.infrastructure.relations.database.PermissionPathsPOId;
import baby.mumu.authentication.infrastructure.relations.database.PermissionPathsRepository;
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
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
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
  private final PermissionRedisRepository permissionRedisRepository;
  private final PermissionPathsRepository permissionPathsRepository;

  @Autowired
  public PermissionGatewayImpl(PermissionRepository permissionRepository,
    RoleGateway roleGateway,
    PermissionConvertor permissionConvertor,
    PermissionArchivedRepository permissionArchivedRepository, JobScheduler jobScheduler,
    ExtensionProperties extensionProperties, PermissionRedisRepository permissionRedisRepository,
    PermissionPathsRepository permissionPathsRepository) {
    this.permissionRepository = permissionRepository;
    this.roleGateway = roleGateway;
    this.permissionConvertor = permissionConvertor;
    this.permissionArchivedRepository = permissionArchivedRepository;
    this.jobScheduler = jobScheduler;
    this.extensionProperties = extensionProperties;
    this.permissionRedisRepository = permissionRedisRepository;
    this.permissionPathsRepository = permissionPathsRepository;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void add(Permission permission) {
    Optional.ofNullable(permission).flatMap(permissionConvertor::toPO)
      .filter(permissionPO -> !permissionRepository.existsByIdOrCode(permissionPO.getId(),
        permissionPO.getCode()) && !permissionArchivedRepository.existsByIdOrCode(
        permissionPO.getId(),
        permissionPO.getCode()))
      .ifPresentOrElse(permissionPO -> {
        permissionRepository.persist(permissionPO);
        permissionPathsRepository.persist(
          new PermissionPathsPO(
            new PermissionPathsPOId(permissionPO.getId(), permissionPO.getId(), 0L),
            permissionPO, permissionPO));
        permissionRedisRepository.deleteById(permissionPO.getId());
      }, () -> {
        throw new MuMuException(ResponseCode.PERMISSION_CODE_OR_ID_ALREADY_EXISTS);
      });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  @DangerousOperation("根据ID删除ID为%0的权限数据")
  public void deleteById(Long id) {
    List<Role> roles = roleGateway.findAllContainPermission(id);
    if (CollectionUtils.isNotEmpty(roles)) {
      throw new MuMuException(ResponseCode.PERMISSION_IS_IN_USE_AND_CANNOT_BE_REMOVED,
        roles.stream().map(Role::getCode).toList());
    }
    Optional.ofNullable(id).ifPresent(permissionId -> {
      permissionRepository.deleteById(permissionId);
      permissionPathsRepository.deleteAllPathsByPermissionId(permissionId);
      permissionArchivedRepository.deleteById(permissionId);
      permissionRedisRepository.deleteById(permissionId);
    });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateById(Permission permission) {
    Optional.ofNullable(permission).flatMap(permissionConvertor::toPO)
      .ifPresent(dataObject -> {
        permissionRepository.merge(dataObject);
        permissionRedisRepository.deleteById(dataObject.getId());
      });
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.0")
  public Page<Permission> findAll(Permission permission, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<PermissionPO> repositoryAll = permissionRepository.findAllPage(
      permissionConvertor.toPO(permission).orElseGet(PermissionPO::new),
      pageRequest);
    List<Permission> authorities = repositoryAll.getContent().stream()
      .map(permissionConvertor::toEntity)
      .filter(Optional::isPresent).map(Optional::get)
      .toList();
    return new PageImpl<>(authorities, pageRequest, repositoryAll.getTotalElements());
  }

  @Override
  @API(status = Status.STABLE, since = "2.2.0")
  public Slice<Permission> findAllSlice(Permission permission, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Slice<PermissionPO> permissionPOSlice = permissionRepository.findAllSlice(
      permissionConvertor.toPO(permission).orElseGet(PermissionPO::new), pageRequest);
    return new SliceImpl<>(permissionPOSlice.getContent().stream()
      .flatMap(permissionPO -> permissionConvertor.toEntity(permissionPO).stream())
      .toList(), pageRequest, permissionPOSlice.hasNext());
  }

  @Override
  @API(status = Status.STABLE, since = "2.2.0")
  public Slice<Permission> findArchivedAllSlice(Permission permission, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Slice<PermissionArchivedPO> permissionArchivedPOS = permissionArchivedRepository.findAllSlice(
      permissionConvertor.toArchivedPO(permission).orElseGet(PermissionArchivedPO::new),
      pageRequest);
    return new SliceImpl<>(permissionArchivedPOS.getContent().stream()
      .flatMap(permissionArchivedPO -> permissionConvertor.toEntity(permissionArchivedPO).stream())
      .toList(), pageRequest, permissionArchivedPOS.hasNext());
  }

  @Override
  @API(status = Status.STABLE, since = "2.0.0")
  public Page<Permission> findArchivedAll(Permission permission, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<PermissionArchivedPO> repositoryAll = permissionArchivedRepository.findAllPage(
      permissionConvertor.toArchivedPO(permission).orElseGet(PermissionArchivedPO::new),
      pageRequest);
    List<Permission> authorities = repositoryAll.getContent().stream()
      .map(permissionConvertor::toEntity)
      .filter(Optional::isPresent).map(Optional::get)
      .toList();
    return new PageImpl<>(authorities, pageRequest, repositoryAll.getTotalElements());
  }

  @Override
  public Optional<Permission> findById(Long id) {
    return Optional.ofNullable(id).flatMap(permissionRedisRepository::findById).flatMap(
      permissionConvertor::toEntity).or(() -> {
      Optional<Permission> permission = permissionRepository.findById(id)
        .flatMap(permissionConvertor::toEntity);
      permission.flatMap(permissionConvertor::toPermissionRedisPO)
        .ifPresent(permissionRedisRepository::save);
      return permission;
    });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @DangerousOperation("根据ID归档ID为%0的权限")
  public void archiveById(Long id) {
    List<Role> authorities = roleGateway.findAllContainPermission(id);
    if (CollectionUtils.isNotEmpty(authorities)) {
      throw new MuMuException(ResponseCode.PERMISSION_IS_IN_USE_AND_CANNOT_BE_ARCHIVE,
        authorities.stream().map(Role::getCode).toList());
    }
    //noinspection DuplicatedCode
    Optional.ofNullable(id).flatMap(permissionRepository::findById)
      .flatMap(permissionConvertor::toArchivedPO).ifPresent(permissionArchivedPO -> {
        permissionArchivedPO.setArchived(true);
        permissionArchivedRepository.persist(permissionArchivedPO);
        permissionRepository.deleteById(permissionArchivedPO.getId());
        permissionRedisRepository.deleteById(permissionArchivedPO.getId());
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
        permissionPathsRepository.deleteAllPathsByPermissionId(permissionId);
        permissionRedisRepository.deleteById(permissionId);
      });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void recoverFromArchiveById(Long id) {
    Optional.ofNullable(id).flatMap(permissionArchivedRepository::findById)
      .flatMap(permissionConvertor::toPO).ifPresent(permissionPO -> {
        permissionPO.setArchived(false);
        permissionArchivedRepository.deleteById(permissionPO.getId());
        permissionRepository.persist(permissionPO);
        permissionRedisRepository.deleteById(permissionPO.getId());
      });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void addAncestor(Long descendantId, Long ancestorId) {
    Optional<PermissionPO> ancestorPermissionPOOptional = permissionRepository.findById(ancestorId);
    Optional<PermissionPO> descendantPermissionPOOptional = permissionRepository.findById(
      descendantId);
    if (ancestorPermissionPOOptional.isPresent() && descendantPermissionPOOptional.isPresent()) {
      // 后代权限
      PermissionPO descendantPermissionPO = descendantPermissionPOOptional.get();
      // 为节点添加从所有祖先到自身的路径
      List<PermissionPathsPO> ancestorAuthorities = permissionPathsRepository.findByDescendantId(
        ancestorId);
      // 成环检测
      Set<Long> ancestorIds = ancestorAuthorities.stream()
        .map(permissionPathsPO -> permissionPathsPO.getId().getAncestorId())
        .collect(
          Collectors.toSet());
      if (ancestorIds.contains(descendantId)) {
        throw new MuMuException(ResponseCode.PERMISSION_CYCLE);
      }
      if (permissionPathsRepository.existsById(
        new PermissionPathsPOId(ancestorId, descendantId, 1L))) {
        throw new MuMuException(ResponseCode.PERMISSION_PATH_ALREADY_EXISTS);
      }
      List<PermissionPathsPO> permissionPathsPOS = ancestorAuthorities.stream()
        .map(ancestorPermission -> new PermissionPathsPO(
          new PermissionPathsPOId(ancestorPermission.getId().getAncestorId(), descendantId,
            ancestorPermission.getId().getDepth() + 1),
          ancestorPermission.getAncestor(), descendantPermissionPO))
        .filter(
          permissionPathsPO -> !permissionPathsRepository.existsById(permissionPathsPO.getId())
        )
        .collect(
          Collectors.toList());
      permissionPathsRepository.persistAll(permissionPathsPOS);
      permissionRedisRepository.deleteById(ancestorId);
      permissionRedisRepository.deleteById(descendantId);
    }
  }

  @Override
  public Page<Permission> findRootPermissions(int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<PermissionPathsPO> repositoryAll = permissionPathsRepository.findRootPermissions(
      pageRequest);
    List<Permission> authorities = repositoryAll.getContent().stream().flatMap(
        permissionPathsPO -> findById(permissionPathsPO.getId().getAncestorId()).stream())
      .collect(Collectors.toList());
    return new PageImpl<>(authorities, pageRequest, repositoryAll.getTotalElements());
  }

  @Override
  public Page<Permission> findDirectPermissions(Long ancestorId, int current, int pageSize) {
    PageRequest pageRequest = PageRequest.of(current - 1, pageSize);
    Page<PermissionPathsPO> repositoryAll = permissionPathsRepository.findDirectPermissions(
      ancestorId,
      pageRequest);
    List<Permission> authorities = repositoryAll.getContent().stream().flatMap(
        permissionPathsPO -> findById(permissionPathsPO.getId().getDescendantId()).stream())
      .collect(Collectors.toList());
    return new PageImpl<>(authorities, pageRequest, repositoryAll.getTotalElements());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deletePath(Long ancestorId, Long descendantId) {
    if (permissionPathsRepository.existsDescendantPermissions(descendantId)) {
      throw new MuMuException(ResponseCode.DESCENDANT_PERMISSION_HAS_DESCENDANT_PERMISSION);
    }
    permissionPathsRepository.deleteById(new PermissionPathsPOId(ancestorId, descendantId, 1L));
    permissionPathsRepository.deleteUnreachableData();
    permissionRedisRepository.deleteById(ancestorId);
    permissionRedisRepository.deleteById(descendantId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  @API(status = Status.STABLE, since = "2.4.0")
  @DangerousOperation("根据Code删除Code为%0的权限数据")
  public void deleteByCode(String code) {
    Optional.ofNullable(code)
      .flatMap(permissionRepository::findByCode)
      .map(PermissionPO::getId)
      .ifPresent(this::deleteById);
  }

  @Override
  @Transactional(readOnly = true)
  public Stream<Permission> findAll() {
    return permissionRepository.findAll()
      .flatMap(
        permissionPO -> permissionConvertor.toEntityDoNotJudgeHasDescendant(permissionPO).stream());
  }

  @Override
  public Optional<Permission> findByCode(String code) {
    return Optional.ofNullable(code).flatMap(permissionRedisRepository::findByCode).flatMap(
      permissionConvertor::toEntity).or(() -> permissionRepository.findByCode(code)
      .flatMap(permissionConvertor::toEntity)
      .map(entity -> {
        permissionConvertor.toPermissionRedisPO(entity)
          .ifPresent(permissionRedisRepository::save);
        return entity;
      }));
  }
}
