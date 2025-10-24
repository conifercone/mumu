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

package baby.mumu.genix.infra.captcha.convertor;

import baby.mumu.basis.mappers.GrpcMapper;
import baby.mumu.genix.client.api.grpc.CaptchaCodeVerifyGrpcCmd;
import baby.mumu.genix.client.cmds.CaptchaCodeGeneratedCmd;
import baby.mumu.genix.client.cmds.CaptchaCodeVerifyCmd;
import baby.mumu.genix.domain.captcha.CaptchaCode;
import baby.mumu.genix.infra.captcha.gatewayimpl.cache.po.CaptchaCodeCacheablePO;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * CaptchaCode mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CaptchaCodeMapper extends GrpcMapper {

  CaptchaCodeMapper INSTANCE = Mappers.getMapper(CaptchaCodeMapper.class);

  @API(status = Status.STABLE, since = "1.0.1")
  CaptchaCodeCacheablePO toCaptchaCodeCacheablePO(CaptchaCode captchaCode);

  @API(status = Status.STABLE, since = "1.0.1")
  CaptchaCode toEntity(CaptchaCodeGeneratedCmd captchaCodeGeneratedCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  CaptchaCode toEntity(CaptchaCodeVerifyCmd captchaCodeVerifyCmd);

  @API(status = Status.STABLE, since = "2.2.0")
  CaptchaCodeVerifyCmd toCaptchaCodeVerifyCmd(
    CaptchaCodeVerifyGrpcCmd captchaCodeVerifyGrpcCmd);

}
