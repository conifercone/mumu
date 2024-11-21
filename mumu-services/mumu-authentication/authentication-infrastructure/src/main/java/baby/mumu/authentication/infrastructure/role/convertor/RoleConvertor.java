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
package baby.mumu.authentication.infrastructure.role.convertor;

import baby.mumu.authentication.client.api.grpc.RoleFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.RoleFindAllGrpcCo;
import baby.mumu.authentication.client.dto.RoleAddCmd;
import baby.mumu.authentication.client.dto.RoleArchivedFindAllCmd;
import baby.mumu.authentication.client.dto.RoleArchivedFindAllSliceCmd;
import baby.mumu.authentication.client.dto.RoleFindAllCmd;
import baby.mumu.authentication.client.dto.RoleFindAllSliceCmd;
import baby.mumu.authentication.client.dto.RoleUpdateCmd;
import baby.mumu.authentication.client.dto.co.RoleArchivedFindAllCo;
import baby.mumu.authentication.client.dto.co.RoleArchivedFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.RoleFindAllCo;
import baby.mumu.authentication.client.dto.co.RoleFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.RoleFindByIdCo;
import baby.mumu.authentication.client.dto.co.RoleFindDirectCo;
import baby.mumu.authentication.client.dto.co.RoleFindRootCo;
import baby.mumu.authentication.domain.permission.Permission;
import baby.mumu.authentication.domain.role.Role;
import baby.mumu.authentication.infrastructure.permission.convertor.PermissionConvertor;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.PermissionRepository;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.redis.PermissionRedisRepository;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.redis.dataobject.PermissionRedisDo;
import baby.mumu.authentication.infrastructure.relations.database.PermissionPathsDo;
import baby.mumu.authentication.infrastructure.relations.database.PermissionPathsDoId;
import baby.mumu.authentication.infrastructure.relations.database.PermissionPathsRepository;
import baby.mumu.authentication.infrastructure.relations.database.RolePathsRepository;
import baby.mumu.authentication.infrastructure.relations.database.RolePermissionDo;
import baby.mumu.authentication.infrastructure.relations.database.RolePermissionDoId;
import baby.mumu.authentication.infrastructure.relations.database.RolePermissionRepository;
import baby.mumu.authentication.infrastructure.relations.redis.RolePermissionRedisDo;
import baby.mumu.authentication.infrastructure.relations.redis.RolePermissionRedisRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.RoleArchivedRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.RoleRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleArchivedDo;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleDo;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.redis.dataobject.RoleRedisDo;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.translation.SimpleTextTranslation;
import baby.mumu.unique.client.api.PrimaryKeyGrpcService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 角色信息转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
public class RoleConvertor {

  private final PermissionConvertor permissionConvertor;
  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;
  private final PrimaryKeyGrpcService primaryKeyGrpcService;
  private final SimpleTextTranslation simpleTextTranslation;
  private final RoleArchivedRepository roleArchivedRepository;
  private final RolePermissionRepository rolePermissionRepository;
  private final PermissionRedisRepository permissionRedisRepository;
  private final RolePermissionRedisRepository rolePermissionRedisRepository;
  private final PermissionPathsRepository permissionPathsRepository;
  private final RolePathsRepository rolePathsRepository;

