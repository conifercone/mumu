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
package baby.mumu.unique.infrastructure.captcha.convertor;

import baby.mumu.basis.mappers.GrpcMapper;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcCmd;
import baby.mumu.unique.client.dto.SimpleCaptchaGeneratedCmd;
import baby.mumu.unique.client.dto.SimpleCaptchaVerifyCmd;
import baby.mumu.unique.client.dto.co.SimpleCaptchaGeneratedCo;
import baby.mumu.unique.domain.captcha.Captcha.SimpleCaptcha;
import baby.mumu.unique.infrastructure.captcha.gatewayimpl.redis.dataobject.SimpleCaptchaDo;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Captcha mapstruct转换器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CaptchaMapper extends GrpcMapper {

  CaptchaMapper INSTANCE = Mappers.getMapper(CaptchaMapper.class);

  @API(status = Status.STABLE, since = "1.0.1")
  SimpleCaptchaDo toDataObject(SimpleCaptcha simpleCaptcha);

  @API(status = Status.STABLE, since = "1.0.1")
  SimpleCaptcha toEntity(SimpleCaptchaGeneratedCmd simpleCaptchaGeneratedCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  SimpleCaptcha toEntity(SimpleCaptchaVerifyCmd simpleCaptchaVerifyCmd);

  @API(status = Status.STABLE, since = "1.0.1")
  SimpleCaptchaGeneratedCo toSimpleCaptchaGeneratedCo(SimpleCaptcha simpleCaptcha);

  @API(status = Status.STABLE, since = "2.2.0")
  SimpleCaptchaVerifyCmd toSimpleCaptchaVerifyCmd(
    SimpleCaptchaVerifyGrpcCmd simpleCaptchaVerifyGrpcCmd);

}
