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

import baby.mumu.authentication.client.dto.co.AuthorityAddCo;
import baby.mumu.authentication.client.dto.co.AuthorityArchivedFindAllCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindAllCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindByIdCo;
import baby.mumu.authentication.client.dto.co.AuthorityUpdateCo;
import baby.mumu.authentication.domain.authority.Authority;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityArchivedDo;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityDo;
import baby.mumu.basis.kotlin.tools.CommonUtil;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * Authority mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AuthorityMapper {

  AuthorityMapper INSTANCE = Mappers.getMapper(AuthorityMapper.class);

  @API(status = Status.STABLE, since = "1.0.1")
  Authority toEntity(AuthorityDo authorityDo);

  @API(status = Status.STABLE, since = "1.0.1")
  Authority toEntity(AuthorityAddCo authorityAddCo);

  @API(status = Status.STABLE, since = "1.0.1")
  Authority toEntity(AuthorityFindAllCo authorityFindAllCo);

  @API(status = Status.STABLE, since = "2.0.0")
  Authority toEntity(AuthorityArchivedDo authorityArchivedDo);

  @API(status = Status.STABLE, since = "2.0.0")
  Authority toEntity(AuthorityArchivedFindAllCo authorityArchivedFindAllCo);

  @API(status = Status.STABLE, since = "1.0.1")
  void toEntity(AuthorityUpdateCo authorityUpdateCo, @MappingTarget Authority authority);

  @API(status = Status.STABLE, since = "1.0.1")
  AuthorityFindByIdCo toFindByIdCo(Authority authority);

  @API(status = Status.STABLE, since = "1.0.1")
  AuthorityFindAllCo toFindAllCo(Authority authority);

  @API(status = Status.STABLE, since = "2.0.0")
  AuthorityArchivedFindAllCo toArchivedFindAllCo(Authority authority);

  @API(status = Status.STABLE, since = "1.0.1")
  AuthorityDo toDataObject(Authority authority);

  @API(status = Status.STABLE, since = "1.0.4")
  AuthorityArchivedDo toArchivedDo(AuthorityDo authorityDo);

  @API(status = Status.STABLE, since = "1.0.4")
  AuthorityDo toDataObject(AuthorityArchivedDo authorityArchivedDo);

  @AfterMapping
  default void convertToAccountTimezone(@MappingTarget AuthorityFindByIdCo authorityFindByIdCo) {
    CommonUtil.convertToAccountZone(authorityFindByIdCo);
  }

  @AfterMapping
  default void convertToAccountTimezone(@MappingTarget AuthorityFindAllCo authorityFindAllCo) {
    CommonUtil.convertToAccountZone(authorityFindAllCo);
  }
}
