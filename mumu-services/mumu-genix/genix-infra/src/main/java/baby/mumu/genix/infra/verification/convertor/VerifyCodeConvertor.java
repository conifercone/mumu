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

package baby.mumu.genix.infra.verification.convertor;

import baby.mumu.genix.client.api.grpc.VerifyCodeVerifyGrpcCmd;
import baby.mumu.genix.client.cmds.VerifyCodeGeneratedCmd;
import baby.mumu.genix.client.cmds.VerifyCodeVerifyCmd;
import baby.mumu.genix.domain.verification.VerifyCode;
import baby.mumu.genix.infra.verification.gatewayimpl.cache.po.VerifyCodeCacheablePO;
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
public class VerifyCodeConvertor {


  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<VerifyCodeCacheablePO> toVerifyCodeCacheablePO(VerifyCode verifyCode) {
    return Optional.ofNullable(verifyCode).map(VerifyCodeMapper.INSTANCE::toVerifyCodeCacheablePO);
  }

  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<VerifyCode> toEntity(
    VerifyCodeGeneratedCmd verifyCodeGeneratedCmd) {
    return Optional.ofNullable(verifyCodeGeneratedCmd).map(VerifyCodeMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "1.0.1")
  public Optional<VerifyCode> toEntity(
    VerifyCodeVerifyCmd verifyCodeVerifyCmd) {
    return Optional.ofNullable(verifyCodeVerifyCmd).map(VerifyCodeMapper.INSTANCE::toEntity);
  }

  @API(status = Status.STABLE, since = "2.2.0")
  public Optional<VerifyCodeVerifyCmd> toVerifyCodeVerifyCmd(
    VerifyCodeVerifyGrpcCmd verifyCodeVerifyGrpcCmd) {
    return Optional.ofNullable(verifyCodeVerifyGrpcCmd)
      .map(VerifyCodeMapper.INSTANCE::toVerifyCodeVerifyCmd);
  }
}
