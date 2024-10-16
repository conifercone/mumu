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
package baby.mumu.authentication.application;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResultCode;
import baby.mumu.unique.client.api.CaptchaGrpcService;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcCmd;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcCo;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 验证码校验
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
public class CaptchaVerify {

  private final CaptchaGrpcService captchaGrpcService;
  private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaVerify.class);

  public CaptchaVerify(CaptchaGrpcService captchaGrpcService) {
    this.captchaGrpcService = captchaGrpcService;
  }

  public void verifyCaptcha(Long captchaId, String captcha) {
    Long captchaIdNotNull = Optional.ofNullable(captchaId)
        .orElseThrow(() -> new MuMuException(ResultCode.CAPTCHA_ID_CANNOT_BE_EMPTY));
    String captchaNotNull = Optional.ofNullable(captcha)
        .orElseThrow(() -> new MuMuException(ResultCode.CAPTCHA_CANNOT_BE_EMPTY));
    try {
      if (!captchaGrpcService.verifySimpleCaptcha(
              SimpleCaptchaVerifyGrpcCmd.newBuilder().setSimpleCaptchaVerifyGrpcCo(
                  SimpleCaptchaVerifyGrpcCo.newBuilder()
                      .setId(Int64Value.of(captchaIdNotNull)).setSource(
                          StringValue.of(captchaNotNull)).build()).build())
          .getResult()) {
        throw new MuMuException(ResultCode.CAPTCHA_INCORRECT);
      }
    } catch (Exception e) {
      LOGGER.error(ResultCode.CAPTCHA_VERIFICATION_EXCEPTION.getResultMsg(), e);
      throw new MuMuException(ResultCode.CAPTCHA_VERIFICATION_EXCEPTION);
    }
  }
}
