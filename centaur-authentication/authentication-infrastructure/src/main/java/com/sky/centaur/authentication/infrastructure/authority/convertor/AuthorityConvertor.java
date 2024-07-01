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
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityDo;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.kotlin.tools.SpringContextUtil;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.unique.client.api.PrimaryKeyGrpcService;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.util.StringUtils;

/**
 * 权限信息转换器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public final class AuthorityConvertor {

  private AuthorityConvertor() {
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<Authority> toEntity(AuthorityDo authorityDo) {
    return Optional.ofNullable(authorityDo).map(
        AuthorityMapper.INSTANCE::toEntity);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<AuthorityDo> toDataObject(Authority authority) {
    return Optional.ofNullable(authority).map(AuthorityMapper.INSTANCE::toDataObject);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<Authority> toEntity(AuthorityAddCo authorityAddCo) {
    return Optional.ofNullable(authorityAddCo).map(authorityAddClientObject -> {
      Authority authority = AuthorityMapper.INSTANCE.toEntity(authorityAddClientObject);
      if (authority.getId() == null) {
        authority.setId(SpringContextUtil.getBean(PrimaryKeyGrpcService.class).snowflake());
        authorityAddClientObject.setId(authority.getId());
      }
      return authority;
    });
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<Authority> toEntity(AuthorityUpdateCo authorityUpdateCo) {
    return Optional.ofNullable(authorityUpdateCo).map(authorityUpdateClientObject -> {
      AuthorityRepository authorityRepository = SpringContextUtil.getBean(
          AuthorityRepository.class);
      if (authorityUpdateClientObject.getId() == null) {
        throw new CentaurException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY);
      }
      return authorityRepository.findById(
              authorityUpdateClientObject.getId()).flatMap(AuthorityConvertor::toEntity)
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
  public static Optional<Authority> toEntity(AuthorityFindAllCo authorityFindAllCo) {
    return Optional.ofNullable(authorityFindAllCo).map(AuthorityMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<AuthorityFindByIdCo> toFindByIdCo(Authority authority) {
    return Optional.ofNullable(authority).map(AuthorityMapper.INSTANCE::toFindByIdCo);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static Optional<AuthorityFindAllCo> toFindAllCo(Authority authority) {
    return Optional.ofNullable(authority).map(AuthorityMapper.INSTANCE::toFindAllCo);
  }
}
