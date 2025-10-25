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
import com.google.protobuf.Int64Value;
import java.util.Optional;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 验证码校验
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.10.0
 */
public class CaptchaCodeVerify {

  private final CaptchaCodeGrpcService captchaCodeGrpcService;
  private static final Logger log = LoggerFactory.getLogger(
    CaptchaCodeVerify.class);

  public CaptchaCodeVerify(CaptchaCodeGrpcService captchaCodeGrpcService) {
    this.captchaCodeGrpcService = captchaCodeGrpcService;
  }

  /**
   * 验证验证码并执行后续逻辑，后续逻辑成功后删除验证码
   *
   * @param id          验证码ID
   * @param captchaCode 验证码
   * @param onSuccess   验证成功后的逻辑，返回T
   * @param <T>         返回值类型
   * @return onSuccess执行结果
   */
  public <T> T verify(Long id, String captchaCode, Supplier<T> onSuccess) {
    Long captchaCodeId = Optional.ofNullable(id)
      .orElseThrow(() -> new ApplicationException(ResponseCode.CAPTCHA_CODE_ID_CANNOT_BE_EMPTY));
    String code = Optional.ofNullable(captchaCode)
      .orElseThrow(() -> new ApplicationException(ResponseCode.CAPTCHA_CODE_CANNOT_BE_EMPTY));

    boolean verified;
    try {
      verified = captchaCodeGrpcService.verify(
        CaptchaCodeVerifyGrpcCmd.newBuilder()
          .setId(captchaCodeId)
          .setSource(code)
          .build()
      ).getValue();
    } catch (Exception e) {
      throw new ApplicationException(ResponseCode.CAPTCHA_CODE_VERIFICATION_EXCEPTION, e);
    }

    if (!verified) {
      throw new ApplicationException(ResponseCode.CAPTCHA_CODE_INCORRECT);
    }

    // 验证成功，执行业务逻辑
    T result = onSuccess.get();

    // 业务逻辑成功后删除验证码
    try {
      captchaCodeGrpcService.delete(Int64Value.of(captchaCodeId));
    } catch (Exception e) {
      // 删除失败不影响业务成功，可记录日志
      CaptchaCodeVerify.log.warn("Failed to delete captcha code: {}", captchaCodeId, e);
    }

    return result;
  }

  // 无返回值
  public void verify(Long id, String captchaCode, Runnable onSuccess) {
    verify(id, captchaCode, () -> {
      onSuccess.run();
      return null;
    });
  }
}
