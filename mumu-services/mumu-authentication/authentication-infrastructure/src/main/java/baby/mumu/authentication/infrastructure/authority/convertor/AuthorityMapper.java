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

import baby.mumu.authentication.client.api.grpc.AuthorityFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AuthorityFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.AuthorityFindByIdGrpcCo;
import baby.mumu.authentication.client.dto.AuthorityAddCmd;
import baby.mumu.authentication.client.dto.AuthorityArchivedFindAllCmd;
import baby.mumu.authentication.client.dto.AuthorityArchivedFindAllSliceCmd;
import baby.mumu.authentication.client.dto.AuthorityFindAllCmd;
import baby.mumu.authentication.client.dto.AuthorityFindAllSliceCmd;
import baby.mumu.authentication.client.dto.AuthorityUpdateCmd;
import baby.mumu.authentication.client.dto.co.AuthorityArchivedFindAllCo;
import baby.mumu.authentication.client.dto.co.AuthorityArchivedFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindAllCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindByIdCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindDirectCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindRootCo;
import baby.mumu.authentication.domain.authority.Authority;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityArchivedDo;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityDo;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.redis.dataobject.AuthorityRedisDo;
import baby.mumu.basis.kotlin.tools.CommonUtil;
import baby.mumu.basis.mappers.GrpcMapper;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Authority mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorityMapper extends GrpcMapper {

  AuthorityMapper INSTANCE = Mappers.getMapper(AuthorityMapper.class);

  @API(status = Status.STABLE, since = "1.0.1")
  Authority toEntity(AuthorityDo authorityDo);

  @API(status = Status.STABLE, since = "1.0.1")
  Authority toEntity(AuthorityAddCmd authorityAddCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  Authority toEntity(AuthorityFindAllCmd authorityFindAllCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  Authority toEntity(AuthorityFindAllSliceCmd authorityFindAllSliceCmd);

  @API(status = Status.STABLE, since = "2.0.0")
  Authority toEntity(AuthorityArchivedDo authorityArchivedDo);

  @API(status = Status.STABLE, since = "2.2.0")
  Authority toEntity(AuthorityRedisDo authorityRedisDo);

  @API(status = Status.STABLE, since = "2.2.0")
  AuthorityRedisDo toAuthorityRedisDo(Authority authority);

  @API(status = Status.STABLE, since = "2.2.0")
  AuthorityDo toDataObject(AuthorityRedisDo authorityRedisDo);

  @API(status = Status.STABLE, since = "2.0.0")
  Authority toEntity(AuthorityArchivedFindAllCmd authorityArchivedFindAllCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  Authority toEntity(AuthorityArchivedFindAllSliceCmd authorityArchivedFindAllSliceCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  void toEntity(AuthorityUpdateCmd authorityUpdateCmd, @MappingTarget Authority authority);

  @API(status = Status.STABLE, since = "1.0.1")
  AuthorityFindByIdCo toFindByIdCo(Authority authority);

  @API(status = Status.STABLE, since = "1.0.1")
  AuthorityFindAllCo toFindAllCo(Authority authority);

  @API(status = Status.STABLE, since = "2.2.0")
  AuthorityFindAllSliceCo toFindAllSliceCo(Authority authority);

  @API(status = Status.STABLE, since = "2.0.0")
  AuthorityArchivedFindAllCo toArchivedFindAllCo(Authority authority);

  @API(status = Status.STABLE, since = "2.2.0")
  AuthorityArchivedFindAllSliceCo toArchivedFindAllSliceCo(Authority authority);

  @API(status = Status.STABLE, since = "1.0.1")
  AuthorityDo toDataObject(Authority authority);

  @API(status = Status.STABLE, since = "1.0.4")
  AuthorityArchivedDo toArchivedDo(AuthorityDo authorityDo);

  @API(status = Status.STABLE, since = "2.2.0")
  AuthorityArchivedDo toArchivedDo(Authority authority);

  @API(status = Status.STABLE, since = "1.0.4")
  AuthorityDo toDataObject(AuthorityArchivedDo authorityArchivedDo);

  @API(status = Status.STABLE, since = "2.2.0")
  AuthorityFindAllCmd toAuthorityFindAllCmd(AuthorityFindAllGrpcCmd authorityFindAllGrpcCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  AuthorityFindAllGrpcCo toAuthorityFindAllGrpcCo(AuthorityFindAllCo authorityFindAllCo);

  @API(status = Status.STABLE, since = "2.3.0")
  AuthorityFindByIdGrpcCo toAuthorityFindByIdGrpcCo(AuthorityFindByIdCo authorityFindByIdCo);

  @API(status = Status.STABLE, since = "2.3.0")
  AuthorityFindRootCo toAuthorityFindRootCo(Authority authority);

  @API(status = Status.STABLE, since = "2.3.0")
  AuthorityFindDirectCo toAuthorityFindDirectCo(Authority authority);

  @AfterMapping
  default void convertToAccountTimezone(@MappingTarget AuthorityFindByIdCo authorityFindByIdCo) {
    CommonUtil.convertToAccountZone(authorityFindByIdCo);
  }

  @AfterMapping
  default void convertToAccountTimezone(
    @MappingTarget AuthorityFindDirectCo authorityFindDirectCo) {
    CommonUtil.convertToAccountZone(authorityFindDirectCo);
  }


  @AfterMapping
  default void convertToAccountTimezone(@MappingTarget AuthorityFindRootCo authorityFindRootCo) {
    CommonUtil.convertToAccountZone(authorityFindRootCo);
  }

  @AfterMapping
  default void convertToAccountTimezone(@MappingTarget AuthorityFindAllCo authorityFindAllCo) {
    CommonUtil.convertToAccountZone(authorityFindAllCo);
  }

  @AfterMapping
  default void convertToAccountTimezone(
    @MappingTarget AuthorityFindAllSliceCo authorityFindAllSliceCo) {
    CommonUtil.convertToAccountZone(authorityFindAllSliceCo);
  }

  @AfterMapping
  default void convertToAccountTimezone(
    @MappingTarget AuthorityArchivedFindAllSliceCo authorityArchivedFindAllSliceCo) {
    CommonUtil.convertToAccountZone(authorityArchivedFindAllSliceCo);
  }
}
