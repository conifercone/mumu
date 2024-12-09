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
package baby.mumu.authentication.infrastructure.permission.convertor;

import baby.mumu.authentication.client.api.grpc.PermissionFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.PermissionFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.PermissionFindByIdGrpcCo;
import baby.mumu.authentication.client.dto.PermissionAddCmd;
import baby.mumu.authentication.client.dto.PermissionArchivedFindAllCmd;
import baby.mumu.authentication.client.dto.PermissionArchivedFindAllSliceCmd;
import baby.mumu.authentication.client.dto.PermissionFindAllCmd;
import baby.mumu.authentication.client.dto.PermissionFindAllSliceCmd;
import baby.mumu.authentication.client.dto.PermissionUpdateCmd;
import baby.mumu.authentication.client.dto.co.PermissionArchivedFindAllCo;
import baby.mumu.authentication.client.dto.co.PermissionArchivedFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.PermissionDownloadAllCo;
import baby.mumu.authentication.client.dto.co.PermissionFindAllCo;
import baby.mumu.authentication.client.dto.co.PermissionFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.PermissionFindByIdCo;
import baby.mumu.authentication.client.dto.co.PermissionFindDirectCo;
import baby.mumu.authentication.client.dto.co.PermissionFindRootCo;
import baby.mumu.authentication.domain.permission.Permission;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.dataobject.PermissionArchivedDo;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.dataobject.PermissionDo;
import baby.mumu.authentication.infrastructure.permission.gatewayimpl.redis.dataobject.PermissionRedisDo;
import baby.mumu.basis.mappers.ClientObjectMapper;
import baby.mumu.basis.mappers.GrpcMapper;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper extends GrpcMapper, ClientObjectMapper {

  PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

  @API(status = Status.STABLE, since = "1.0.1")
  Permission toEntity(PermissionDo permissionDo);

  @API(status = Status.STABLE, since = "1.0.1")
  Permission toEntity(PermissionAddCmd permissionAddCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  Permission toEntity(PermissionFindAllCmd permissionFindAllCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  Permission toEntity(PermissionFindAllSliceCmd permissionFindAllSliceCmd);

  @API(status = Status.STABLE, since = "2.0.0")
  Permission toEntity(PermissionArchivedDo permissionArchivedDo);

  @API(status = Status.STABLE, since = "2.2.0")
  Permission toEntity(PermissionRedisDo permissionRedisDo);

  @API(status = Status.STABLE, since = "2.2.0")
  PermissionRedisDo toPermissionRedisDo(Permission permission);

  @API(status = Status.STABLE, since = "2.2.0")
  PermissionDo toDataObject(PermissionRedisDo permissionRedisDo);

  @API(status = Status.STABLE, since = "2.0.0")
  Permission toEntity(PermissionArchivedFindAllCmd permissionArchivedFindAllCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  Permission toEntity(PermissionArchivedFindAllSliceCmd permissionArchivedFindAllSliceCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  void toEntity(PermissionUpdateCmd permissionUpdateCmd, @MappingTarget Permission permission);

  @API(status = Status.STABLE, since = "1.0.1")
  PermissionFindByIdCo toFindByIdCo(Permission permission);

  @API(status = Status.STABLE, since = "1.0.1")
  PermissionFindAllCo toFindAllCo(Permission permission);

  @API(status = Status.STABLE, since = "2.2.0")
  PermissionFindAllSliceCo toFindAllSliceCo(Permission permission);

  @API(status = Status.STABLE, since = "2.0.0")
  PermissionArchivedFindAllCo toArchivedFindAllCo(Permission permission);

  @API(status = Status.STABLE, since = "2.2.0")
  PermissionArchivedFindAllSliceCo toArchivedFindAllSliceCo(Permission permission);

  @API(status = Status.STABLE, since = "1.0.1")
  PermissionDo toDataObject(Permission permission);

  @API(status = Status.STABLE, since = "1.0.4")
  PermissionArchivedDo toArchivedDo(PermissionDo permissionDo);

  @API(status = Status.STABLE, since = "2.2.0")
  PermissionArchivedDo toArchivedDo(Permission permission);

  @API(status = Status.STABLE, since = "1.0.4")
  PermissionDo toDataObject(PermissionArchivedDo permissionArchivedDo);

  @API(status = Status.STABLE, since = "2.2.0")
  PermissionFindAllCmd toPermissionFindAllCmd(PermissionFindAllGrpcCmd authorityFindAllGrpcCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  PermissionFindAllGrpcCo toPermissionFindAllGrpcCo(PermissionFindAllCo permissionFindAllCo);

  @API(status = Status.STABLE, since = "2.3.0")
  PermissionFindByIdGrpcCo toPermissionFindByIdGrpcCo(PermissionFindByIdCo permissionFindByIdCo);

  @API(status = Status.STABLE, since = "2.3.0")
  PermissionFindRootCo toPermissionFindRootCo(Permission permission);

  @API(status = Status.STABLE, since = "2.3.0")
  PermissionFindDirectCo toPermissionFindDirectCo(Permission permission);

  @API(status = Status.STABLE, since = "2.4.0")
  PermissionDownloadAllCo toPermissionDownloadAllCo(Permission permission);
}
