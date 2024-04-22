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
import com.sky.centaur.authentication.client.dto.co.AuthorityDeleteCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityFindAllCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityUpdateCo;
import com.sky.centaur.authentication.domain.authority.Authority;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityDo;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityNodeDo;
import com.sky.centaur.basis.tools.SpringContextUtil;
import com.sky.centaur.unique.client.api.PrimaryKeyGrpcService;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;

/**
 * 权限信息转换器
 *
 * @author kaiyu.shan
 * @since 2024-02-23
 */
public class AuthorityConvertor {

  @Contract("_ -> new")
  public static @NotNull Authority toEntity(@NotNull AuthorityDo authorityDo) {
    return new Authority(authorityDo.getId(), authorityDo.getCode(), authorityDo.getName());
  }

  @Contract("_ -> new")
  public static @NotNull AuthorityDo toDataObject(@NotNull Authority authority) {
    AuthorityDo authorityDo = new AuthorityDo();
    BeanUtils.copyProperties(authority, authorityDo);
    return authorityDo;
  }

  @Contract("_ -> new")
  public static @NotNull AuthorityNodeDo toNodeDataObject(@NotNull Authority authority) {
    AuthorityNodeDo authorityNodeDo = new AuthorityNodeDo();
    BeanUtils.copyProperties(authority, authorityNodeDo);
    return authorityNodeDo;
  }

  public static @NotNull Authority toEntity(@NotNull AuthorityAddCo authorityAddCo) {
    Authority authority = new Authority();
    BeanUtils.copyProperties(authorityAddCo, authority);
    if (authority.getId() == null) {
      authority.setId(SpringContextUtil.getBean(PrimaryKeyGrpcService.class).snowflake());
    }
    return authority;
  }

  public static @NotNull Authority toEntity(@NotNull AuthorityUpdateCo authorityUpdateCo) {
    Authority authority = new Authority();
    BeanUtils.copyProperties(authorityUpdateCo, authority);
    return authority;
  }

  public static @NotNull Authority toEntity(@NotNull AuthorityFindAllCo authorityFindAllCo) {
    Authority authority = new Authority();
    BeanUtils.copyProperties(authorityFindAllCo, authority);
    return authority;
  }

  public static @NotNull Authority toEntity(@NotNull AuthorityDeleteCo authorityDeleteCo) {
    Authority authority = new Authority();
    BeanUtils.copyProperties(authorityDeleteCo, authority);
    return authority;
  }

  public static @NotNull AuthorityFindAllCo toFindAllCo(@NotNull Authority authority) {
    AuthorityFindAllCo authorityFindAllCo = new AuthorityFindAllCo();
    BeanUtils.copyProperties(authority, authorityFindAllCo);
    return authorityFindAllCo;
  }
}
