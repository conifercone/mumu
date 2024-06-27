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
package com.sky.centaur.authentication.mapstruct;

import com.sky.centaur.authentication.client.dto.co.AuthorityUpdateCo;
import com.sky.centaur.authentication.domain.authority.Authority;
import com.sky.centaur.authentication.infrastructure.authority.convertor.AuthorityMapper;
import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityDo;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

/**
 * AuthorityMapper单元测试
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
public class AuthorityMapperTest {

  @Test
  public void toEntity() {
    AuthorityMapper instance = AuthorityMapper.INSTANCE;
    AuthorityDo authorityDo = new AuthorityDo();
    authorityDo.setId(1L);
    authorityDo.setCode("test");
    authorityDo.setModifier(1L);
    authorityDo.setModificationTime(OffsetDateTime.now());
    Authority entity = instance.toEntity(authorityDo);
    System.out.println(entity);
  }

  @Test
  public void toEntityForExistObject() {
    AuthorityMapper instance = AuthorityMapper.INSTANCE;
    AuthorityUpdateCo authorityUpdateCo = new AuthorityUpdateCo();
    authorityUpdateCo.setId(1L);
    authorityUpdateCo.setCode("test");
    Authority authority = new Authority();
    authority.setId(2L);
    authority.setName("test");
    instance.toEntity(authorityUpdateCo, authority);
    System.out.println(authority);
  }
}
