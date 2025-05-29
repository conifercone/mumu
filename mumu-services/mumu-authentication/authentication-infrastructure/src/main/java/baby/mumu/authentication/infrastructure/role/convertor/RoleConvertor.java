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
package baby.mumu.authentication.infrastructure.role.convertor;

import baby.mumu.authentication.client.api.grpc.RoleFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.RoleFindAllGrpcDTO;
import baby.mumu.authentication.client.api.grpc.RoleFindByIdGrpcDTO;
import baby.mumu.authentication.client.cmds.RoleAddCmd;
import baby.mumu.authentication.client.cmds.RoleArchivedFindAllCmd;
import baby.mumu.authentication.client.cmds.RoleArchivedFindAllSliceCmd;
import baby.mumu.authentication.client.cmds.RoleFindAllCmd;
import baby.mumu.authentication.client.cmds.RoleFindAllSliceCmd;
import baby.mumu.authentication.client.cmds.RoleUpdateCmd;
import baby.mumu.authentication.client.dto.RoleArchivedFindAllDTO;
import baby.mumu.authentication.client.dto.RoleArchivedFindAllSliceDTO;
import baby.mumu.authentication.client.dto.RoleFindAllDTO;
import baby.mumu.authentication.client.dto.RoleFindAllSliceDTO;
import baby.mumu.authentication.client.dto.RoleFindByCodeDTO;
import baby.mumu.authentication.client.dto.RoleFindByIdDTO;
import baby.mumu.authentication.client.dto.RoleFindDirectDTO;
import baby.mumu.authentication.client.dto.RoleFindRootDTO;
import baby.mumu.authentication.domain.permission.Permission;
import baby.mumu.authentication.domain.role.Role;
import baby.mumu.authentication.infrastructure.permission.convertor.PermissionConvertor;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.cache.PermissionCacheRepository;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.cache.po.PermissionCacheablePO;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.PermissionRepository;
import baby.mumu.authentication.infrastructure.relations.cache.RolePermissionCacheRepository;
import baby.mumu.authentication.infrastructure.relations.cache.RolePermissionCacheablePO;
import baby.mumu.authentication.infrastructure.relations.database.PermissionPathPO;
import baby.mumu.authentication.infrastructure.relations.database.PermissionPathPOId;
import baby.mumu.authentication.infrastructure.relations.database.PermissionPathRepository;
import baby.mumu.authentication.infrastructure.relations.database.RolePathRepository;
import baby.mumu.authentication.infrastructure.relations.database.RolePermissionPO;
import baby.mumu.authentication.infrastructure.relations.database.RolePermissionPOId;
import baby.mumu.authentication.infrastructure.relations.database.RolePermissionRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.cache.po.RoleCacheablePO;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.RoleArchivedRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.RoleRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.po.RoleArchivedPO;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.po.RolePO;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.translation.SimpleTextTranslation;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
public class RoleConvertor {

  private final PermissionConvertor permissionConvertor;
  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;
  private final SimpleTextTranslation simpleTextTranslation;
  private final RoleArchivedRepository roleArchivedRepository;
  private final RolePermissionRepository rolePermissionRepository;
  private final PermissionCacheRepository permissionCacheRepository;
  private final RolePermissionCacheRepository rolePermissionCacheRepository;
  private final PermissionPathRepository permissionPathRepository;
  private final RolePathRepository rolePathRepository;

