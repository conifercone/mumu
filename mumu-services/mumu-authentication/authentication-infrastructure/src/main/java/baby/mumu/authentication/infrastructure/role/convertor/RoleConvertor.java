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
import baby.mumu.authentication.domain.authority.Authority;
import baby.mumu.authentication.domain.role.Role;
import baby.mumu.authentication.infrastructure.authority.convertor.AuthorityConvertor;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.AuthorityRepository;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.redis.AuthorityRedisRepository;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.redis.dataobject.AuthorityRedisDo;
import baby.mumu.authentication.infrastructure.relations.database.RoleAuthorityDo;
import baby.mumu.authentication.infrastructure.relations.database.RoleAuthorityDoId;
import baby.mumu.authentication.infrastructure.relations.database.RoleAuthorityRepository;
import baby.mumu.authentication.infrastructure.relations.redis.RoleAuthorityRedisDo;
import baby.mumu.authentication.infrastructure.relations.redis.RoleAuthorityRedisRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.RoleArchivedRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.RoleRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleArchivedDo;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleDo;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.redis.dataobject.RoleRedisDo;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResultCode;
import baby.mumu.extension.translation.SimpleTextTranslation;
import baby.mumu.unique.client.api.PrimaryKeyGrpcService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
  private final RoleAuthorityRepository roleAuthorityRepository;
  private final AuthorityRedisRepository authorityRedisRepository;
  private final RoleAuthorityRedisRepository roleAuthorityRedisRepository;

  @Autowired
  public RoleConvertor(AuthorityConvertor authorityConvertor, RoleRepository roleRepository,
      AuthorityRepository authorityRepository, PrimaryKeyGrpcService primaryKeyGrpcService,
      ObjectProvider<SimpleTextTranslation> simpleTextTranslation,
      RoleArchivedRepository roleArchivedRepository,
      RoleAuthorityRepository roleAuthorityRepository,
      AuthorityRedisRepository authorityRedisRepository,
      RoleAuthorityRedisRepository roleAuthorityRedisRepository) {
    this.authorityConvertor = authorityConvertor;
    this.roleRepository = roleRepository;
    this.authorityRepository = authorityRepository;
    this.primaryKeyGrpcService = primaryKeyGrpcService;
    this.simpleTextTranslation = simpleTextTranslation.getIfAvailable();
    this.roleArchivedRepository = roleArchivedRepository;
    this.roleAuthorityRepository = roleAuthorityRepository;
    this.authorityRedisRepository = authorityRedisRepository;
    this.roleAuthorityRedisRepository = roleAuthorityRedisRepository;
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Role> toEntity(RoleDo roleDo) {
    //noinspection DuplicatedCode
    return Optional.ofNullable(roleDo).map(roleDataObject -> {
      Role role = RoleMapper.INSTANCE.toEntity(roleDataObject);
      setAuthorities(role, getAuthorityIds(role));
      return role;
    });
  }

  private @NotNull List<Long> getAuthorityIds(Role role) {
    return roleAuthorityRedisRepository.findById(role.getId())
        .map(RoleAuthorityRedisDo::getAuthorityIds).orElseGet(() -> {
          List<Long> authorityIds = roleAuthorityRepository.findByRoleId(role.getId()).stream()
              .map(RoleAuthorityDo::getId)
              .map(RoleAuthorityDoId::getAuthorityId).distinct().collect(Collectors.toList());
          roleAuthorityRedisRepository.save(new RoleAuthorityRedisDo(role.getId(), authorityIds));
          return authorityIds;
        });
  }

  private void setAuthorities(Role role, List<Long> authorityIds) {
    // 查询缓存中存在的数据
    List<AuthorityRedisDo> authorityRedisDos = authorityRedisRepository.findAllById(
        authorityIds);
    // 缓存中存在的权限ID
    List<Long> cachedCollectionOfAuthorityIDs = authorityRedisDos.stream()
        .map(AuthorityRedisDo::getId)
        .collect(Collectors.toList());
    // 已缓存的权限
    List<Authority> cachedCollectionOfAuthority = authorityRedisDos.stream()
        .flatMap(authorityRedisDo -> authorityConvertor.toEntity(authorityRedisDo).stream())
        .collect(
            Collectors.toList());
    // 未缓存的权限
    List<Authority> uncachedCollectionOfAuthority = Optional.of(
            CollectionUtils.subtract(authorityIds, cachedCollectionOfAuthorityIDs))
        .filter(CollectionUtils::isNotEmpty).map(
            uncachedCollectionOfAuthorityId -> authorityRepository.findAllById(
                    uncachedCollectionOfAuthorityId)
                .stream()
                .flatMap(authorityDo -> authorityConvertor.toEntity(authorityDo).stream())
                .collect(
                    Collectors.toList())).orElse(new ArrayList<>());
    // 未缓存的权限放入缓存
    if (CollectionUtils.isNotEmpty(uncachedCollectionOfAuthority)) {
      authorityRedisRepository.saveAll(uncachedCollectionOfAuthority.stream()
          .flatMap(authority -> authorityConvertor.toAuthorityRedisDo(authority).stream())
          .collect(
              Collectors.toList()));
    }
    // 合并已缓存和未缓存的权限
    role.setAuthorities(new ArrayList<>(
        CollectionUtils.union(cachedCollectionOfAuthority, uncachedCollectionOfAuthority)));
  }

  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<Role> toEntity(RoleArchivedDo roleArchivedDo) {
    //noinspection DuplicatedCode
    return Optional.ofNullable(roleArchivedDo).map(roleArchivedDataObject -> {
      Role role = RoleMapper.INSTANCE.toEntity(roleArchivedDataObject);
      setAuthorities(role, getAuthorityIds(role));
      return role;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<RoleDo> toDataObject(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toDataObject);
  }


  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Role> toEntity(RoleAddCo roleAddCo) {
    return Optional.ofNullable(roleAddCo).map(roleAddClientObject -> {
      Role role = RoleMapper.INSTANCE.toEntity(roleAddClientObject);
      if (role.getId() == null) {
        role.setId(primaryKeyGrpcService.snowflake());
        roleAddClientObject.setId(role.getId());
      }
      Optional.ofNullable(roleAddClientObject.getAuthorityIds())
          .filter(CollectionUtils::isNotEmpty)
          .ifPresent(authorityIds -> setAuthorities(role, authorityIds));
      return role;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Role> toEntity(RoleUpdateCo roleUpdateCo) {
    return Optional.ofNullable(roleUpdateCo).flatMap(roleUpdateClientObject -> {
      Optional.ofNullable(roleUpdateClientObject.getId())
          .orElseThrow(() -> new MuMuException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY));
      Optional<RoleDo> roleDoOptional = roleRepository.findById(roleUpdateClientObject.getId());
      return roleDoOptional.flatMap(roleDo -> toEntity(roleDo).map(roleDomain -> {
        String codeBeforeUpdated = roleDomain.getCode();
        RoleMapper.INSTANCE.toEntity(roleUpdateClientObject, roleDomain);
        String codeAfterUpdated = roleDomain.getCode();
        if (StringUtils.isNotBlank(codeAfterUpdated) && !codeAfterUpdated.equals(codeBeforeUpdated)
            && (roleRepository.existsByCode(codeAfterUpdated)
            || roleArchivedRepository.existsByCode(codeAfterUpdated))) {
          throw new MuMuException(ResultCode.ROLE_CODE_ALREADY_EXISTS);
        }
        Optional.ofNullable(roleUpdateClientObject.getAuthorityIds())
            .ifPresent(authorities -> setAuthorities(roleDomain, authorities));
        return roleDomain;
      }));
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Role> toEntity(RoleFindAllQueryCo roleFindAllQueryCo) {
    return Optional.ofNullable(roleFindAllQueryCo).map(RoleMapper.INSTANCE::toEntity).map(role -> {
      if (CollectionUtils.isNotEmpty(roleFindAllQueryCo.getAuthorityIds())) {
        setAuthorities(role, roleFindAllQueryCo.getAuthorityIds());
      }
      return role;
    });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Role> toEntity(RoleFindAllSliceQueryCo roleFindAllSliceQueryCo) {
    return Optional.ofNullable(roleFindAllSliceQueryCo).map(RoleMapper.INSTANCE::toEntity)
        .map(role -> {
          if (CollectionUtils.isNotEmpty(roleFindAllSliceQueryCo.getAuthorityIds())) {
            setAuthorities(role, roleFindAllSliceQueryCo.getAuthorityIds());
          }
          return role;
        });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<RoleFindAllCo> toFindAllCo(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toFindAllCo).map(roleFindAllCo -> {
      Optional.ofNullable(simpleTextTranslation).flatMap(
              simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                  roleFindAllCo.getName()))
          .ifPresent(roleFindAllCo::setName);
      return roleFindAllCo;
    });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<RoleFindAllSliceCo> toFindAllSliceCo(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toFindAllSliceCo)
        .map(roleFindAllSliceCo -> {
          Optional.ofNullable(simpleTextTranslation).flatMap(
                  simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                      roleFindAllSliceCo.getName()))
              .ifPresent(roleFindAllSliceCo::setName);
          return roleFindAllSliceCo;
        });
  }


  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Role> toEntity(RoleArchivedFindAllQueryCo roleArchivedFindAllQueryCo) {
    return Optional.ofNullable(roleArchivedFindAllQueryCo).map(RoleMapper.INSTANCE::toEntity)
        .map(role -> {
          if (CollectionUtils.isNotEmpty(roleArchivedFindAllQueryCo.getAuthorityIds())) {
            setAuthorities(role, roleArchivedFindAllQueryCo.getAuthorityIds());
          }
          return role;
        });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Role> toEntity(RoleArchivedFindAllSliceQueryCo roleArchivedFindAllSliceQueryCo) {
    return Optional.ofNullable(roleArchivedFindAllSliceQueryCo).map(RoleMapper.INSTANCE::toEntity)
        .map(role -> {
          if (CollectionUtils.isNotEmpty(roleArchivedFindAllSliceQueryCo.getAuthorityIds())) {
            setAuthorities(role, roleArchivedFindAllSliceQueryCo.getAuthorityIds());
          }
          return role;
        });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Role> toEntity(RoleRedisDo roleRedisDo) {
    return Optional.ofNullable(roleRedisDo).map(RoleMapper.INSTANCE::toEntity)
        .map(role -> {
          setAuthorities(role, getAuthorityIds(role));
          return role;
        });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<RoleArchivedFindAllCo> toArchivedFindAllCo(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toArchivedFindAllCo)
        .map(roleArchivedFindAllCo -> {
          Optional.ofNullable(simpleTextTranslation).flatMap(
                  simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                      roleArchivedFindAllCo.getName()))
              .ifPresent(roleArchivedFindAllCo::setName);
          return roleArchivedFindAllCo;
        });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<RoleArchivedFindAllSliceCo> toArchivedFindAllSliceCo(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toArchivedFindAllSliceCo)
        .map(roleArchivedFindAllSliceCo -> {
          Optional.ofNullable(simpleTextTranslation).flatMap(
                  simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                      roleArchivedFindAllSliceCo.getName()))
              .ifPresent(roleArchivedFindAllSliceCo::setName);
          return roleArchivedFindAllSliceCo;
        });
  }


  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<RoleArchivedDo> toArchivedDo(RoleDo roleDo) {
    return Optional.ofNullable(roleDo).map(RoleMapper.INSTANCE::toArchivedDo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<RoleRedisDo> toRoleRedisDo(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toRoleRedisDo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<RoleArchivedDo> toArchivedDo(Role role) {
    return Optional.ofNullable(role).map(RoleMapper.INSTANCE::toArchivedDo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<RoleDo> toDataObject(RoleArchivedDo roleArchivedDo) {
    return Optional.ofNullable(roleArchivedDo).map(RoleMapper.INSTANCE::toDataObject);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.1.0")
  public List<RoleAuthorityDo> toRoleAuthorityDos(Role role) {
    return Optional.ofNullable(role).flatMap(roleNonNull -> Optional.ofNullable(
        roleNonNull.getAuthorities())).map(authorities -> authorities.stream().map(authority -> {
      RoleAuthorityDo roleAuthorityDo = new RoleAuthorityDo();
      roleAuthorityDo.setId(RoleAuthorityDoId.builder().roleId(role.getId()).authorityId(
          authority.getId()).build());
      roleRepository.findById(role.getId()).ifPresent(roleAuthorityDo::setRole);
      authorityRepository.findById(authority.getId()).ifPresent(roleAuthorityDo::setAuthority);
      return roleAuthorityDo;
    }).toList()).orElse(new ArrayList<>());
  }
}
