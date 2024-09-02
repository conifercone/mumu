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
import com.sky.centaur.authentication.domain.authority.Authority;
import com.sky.centaur.authentication.domain.role.Role;
import com.sky.centaur.authentication.infrastructure.authority.convertor.AuthorityConvertor;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.AuthorityRepository;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.RoleArchivedRepository;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.RoleRepository;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleArchivedDo;
import com.sky.centaur.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleDo;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.extension.translation.SimpleTextTranslation;
import com.sky.centaur.unique.client.api.PrimaryKeyGrpcService;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 角色信息转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
public class RoleConvertor {

  private final AuthorityConvertor authorityConvertor;
  private final RoleRepository roleRepository;
  private final AuthorityRepository authorityRepository;
  private final PrimaryKeyGrpcService primaryKeyGrpcService;
  private final SimpleTextTranslation simpleTextTranslation;
  private final RoleArchivedRepository roleArchivedRepository;

  @Autowired
  public RoleConvertor(AuthorityConvertor authorityConvertor, RoleRepository roleRepository,
      AuthorityRepository authorityRepository, PrimaryKeyGrpcService primaryKeyGrpcService,
      ObjectProvider<SimpleTextTranslation> simpleTextTranslation,
      RoleArchivedRepository roleArchivedRepository) {
    this.authorityConvertor = authorityConvertor;
    this.roleRepository = roleRepository;
    this.authorityRepository = authorityRepository;
    this.primaryKeyGrpcService = primaryKeyGrpcService;
    this.simpleTextTranslation = simpleTextTranslation.getIfAvailable();
    this.roleArchivedRepository = roleArchivedRepository;
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Role> toEntity(RoleDo roleDo) {
    //noinspection DuplicatedCode
    return Optional.ofNullable(roleDo).map(roleDataObject -> {
      Role role = RoleMapper.INSTANCE.toEntity(roleDataObject);
      Optional.ofNullable(roleDataObject.getAuthorities())
          .filter(authorityList -> !CollectionUtils.isEmpty(
              authorityList)).ifPresent(authorities -> role.setAuthorities(
              authorityRepository.findAuthorityDoByIdIn(authorities).stream()
                  .map(authorityConvertor::toEntity)
                  .filter(Optional::isPresent).map(Optional::get)
                  .collect(Collectors.toList())));
      return role;
    });
  }

  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<Role> toEntity(RoleArchivedDo roleArchivedDo) {
    //noinspection DuplicatedCode
    return Optional.ofNullable(roleArchivedDo).map(roleArchivedDataObject -> {
      Role role = RoleMapper.INSTANCE.toEntity(roleArchivedDataObject);
      Optional.ofNullable(roleArchivedDataObject.getAuthorities())
          .filter(authorityList -> !CollectionUtils.isEmpty(
              authorityList)).ifPresent(authorities -> role.setAuthorities(
              authorityRepository.findAuthorityDoByIdIn(authorities).stream()
                  .map(authorityConvertor::toEntity)
                  .filter(Optional::isPresent).map(Optional::get)
                  .collect(Collectors.toList())));
      return role;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<RoleDo> toDataObject(Role role) {
    return Optional.ofNullable(role).map(roleDomain -> {
      RoleDo roleDo = RoleMapper.INSTANCE.toDataObject(roleDomain);
      if (!CollectionUtils.isEmpty(roleDomain.getAuthorities())) {
        roleDo.setAuthorities(
            roleDomain.getAuthorities().stream().map(Authority::getId)
                .collect(Collectors.toList()));
      }
      return roleDo;
    });
  }


  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Role> toEntity(RoleAddCo roleAddCo) {
    return Optional.ofNullable(roleAddCo).map(roleAddClientObject -> {
      Role role = RoleMapper.INSTANCE.toEntity(roleAddClientObject);
      if (role.getId() == null) {
        role.setId(primaryKeyGrpcService.snowflake());
        roleAddClientObject.setId(role.getId());
      }
      role.setAuthorities(authorityRepository.findAuthorityDoByIdIn(
              roleAddClientObject.getAuthorities()).stream()
          .map(authorityConvertor::toEntity)
          .filter(Optional::isPresent).map(Optional::get)
          .collect(Collectors.toList()));
      return role;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Role> toEntity(RoleUpdateCo roleUpdateCo) {
    return Optional.ofNullable(roleUpdateCo).map(roleUpdateClientObject -> {
      Optional.ofNullable(roleUpdateClientObject.getId())
          .orElseThrow(() -> new CentaurException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
      Optional<RoleDo> roleDoOptional = roleRepository.findById(roleUpdateClientObject.getId());
      return roleDoOptional.flatMap(roleDo -> toEntity(roleDo).map(roleDomain -> {
        String codeBeforeUpdated = roleDomain.getCode();
        RoleMapper.INSTANCE.toEntity(roleUpdateClientObject, roleDomain);
        String codeAfterUpdated = roleDomain.getCode();
        if (StringUtils.isNotBlank(codeAfterUpdated) && !codeAfterUpdated.equals(codeBeforeUpdated)
            && (roleRepository.existsByCode(codeAfterUpdated)
            || roleArchivedRepository.existsByCode(codeAfterUpdated))) {
          throw new CentaurException(ResultCode.ROLE_CODE_ALREADY_EXISTS);
        }
        Optional.ofNullable(roleUpdateClientObject.getAuthorities())
            .ifPresent(authorities -> roleDomain.setAuthorities(
                authorityRepository.findAuthorityDoByIdIn(
                        authorities).stream()
                    .map(authorityConvertor::toEntity)
                    .filter(Optional::isPresent).map(Optional::get)
                    .collect(Collectors.toList())));
        return roleDomain;
      })).orElse(null);
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Role> toEntity(RoleFindAllCo roleFindAllCo) {
    return Optional.ofNullable(roleFindAllCo).map(roleFindAllClientObject -> {
      Role role = RoleMapper.INSTANCE.toEntity(roleFindAllClientObject);
      if (!CollectionUtils.isEmpty(roleFindAllClientObject.getAuthorities())) {
        role.setAuthorities(
            authorityRepository.findAuthorityDoByIdIn(roleFindAllClientObject.getAuthorities())
                .stream()
                .map(authorityConvertor::toEntity)
                .filter(Optional::isPresent).map(Optional::get).collect(
                    Collectors.toList()));
      }
      return role;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<RoleFindAllCo> toFindAllCo(Role role) {
    return Optional.ofNullable(role).map(roleDomain -> {
      RoleFindAllCo roleFindAllCo = RoleMapper.INSTANCE.toFindAllCo(roleDomain);
      if (!CollectionUtils.isEmpty(roleDomain.getAuthorities())) {
        roleFindAllCo.setAuthorities(
            roleDomain.getAuthorities().stream().map(Authority::getId).collect(
                Collectors.toList()));
      }
      return roleFindAllCo;
    }).map(roleFindAllCo -> {
      Optional.ofNullable(simpleTextTranslation).flatMap(
              simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                  roleFindAllCo.getName()))
          .ifPresent(roleFindAllCo::setName);
      return roleFindAllCo;
    });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<RoleArchivedDo> toArchivedDo(RoleDo roleDo) {
    return Optional.ofNullable(roleDo).map(RoleMapper.INSTANCE::toArchivedDo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<RoleDo> toDataObject(RoleArchivedDo roleArchivedDo) {
    return Optional.ofNullable(roleArchivedDo).map(RoleMapper.INSTANCE::toDataObject);
  }
}