  @Autowired
  public RoleConvertor(PermissionConvertor permissionConvertor, RoleRepository roleRepository,
    PermissionRepository permissionRepository,
    ObjectProvider<SimpleTextTranslation> simpleTextTranslation,
    RoleArchivedRepository roleArchivedRepository,
    RolePermissionRepository rolePermissionRepository,
    PermissionCacheRepository permissionCacheRepository,
    RolePermissionCacheRepository rolePermissionCacheRepository,
    PermissionPathRepository permissionPathRepository, RolePathRepository rolePathRepository) {
    this.permissionConvertor = permissionConvertor;
    this.roleRepository = roleRepository;
    this.permissionRepository = permissionRepository;
    this.simpleTextTranslation = simpleTextTranslation.getIfAvailable();
    this.roleArchivedRepository = roleArchivedRepository;
    this.rolePermissionRepository = rolePermissionRepository;
    this.permissionCacheRepository = permissionCacheRepository;
    this.rolePermissionCacheRepository = rolePermissionCacheRepository;
    this.permissionPathRepository = permissionPathRepository;
    this.rolePathRepository = rolePathRepository;
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Role> toEntity(RolePO rolePO) {
    // noinspection DuplicatedCode
    return Optional.ofNullable(rolePO).map(roleDataObject -> {
      Role role = RoleMapper.INSTANCE.toEntity(roleDataObject);
      setAuthorities(role, getPermissionIds(role));
      return role;
    }).flatMap(this::hasDescendant);
  }

  private Optional<Role> hasDescendant(Role role) {
    return Optional.ofNullable(role).map(roleNotNull -> {
      roleNotNull.setHasDescendant(
        rolePathRepository.existsDescendantRoles(role.getId()));
      return roleNotNull;
    });
  }

  private @NotNull List<Long> getPermissionIds(@NotNull Role role) {
    return rolePermissionCacheRepository.findById(role.getId())
      .map(RolePermissionCacheablePO::getPermissionIds).orElseGet(() -> {
        List<Long> permissionIds = rolePermissionRepository.findByRoleId(role.getId()).stream()
          .map(RolePermissionPO::getId)
          .map(RolePermissionPOId::getPermissionId).distinct().collect(Collectors.toList());
        rolePermissionCacheRepository.save(
          new RolePermissionCacheablePO(role.getId(), permissionIds));
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
          getAuthorities(permissionPathRepository.findByAncestorIdIn(
            ancestorIds).stream().map(PermissionPathPO::getId).map(
            PermissionPathPOId::getDescendantId).distinct().collect(Collectors.toList())));
      }
    });
  }

  private @NotNull ArrayList<Permission> getAuthorities(List<Long> permissionIds) {
    // 查询缓存中存在的数据
    List<PermissionCacheablePO> permissionCacheablePOS = permissionCacheRepository.findAllById(
      permissionIds);
    // 缓存中存在的权限ID
    List<Long> cachedCollectionOfPermissionIDs = permissionCacheablePOS.stream()
      .map(PermissionCacheablePO::getId)
      .collect(Collectors.toList());
    // 已缓存的权限
    List<Permission> cachedCollectionOfPermission = permissionCacheablePOS.stream()
      .flatMap(
        permissionCacheablePO -> permissionConvertor.toEntity(permissionCacheablePO).stream())
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
      permissionCacheRepository.saveAll(uncachedCollectionOfPermission.stream()
        .flatMap(permission -> permissionConvertor.toPermissionCacheablePO(permission).stream())
        .collect(
          Collectors.toList()));
    }
    // 合并已缓存和未缓存的权限
    return new ArrayList<>(
      CollectionUtils.union(cachedCollectionOfPermission, uncachedCollectionOfPermission));
  }

  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<Role> toEntity(RoleArchivedPO roleArchivedPO) {
    // noinspection DuplicatedCode
    return Optional.ofNullable(roleArchivedPO).map(roleArchivedDataObject -> {
      Role role = RoleMapper.INSTANCE.toEntity(roleArchivedDataObject);
      setAuthorities(role, getPermissionIds(role));
      return role;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<RolePO> toPO(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toPO);
  }


  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Role> toEntity(RoleAddCmd roleAddCmd) {
    return Optional.ofNullable(roleAddCmd).map(roleAddCmdNotNull -> {
      Role role = RoleMapper.INSTANCE.toEntity(roleAddCmdNotNull);
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
      Optional<RolePO> roleDoOptional = roleRepository.findById(roleUpdateCmdNotNull.getId());
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
  public Optional<RoleFindAllDTO> toFindAllDTO(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toFindAllDTO).map(roleFindAllCo -> {
      Optional.ofNullable(simpleTextTranslation).flatMap(
          simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
            roleFindAllCo.getName()))
        .ifPresent(roleFindAllCo::setName);
      return roleFindAllCo;
    });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<RoleFindAllSliceDTO> toFindAllSliceDTO(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toFindAllSliceDTO)
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
  public Optional<Role> toEntity(RoleCacheablePO roleCacheablePO) {
    return Optional.ofNullable(roleCacheablePO).map(RoleMapper.INSTANCE::toEntity)
      .map(role -> {
        setAuthorities(role, getPermissionIds(role));
        return role;
      });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<RoleArchivedFindAllDTO> toArchivedFindAllDTO(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toArchivedFindAllDTO)
      .map(roleArchivedFindAllCo -> {
        Optional.ofNullable(simpleTextTranslation).flatMap(
            simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
              roleArchivedFindAllCo.getName()))
          .ifPresent(roleArchivedFindAllCo::setName);
        return roleArchivedFindAllCo;
      });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<RoleArchivedFindAllSliceDTO> toArchivedFindAllSliceDTO(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toArchivedFindAllSliceDTO)
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
  public Optional<RoleArchivedPO> toArchivedPO(RolePO rolePO) {
    return Optional.ofNullable(rolePO).map(RoleMapper.INSTANCE::toArchivedPO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<RoleCacheablePO> toRoleCacheablePO(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toRoleCacheablePO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<RoleArchivedPO> toArchivedPO(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toArchivedPO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<RolePO> toPO(RoleArchivedPO roleArchivedPO) {
    return Optional.ofNullable(roleArchivedPO).map(RoleMapper.INSTANCE::toPO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.1.0")
  public List<RolePermissionPO> toRolePermissionPOS(Role role) {
    return Optional.ofNullable(role).flatMap(roleNonNull -> Optional.ofNullable(
      roleNonNull.getPermissions())).map(authorities -> authorities.stream().map(permission -> {
      RolePermissionPO rolePermissionPO = new RolePermissionPO();
      rolePermissionPO.setId(RolePermissionPOId.builder().roleId(role.getId()).permissionId(
        permission.getId()).build());
      roleRepository.findById(role.getId()).ifPresent(rolePermissionPO::setRole);
      permissionRepository.findById(permission.getId()).ifPresent(rolePermissionPO::setPermission);
      return rolePermissionPO;
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
  public Optional<RoleFindAllGrpcDTO> toRoleFindAllGrpcDTO(RoleFindAllDTO roleFindAllDTO) {
    return Optional.ofNullable(roleFindAllDTO).map(RoleMapper.INSTANCE::toRoleFindAllGrpcDTO)
      .map(roleFindAllGrpcCo ->
        roleFindAllGrpcCo.toBuilder().addAllPermissions(
          Optional.ofNullable(roleFindAllDTO.getPermissions()).map(
            permissions -> permissions.stream()
              .map(RoleMapper.INSTANCE::toRoleFindAllPermissionGrpcDTO)
              .collect(Collectors.toList())).orElse(new ArrayList<>())).build()
      );
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.4.0")
  public Optional<RoleFindByIdDTO> toRoleFindByIdDTO(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toRoleFindByIdDTO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.5.0")
  public Optional<RoleFindByCodeDTO> toRoleFindByCodeDTO(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toRoleFindByCodeDTO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.4.0")
  public Optional<RoleFindRootDTO> toRoleFindRootDTO(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toRoleFindRootDTO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.4.0")
  public Optional<RoleFindDirectDTO> toRoleFindDirectDTO(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toRoleFindDirectDTO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.4.0")
  public Optional<RoleFindByIdGrpcDTO> toRoleFindByIdGrpcDTO(RoleFindByIdDTO roleFindByIdDTO) {
    return Optional.ofNullable(roleFindByIdDTO).map(RoleMapper.INSTANCE::toRoleFindByIdGrpcDTO);
  }
}