  @Autowired
  public RoleConvertor(PermissionConvertor permissionConvertor, RoleRepository roleRepository,
    PermissionRepository permissionRepository, PrimaryKeyGrpcService primaryKeyGrpcService,
    ObjectProvider<SimpleTextTranslation> simpleTextTranslation,
    RoleArchivedRepository roleArchivedRepository,
    RolePermissionRepository rolePermissionRepository,
    PermissionRedisRepository permissionRedisRepository,
    RolePermissionRedisRepository rolePermissionRedisRepository,
    PermissionPathsRepository permissionPathsRepository, RolePathsRepository rolePathsRepository) {
    this.permissionConvertor = permissionConvertor;
    this.roleRepository = roleRepository;
    this.permissionRepository = permissionRepository;
    this.primaryKeyGrpcService = primaryKeyGrpcService;
    this.simpleTextTranslation = simpleTextTranslation.getIfAvailable();
    this.roleArchivedRepository = roleArchivedRepository;
    this.rolePermissionRepository = rolePermissionRepository;
    this.permissionRedisRepository = permissionRedisRepository;
    this.rolePermissionRedisRepository = rolePermissionRedisRepository;
    this.permissionPathsRepository = permissionPathsRepository;
    this.rolePathsRepository = rolePathsRepository;
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Role> toEntity(RoleDo roleDo) {
    //noinspection DuplicatedCode
    return Optional.ofNullable(roleDo).map(roleDataObject -> {
      Role role = RoleMapper.INSTANCE.toEntity(roleDataObject);
      setAuthorities(role, getPermissionIds(role));
      return role;
    }).flatMap(this::hasDescendant);
  }

  private Optional<Role> hasDescendant(Role role) {
    return Optional.ofNullable(role).map(roleNotNull -> {
      roleNotNull.setHasDescendant(
        rolePathsRepository.existsDescendantRoles(role.getId()));
      return roleNotNull;
    });
  }

  private @NotNull List<Long> getPermissionIds(@NotNull Role role) {
    return rolePermissionRedisRepository.findById(role.getId())
      .map(RolePermissionRedisDo::getPermissionIds).orElseGet(() -> {
        List<Long> permissionIds = rolePermissionRepository.findByRoleId(role.getId()).stream()
          .map(RolePermissionDo::getId)
          .map(RolePermissionDoId::getPermissionId).distinct().collect(Collectors.toList());
        rolePermissionRedisRepository.save(new RolePermissionRedisDo(role.getId(), permissionIds));
        return permissionIds;
      });
  }

  private void setAuthorities(Role role, List<Long> permissionIds) {
    Optional.ofNullable(role).ifPresent(roleDataObject -> {
      ArrayList<Permission> authorities = getAuthorities(
        Optional.ofNullable(permissionIds).map(
            permissionIdsNotNull -> permissionIdsNotNull.stream().distinct()
              .collect(Collectors.toList()))
          .orElse(new ArrayList<>()));
      roleDataObject.setPermissions(authorities);
      List<Long> ancestorIds = authorities.stream().filter(Permission::isHasDescendant)
        .map(Permission::getId)
        .collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(ancestorIds)) {
        roleDataObject.setDescendantPermissions(
          getAuthorities(permissionPathsRepository.findByAncestorIdIn(
            ancestorIds).stream().map(PermissionPathsDo::getId).map(
            PermissionPathsDoId::getDescendantId).distinct().collect(Collectors.toList())));
      }
    });
  }

