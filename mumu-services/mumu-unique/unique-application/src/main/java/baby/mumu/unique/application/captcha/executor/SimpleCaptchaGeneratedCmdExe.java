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
package baby.mumu.unique.application.captcha.executor;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.unique.client.dto.SimpleCaptchaGeneratedCmd;
import baby.mumu.unique.client.dto.co.SimpleCaptchaGeneratedCo;
import baby.mumu.unique.domain.captcha.gateway.CaptchaGateway;
import baby.mumu.unique.infrastructure.captcha.convertor.CaptchaConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 简单验证码生成指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Component
public class SimpleCaptchaGeneratedCmdExe {

  private final CaptchaGateway captchaGateway;
  private final CaptchaConvertor captchaConvertor;

  @Autowired
  public SimpleCaptchaGeneratedCmdExe(CaptchaGateway captchaGateway,
      CaptchaConvertor captchaConvertor) {
    this.captchaGateway = captchaGateway;
    this.captchaConvertor = captchaConvertor;
  }

  public SimpleCaptchaGeneratedCo execute(SimpleCaptchaGeneratedCmd simpleCaptchaGeneratedCmd) {
    Assert.notNull(simpleCaptchaGeneratedCmd, "SimpleCaptchaGeneratedCmd cannot be null");
    return captchaConvertor.toEntity(simpleCaptchaGeneratedCmd)
        .map(captchaGateway::generateSimpleCaptcha)
        .flatMap(captchaConvertor::toSimpleCaptchaGeneratedCo)
        .orElseThrow(() -> new MuMuException(
          ResponseCode.SIMPLE_CAPTCHA_GENERATION_FAILED));
  }
}
