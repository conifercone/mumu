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

package baby.mumu.genix.infra.captcha.convertor;

import baby.mumu.genix.client.api.grpc.CaptchaCodeGeneratedGrpcDTO;
import baby.mumu.genix.client.api.grpc.CaptchaCodeVerifyGrpcCmd;
import baby.mumu.genix.client.cmds.CaptchaCodeGeneratedCmd;
import baby.mumu.genix.client.cmds.CaptchaCodeVerifyCmd;
import baby.mumu.genix.client.dto.CaptchaCodeGeneratedDTO;
import baby.mumu.genix.domain.captcha.CaptchaCode;
import baby.mumu.genix.infra.captcha.gatewayimpl.cache.po.CaptchaCodeCacheablePO;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.stereotype.Component;

/**
 * 验证码对象转换类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Component
public class CaptchaCodeConvertor {


  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<CaptchaCodeCacheablePO> toCaptchaCodeCacheablePO(CaptchaCode captchaCode) {
    return Optional.ofNullable(captchaCode)
      .map(CaptchaCodeMapper.INSTANCE::toCaptchaCodeCacheablePO);
  }

  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<CaptchaCode> toEntity(
    CaptchaCodeGeneratedCmd captchaCodeGeneratedCmd) {
    return Optional.ofNullable(captchaCodeGeneratedCmd).map(CaptchaCodeMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<CaptchaCode> toEntity(
    CaptchaCodeVerifyCmd captchaCodeVerifyCmd) {
    return Optional.ofNullable(captchaCodeVerifyCmd).map(CaptchaCodeMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<CaptchaCodeVerifyCmd> toCaptchaCodeVerifyCmd(
    CaptchaCodeVerifyGrpcCmd captchaCodeVerifyGrpcCmd) {
    return Optional.ofNullable(captchaCodeVerifyGrpcCmd)
      .map(CaptchaCodeMapper.INSTANCE::toCaptchaCodeVerifyCmd);
  }

  @API(status = Status.STABLE, since = "2.15.0")
  public Optional<CaptchaCodeGeneratedDTO> toCaptchaCodeGeneratedDTO(
    CaptchaCode captchaCode) {
    return Optional.ofNullable(captchaCode)
      .map(CaptchaCodeMapper.INSTANCE::toCaptchaCodeGeneratedDTO);
  }

  @API(status = Status.STABLE, since = "2.15.0")
  public Optional<CaptchaCodeGeneratedGrpcDTO> toCaptchaCodeGeneratedGrpcDTO(
    CaptchaCodeGeneratedDTO captchaCodeGeneratedDTO) {
    return Optional.ofNullable(captchaCodeGeneratedDTO)
      .map(CaptchaCodeMapper.INSTANCE::toCaptchaCodeGeneratedGrpcDTO);
  }
}
