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

package baby.mumu.iam.infrastructure.permission.convertor;

import baby.mumu.basis.mappers.DataTransferObjectMapper;
import baby.mumu.basis.mappers.GrpcMapper;
import baby.mumu.iam.client.api.grpc.PermissionFindAllGrpcCmd;
import baby.mumu.iam.client.api.grpc.PermissionFindAllGrpcDTO;
import baby.mumu.iam.client.api.grpc.PermissionFindByIdGrpcDTO;
import baby.mumu.iam.client.cmds.PermissionAddCmd;
import baby.mumu.iam.client.cmds.PermissionArchivedFindAllCmd;
import baby.mumu.iam.client.cmds.PermissionArchivedFindAllSliceCmd;
import baby.mumu.iam.client.cmds.PermissionFindAllCmd;
import baby.mumu.iam.client.cmds.PermissionFindAllSliceCmd;
import baby.mumu.iam.client.cmds.PermissionUpdateCmd;
import baby.mumu.iam.client.dto.PermissionArchivedFindAllDTO;
import baby.mumu.iam.client.dto.PermissionArchivedFindAllSliceDTO;
import baby.mumu.iam.client.dto.PermissionDownloadAllDTO;
import baby.mumu.iam.client.dto.PermissionFindAllDTO;
import baby.mumu.iam.client.dto.PermissionFindAllSliceDTO;
import baby.mumu.iam.client.dto.PermissionFindByCodeDTO;
import baby.mumu.iam.client.dto.PermissionFindByIdDTO;
import baby.mumu.iam.client.dto.PermissionFindDirectDTO;
import baby.mumu.iam.client.dto.PermissionFindRootDTO;
import baby.mumu.iam.client.dto.PermissionIncludePathDownloadAllDTO;
import baby.mumu.iam.client.dto.PermissionIncludePathDownloadAllDTO.PermissionIncludePathDTO;
import baby.mumu.iam.domain.permission.Permission;
import baby.mumu.iam.infrastructure.permission.gatewayimpl.cache.po.PermissionCacheablePO;
import baby.mumu.iam.infrastructure.permission.gatewayimpl.database.po.PermissionArchivedPO;
import baby.mumu.iam.infrastructure.permission.gatewayimpl.database.po.PermissionPO;
import baby.mumu.iam.infrastructure.relations.database.PermissionPathPOId;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Permission mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper extends GrpcMapper, DataTransferObjectMapper {

  PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

  @API(status = Status.STABLE, since = "1.0.1")
  Permission toEntity(PermissionPO permissionPO);

  @API(status = Status.STABLE, since = "1.0.1")
  Permission toEntity(PermissionAddCmd permissionAddCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  Permission toEntity(PermissionFindAllCmd permissionFindAllCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  Permission toEntity(PermissionFindAllSliceCmd permissionFindAllSliceCmd);

  @API(status = Status.STABLE, since = "2.0.0")
  Permission toEntity(PermissionArchivedPO permissionArchivedPO);

  @API(status = Status.STABLE, since = "2.2.0")
  Permission toEntity(PermissionCacheablePO permissionCacheablePO);

  @API(status = Status.STABLE, since = "2.2.0")
  PermissionCacheablePO toPermissionCacheablePO(Permission permission);

  @API(status = Status.STABLE, since = "2.2.0")
  PermissionPO toPO(PermissionCacheablePO permissionCacheablePO);

  @API(status = Status.STABLE, since = "2.0.0")
  Permission toEntity(PermissionArchivedFindAllCmd permissionArchivedFindAllCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  Permission toEntity(PermissionArchivedFindAllSliceCmd permissionArchivedFindAllSliceCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  void toEntity(PermissionUpdateCmd permissionUpdateCmd, @MappingTarget Permission permission);

  @API(status = Status.STABLE, since = "1.0.1")
  PermissionFindByIdDTO toFindByIdDTO(Permission permission);

  @API(status = Status.STABLE, since = "2.4.0")
  PermissionFindByCodeDTO toFindByCodeDTO(Permission permission);

  @API(status = Status.STABLE, since = "1.0.1")
  PermissionFindAllDTO toFindAllDTO(Permission permission);

  @API(status = Status.STABLE, since = "2.2.0")
  PermissionFindAllSliceDTO toFindAllSliceDTO(Permission permission);

  @API(status = Status.STABLE, since = "2.0.0")
  PermissionArchivedFindAllDTO toArchivedFindAllDTO(Permission permission);

  @API(status = Status.STABLE, since = "2.2.0")
  PermissionArchivedFindAllSliceDTO toArchivedFindAllSliceDTO(Permission permission);

  @API(status = Status.STABLE, since = "1.0.1")
  PermissionPO toPO(Permission permission);

  @API(status = Status.STABLE, since = "1.0.4")
  PermissionArchivedPO toArchivedPO(PermissionPO permissionPO);

  @API(status = Status.STABLE, since = "2.2.0")
  PermissionArchivedPO toArchivedPO(Permission permission);

  @API(status = Status.STABLE, since = "1.0.4")
  PermissionPO toPO(PermissionArchivedPO permissionArchivedPO);

  @API(status = Status.STABLE, since = "2.2.0")
  PermissionFindAllCmd toPermissionFindAllCmd(PermissionFindAllGrpcCmd authorityFindAllGrpcCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  PermissionFindAllGrpcDTO toPermissionFindAllGrpcDTO(PermissionFindAllDTO permissionFindAllDTO);

  @API(status = Status.STABLE, since = "2.3.0")
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
  PermissionIncludePathDTO toPermissionIncludePathDTO(PermissionPathPOId permissionPathPOId);
}
