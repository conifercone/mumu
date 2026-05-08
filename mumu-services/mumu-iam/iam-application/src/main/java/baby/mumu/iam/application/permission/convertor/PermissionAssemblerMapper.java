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

package baby.mumu.iam.application.permission.convertor;

import baby.mumu.basis.mappers.DataTransferObjectMapper;
import baby.mumu.basis.mappers.GrpcMapper;
import baby.mumu.iam.client.api.grpc.PermissionFindAllGrpcCmd;
import baby.mumu.iam.client.api.grpc.PermissionFindByIdGrpcDTO;
import baby.mumu.iam.client.api.grpc.PermissionGrpcDTO;
import baby.mumu.iam.client.cmds.*;
import baby.mumu.iam.client.dto.*;
import baby.mumu.iam.client.dto.PermissionIncludePathDownloadAllDTO.PermissionPathDTO;
import baby.mumu.iam.domain.permission.Permission;
import baby.mumu.iam.domain.permission.PermissionRelation;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Permission application assembler mapper
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionAssemblerMapper extends GrpcMapper, DataTransferObjectMapper {

    PermissionAssemblerMapper INSTANCE = Mappers.getMapper(PermissionAssemblerMapper.class);

    @API(status = Status.STABLE, since = "1.0.1")
    Permission toEntity(PermissionAddCmd permissionAddCmd);

    @API(status = Status.STABLE, since = "1.0.1")
    Permission toEntity(PermissionFindAllCmd permissionFindAllCmd);

    @API(status = Status.STABLE, since = "2.2.0")
    Permission toEntity(PermissionFindAllSliceCmd permissionFindAllSliceCmd);

    @API(status = Status.STABLE, since = "2.0.0")
    Permission toEntity(PermissionArchivedFindAllCmd permissionArchivedFindAllCmd);

    @API(status = Status.STABLE, since = "2.2.0")
    Permission toEntity(PermissionArchivedFindAllSliceCmd permissionArchivedFindAllSliceCmd);

    @API(status = Status.STABLE, since = "1.0.1")
    void toEntity(PermissionUpdateCmd permissionUpdateCmd, @MappingTarget Permission permission);

    @API(status = Status.STABLE, since = "1.0.1")
    PermissionFindByIdDTO toPermissionFindByIdDTO(Permission permission);

    @API(status = Status.STABLE, since = "2.4.0")
    PermissionFindByCodeDTO toPermissionFindByCodeDTO(Permission permission);

    @API(status = Status.STABLE, since = "1.0.1")
    PermissionFindAllDTO toPermissionFindAllDTO(Permission permission);

    @API(status = Status.STABLE, since = "2.14.0")
    List<PermissionFindAllDTO> toPermissionFindAllDTOS(List<Permission> permissions);

    @API(status = Status.STABLE, since = "2.2.0")
    PermissionFindAllSliceDTO toPermissionFindAllSliceDTO(Permission permission);

    @API(status = Status.STABLE, since = "2.0.0")
    PermissionArchivedFindAllDTO toPermissionArchivedFindAllDTO(Permission permission);

    @API(status = Status.STABLE, since = "2.2.0")
    PermissionArchivedFindAllSliceDTO toPermissionArchivedFindAllSliceDTO(Permission permission);

    @API(status = Status.STABLE, since = "2.2.0")
    PermissionFindAllCmd toPermissionFindAllCmd(PermissionFindAllGrpcCmd authorityFindAllGrpcCmd);

    @API(status = Status.STABLE, since = "2.2.0")
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    PermissionGrpcDTO toPermissionGrpcDTO(PermissionFindAllDTO permissionFindAllDTO);

    @API(status = Status.STABLE, since = "2.3.0")
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    PermissionFindByIdGrpcDTO toPermissionFindByIdGrpcDTO(
        PermissionFindByIdDTO permissionFindByIdDTO);

    @API(status = Status.STABLE, since = "2.3.0")
    PermissionFindRootDTO toPermissionFindRootDTO(Permission permission);

    @API(status = Status.STABLE, since = "2.3.0")
    PermissionFindDirectDTO toPermissionFindDirectDTO(Permission permission);

    @API(status = Status.STABLE, since = "2.4.0")
    PermissionDownloadAllDTO toPermissionDownloadAllDTO(Permission permission);

    @API(status = Status.STABLE, since = "2.6.0")
    PermissionIncludePathDownloadAllDTO toPermissionIncludePathDownloadAllDTO(Permission permission);

    @API(status = Status.STABLE, since = "2.6.0")
    PermissionPathDTO toPermissionPathDTO(PermissionRelation permissionRelation);

    @API(status = Status.STABLE, since = "2.13.0")
    PermissionUpdatedDataDTO toPermissionUpdatedDataDTO(Permission permission);
}
