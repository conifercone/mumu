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
package com.sky.centaur.unique.infrastructure.captcha.gatewayimpl;

import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.kotlin.tools.CommonUtil;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.unique.domain.captcha.Captcha.SimpleCaptcha;
import com.sky.centaur.unique.domain.captcha.gateway.CaptchaGateway;
import com.sky.centaur.unique.domain.pk.gateway.PrimaryKeyGateway;
import com.sky.centaur.unique.infrastructure.captcha.convertor.CaptchaConvertor;
import com.sky.centaur.unique.infrastructure.captcha.gatewayimpl.redis.SimpleCaptchaRepository;
import com.sky.centaur.unique.infrastructure.captcha.gatewayimpl.redis.dataobject.SimpleCaptchaDo;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 验证码领域网关实现
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
@Component
public class CaptchaGatewayImpl implements CaptchaGateway {

  private final PrimaryKeyGateway primaryKeyGateway;
  private final SimpleCaptchaRepository simpleCaptchaRepository;

  @Autowired
  public CaptchaGatewayImpl(PrimaryKeyGateway primaryKeyGateway,
      SimpleCaptchaRepository simpleCaptchaRepository) {
    this.primaryKeyGateway = primaryKeyGateway;
    this.simpleCaptchaRepository = simpleCaptchaRepository;
  }

  @Override
  public SimpleCaptcha generateSimpleCaptcha(SimpleCaptcha simpleCaptcha) {
    SimpleCaptchaDo simpleCaptchaDo = Optional.ofNullable(simpleCaptcha)
        .flatMap(simpleCaptchaDomain -> {
          Optional.ofNullable(simpleCaptchaDomain.getId()).ifPresentOrElse(id -> {
            if (simpleCaptchaRepository.existsById(id)) {
              throw new CentaurException(ResultCode.DATA_ALREADY_EXISTS);
            }
          }, () -> simpleCaptchaDomain.setId(primaryKeyGateway.snowflake()));
          Optional.ofNullable(simpleCaptchaDomain.getLength()).filter(length -> length > 0)
              .orElseThrow(() -> new CentaurException(
                  ResultCode.SIMPLE_CAPTCHA_LENGTH_NEEDS_TO_BE_GREATER_THAN_0));
          if (!StringUtils.hasText(simpleCaptchaDomain.getTarget())) {
            simpleCaptchaDomain.setTarget(
                CommonUtil.generateRandomString(simpleCaptchaDomain.getLength()));
          }
          Optional.ofNullable(simpleCaptchaDomain.getTtl()).orElseThrow(() -> new CentaurException(
              ResultCode.SIMPLE_CAPTCHA_VALIDITY_PERIOD_CANNOT_BE_EMPTY));
          return CaptchaConvertor.toDataObject(simpleCaptchaDomain);
        }).orElseThrow(() -> new CentaurException(ResultCode.DATA_CONVERSION_FAILED));
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
