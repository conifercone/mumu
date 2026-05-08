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

package baby.mumu.iam.infra.permission.mapper;

import baby.mumu.iam.domain.permission.Permission;
import baby.mumu.iam.domain.permission.PermissionRelation;
import baby.mumu.iam.infra.permission.gatewayimpl.cache.po.PermissionCacheablePO;
import baby.mumu.iam.infra.permission.gatewayimpl.database.po.PermissionArchivedPO;
import baby.mumu.iam.infra.permission.gatewayimpl.database.po.PermissionPO;
import baby.mumu.iam.infra.relations.database.PermissionPathPOId;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Permission persistence mapper
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionPersistenceMapper {

    PermissionPersistenceMapper INSTANCE = Mappers.getMapper(PermissionPersistenceMapper.class);

    @API(status = Status.STABLE, since = "1.0.1")
    Permission toEntity(PermissionPO permissionPO);

    @API(status = Status.STABLE, since = "2.14.0")
    List<Permission> toEntities(List<PermissionPO> permissionPOList);

    @API(status = Status.STABLE, since = "2.0.0")
    Permission toEntity(PermissionArchivedPO permissionArchivedPO);

    @API(status = Status.STABLE, since = "2.14.0")
    List<Permission> toEntitiesFromArchivedPO(List<PermissionArchivedPO> permissionArchivedPOList);

    @API(status = Status.STABLE, since = "2.2.0")
    Permission toEntity(PermissionCacheablePO permissionCacheablePO);

    @API(status = Status.STABLE, since = "2.2.0")
    PermissionCacheablePO toPermissionCacheablePO(Permission permission);

    @API(status = Status.STABLE, since = "1.0.1")
    PermissionPO toPermissionPO(Permission permission);

    @API(status = Status.STABLE, since = "1.0.4")
    PermissionArchivedPO toPermissionArchivedPO(PermissionPO permissionPO);

    @API(status = Status.STABLE, since = "2.2.0")
    PermissionArchivedPO toPermissionArchivedPO(Permission permission);

    @API(status = Status.STABLE, since = "1.0.4")
    PermissionPO toPermissionPO(PermissionArchivedPO permissionArchivedPO);

    @API(status = Status.STABLE, since = "2.6.0")
    @Mapping(target = "ancestorId", source = "ancestorId")
    @Mapping(target = "descendantId", source = "descendantId")
    @Mapping(target = "depth", source = "depth")
    PermissionRelation toRelation(PermissionPathPOId permissionPathPOId);

    @API(status = Status.STABLE, since = "2.6.0")
    List<PermissionRelation> toRelations(List<PermissionPathPOId> permissionPathPOIds);
}
