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

package baby.mumu.genix.infra.captcha.gatewayimpl;

import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.genix.domain.captcha.CaptchaCode;
import baby.mumu.genix.domain.captcha.gateway.CaptchaCodeGateway;
import baby.mumu.genix.domain.pk.gateway.PrimaryKeyGateway;
import baby.mumu.genix.infra.captcha.convertor.CaptchaCodeConvertor;
import baby.mumu.genix.infra.captcha.gatewayimpl.cache.CaptchaCodeCacheRepository;
import baby.mumu.genix.infra.captcha.gatewayimpl.cache.po.CaptchaCodeCacheablePO;
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
public class CaptchaCodeGatewayImpl implements
  CaptchaCodeGateway {

  private final PrimaryKeyGateway primaryKeyGateway;
  private final CaptchaCodeCacheRepository captchaCodeCacheRepository;
  private final CaptchaCodeConvertor captchaCodeConvertor;

  @Autowired
  public CaptchaCodeGatewayImpl(PrimaryKeyGateway primaryKeyGateway,
    CaptchaCodeCacheRepository captchaCodeCacheRepository,
    CaptchaCodeConvertor captchaCodeConvertor) {
    this.primaryKeyGateway = primaryKeyGateway;
    this.captchaCodeCacheRepository = captchaCodeCacheRepository;
    this.captchaCodeConvertor = captchaCodeConvertor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CaptchaCode generate(CaptchaCode captchaCode) {
    CaptchaCodeCacheablePO captchaCodeCacheablePO = Optional.ofNullable(captchaCode)
      .flatMap(captchaCodeNotNull -> {
        captchaCodeNotNull.setId(primaryKeyGateway.snowflake());
        Optional.ofNullable(captchaCodeNotNull.getLength()).filter(length -> length > 0)
          .orElseThrow(() -> new ApplicationException(
            ResponseCode.CAPTCHA_CODE_LENGTH_NEEDS_TO_BE_GREATER_THAN_0));
        captchaCodeNotNull.setTarget(
          RandomStringUtils.secure().nextAlphanumeric(captchaCodeNotNull.getLength()));
        Optional.ofNullable(captchaCodeNotNull.getTtl()).orElseThrow(() -> new ApplicationException(
          ResponseCode.CAPTCHA_CODE_VALIDITY_PERIOD_CANNOT_BE_EMPTY));
        return captchaCodeConvertor.toCaptchaCodeCacheablePO(captchaCodeNotNull);
      }).orElseThrow(() -> new ApplicationException(ResponseCode.DATA_CONVERSION_FAILED));
    captchaCodeCacheRepository.save(captchaCodeCacheablePO);
    return captchaCode;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean verify(CaptchaCode captchaCode) {
    return Optional.ofNullable(captchaCode).flatMap(
        captchaCodeNotNull -> captchaCodeCacheRepository.findById(captchaCodeNotNull.getId()))
      .map(
        captchaCodeCacheablePO -> captchaCodeCacheablePO.getTarget()
          .equalsIgnoreCase(captchaCode.getSource()))
      .orElse(false);
  }
}
