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
package baby.mumu.authentication.infrastructure.authority.convertor;

import baby.mumu.authentication.client.dto.AuthorityArchivedFindAllCmd;
import baby.mumu.authentication.client.dto.AuthorityArchivedFindAllSliceCmd;
import baby.mumu.authentication.client.dto.AuthorityFindAllCmd;
import baby.mumu.authentication.client.dto.AuthorityFindAllSliceCmd;
import baby.mumu.authentication.client.dto.co.AuthorityAddCo;
import baby.mumu.authentication.client.dto.co.AuthorityArchivedFindAllCo;
import baby.mumu.authentication.client.dto.co.AuthorityArchivedFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindAllCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindByIdCo;
import baby.mumu.authentication.client.dto.co.AuthorityUpdateCo;
import baby.mumu.authentication.domain.authority.Authority;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.AuthorityArchivedRepository;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.AuthorityRepository;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityArchivedDo;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityDo;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.redis.dataobject.AuthorityRedisDo;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResultCode;
import baby.mumu.extension.translation.SimpleTextTranslation;
import baby.mumu.unique.client.api.PrimaryKeyGrpcService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 权限信息转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
public class AuthorityConvertor {

  private final PrimaryKeyGrpcService primaryKeyGrpcService;
  private final AuthorityRepository authorityRepository;
  private final SimpleTextTranslation simpleTextTranslation;
  private final AuthorityArchivedRepository authorityArchivedRepository;

