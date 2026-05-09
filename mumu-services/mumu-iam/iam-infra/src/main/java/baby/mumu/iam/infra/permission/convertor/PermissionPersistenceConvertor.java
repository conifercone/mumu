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

package baby.mumu.iam.infra.permission.convertor;

import baby.mumu.iam.domain.permission.Permission;
import baby.mumu.iam.domain.permission.PermissionRelation;
import baby.mumu.iam.infra.permission.gatewayimpl.cache.po.PermissionCacheablePO;
import baby.mumu.iam.infra.permission.gatewayimpl.database.po.PermissionArchivedPO;
import baby.mumu.iam.infra.permission.gatewayimpl.database.po.PermissionPO;
import baby.mumu.iam.infra.permission.mapper.PermissionPersistenceMapper;
import baby.mumu.iam.infra.relations.database.PermissionPathPO;
import baby.mumu.iam.infra.relations.database.PermissionPathRepository;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限信息持久化转换器 (Infrastructure Layer)
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
public class PermissionPersistenceConvertor {

    private final PermissionPathRepository permissionPathRepository;

    @Autowired
    public PermissionPersistenceConvertor(PermissionPathRepository permissionPathRepository) {
        this.permissionPathRepository = permissionPathRepository;
    }

    @API(status = Status.STABLE, since = "1.0.1")
    public Optional<Permission> toEntity(PermissionPO permissionPO) {
        return Optional.ofNullable(PermissionPersistenceMapper.INSTANCE.toEntity(permissionPO)).flatMap(this::hasDescendant);
    }

    @API(status = Status.STABLE, since = "2.14.0")
    public List<Permission> toEntities(List<PermissionPO> permissionPOList) {
        List<Permission> permissions =
            Optional.ofNullable(PermissionPersistenceMapper.INSTANCE.toEntities(permissionPOList)).orElse(new ArrayList<>());
        return this.hasDescendant(permissions);
    }

    @API(status = Status.STABLE, since = "2.0.0")
    public Optional<Permission> toEntity(PermissionArchivedPO permissionArchivedPO) {
        return Optional.ofNullable(PermissionPersistenceMapper.INSTANCE.toEntity(permissionArchivedPO)).flatMap(this::hasDescendant);
    }

    @API(status = Status.STABLE, since = "2.14.0")
    public List<Permission> toEntitiesFromArchivedPO(List<PermissionArchivedPO> permissionArchivedPOList) {
        List<Permission> permissions =
            Optional.ofNullable(PermissionPersistenceMapper.INSTANCE.toEntitiesFromArchivedPO(permissionArchivedPOList)).orElse(new ArrayList<>());
        return this.hasDescendant(permissions);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<Permission> toEntity(PermissionCacheablePO permissionCacheablePO) {
        return Optional.ofNullable(PermissionPersistenceMapper.INSTANCE.toEntity(permissionCacheablePO)).flatMap(this::hasDescendant);
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<PermissionCacheablePO> toPermissionCacheablePO(Permission permission) {
        return Optional.ofNullable(PermissionPersistenceMapper.INSTANCE.toPermissionCacheablePO(permission));
    }

    @API(status = Status.STABLE, since = "1.0.1")
    public Optional<PermissionPO> toPermissionPO(Permission permission) {
        return Optional.ofNullable(PermissionPersistenceMapper.INSTANCE.toPermissionPO(permission));
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<PermissionArchivedPO> toPermissionArchivedPO(Permission permission) {
        return Optional.ofNullable(PermissionPersistenceMapper.INSTANCE.toPermissionArchivedPO(permission));
    }

    @API(status = Status.STABLE, since = "2.2.0")
    public Optional<PermissionArchivedPO> toPermissionArchivedPO(PermissionPO permissionPO) {
        return Optional.ofNullable(PermissionPersistenceMapper.INSTANCE.toPermissionArchivedPO(permissionPO));
    }

    @API(status = Status.STABLE, since = "1.0.4")
    public Optional<PermissionPO> toPermissionPO(PermissionArchivedPO permissionArchivedPO) {
        return Optional.ofNullable(PermissionPersistenceMapper.INSTANCE.toPermissionPO(permissionArchivedPO));
    }

    @API(status = Status.STABLE, since = "2.6.0")
    public List<PermissionRelation> toRelations(List<PermissionPathPO> paths) {
        return Optional.ofNullable(paths)
            .map(p -> PermissionPersistenceMapper.INSTANCE.toRelations(
                p.stream().map(PermissionPathPO::getId).collect(Collectors.toList())))
            .orElse(new ArrayList<>());
    }

    private Optional<Permission> hasDescendant(Permission permission) {
        return Optional.ofNullable(permission).map(permissionNotNull -> {
            permissionNotNull.setHasDescendant(
                permissionPathRepository.existsDescendantPermissions(permission.getId()));
            return permissionNotNull;
        });
    }

    private List<Permission> hasDescendant(List<Permission> permissions) {
        // noinspection DuplicatedCode
        List<Long> permissionIds = Optional.ofNullable(permissions).orElse(new ArrayList<>()).stream()
            .map(Permission::getId).toList();
        if (permissionIds.isEmpty()) {
            return permissions;
        }
        Set<Long> ancestorIdsWithDescendants = new HashSet<>(
            permissionPathRepository.findAncestorIdsWithDescendants(
                permissionIds));
        permissions.forEach(p -> {
            if (ancestorIdsWithDescendants.contains(p.getId())) {
                p.setHasDescendant(true);
            }
        });
        return permissions;
    }
}
