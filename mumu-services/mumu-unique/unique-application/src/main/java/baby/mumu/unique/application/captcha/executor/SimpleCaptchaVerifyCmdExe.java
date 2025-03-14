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
package baby.mumu.unique.application.captcha.executor;

import baby.mumu.unique.client.cmds.SimpleCaptchaVerifyCmd;
import baby.mumu.unique.domain.captcha.gateway.CaptchaGateway;
import baby.mumu.unique.infrastructure.captcha.convertor.CaptchaConvertor;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 简单验证码验证指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 1.0.1
 */
@Component
public class SimpleCaptchaVerifyCmdExe {

  private final CaptchaGateway captchaGateway;
  private final CaptchaConvertor captchaConvertor;

  @Autowired
  public SimpleCaptchaVerifyCmdExe(CaptchaGateway captchaGateway,
    CaptchaConvertor captchaConvertor) {
    this.captchaGateway = captchaGateway;
    this.captchaConvertor = captchaConvertor;
  }

  public boolean execute(SimpleCaptchaVerifyCmd simpleCaptchaVerifyCmd) {
    return Optional.ofNullable(simpleCaptchaVerifyCmd).flatMap(captchaConvertor::toEntity)
      .map(captchaGateway::verifySimpleCaptcha)
      .orElse(false);
  }
}
