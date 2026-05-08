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

package baby.mumu.iam.application.role.convertor;

import baby.mumu.basis.mappers.DataTransferObjectMapper;
import baby.mumu.basis.mappers.GrpcMapper;
import baby.mumu.iam.client.api.grpc.RoleFindAllGrpcCmd;
import baby.mumu.iam.client.api.grpc.RoleFindByIdGrpcDTO;
import baby.mumu.iam.client.api.grpc.RoleGrpcDTO;
import baby.mumu.iam.client.api.grpc.RolePermissionGrpcDTO;
import baby.mumu.iam.client.cmds.*;
import baby.mumu.iam.client.dto.*;
import baby.mumu.iam.client.dto.RoleFindAllDTO.RolePermissionDTO;
import baby.mumu.iam.domain.role.Role;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * Role application assembler mapper
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleAssemblerMapper extends GrpcMapper, DataTransferObjectMapper {

    RoleAssemblerMapper INSTANCE = Mappers.getMapper(RoleAssemblerMapper.class);

    @API(status = Status.STABLE, since = "1.0.1")
    Role toEntity(RoleAddCmd roleAddCmd);

    @API(status = Status.STABLE, since = "1.0.1")
    Role toEntity(RoleFindAllCmd roleFindAllCmd);

    @API(status = Status.STABLE, since = "2.2.0")
    Role toEntity(RoleFindAllSliceCmd roleFindAllSliceCmd);

    @API(status = Status.STABLE, since = "2.2.0")
    Role toEntity(RoleArchivedFindAllCmd roleArchivedFindAllCmd);

    @API(status = Status.STABLE, since = "2.2.0")
    Role toEntity(RoleArchivedFindAllSliceCmd roleArchivedFindAllSliceCmd);

    @API(status = Status.STABLE, since = "1.0.1")
    void toEntity(RoleUpdateCmd roleUpdateCmd, @MappingTarget Role role);

    @API(status = Status.STABLE, since = "1.0.1")
    RoleFindAllDTO toRoleFindAllDTO(Role role);

    @API(status = Status.STABLE, since = "2.2.0")
    RoleFindAllSliceDTO toRoleFindAllSliceDTO(Role role);

    @API(status = Status.STABLE, since = "2.4.0")
    RoleFindByIdDTO toRoleFindByIdDTO(Role role);

    @API(status = Status.STABLE, since = "2.5.0")
    RoleFindByCodeDTO toRoleFindByCodeDTO(Role role);

    @API(status = Status.STABLE, since = "2.4.0")
    RoleFindRootDTO toRoleFindRootDTO(Role role);

    @API(status = Status.STABLE, since = "2.4.0")
    RoleFindDirectDTO toRoleFindDirectDTO(Role role);

    @API(status = Status.STABLE, since = "2.2.0")
    RoleArchivedFindAllDTO toRoleArchivedFindAllDTO(Role role);

    @API(status = Status.STABLE, since = "2.2.0")
    RoleArchivedFindAllSliceDTO toRoleArchivedFindAllSliceDTO(Role role);

    @API(status = Status.STABLE, since = "2.2.0")
    RoleFindAllCmd toRoleFindAllCmd(RoleFindAllGrpcCmd roleFindAllGrpcCmd);

    @API(status = Status.STABLE, since = "2.2.0")
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    RoleGrpcDTO toRoleGrpcDTO(RoleFindAllDTO roleFindAllDTO);

    @API(status = Status.STABLE, since = "2.2.0")
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    RolePermissionGrpcDTO toRolePermissionGrpcDTO(
        RolePermissionDTO rolePermissionDTO);

    @API(status = Status.STABLE, since = "2.4.0")
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    RoleFindByIdGrpcDTO toRoleFindByIdGrpcDTO(
        RoleFindByIdDTO roleFindByIdDTO);

    @API(status = Status.STABLE, since = "2.13.0")
    RoleUpdatedDataDTO toRoleUpdatedDataDTO(Role role);
}
