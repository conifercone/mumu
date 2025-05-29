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
package baby.mumu.unique.infrastructure.captcha.gatewayimpl;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.unique.domain.captcha.Captcha.SimpleCaptcha;
import baby.mumu.unique.domain.captcha.gateway.CaptchaGateway;
import baby.mumu.unique.domain.pk.gateway.PrimaryKeyGateway;
import baby.mumu.unique.infrastructure.captcha.convertor.CaptchaConvertor;
import baby.mumu.unique.infrastructure.captcha.gatewayimpl.cache.SimpleCaptchaCacheRepository;
import baby.mumu.unique.infrastructure.captcha.gatewayimpl.cache.po.SimpleCaptchaPO;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 验证码领域网关实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Component
public class CaptchaGatewayImpl implements CaptchaGateway {

  private final PrimaryKeyGateway primaryKeyGateway;
  private final SimpleCaptchaCacheRepository simpleCaptchaCacheRepository;
  private final CaptchaConvertor captchaConvertor;

  @Autowired
  public CaptchaGatewayImpl(PrimaryKeyGateway primaryKeyGateway,
    SimpleCaptchaCacheRepository simpleCaptchaCacheRepository,
    CaptchaConvertor captchaConvertor) {
    this.primaryKeyGateway = primaryKeyGateway;
    this.simpleCaptchaCacheRepository = simpleCaptchaCacheRepository;
    this.captchaConvertor = captchaConvertor;
  }

  @Override
  public SimpleCaptcha generateSimpleCaptcha(SimpleCaptcha simpleCaptcha) {
    SimpleCaptchaPO simpleCaptchaPO = Optional.ofNullable(simpleCaptcha)
      .flatMap(simpleCaptchaDomain -> {
        simpleCaptchaDomain.setId(primaryKeyGateway.snowflake());
        Optional.ofNullable(simpleCaptchaDomain.getLength()).filter(length -> length > 0)
          .orElseThrow(() -> new MuMuException(
            ResponseCode.SIMPLE_CAPTCHA_LENGTH_NEEDS_TO_BE_GREATER_THAN_0));
        simpleCaptchaDomain.setTarget(
          RandomStringUtils.secure().nextAlphanumeric(simpleCaptchaDomain.getLength()));
        Optional.ofNullable(simpleCaptchaDomain.getTtl()).orElseThrow(() -> new MuMuException(
          ResponseCode.SIMPLE_CAPTCHA_VALIDITY_PERIOD_CANNOT_BE_EMPTY));
        return captchaConvertor.toPO(simpleCaptchaDomain);
      }).orElseThrow(() -> new MuMuException(ResponseCode.DATA_CONVERSION_FAILED));
    simpleCaptchaCacheRepository.save(simpleCaptchaPO);
    return simpleCaptcha;
  }

  @Override
  public boolean verifySimpleCaptcha(SimpleCaptcha simpleCaptcha) {
    return Optional.ofNullable(simpleCaptcha).flatMap(
        simpleCaptchaDomain -> simpleCaptchaCacheRepository.findById(simpleCaptchaDomain.getId()))
      .map(
        simpleCaptchaPO -> simpleCaptchaPO.getTarget().equalsIgnoreCase(simpleCaptcha.getSource()))
      .orElse(false);
  }
}