  @Autowired
  public AuthorityConvertor(PrimaryKeyGrpcService primaryKeyGrpcService,
      AuthorityRepository authorityRepository,
      ObjectProvider<SimpleTextTranslation> simpleTextTranslation,
      AuthorityArchivedRepository authorityArchivedRepository) {
    this.primaryKeyGrpcService = primaryKeyGrpcService;
    this.authorityRepository = authorityRepository;
    this.simpleTextTranslation = simpleTextTranslation.getIfAvailable();
    this.authorityArchivedRepository = authorityArchivedRepository;
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Authority> toEntity(AuthorityDo authorityDo) {
    return Optional.ofNullable(authorityDo).map(
        AuthorityMapper.INSTANCE::toEntity);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<AuthorityDo> toDataObject(Authority authority) {
    return Optional.ofNullable(authority).map(AuthorityMapper.INSTANCE::toDataObject);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Authority> toEntity(AuthorityAddCo authorityAddCo) {
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
  public Optional<Authority> toEntity(AuthorityUpdateCo authorityUpdateCo) {
    return Optional.ofNullable(authorityUpdateCo).map(authorityUpdateClientObject -> {
      if (authorityUpdateClientObject.getId() == null) {
        throw new MuMuException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY);
      }
      return authorityRepository.findById(
              authorityUpdateClientObject.getId()).flatMap(this::toEntity)
          .map(authority -> {
            String codeBeforeUpdate = authority.getCode();
            AuthorityMapper.INSTANCE.toEntity(authorityUpdateClientObject, authority);
            String codeAfterUpdate = authority.getCode();
            if (StringUtils.isNotBlank(codeAfterUpdate) && !codeAfterUpdate.equals(codeBeforeUpdate)
                && (authorityRepository.existsByCode(
                codeAfterUpdate) || authorityArchivedRepository.existsByCode(
                codeAfterUpdate))) {
              throw new MuMuException(ResultCode.AUTHORITY_CODE_ALREADY_EXISTS);
            }
            return authority;
          }).orElse(null);
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<Authority> toEntity(AuthorityFindAllCmd authorityFindAllCmd) {
    return Optional.ofNullable(authorityFindAllCmd).map(AuthorityMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Authority> toEntity(AuthorityFindAllSliceCmd authorityFindAllSliceCmd) {
    return Optional.ofNullable(authorityFindAllSliceCmd)
        .map(AuthorityMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<Authority> toEntity(AuthorityArchivedDo authorityArchivedDo) {
    return Optional.ofNullable(authorityArchivedDo).map(AuthorityMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Authority> toEntity(AuthorityRedisDo authorityRedisDo) {
    return Optional.ofNullable(authorityRedisDo).map(AuthorityMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AuthorityRedisDo> toAuthorityRedisDo(Authority authority) {
    return Optional.ofNullable(authority).map(AuthorityMapper.INSTANCE::toAuthorityRedisDo);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AuthorityDo> toDataObject(AuthorityRedisDo authorityRedisDo) {
    return Optional.ofNullable(authorityRedisDo).map(AuthorityMapper.INSTANCE::toDataObject);
  }

  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<Authority> toEntity(
      @Valid AuthorityArchivedFindAllCmd authorityArchivedFindAllCmd) {
    return Optional.ofNullable(authorityArchivedFindAllCmd)
        .map(AuthorityMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<Authority> toEntity(
      @Valid AuthorityArchivedFindAllSliceCmd authorityArchivedFindAllSliceCmd) {
    return Optional.ofNullable(authorityArchivedFindAllSliceCmd)
        .map(AuthorityMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public Optional<AuthorityFindByIdCo> toFindByIdCo(Authority authority) {
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
  public Optional<AuthorityFindAllCo> toFindAllCo(Authority authority) {
    return Optional.ofNullable(authority).map(AuthorityMapper.INSTANCE::toFindAllCo)
        .map(authorityFindAllCo -> {
          Optional.ofNullable(simpleTextTranslation).flatMap(
                  simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                      authorityFindAllCo.getName()))
              .ifPresent(authorityFindAllCo::setName);
          return authorityFindAllCo;
        });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AuthorityFindAllSliceCo> toFindAllSliceCo(Authority authority) {
    return Optional.ofNullable(authority).map(AuthorityMapper.INSTANCE::toFindAllSliceCo)
        .map(authorityFindAllSliceCo -> {
          Optional.ofNullable(simpleTextTranslation).flatMap(
                  simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                      authorityFindAllSliceCo.getName()))
              .ifPresent(authorityFindAllSliceCo::setName);
          return authorityFindAllSliceCo;
        });
  }

  @API(status = Status.STABLE, since = "2.0.0")
  public Optional<AuthorityArchivedFindAllCo> toArchivedFindAllCo(Authority authority) {
    return Optional.ofNullable(authority).map(AuthorityMapper.INSTANCE::toArchivedFindAllCo)
        .map(authorityArchivedFindAllCo -> {
          Optional.ofNullable(simpleTextTranslation).flatMap(
                  simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                      authorityArchivedFindAllCo.getName()))
              .ifPresent(authorityArchivedFindAllCo::setName);
          return authorityArchivedFindAllCo;
        });
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AuthorityArchivedFindAllSliceCo> toArchivedFindAllSliceCo(Authority authority) {
    return Optional.ofNullable(authority).map(AuthorityMapper.INSTANCE::toArchivedFindAllSliceCo)
        .map(authorityArchivedFindAllSliceCo -> {
          Optional.ofNullable(simpleTextTranslation).flatMap(
                  simpleTextTranslationBean -> simpleTextTranslationBean.translateToAccountLanguageIfPossible(
                      authorityArchivedFindAllSliceCo.getName()))
              .ifPresent(authorityArchivedFindAllSliceCo::setName);
          return authorityArchivedFindAllSliceCo;
        });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<AuthorityArchivedDo> toArchivedDo(AuthorityDo authorityDo) {
    return Optional.ofNullable(authorityDo).map(AuthorityMapper.INSTANCE::toArchivedDo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<AuthorityArchivedDo> toArchivedDo(Authority authority) {
    return Optional.ofNullable(authority).map(AuthorityMapper.INSTANCE::toArchivedDo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.4")
  public Optional<AuthorityDo> toDataObject(AuthorityArchivedDo authorityArchivedDo) {
    return Optional.ofNullable(authorityArchivedDo).map(AuthorityMapper.INSTANCE::toDataObject);
  }
}
