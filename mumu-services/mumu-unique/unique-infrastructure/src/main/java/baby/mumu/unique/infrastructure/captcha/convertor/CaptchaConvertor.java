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
package baby.mumu.unique.infrastructure.captcha.convertor;

import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcCmd;
import baby.mumu.unique.client.cmds.SimpleCaptchaGeneratedCmd;
import baby.mumu.unique.client.cmds.SimpleCaptchaVerifyCmd;
import baby.mumu.unique.client.dto.SimpleCaptchaGeneratedDTO;
import baby.mumu.unique.domain.captcha.Captcha.SimpleCaptcha;
import baby.mumu.unique.infrastructure.captcha.gatewayimpl.cache.po.SimpleCaptchaPO;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;
import org.springframework.stereotype.Component;

/**
 * 验证码对象转换类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Component
public class CaptchaConvertor {


  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<SimpleCaptchaPO> toPO(SimpleCaptcha simpleCaptcha) {
    return Optional.ofNullable(simpleCaptcha).map(CaptchaMapper.INSTANCE::toPO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<SimpleCaptcha> toEntity(
    SimpleCaptchaGeneratedCmd simpleCaptchaGeneratedCmd) {
    return Optional.ofNullable(simpleCaptchaGeneratedCmd).map(CaptchaMapper.INSTANCE::toEntity);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<SimpleCaptcha> toEntity(
    SimpleCaptchaVerifyCmd simpleCaptchaVerifyCmd) {
    return Optional.ofNullable(simpleCaptchaVerifyCmd).map(CaptchaMapper.INSTANCE::toEntity);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<SimpleCaptchaGeneratedDTO> toSimpleCaptchaGeneratedDTO(
    SimpleCaptcha simpleCaptcha) {
    return Optional.ofNullable(simpleCaptcha)
      .map(CaptchaMapper.INSTANCE::toSimpleCaptchaGeneratedDTO);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<SimpleCaptchaVerifyCmd> toSimpleCaptchaVerifyCmd(
    SimpleCaptchaVerifyGrpcCmd simpleCaptchaVerifyGrpcCmd) {
    return Optional.ofNullable(simpleCaptchaVerifyGrpcCmd)
      .map(CaptchaMapper.INSTANCE::toSimpleCaptchaVerifyCmd);
  }
}
