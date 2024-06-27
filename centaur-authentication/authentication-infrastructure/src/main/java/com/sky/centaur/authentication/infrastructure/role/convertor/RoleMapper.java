/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
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
package com.sky.centaur.authentication.infrastructure.role.convertor;

import com.sky.centaur.authentication.client.dto.co.RoleAddCo;
import com.sky.centaur.authentication.client.dto.co.RoleFindAllCo;
import com.sky.centaur.authentication.client.dto.co.RoleUpdateCo;
import com.sky.centaur.authentication.domain.role.Role;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleDo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * Role mapstruct转换器
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {

  RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

  @Mappings(value = {
      @Mapping(target = "authorities", ignore = true)
  })
  Role toEntity(RoleDo roleDo);

  @Mappings(value = {
      @Mapping(target = "authorities", ignore = true)
  })
  Role toEntity(RoleAddCo roleAddCo);

  @Mappings(value = {
      @Mapping(target = "authorities", ignore = true)
  })
  Role toEntity(RoleFindAllCo roleFindAllCo);

  @Mappings(value = {
      @Mapping(target = "authorities", ignore = true)
  })
  void toEntity(RoleUpdateCo roleUpdateCo, @MappingTarget Role role);

  @Mappings(value = {
      @Mapping(target = "authorities", ignore = true)
  })
  RoleFindAllCo toFindAllCo(Role role);

  @Mappings(value = {
      @Mapping(target = "authorities", ignore = true),
      @Mapping(target = "users", ignore = true)
  })
  RoleDo toDataObject(Role role);
}
