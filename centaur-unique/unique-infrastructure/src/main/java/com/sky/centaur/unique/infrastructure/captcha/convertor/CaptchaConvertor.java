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
package com.sky.centaur.unique.infrastructure.captcha.convertor;

import com.expediagroup.beans.transformer.BeanTransformer;
import com.sky.centaur.unique.client.dto.co.SimpleCaptchaGeneratedCo;
import com.sky.centaur.unique.client.dto.co.SimpleCaptchaVerifyCo;
import com.sky.centaur.unique.domain.captcha.Captcha.SimpleCaptcha;
import com.sky.centaur.unique.infrastructure.captcha.gatewayimpl.redis.dataobject.SimpleCaptchaDo;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;

/**
 * 验证码对象转换类
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
public final class CaptchaConvertor {

  private static final BeanTransformer BEAN_TRANSFORMER = new com.expediagroup.beans.BeanUtils().getTransformer()
      .setDefaultValueForMissingField(true)
      .setDefaultValueForMissingPrimitiveField(false);

  private CaptchaConvertor() {
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public static Optional<SimpleCaptchaDo> toDataObject(SimpleCaptcha simpleCaptcha) {
    return Optional.ofNullable(simpleCaptcha).map(
        simpleCaptchaDomain -> {
          BEAN_TRANSFORMER.resetFieldsTransformationSkip();
          return BEAN_TRANSFORMER.transform(simpleCaptchaDomain,
              SimpleCaptchaDo.class);
        });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public static Optional<SimpleCaptcha> toEntity(
      SimpleCaptchaGeneratedCo simpleCaptchaGeneratedCo) {
    return Optional.ofNullable(simpleCaptchaGeneratedCo).map(
        simpleCaptchaGeneratedClientObject -> {
          BEAN_TRANSFORMER.resetFieldsTransformationSkip();
          return BEAN_TRANSFORMER.transform(
              simpleCaptchaGeneratedClientObject, SimpleCaptcha.class);
        });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public static Optional<SimpleCaptcha> toEntity(
      SimpleCaptchaVerifyCo simpleCaptchaVerifyCo) {
    return Optional.ofNullable(simpleCaptchaVerifyCo).map(
        simpleCaptchaVerifyClientObject -> {
          BEAN_TRANSFORMER.resetFieldsTransformationSkip();
          return BEAN_TRANSFORMER.transform(
              simpleCaptchaVerifyClientObject, SimpleCaptcha.class);
        });
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public static Optional<SimpleCaptchaGeneratedCo> toSimpleCaptchaGeneratedCo(
      SimpleCaptcha simpleCaptcha) {
    return Optional.ofNullable(simpleCaptcha).map(
        simpleCaptchaDomain -> {
          BEAN_TRANSFORMER.resetFieldsTransformationSkip();
          return BEAN_TRANSFORMER.transform(
              simpleCaptchaDomain, SimpleCaptchaGeneratedCo.class);
        });
  }
}
