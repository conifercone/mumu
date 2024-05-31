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

import com.expediagroup.beans.transformer.BeanTransformer;
import com.sky.centaur.authentication.client.dto.co.AuthorityAddCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityFindAllCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityFindByIdCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityUpdateCo;
import com.sky.centaur.authentication.domain.authority.Authority;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.AuthorityRepository;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityDo;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.kotlin.tools.BeanUtil;
import com.sky.centaur.basis.kotlin.tools.SpringContextUtil;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.unique.client.api.PrimaryKeyGrpcService;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 权限信息转换器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public final class AuthorityConvertor {

  private static final BeanTransformer BEAN_TRANSFORMER = new com.expediagroup.beans.BeanUtils().getTransformer()
      .setDefaultValueForMissingField(true)
      .setDefaultValueForMissingPrimitiveField(false);

  private AuthorityConvertor() {
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public static @NotNull Authority toEntity(@NotNull AuthorityDo authorityDo) {
    return new Authority(authorityDo.getId(), authorityDo.getCode(), authorityDo.getName());
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.0")
  public static @NotNull AuthorityDo toDataObject(@NotNull Authority authority) {
    return BEAN_TRANSFORMER.transform(authority, AuthorityDo.class);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static @NotNull Authority toEntity(@NotNull AuthorityAddCo authorityAddCo) {
    Authority authority = BEAN_TRANSFORMER.transform(authorityAddCo, Authority.class);
    if (authority.getId() == null) {
      authority.setId(SpringContextUtil.getBean(PrimaryKeyGrpcService.class).snowflake());
      authorityAddCo.setId(authority.getId());
    }
    return authority;
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static @NotNull Authority toEntity(@NotNull AuthorityUpdateCo authorityUpdateCo) {
    AuthorityRepository authorityRepository = SpringContextUtil.getBean(AuthorityRepository.class);
    if (authorityUpdateCo.getId() == null) {
      throw new CentaurException(ResultCode.PRIMARY_KEY_CANNOT_BE_EMPTY);
    }
    Optional<AuthorityDo> authorityDoOptional = authorityRepository.findById(
        authorityUpdateCo.getId());
    if (authorityDoOptional.isPresent()) {
      Authority authority = toEntity(authorityDoOptional.get());
      BEAN_TRANSFORMER.skipTransformationForField(BeanUtil.getNullPropertyNames(authorityUpdateCo))
          .transform(authorityUpdateCo, authority);
      return authority;
    } else {
      throw new CentaurException(ResultCode.DATA_DOES_NOT_EXIST);
    }
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static @NotNull Authority toEntity(@NotNull AuthorityFindAllCo authorityFindAllCo) {
    return BEAN_TRANSFORMER.transform(authorityFindAllCo, Authority.class);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static @NotNull AuthorityFindByIdCo toFindByIdCo(@NotNull Authority authority) {
    return BEAN_TRANSFORMER.transform(authority, AuthorityFindByIdCo.class);
  }

  @API(status = Status.STABLE, since = "1.0.0")
  public static @NotNull AuthorityFindAllCo toFindAllCo(@NotNull Authority authority) {
    return BEAN_TRANSFORMER.transform(authority, AuthorityFindAllCo.class);
  }
}
