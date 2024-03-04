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
import com.sky.centaur.authentication.domain.authority.Authority;
import com.sky.centaur.authentication.domain.role.Role;
import com.sky.centaur.authentication.infrastructure.authority.convertor.AuthorityConvertor;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.AuthorityRepository;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityNodeDo;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleDo;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleNodeDo;
import com.sky.centaur.basis.tools.SpringContextUtil;
import com.sky.centaur.unique.client.api.PrimaryKeyGrpcService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;

/**
 * 角色信息转换器
 *
 * @author 单开宇
 * @since 2024-02-23
 */
public class RoleConvertor {

  @Contract("_ -> new")
  public static @NotNull Role toEntity(@NotNull RoleDo roleDo) {
    AuthorityRepository authorityRepository = SpringContextUtil.getBean(AuthorityRepository.class);
    return new Role(roleDo.getId(), roleDo.getCode(), roleDo.getName(),
        authorityRepository.findAuthorityDoByIdIn(
                roleDo.getAuthorities()).stream().map(AuthorityConvertor::toEntity)
            .collect(Collectors.toList()));
  }

  @Contract("_ -> new")
  public static @NotNull RoleDo toDataObject(@NotNull Role role) {
    RoleDo roleDo = new RoleDo();
    roleDo.setId(role.id());
    roleDo.setName(role.name());
    roleDo.setCode(role.code());
    if (!CollectionUtils.isEmpty(role.authorities())) {
      roleDo.setAuthorities(
          role.authorities().stream().map(Authority::getId).collect(Collectors.toList()));
    }
    return roleDo;
  }

  @Contract("_ -> new")
  public static @NotNull RoleNodeDo toNodeDataObject(@NotNull Role role) {
    RoleNodeDo roleNodeDo = new RoleNodeDo();
    roleNodeDo.setId(role.id());
    roleNodeDo.setCode(role.code());
    Optional.ofNullable(role.authorities()).ifPresent(
        authorities -> {
          List<AuthorityNodeDo> authorityNodeDos = authorities.stream()
              .map(AuthorityConvertor::toNodeDataObject).collect(Collectors.toList());
          roleNodeDo.setAuthorities(authorityNodeDos);
        });
    return roleNodeDo;
  }

  public static @NotNull Role toEntity(@NotNull RoleAddCo roleAddCo) {
    AuthorityRepository authorityRepository = SpringContextUtil.getBean(AuthorityRepository.class);
    return new Role(roleAddCo.getId() == null ?
        SpringContextUtil.getBean(PrimaryKeyGrpcService.class).snowflake()
        : roleAddCo.getId(), roleAddCo.getCode(), roleAddCo.getName(),
        authorityRepository.findAuthorityDoByIdIn(
                roleAddCo.getAuthorities()).stream().map(AuthorityConvertor::toEntity)
            .collect(Collectors.toList()));
  }
}
