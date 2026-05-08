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

package baby.mumu.iam.infra.role.mapper;

import baby.mumu.iam.domain.role.Role;
import baby.mumu.iam.infra.role.gatewayimpl.cache.po.RoleCacheablePO;
import baby.mumu.iam.infra.role.gatewayimpl.database.po.RoleArchivedPO;
import baby.mumu.iam.infra.role.gatewayimpl.database.po.RolePO;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Role infrastructure persistence mapper
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RolePersistenceMapper {

    RolePersistenceMapper INSTANCE = Mappers.getMapper(RolePersistenceMapper.class);

    @API(status = Status.STABLE, since = "1.0.1")
    Role toEntity(RolePO rolePO);

    @API(status = Status.STABLE, since = "2.14.0")
    List<Role> toEntities(List<RolePO> rolePOList);

    @API(status = Status.STABLE, since = "2.2.0")
    Role toEntity(RoleCacheablePO roleCacheablePO);

    @API(status = Status.STABLE, since = "2.2.0")
    RoleCacheablePO toRoleCacheablePO(Role role);

    @API(status = Status.STABLE, since = "1.0.1")
    RolePO toRolePO(Role role);

    @API(status = Status.STABLE, since = "1.0.4")
    RoleArchivedPO toRoleArchivedPO(RolePO rolePO);

    @API(status = Status.STABLE, since = "2.2.0")
    RoleArchivedPO toRoleArchivedPO(Role role);

    @API(status = Status.STABLE, since = "1.0.4")
    RolePO toRolePO(RoleArchivedPO roleArchivedPO);

    @API(status = Status.STABLE, since = "1.0.4")
    Role toEntity(RoleArchivedPO roleArchivedPO);

    @API(status = Status.STABLE, since = "2.14.0")
    List<Role> toEntitiesFromArchivedPO(List<RoleArchivedPO> roleArchivedPOList);
}
