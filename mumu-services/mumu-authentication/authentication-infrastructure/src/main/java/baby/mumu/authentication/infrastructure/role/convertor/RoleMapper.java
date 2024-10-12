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

import baby.mumu.authentication.client.dto.co.RoleAddCo;
import baby.mumu.authentication.client.dto.co.RoleArchivedFindAllCo;
import baby.mumu.authentication.client.dto.co.RoleArchivedFindAllQueryCo;
import baby.mumu.authentication.client.dto.co.RoleArchivedFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.RoleArchivedFindAllSliceQueryCo;
import baby.mumu.authentication.client.dto.co.RoleFindAllCo;
import baby.mumu.authentication.client.dto.co.RoleFindAllQueryCo;
import baby.mumu.authentication.client.dto.co.RoleFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.RoleFindAllSliceQueryCo;
import baby.mumu.authentication.client.dto.co.RoleUpdateCo;
import baby.mumu.authentication.domain.role.Role;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleArchivedDo;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleDo;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.redis.dataobject.RoleRedisDo;
import baby.mumu.basis.kotlin.tools.CommonUtil;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.AfterMapping;
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
public interface RoleMapper {

  RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

  @API(status = Status.STABLE, since = "1.0.1")
  Role toEntity(RoleDo roleDo);

  @API(status = Status.STABLE, since = "1.0.1")
  Role toEntity(RoleAddCo roleAddCo);

  @API(status = Status.STABLE, since = "1.0.1")
  Role toEntity(RoleFindAllQueryCo roleFindAllQueryCo);

  @API(status = Status.STABLE, since = "2.2.0")
  Role toEntity(RoleFindAllSliceQueryCo roleFindAllSliceQueryCo);

  @API(status = Status.STABLE, since = "2.2.0")
  Role toEntity(RoleArchivedFindAllQueryCo roleArchivedFindAllQueryCo);

  @API(status = Status.STABLE, since = "2.2.0")
  Role toEntity(RoleArchivedFindAllSliceQueryCo roleArchivedFindAllSliceQueryCo);

  @API(status = Status.STABLE, since = "2.2.0")
  Role toEntity(RoleRedisDo roleRedisDo);

  @API(status = Status.STABLE, since = "2.2.0")
  RoleRedisDo toRoleRedisDo(Role role);

  @API(status = Status.STABLE, since = "1.0.1")
  void toEntity(RoleUpdateCo roleUpdateCo, @MappingTarget Role role);

  @API(status = Status.STABLE, since = "1.0.1")
  RoleFindAllCo toFindAllCo(Role role);

  @API(status = Status.STABLE, since = "2.2.0")
  RoleFindAllSliceCo toFindAllSliceCo(Role role);

  @API(status = Status.STABLE, since = "2.2.0")
  RoleArchivedFindAllCo toArchivedFindAllCo(Role role);

  @API(status = Status.STABLE, since = "2.2.0")
  RoleArchivedFindAllSliceCo toArchivedFindAllSliceCo(Role role);

  @API(status = Status.STABLE, since = "1.0.1")
  RoleDo toDataObject(Role role);

  @API(status = Status.STABLE, since = "1.0.4")
  RoleArchivedDo toArchivedDo(RoleDo roleDo);

  @API(status = Status.STABLE, since = "2.2.0")
  RoleArchivedDo toArchivedDo(Role role);

  @API(status = Status.STABLE, since = "1.0.4")
  RoleDo toDataObject(RoleArchivedDo roleArchivedDo);

  @API(status = Status.STABLE, since = "1.0.4")
  Role toEntity(RoleArchivedDo roleArchivedDo);

  @AfterMapping
  default void convertToAccountTimezone(@MappingTarget RoleFindAllCo roleFindAllCo) {
    CommonUtil.convertToAccountZone(roleFindAllCo);
  }

  @AfterMapping
  default void convertToAccountTimezone(@MappingTarget RoleFindAllSliceCo roleFindAllSliceCo) {
    CommonUtil.convertToAccountZone(roleFindAllSliceCo);
  }

  @AfterMapping
  default void convertToAccountTimezone(
      @MappingTarget RoleArchivedFindAllSliceCo roleArchivedFindAllSliceCo) {
    CommonUtil.convertToAccountZone(roleArchivedFindAllSliceCo);
  }

  @AfterMapping
  default void convertToAccountTimezone(
      @MappingTarget RoleArchivedFindAllCo roleArchivedFindAllCo) {
    CommonUtil.convertToAccountZone(roleArchivedFindAllCo);
  }
}
