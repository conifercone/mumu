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

package com.sky.centaur.authentication.infrastructure.authority.convertor;

import com.sky.centaur.authentication.client.dto.co.AuthorityAddCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityFindAllCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityFindByIdCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityUpdateCo;
import com.sky.centaur.authentication.domain.authority.Authority;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.AuthorityRepository;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityArchivedDo;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityDo;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.extension.translation.SimpleTextTranslation;
import com.sky.centaur.unique.client.api.PrimaryKeyGrpcService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * 权限信息转换器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
@Validated
public class AuthorityConvertor {

  private final PrimaryKeyGrpcService primaryKeyGrpcService;
  private final AuthorityRepository authorityRepository;
  private final SimpleTextTranslation simpleTextTranslation;

  @Autowired
  public AuthorityConvertor(PrimaryKeyGrpcService primaryKeyGrpcService,
      AuthorityRepository authorityRepository,
      ObjectProvider<SimpleTextTranslation> simpleTextTranslation) {
    this.primaryKeyGrpcService = primaryKeyGrpcService;
    this.authorityRepository = authorityRepository;
    this.simpleTextTranslation = simpleTextTranslation.getIfAvailable();
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Authority> toEntity(@Valid AuthorityDo authorityDo) {
    return Optional.ofNullable(authorityDo).map(
        AuthorityMapper.INSTANCE::toEntity);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<AuthorityDo> toDataObject(@Valid Authority authority) {
    return Optional.ofNullable(authority).map(AuthorityMapper.INSTANCE::toDataObject);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Authority> toEntity(@Valid AuthorityAddCo authorityAddCo) {
    return Optional.ofNullable(authorityAddCo).map(authorityAddClientObject -> {
      Authority authority = AuthorityMapper.INSTANCE.toEntity(authorityAddClientObject);
      if (authority.getId() == null) {
        authority.setId(primaryKeyGrpcService.snowflake());
        authorityAddClientObject.setId(authority.getId());
      }
      return authority;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Authority> toEntity(@Valid AuthorityUpdateCo authorityUpdateCo) {
    return Optional.ofNullable(authorityUpdateCo).map(authorityUpdateClientObject -> {
      if (authorityUpdateClientObject.getId() == null) {
        throw new CentaurException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY);
      }
      return authorityRepository.findById(
              authorityUpdateClientObject.getId()).flatMap(this::toEntity)
          .map(authority -> {
            String codeBeforeUpdate = authority.getCode();
            AuthorityMapper.INSTANCE.toEntity(authorityUpdateClientObject, authority);
            String codeAfterUpdate = authority.getCode();
            if (StringUtils.hasText(codeAfterUpdate) && !codeAfterUpdate.equals(codeBeforeUpdate)
                && authorityRepository.existsByCode(
                codeAfterUpdate)) {
              throw new CentaurException(ResultCode.AUTHORITY_CODE_ALREADY_EXISTS);
            }
            return authority;
          }).orElse(null);
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Authority> toEntity(@Valid AuthorityFindAllCo authorityFindAllCo) {
    return Optional.ofNullable(authorityFindAllCo).map(AuthorityMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<AuthorityFindByIdCo> toFindByIdCo(@Valid Authority authority) {
    return Optional.ofNullable(authority).map(AuthorityMapper.INSTANCE::toFindByIdCo)
        .map(authorityFindByIdCo -> {
          Optional.ofNullable(simpleTextTranslation).flatMap(
                  simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                      authorityFindByIdCo.getName()))
              .ifPresent(authorityFindByIdCo::setName);
          return authorityFindByIdCo;
        });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<AuthorityFindAllCo> toFindAllCo(@Valid Authority authority) {
    return Optional.ofNullable(authority).map(AuthorityMapper.INSTANCE::toFindAllCo)
        .map(authorityFindAllCo -> {
          Optional.ofNullable(simpleTextTranslation).flatMap(
                  simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                      authorityFindAllCo.getName()))
              .ifPresent(authorityFindAllCo::setName);
          return authorityFindAllCo;
        });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<AuthorityArchivedDo> toArchivedDo(AuthorityDo authorityDo) {
    return Optional.ofNullable(authorityDo).map(AuthorityMapper.INSTANCE::toArchivedDo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<AuthorityDo> toDataObject(AuthorityArchivedDo authorityArchivedDo) {
    return Optional.ofNullable(authorityArchivedDo).map(AuthorityMapper.INSTANCE::toDataObject);
  }
}