  private @NotNull ArrayList<Permission> getAuthorities(List<Long> permissionIds) {
    // 查询缓存中存在的数据
    List<PermissionRedisDo> permissionRedisDos = permissionRedisRepository.findAllById(
      permissionIds);
    // 缓存中存在的权限ID
    List<Long> cachedCollectionOfPermissionIDs = permissionRedisDos.stream()
      .map(PermissionRedisDo::getId)
      .collect(Collectors.toList());
    // 已缓存的权限
    List<Permission> cachedCollectionOfPermission = permissionRedisDos.stream()
      .flatMap(permissionRedisDo -> permissionConvertor.toEntity(permissionRedisDo).stream())
      .collect(
        Collectors.toList());
    // 未缓存的权限
    List<Permission> uncachedCollectionOfPermission = Optional.of(
        CollectionUtils.subtract(permissionIds, cachedCollectionOfPermissionIDs))
      .filter(CollectionUtils::isNotEmpty).map(
        uncachedCollectionOfPermissionId -> permissionRepository.findAllById(
            uncachedCollectionOfPermissionId)
          .stream()
          .flatMap(permissionDo -> permissionConvertor.toEntity(permissionDo).stream())
          .collect(
            Collectors.toList())).orElse(new ArrayList<>());
    // 未缓存的权限放入缓存
    if (CollectionUtils.isNotEmpty(uncachedCollectionOfPermission)) {
      permissionRedisRepository.saveAll(uncachedCollectionOfPermission.stream()
        .flatMap(permission -> permissionConvertor.toPermissionRedisDo(permission).stream())
        .collect(
          Collectors.toList()));
    }
    // 合并已缓存和未缓存的权限
    return new ArrayList<>(
      CollectionUtils.union(cachedCollectionOfPermission, uncachedCollectionOfPermission));
  }

  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<Role> toEntity(RoleArchivedDo roleArchivedDo) {
    //noinspection DuplicatedCode
    return Optional.ofNullable(roleArchivedDo).map(roleArchivedDataObject -> {
      Role role = RoleMapper.INSTANCE.toEntity(roleArchivedDataObject);
      setAuthorities(role, getPermissionIds(role));
      return role;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<RoleDo> toDataObject(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toDataObject);
  }


  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Role> toEntity(RoleAddCmd roleAddCmd) {
    return Optional.ofNullable(roleAddCmd).map(roleAddCmdNotNull -> {
      Role role = RoleMapper.INSTANCE.toEntity(roleAddCmdNotNull);
      if (role.getId() == null) {
        role.setId(primaryKeyGrpcService.snowflake());
        roleAddCmdNotNull.setId(role.getId());
      }
      Optional.ofNullable(roleAddCmdNotNull.getPermissionIds())
        .filter(CollectionUtils::isNotEmpty)
        .ifPresent(permissionIds -> setAuthorities(role, permissionIds));
      return role;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Role> toEntity(RoleUpdateCmd roleUpdateCmd) {
    return Optional.ofNullable(roleUpdateCmd).flatMap(roleUpdateCmdNotNull -> {
      Optional.ofNullable(roleUpdateCmdNotNull.getId())
        .orElseThrow(() -> new MuMuException(ResponseCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
      Optional<RoleDo> roleDoOptional = roleRepository.findById(roleUpdateCmdNotNull.getId());
      return roleDoOptional.flatMap(roleDo -> toEntity(roleDo).map(roleDomain -> {
        String codeBeforeUpdated = roleDomain.getCode();
        RoleMapper.INSTANCE.toEntity(roleUpdateCmdNotNull, roleDomain);
        String codeAfterUpdated = roleDomain.getCode();
        if (StringUtils.isNotBlank(codeAfterUpdated) && !codeAfterUpdated.equals(codeBeforeUpdated)
          && (roleRepository.existsByCode(codeAfterUpdated)
          || roleArchivedRepository.existsByCode(codeAfterUpdated))) {
          throw new MuMuException(ResponseCode.ROLE_CODE_ALREADY_EXISTS);
        }
        Optional.ofNullable(roleUpdateCmdNotNull.getPermissionIds())
          .ifPresent(authorities -> setAuthorities(roleDomain, authorities));
        return roleDomain;
      }));
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Role> toEntity(RoleFindAllCmd roleFindAllCmd) {
    return Optional.ofNullable(roleFindAllCmd).map(RoleMapper.INSTANCE::toEntity).map(role -> {
      if (CollectionUtils.isNotEmpty(roleFindAllCmd.getPermissionIds())) {
        setAuthorities(role, roleFindAllCmd.getPermissionIds());
      }
      return role;
    });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Role> toEntity(RoleFindAllSliceCmd roleFindAllSliceCmd) {
    return Optional.ofNullable(roleFindAllSliceCmd).map(RoleMapper.INSTANCE::toEntity)
      .map(role -> {
        if (CollectionUtils.isNotEmpty(roleFindAllSliceCmd.getPermissionIds())) {
          setAuthorities(role, roleFindAllSliceCmd.getPermissionIds());
        }
        return role;
      });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<RoleFindAllCo> toFindAllCo(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toFindAllCo).map(roleFindAllCo -> {
      Optional.ofNullable(simpleTextTranslation).flatMap(
          simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
            roleFindAllCo.getName()))
        .ifPresent(roleFindAllCo::setName);
      return roleFindAllCo;
    });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<RoleFindAllSliceCo> toFindAllSliceCo(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toFindAllSliceCo)
      .map(roleFindAllSliceCo -> {
        Optional.ofNullable(simpleTextTranslation).flatMap(
            simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
              roleFindAllSliceCo.getName()))
          .ifPresent(roleFindAllSliceCo::setName);
        return roleFindAllSliceCo;
      });
  }


  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Role> toEntity(RoleArchivedFindAllCmd roleArchivedFindAllCmd) {
    return Optional.ofNullable(roleArchivedFindAllCmd).map(RoleMapper.INSTANCE::toEntity)
      .map(role -> {
        if (CollectionUtils.isNotEmpty(roleArchivedFindAllCmd.getPermissionIds())) {
          setAuthorities(role, roleArchivedFindAllCmd.getPermissionIds());
        }
        return role;
      });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Role> toEntity(RoleArchivedFindAllSliceCmd roleArchivedFindAllSliceCmd) {
    return Optional.ofNullable(roleArchivedFindAllSliceCmd).map(RoleMapper.INSTANCE::toEntity)
      .map(role -> {
        if (CollectionUtils.isNotEmpty(roleArchivedFindAllSliceCmd.getPermissionIds())) {
          setAuthorities(role, roleArchivedFindAllSliceCmd.getPermissionIds());
        }
        return role;
      });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Role> toEntity(RoleRedisDo roleRedisDo) {
    return Optional.ofNullable(roleRedisDo).map(RoleMapper.INSTANCE::toEntity)
      .map(role -> {
        setAuthorities(role, getPermissionIds(role));
        return role;
      });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<RoleArchivedFindAllCo> toArchivedFindAllCo(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toArchivedFindAllCo)
      .map(roleArchivedFindAllCo -> {
        Optional.ofNullable(simpleTextTranslation).flatMap(
            simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
              roleArchivedFindAllCo.getName()))
          .ifPresent(roleArchivedFindAllCo::setName);
        return roleArchivedFindAllCo;
      });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<RoleArchivedFindAllSliceCo> toArchivedFindAllSliceCo(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toArchivedFindAllSliceCo)
      .map(roleArchivedFindAllSliceCo -> {
        Optional.ofNullable(simpleTextTranslation).flatMap(
            simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
              roleArchivedFindAllSliceCo.getName()))
          .ifPresent(roleArchivedFindAllSliceCo::setName);
        return roleArchivedFindAllSliceCo;
      });
  }


  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<RoleArchivedDo> toArchivedDo(RoleDo roleDo) {
    return Optional.ofNullable(roleDo).map(RoleMapper.INSTANCE::toArchivedDo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<RoleRedisDo> toRoleRedisDo(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toRoleRedisDo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<RoleArchivedDo> toArchivedDo(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toArchivedDo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<RoleDo> toDataObject(RoleArchivedDo roleArchivedDo) {
    return Optional.ofNullable(roleArchivedDo).map(RoleMapper.INSTANCE::toDataObject);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.1.0")
  public List<RolePermissionDo> toRolePermissionDos(Role role) {
    return Optional.ofNullable(role).flatMap(roleNonNull -> Optional.ofNullable(
      roleNonNull.getPermissions())).map(authorities -> authorities.stream().map(permission -> {
      RolePermissionDo rolePermissionDo = new RolePermissionDo();
      rolePermissionDo.setId(RolePermissionDoId.builder().roleId(role.getId()).permissionId(
        permission.getId()).build());
      roleRepository.findById(role.getId()).ifPresent(rolePermissionDo::setRole);
      permissionRepository.findById(permission.getId()).ifPresent(rolePermissionDo::setPermission);
      return rolePermissionDo;
    }).toList()).orElse(new ArrayList<>());
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<RoleFindAllCmd> toRoleFindAllCmd(RoleFindAllGrpcCmd roleFindAllGrpcCmd) {
    return Optional.ofNullable(roleFindAllGrpcCmd).map(RoleMapper.INSTANCE::toRoleFindAllCmd)
      .map(roleFindAllCmd -> {
        if (roleFindAllCmd.getCurrent() == null) {
          roleFindAllCmd.setCurrent(1);
        }
        if (roleFindAllCmd.getPageSize() == null) {
          roleFindAllCmd.setPageSize(10);
        }
        return roleFindAllCmd;
      });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<RoleFindAllGrpcCo> toRoleFindAllGrpcCo(RoleFindAllCo roleFindAllCo) {
    return Optional.ofNullable(roleFindAllCo).map(RoleMapper.INSTANCE::toRoleFindAllGrpcCo)
      .map(roleFindAllGrpcCo ->
        roleFindAllGrpcCo.toBuilder().addAllPermissions(
          Optional.ofNullable(roleFindAllCo.getPermissions()).map(
            permissions -> permissions.stream()
              .map(RoleMapper.INSTANCE::toRoleFindAllPermissionGrpcCo)
              .collect(Collectors.toList())).orElse(new ArrayList<>())).build()
      );
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.4.0")
  public Optional<RoleFindByIdCo> toRoleFindByIdCo(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toRoleFindByIdCo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.4.0")
  public Optional<RoleFindRootCo> toRoleFindRootCo(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toRoleFindRootCo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.4.0")
  public Optional<RoleFindDirectCo> toRoleFindDirectCo(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toRoleFindDirectCo);
  }
}
