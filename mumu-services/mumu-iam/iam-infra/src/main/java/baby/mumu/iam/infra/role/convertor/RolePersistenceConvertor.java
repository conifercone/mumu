/*
 * Copyright (c) 2024-2026, the original author or authors.
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

package baby.mumu.iam.infra.role.convertor;

import baby.mumu.iam.domain.permission.Permission;
import baby.mumu.iam.domain.role.Role;
import baby.mumu.iam.infra.permission.convertor.PermissionPersistenceConvertor;
import baby.mumu.iam.infra.permission.gatewayimpl.cache.PermissionCacheRepository;
import baby.mumu.iam.infra.permission.gatewayimpl.cache.po.PermissionCacheablePO;
import baby.mumu.iam.infra.permission.gatewayimpl.database.PermissionRepository;
import baby.mumu.iam.infra.relations.cache.RolePermissionCacheRepository;
import baby.mumu.iam.infra.relations.cache.RolePermissionCacheablePO;
import baby.mumu.iam.infra.relations.database.*;
import baby.mumu.iam.infra.role.gatewayimpl.cache.po.RoleCacheablePO;
import baby.mumu.iam.infra.role.gatewayimpl.database.RoleRepository;
import baby.mumu.iam.infra.role.gatewayimpl.database.po.RoleArchivedPO;
import baby.mumu.iam.infra.role.gatewayimpl.database.po.RolePO;
import baby.mumu.iam.infra.role.mapper.RolePersistenceMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色信息持久化转换器 (Infrastructure Layer)
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
public class RolePersistenceConvertor {

    private final PermissionPersistenceConvertor permissionPersistenceConvertor;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionCacheRepository permissionCacheRepository;
    private final RolePermissionCacheRepository rolePermissionCacheRepository;
    private final PermissionPathRepository permissionPathRepository;
    private final RolePathRepository rolePathRepository;

    @Autowired
    public RolePersistenceConvertor(PermissionPersistenceConvertor permissionPersistenceConvertor, RoleRepository roleRepository,
                         PermissionRepository permissionRepository,
                         RolePermissionRepository rolePermissionRepository,
                         PermissionCacheRepository permissionCacheRepository,
                         RolePermissionCacheRepository rolePermissionCacheRepository,
                         PermissionPathRepository permissionPathRepository, RolePathRepository rolePathRepository) {
        this.permissionPersistenceConvertor = permissionPersistenceConvertor;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
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
            Role role = RolePersistenceMapper.INSTANCE.toEntity(roleDataObject);
            setAuthorities(role, getPermissionIds(role));
            return role;
        }).flatMap(this::hasDescendant);
    }

    @API(status = Status.STABLE, since = "2.14.0")
    public List<Role> toEntities(List<RolePO> rolePOList) {
        List<Role> roles =
            Optional.ofNullable(RolePersistenceMapper.INSTANCE.toEntities(rolePOList)).orElse(new ArrayList<>());
        roles.forEach(role -> setAuthorities(role, getPermissionIds(role)));
        return this.hasDescendant(roles);
    }

    private Optional<Role> hasDescendant(Role role) {
        return Optional.ofNullable(role).map(roleNotNull -> {
            roleNotNull.setHasDescendant(
                rolePathRepository.existsDescendantRoles(role.getId()));
            return roleNotNull;
        });
    }

    private List<Role> hasDescendant(List<Role> roles) {
        // noinspection DuplicatedCode
        List<Long> roleIds = Optional.ofNullable(roles).orElse(new ArrayList<>()).stream()
            .map(Role::getId).toList();
        if (roleIds.isEmpty()) {
            return roles;
        }
        Set<Long> ancestorIdsWithDescendants = new HashSet<>(
            rolePathRepository.findAncestorIdsWithDescendants(
                roleIds));
        roles.forEach(p -> {
            if (ancestorIdsWithDescendants.contains(p.getId())) {
                p.setHasDescendant(true);
            }
        });
        return roles;
    }

    private @NonNull List<Long> getPermissionIds(@NonNull Role role) {
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

    private @NonNull ArrayList<Permission> getAuthorities(List<Long> permissionIds) {
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
                permissionCacheablePO -> permissionPersistenceConvertor.toEntity(permissionCacheablePO).stream())
            .collect(
                Collectors.toList());
        // 未缓存的权限
        List<Permission> uncachedCollectionOfPermission = Optional.of(
                CollectionUtils.subtract(permissionIds, cachedCollectionOfPermissionIDs))
            .filter(CollectionUtils::isNotEmpty).map(
                uncachedCollectionOfPermissionId -> permissionRepository.findAllById(
                        uncachedCollectionOfPermissionId)
                    .stream()
                    .flatMap(permissionDo -> permissionPersistenceConvertor.toEntity(permissionDo).stream())
                    .collect(
                        Collectors.toList())).orElse(new ArrayList<>());
        // 未缓存的权限放入缓存
        if (CollectionUtils.isNotEmpty(uncachedCollectionOfPermission)) {
            permissionCacheRepository.saveAll(uncachedCollectionOfPermission.stream()
                .flatMap(permission -> permissionPersistenceConvertor.toPermissionCacheablePO(permission).stream())
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
            Role role = RolePersistenceMapper.INSTANCE.toEntity(roleArchivedDataObject);
            setAuthorities(role, getPermissionIds(role));
            return role;
        });
    }

    @API(status = Status.STABLE, since = "2.14.0")
    public List<Role> toEntitiesFromArchivedPO(List<RoleArchivedPO> roleArchivedPOList) {
        List<Role> roles =
            Optional.ofNullable(RolePersistenceMapper.INSTANCE.toEntitiesFromArchivedPO(roleArchivedPOList)).orElse(new ArrayList<>());
        roles.forEach(role -> setAuthorities(role, getPermissionIds(role)));
        return roles;
    }

    @API(status = Status.STABLE, since = "1.0.0")
    public Optional<RolePO> toRolePO(Role role) {
        return Optional.ofNullable(RolePersistenceMapper.INSTANCE.toRolePO(role));
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<Role> toEntity(RoleCacheablePO roleCacheablePO) {
        return Optional.ofNullable(RolePersistenceMapper.INSTANCE.toEntity(roleCacheablePO))
            .map(role -> {
                setAuthorities(role, getPermissionIds(role));
                return role;
            });
    }

    @API(status = Status.STABLE, since = "2.14.0")
    public List<Role> toEntitiesFromCacheablePO(List<RoleCacheablePO> roleCacheablePOList) {
        List<Role> roles = Optional.ofNullable(roleCacheablePOList).orElse(new ArrayList<>()).stream()
            .flatMap(roleCacheablePO -> Optional.ofNullable(RolePersistenceMapper.INSTANCE.toEntity(roleCacheablePO))
                .stream())
            .peek(role -> setAuthorities(role, getPermissionIds(role)))
            .toList();
        return this.hasDescendant(roles);
    }

    @API(status = Status.STABLE, since = "1.0.4")
    public Optional<RoleArchivedPO> toRoleArchivedPO(RolePO rolePO) {
        return Optional.ofNullable(RolePersistenceMapper.INSTANCE.toRoleArchivedPO(rolePO));
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<RoleCacheablePO> toRoleCacheablePO(Role role) {
        return Optional.ofNullable(RolePersistenceMapper.INSTANCE.toRoleCacheablePO(role));
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<RoleArchivedPO> toRoleArchivedPO(Role role) {
        return Optional.ofNullable(RolePersistenceMapper.INSTANCE.toRoleArchivedPO(role));
    }

    @API(status = Status.STABLE, since = "1.0.4")
    public Optional<RolePO> toRolePO(RoleArchivedPO roleArchivedPO) {
        return Optional.ofNullable(RolePersistenceMapper.INSTANCE.toRolePO(roleArchivedPO));
    }

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
}
