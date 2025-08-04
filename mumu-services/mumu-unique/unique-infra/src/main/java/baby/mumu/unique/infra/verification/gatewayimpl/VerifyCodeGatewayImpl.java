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

package baby.mumu.unique.infra.verification.gatewayimpl;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.unique.domain.pk.gateway.PrimaryKeyGateway;
import baby.mumu.unique.domain.verification.VerifyCode;
import baby.mumu.unique.infra.verification.convertor.VerifyCodeConvertor;
import baby.mumu.unique.infra.verification.gatewayimpl.cache.VerifyCodeCacheRepository;
import baby.mumu.unique.infra.verification.gatewayimpl.cache.po.VerifyCodeCacheablePO;
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
public class VerifyCodeGatewayImpl implements
  baby.mumu.unique.domain.verification.gateway.VerifyCodeGateway {

  private final PrimaryKeyGateway primaryKeyGateway;
  private final VerifyCodeCacheRepository verifyCodeCacheRepository;
  private final VerifyCodeConvertor verifyCodeConvertor;

  @Autowired
  public VerifyCodeGatewayImpl(PrimaryKeyGateway primaryKeyGateway,
    VerifyCodeCacheRepository verifyCodeCacheRepository,
    VerifyCodeConvertor verifyCodeConvertor) {
    this.primaryKeyGateway = primaryKeyGateway;
    this.verifyCodeCacheRepository = verifyCodeCacheRepository;
    this.verifyCodeConvertor = verifyCodeConvertor;
  }

  @Override
  public Long generate(VerifyCode verifyCode) {
    VerifyCodeCacheablePO verifyCodeCacheablePO = Optional.ofNullable(verifyCode)
      .flatMap(verifyCodeNotNull -> {
        verifyCodeNotNull.setId(primaryKeyGateway.snowflake());
        Optional.ofNullable(verifyCodeNotNull.getLength()).filter(length -> length > 0)
          .orElseThrow(() -> new MuMuException(
            ResponseCode.VERIFICATION_CODE_LENGTH_NEEDS_TO_BE_GREATER_THAN_0));
        verifyCodeNotNull.setTarget(
          RandomStringUtils.secure().nextAlphanumeric(verifyCodeNotNull.getLength()));
        Optional.ofNullable(verifyCodeNotNull.getTtl()).orElseThrow(() -> new MuMuException(
          ResponseCode.VERIFICATION_CODE_VALIDITY_PERIOD_CANNOT_BE_EMPTY));
        return verifyCodeConvertor.toVerifyCodeCacheablePO(verifyCodeNotNull);
      }).orElseThrow(() -> new MuMuException(ResponseCode.DATA_CONVERSION_FAILED));
    verifyCodeCacheRepository.save(verifyCodeCacheablePO);
    return verifyCodeCacheablePO.getId();
  }

  @Override
  public boolean verify(VerifyCode verifyCode) {
    return Optional.ofNullable(verifyCode).flatMap(
        verifyCodeNotNull -> verifyCodeCacheRepository.findById(verifyCodeNotNull.getId()))
      .map(
        verifyCodeCacheablePO -> verifyCodeCacheablePO.getTarget()
          .equalsIgnoreCase(verifyCode.getSource()))
      .orElse(false);
  }
}
