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
import baby.mumu.authentication.client.api.grpc.RoleFindAllGrpcDTO;
import baby.mumu.authentication.client.api.grpc.RoleFindAllPermissionGrpcDTO;
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
import baby.mumu.authentication.client.dto.RoleFindAllDTO.RoleFindAllPermissionCo;
import baby.mumu.authentication.client.dto.RoleFindAllSliceDTO;
import baby.mumu.authentication.client.dto.RoleFindByCodeDTO;
import baby.mumu.authentication.client.dto.RoleFindByIdDTO;
import baby.mumu.authentication.client.dto.RoleFindDirectDTO;
import baby.mumu.authentication.client.dto.RoleFindRootDTO;
import baby.mumu.authentication.domain.role.Role;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleArchivedDO;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleDO;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.redis.dataobject.RoleRedisDO;
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
 * Role mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper extends GrpcMapper, ClientObjectMapper {

  RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

  @API(status = Status.STABLE, since = "1.0.1")
  Role toEntity(RoleDO roleDo);

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

  @API(status = Status.STABLE, since = "2.2.0")
  Role toEntity(RoleRedisDO roleRedisDo);

  @API(status = Status.STABLE, since = "2.2.0")
  RoleRedisDO toRoleRedisDO(Role role);

  @API(status = Status.STABLE, since = "1.0.1")
  void toEntity(RoleUpdateCmd roleUpdateCmd, @MappingTarget Role role);

  @API(status = Status.STABLE, since = "1.0.1")
  RoleFindAllDTO toFindAllDTO(Role role);

  @API(status = Status.STABLE, since = "2.2.0")
  RoleFindAllSliceDTO toFindAllSliceDTO(Role role);

  @API(status = Status.STABLE, since = "2.4.0")
  RoleFindByIdDTO toRoleFindByIdDTO(Role role);

  @API(status = Status.STABLE, since = "2.5.0")
  RoleFindByCodeDTO toRoleFindByCodeDTO(Role role);

  @API(status = Status.STABLE, since = "2.4.0")
  RoleFindRootDTO toRoleFindRootDTO(Role role);

  @API(status = Status.STABLE, since = "2.4.0")
  RoleFindDirectDTO toRoleFindDirectDTO(Role role);

  @API(status = Status.STABLE, since = "2.2.0")
  RoleArchivedFindAllDTO toArchivedFindAllDTO(Role role);

  @API(status = Status.STABLE, since = "2.2.0")
  RoleArchivedFindAllSliceDTO toArchivedFindAllSliceDTO(Role role);

  @API(status = Status.STABLE, since = "1.0.1")
  RoleDO toDataObject(Role role);

  @API(status = Status.STABLE, since = "1.0.4")
  RoleArchivedDO toArchivedDO(RoleDO roleDo);

  @API(status = Status.STABLE, since = "2.2.0")
  RoleArchivedDO toArchivedDO(Role role);

  @API(status = Status.STABLE, since = "1.0.4")
  RoleDO toDataObject(RoleArchivedDO roleArchivedDo);

  @API(status = Status.STABLE, since = "1.0.4")
  Role toEntity(RoleArchivedDO roleArchivedDo);

  @API(status = Status.STABLE, since = "2.2.0")
  RoleFindAllCmd toRoleFindAllCmd(RoleFindAllGrpcCmd roleFindAllGrpcCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  RoleFindAllGrpcDTO toRoleFindAllGrpcDTO(RoleFindAllDTO roleFindAllDTO);

  @API(status = Status.STABLE, since = "2.2.0")
  RoleFindAllPermissionGrpcDTO toRoleFindAllPermissionGrpcDTO(
    RoleFindAllPermissionCo roleFindAllPermissionCo);

  @API(status = Status.STABLE, since = "2.4.0")
  RoleFindByIdGrpcDTO toRoleFindByIdGrpcDTO(
    RoleFindByIdDTO roleFindByIdDTO);
}
