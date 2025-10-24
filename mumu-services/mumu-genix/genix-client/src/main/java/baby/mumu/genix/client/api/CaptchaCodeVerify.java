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

package baby.mumu.genix.client.api;

import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.genix.client.api.grpc.CaptchaCodeVerifyGrpcCmd;
import java.util.Optional;

/**
 * 验证码校验
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.10.0
 */
public class CaptchaCodeVerify {

  private final CaptchaCodeGrpcService captchaCodeGrpcService;

  public CaptchaCodeVerify(CaptchaCodeGrpcService captchaCodeGrpcService) {
    this.captchaCodeGrpcService = captchaCodeGrpcService;
  }

  public void verify(Long id, String captchaCode) {
    Long captchaCodeId = Optional.ofNullable(id)
      .orElseThrow(
        () -> new ApplicationException(ResponseCode.CAPTCHA_CODE_ID_CANNOT_BE_EMPTY));
    String code = Optional.ofNullable(captchaCode)
      .orElseThrow(
        () -> new ApplicationException(ResponseCode.CAPTCHA_CODE_CANNOT_BE_EMPTY));
    try {
      if (!captchaCodeGrpcService.verify(
          CaptchaCodeVerifyGrpcCmd.newBuilder().setId(captchaCodeId).setSource(
            code).build())
        .getValue()) {
        throw new ApplicationException(ResponseCode.CAPTCHA_CODE_INCORRECT);
      }
    } catch (Exception e) {
      throw new ApplicationException(ResponseCode.CAPTCHA_CODE_VERIFICATION_EXCEPTION);
    }
  }
}
