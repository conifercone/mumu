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

package com.sky.centaur.unique.application.captcha.executor;

import com.sky.centaur.unique.client.dto.SimpleCaptchaVerifyCmd;
import com.sky.centaur.unique.domain.captcha.gateway.CaptchaGateway;
import com.sky.centaur.unique.infrastructure.captcha.convertor.CaptchaConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 简单验证码验证指令执行器
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
@Component
public class SimpleCaptchaVerifyCmdExe {

  private final CaptchaGateway captchaGateway;

  @Autowired
  public SimpleCaptchaVerifyCmdExe(CaptchaGateway captchaGateway) {
    this.captchaGateway = captchaGateway;
  }

  public boolean execute(SimpleCaptchaVerifyCmd simpleCaptchaVerifyCmd) {
    Assert.notNull(simpleCaptchaVerifyCmd, "SimpleCaptchaVerifyCmd cannot be null");
    return CaptchaConvertor.toEntity(simpleCaptchaVerifyCmd.getSimpleCaptchaVerifyCo())
        .map(captchaGateway::verifySimpleCaptcha)
        .orElse(false);
  }
}
