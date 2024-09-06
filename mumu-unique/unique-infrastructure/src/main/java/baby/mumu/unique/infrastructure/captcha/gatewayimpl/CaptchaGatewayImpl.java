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
package baby.mumu.unique.infrastructure.captcha.gatewayimpl;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.kotlin.tools.CommonUtil;
import baby.mumu.basis.response.ResultCode;
import baby.mumu.unique.domain.captcha.Captcha.SimpleCaptcha;
import baby.mumu.unique.domain.captcha.gateway.CaptchaGateway;
import baby.mumu.unique.domain.pk.gateway.PrimaryKeyGateway;
import baby.mumu.unique.infrastructure.captcha.convertor.CaptchaConvertor;
import baby.mumu.unique.infrastructure.captcha.gatewayimpl.redis.SimpleCaptchaRepository;
import baby.mumu.unique.infrastructure.captcha.gatewayimpl.redis.dataobject.SimpleCaptchaDo;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 验证码领域网关实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Component
public class CaptchaGatewayImpl implements CaptchaGateway {

  private final PrimaryKeyGateway primaryKeyGateway;
  private final SimpleCaptchaRepository simpleCaptchaRepository;
  private final CaptchaConvertor captchaConvertor;

  @Autowired
  public CaptchaGatewayImpl(PrimaryKeyGateway primaryKeyGateway,
      SimpleCaptchaRepository simpleCaptchaRepository, CaptchaConvertor captchaConvertor) {
    this.primaryKeyGateway = primaryKeyGateway;
    this.simpleCaptchaRepository = simpleCaptchaRepository;
    this.captchaConvertor = captchaConvertor;
  }

  @Override
  public SimpleCaptcha generateSimpleCaptcha(SimpleCaptcha simpleCaptcha) {
    SimpleCaptchaDo simpleCaptchaDo = Optional.ofNullable(simpleCaptcha)
        .flatMap(simpleCaptchaDomain -> {
          Optional.ofNullable(simpleCaptchaDomain.getId()).ifPresentOrElse(id -> {
            if (simpleCaptchaRepository.existsById(id)) {
              throw new MuMuException(ResultCode.DATA_ALREADY_EXISTS);
            }
          }, () -> simpleCaptchaDomain.setId(primaryKeyGateway.snowflake()));
          Optional.ofNullable(simpleCaptchaDomain.getLength()).filter(length -> length > 0)
              .orElseThrow(() -> new MuMuException(
                  ResultCode.SIMPLE_CAPTCHA_LENGTH_NEEDS_TO_BE_GREATER_THAN_0));
          if (StringUtils.isBlank(simpleCaptchaDomain.getTarget())) {
            simpleCaptchaDomain.setTarget(
                CommonUtil.generateRandomString(simpleCaptchaDomain.getLength()));
          }
          Optional.ofNullable(simpleCaptchaDomain.getTtl()).orElseThrow(() -> new MuMuException(
              ResultCode.SIMPLE_CAPTCHA_VALIDITY_PERIOD_CANNOT_BE_EMPTY));
          return captchaConvertor.toDataObject(simpleCaptchaDomain);
        }).orElseThrow(() -> new MuMuException(ResultCode.DATA_CONVERSION_FAILED));
    simpleCaptchaRepository.save(simpleCaptchaDo);
    return simpleCaptcha;
  }

  @Override
  public boolean verifySimpleCaptcha(SimpleCaptcha simpleCaptcha) {
    return Optional.ofNullable(simpleCaptcha).flatMap(
            simpleCaptchaDomain -> simpleCaptchaRepository.findById(simpleCaptchaDomain.getId()))
        .map(simpleCaptchaDo -> simpleCaptchaDo.getTarget().equals(simpleCaptcha.getSource()))
        .orElse(false);
  }
}
