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

import com.expediagroup.beans.BeanUtils;
import com.expediagroup.beans.transformer.BeanTransformer;
import com.sky.centaur.authentication.client.dto.co.RoleAddCo;
import com.sky.centaur.authentication.client.dto.co.RoleFindAllCo;
import com.sky.centaur.authentication.client.dto.co.RoleUpdateCo;
import com.sky.centaur.authentication.domain.authority.Authority;
import com.sky.centaur.authentication.domain.role.Role;
import com.sky.centaur.authentication.infrastructure.authority.convertor.AuthorityConvertor;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.AuthorityRepository;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.RoleRepository;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleDo;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.kotlin.tools.SpringContextUtil;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.unique.client.api.PrimaryKeyGrpcService;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;

/**
 * 角色信息转换器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public final class RoleConvertor {

  private static final BeanTransformer BEAN_TRANSFORMER = new BeanUtils().getTransformer()
      .setDefaultValueForMissingField(true)
      .setDefaultValueForMissingPrimitiveField(false);

  private RoleConvertor() {
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public static @NotNull Role toEntity(@NotNull RoleDo roleDo) {
    //noinspection DuplicatedCode
    AuthorityRepository authorityRepository = SpringContextUtil.getBean(AuthorityRepository.class);
    Role role = BEAN_TRANSFORMER
        .skipTransformationForField("authorities")
        .transform(roleDo, Role.class);
    Optional.ofNullable(roleDo.getAuthorities()).filter(authorityList -> !CollectionUtils.isEmpty(
        authorityList)).ifPresent(authorities -> role.setAuthorities(
        authorityRepository.findAuthorityDoByIdIn(authorities).stream()
            .map(AuthorityConvertor::toEntity)
            .collect(Collectors.toList())));
    return role;
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public static @NotNull RoleDo toDataObject(@NotNull Role role) {
    RoleDo roleDo = BEAN_TRANSFORMER
        .skipTransformationForField("authorities")
        .transform(role, RoleDo.class);
    if (!CollectionUtils.isEmpty(role.getAuthorities())) {
      roleDo.setAuthorities(
          role.getAuthorities().stream().map(Authority::getId).collect(Collectors.toList()));
    }
    return roleDo;
  }


  @API(status = Status.STABLE, since = "1.0.0")
  public static @NotNull Role toEntity(@NotNull RoleAddCo roleAddCo) {
    AuthorityRepository authorityRepository = SpringContextUtil.getBean(AuthorityRepository.class);
    Role role = BEAN_TRANSFORMER
        .skipTransformationForField("authorities")
        .transform(roleAddCo, Role.class);
    if (role.getId() == null) {
      role.setId(SpringContextUtil.getBean(PrimaryKeyGrpcService.class).snowflake());
      roleAddCo.setId(role.getId());
    }
    role.setAuthorities(authorityRepository.findAuthorityDoByIdIn(
            roleAddCo.getAuthorities()).stream().map(AuthorityConvertor::toEntity)
        .collect(Collectors.toList()));
    return role;
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static @NotNull Role toEntity(@NotNull RoleUpdateCo roleUpdateCo) {
    if (roleUpdateCo.getId() == null) {
      throw new CentaurException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY);
    }
    //noinspection DuplicatedCode
    AuthorityRepository authorityRepository = SpringContextUtil.getBean(AuthorityRepository.class);
    RoleRepository roleRepository = SpringContextUtil.getBean(RoleRepository.class);
    Optional<RoleDo> roleDoOptional = roleRepository.findById(roleUpdateCo.getId());
    if (roleDoOptional.isPresent()) {
      Role role = toEntity(roleDoOptional.get());
      Optional.ofNullable(roleUpdateCo.getName()).ifPresent(role::setName);
      Optional.ofNullable(roleUpdateCo.getCode()).ifPresent(role::setCode);
      Optional.ofNullable(roleUpdateCo.getAuthorities())
          .ifPresent(authorities -> role.setAuthorities(authorityRepository.findAuthorityDoByIdIn(
                  authorities).stream().map(AuthorityConvertor::toEntity)
              .collect(Collectors.toList())));
      return role;
    } else {
      throw new CentaurException(ResultCode.DATA_DOES_NOT_EXIST);
    }
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static @NotNull Role toEntity(@NotNull RoleFindAllCo roleFindAllCo) {
    AuthorityRepository authorityRepository = SpringContextUtil.getBean(AuthorityRepository.class);
    Role role = BEAN_TRANSFORMER
        .skipTransformationForField("authorities")
        .transform(roleFindAllCo, Role.class);
    if (!CollectionUtils.isEmpty(roleFindAllCo.getAuthorities())) {
      role.setAuthorities(
          authorityRepository.findAuthorityDoByIdIn(roleFindAllCo.getAuthorities()).stream()
              .map(AuthorityConvertor::toEntity).collect(
                  Collectors.toList()));
    }
    return role;
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static @NotNull RoleFindAllCo toFindAllCo(@NotNull Role role) {
    RoleFindAllCo roleFindAllCo = BEAN_TRANSFORMER
        .skipTransformationForField("authorities")
        .transform(role, RoleFindAllCo.class);
    if (!CollectionUtils.isEmpty(role.getAuthorities())) {
      roleFindAllCo.setAuthorities(role.getAuthorities().stream().map(Authority::getId).collect(
          Collectors.toList()));
    }
    return roleFindAllCo;
  }
}
