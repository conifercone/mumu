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

  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<Role> toEntity(RoleDo roleDo) {
    return Optional.ofNullable(roleDo).map(roleDataObject -> {
      AuthorityRepository authorityRepository = SpringContextUtil.getBean(
          AuthorityRepository.class);
      Role role = BEAN_TRANSFORMER
          .skipTransformationForField("authorities")
          .transform(roleDataObject, Role.class);
      Optional.ofNullable(roleDataObject.getAuthorities())
          .filter(authorityList -> !CollectionUtils.isEmpty(
              authorityList)).ifPresent(authorities -> role.setAuthorities(
              authorityRepository.findAuthorityDoByIdIn(authorities).stream()
                  .map(AuthorityConvertor::toEntity)
                  .collect(Collectors.toList())));
      return role;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<RoleDo> toDataObject(Role role) {
    return Optional.ofNullable(role).map(roleDomain -> {
      RoleDo roleDo = BEAN_TRANSFORMER
          .skipTransformationForField("authorities")
          .transform(roleDomain, RoleDo.class);
      if (!CollectionUtils.isEmpty(roleDomain.getAuthorities())) {
        roleDo.setAuthorities(
            roleDomain.getAuthorities().stream().map(Authority::getId)
                .collect(Collectors.toList()));
      }
      return roleDo;
    });
  }


  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<Role> toEntity(RoleAddCo roleAddCo) {
    return Optional.ofNullable(roleAddCo).map(roleAddClientObject -> {
      AuthorityRepository authorityRepository = SpringContextUtil.getBean(
          AuthorityRepository.class);
      Role role = BEAN_TRANSFORMER
          .skipTransformationForField("authorities")
          .transform(roleAddClientObject, Role.class);
      if (role.getId() == null) {
        role.setId(SpringContextUtil.getBean(PrimaryKeyGrpcService.class).snowflake());
        roleAddClientObject.setId(role.getId());
      }
      role.setAuthorities(authorityRepository.findAuthorityDoByIdIn(
              roleAddClientObject.getAuthorities()).stream().map(AuthorityConvertor::toEntity)
          .collect(Collectors.toList()));
      return role;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<Role> toEntity(RoleUpdateCo roleUpdateCo) {
    return Optional.ofNullable(roleUpdateCo).map(roleUpdateClientObject -> {
      Optional.ofNullable(roleUpdateClientObject.getId())
          .orElseThrow(() -> new CentaurException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
      AuthorityRepository authorityRepository = SpringContextUtil.getBean(
          AuthorityRepository.class);
      RoleRepository roleRepository = SpringContextUtil.getBean(RoleRepository.class);
      Optional<RoleDo> roleDoOptional = roleRepository.findById(roleUpdateClientObject.getId());
      return roleDoOptional.flatMap(roleDo -> toEntity(roleDo).map(roleDomain -> {
        Optional.ofNullable(roleUpdateClientObject.getName()).ifPresent(roleDomain::setName);
        Optional.ofNullable(roleUpdateClientObject.getCode()).ifPresent(roleDomain::setCode);
        Optional.ofNullable(roleUpdateClientObject.getAuthorities())
            .ifPresent(authorities -> roleDomain.setAuthorities(
                authorityRepository.findAuthorityDoByIdIn(
                        authorities).stream().map(AuthorityConvertor::toEntity)
                    .collect(Collectors.toList())));
        return roleDomain;
      })).orElse(null);
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<Role> toEntity(RoleFindAllCo roleFindAllCo) {
    return Optional.ofNullable(roleFindAllCo).map(roleFindAllClientObject -> {
      AuthorityRepository authorityRepository = SpringContextUtil.getBean(
          AuthorityRepository.class);
      Role role = BEAN_TRANSFORMER
          .skipTransformationForField("authorities")
          .transform(roleFindAllClientObject, Role.class);
      if (!CollectionUtils.isEmpty(roleFindAllClientObject.getAuthorities())) {
        role.setAuthorities(
            authorityRepository.findAuthorityDoByIdIn(roleFindAllClientObject.getAuthorities())
                .stream()
                .map(AuthorityConvertor::toEntity).collect(
                    Collectors.toList()));
      }
      return role;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<RoleFindAllCo> toFindAllCo(Role role) {
    return Optional.ofNullable(role).map(roleDomain -> {
      RoleFindAllCo roleFindAllCo = BEAN_TRANSFORMER
          .skipTransformationForField("authorities")
          .transform(roleDomain, RoleFindAllCo.class);
      if (!CollectionUtils.isEmpty(roleDomain.getAuthorities())) {
        roleFindAllCo.setAuthorities(
            roleDomain.getAuthorities().stream().map(Authority::getId).collect(
                Collectors.toList()));
      }
      return roleFindAllCo;
    });

  }
}
