/*
 * Copyright (c) 2024-2025, the original author or authors.
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

package baby.mumu.genix.infra.verification.convertor;

import baby.mumu.basis.mappers.GrpcMapper;
import baby.mumu.genix.client.api.grpc.VerifyCodeVerifyGrpcCmd;
import baby.mumu.genix.client.cmds.VerifyCodeGeneratedCmd;
import baby.mumu.genix.client.cmds.VerifyCodeVerifyCmd;
import baby.mumu.genix.domain.verification.VerifyCode;
import baby.mumu.genix.infra.verification.gatewayimpl.cache.po.VerifyCodeCacheablePO;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * VerifyCode mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VerifyCodeMapper extends GrpcMapper {

  VerifyCodeMapper INSTANCE = Mappers.getMapper(VerifyCodeMapper.class);

  @API(status = Status.STABLE, since = "1.0.1")
  VerifyCodeCacheablePO toVerifyCodeCacheablePO(VerifyCode verifyCode);

  @API(status = Status.STABLE, since = "1.0.1")
  VerifyCode toEntity(VerifyCodeGeneratedCmd verifyCodeGeneratedCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  VerifyCode toEntity(VerifyCodeVerifyCmd verifyCodeVerifyCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  VerifyCodeVerifyCmd toVerifyCodeVerifyCmd(
    VerifyCodeVerifyGrpcCmd verifyCodeVerifyGrpcCmd);

}
