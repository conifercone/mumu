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
package baby.mumu.unique.client.api;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcCmd;
import java.util.Optional;

/**
 * 验证码校验
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.10.0
 */
public class CaptchaVerify {

  private final CaptchaGrpcService captchaGrpcService;

  public CaptchaVerify(CaptchaGrpcService captchaGrpcService) {
    this.captchaGrpcService = captchaGrpcService;
  }

  public void verifyCaptcha(Long captchaId, String captcha) {
    Long captchaIdNotNull = Optional.ofNullable(captchaId)
      .orElseThrow(() -> new MuMuException(ResponseCode.CAPTCHA_ID_CANNOT_BE_EMPTY));
    String captchaNotNull = Optional.ofNullable(captcha)
      .orElseThrow(() -> new MuMuException(ResponseCode.CAPTCHA_CANNOT_BE_EMPTY));
    try {
      if (!captchaGrpcService.verifySimpleCaptcha(
          SimpleCaptchaVerifyGrpcCmd.newBuilder().setId(captchaIdNotNull).setSource(
            captchaNotNull).build())
        .getResult()) {
        throw new MuMuException(ResponseCode.CAPTCHA_INCORRECT);
      }
    } catch (Exception e) {
      throw new MuMuException(ResponseCode.CAPTCHA_VERIFICATION_EXCEPTION);
    }
  }
}
