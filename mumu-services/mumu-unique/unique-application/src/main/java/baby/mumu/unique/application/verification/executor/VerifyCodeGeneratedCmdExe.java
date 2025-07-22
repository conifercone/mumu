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

package baby.mumu.unique.application.verification.executor;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.unique.client.cmds.VerifyCodeGeneratedCmd;
import baby.mumu.unique.infra.verification.convertor.VerifyCodeConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 简单验证码生成指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Component
public class VerifyCodeGeneratedCmdExe {

  private final baby.mumu.unique.domain.verification.gateway.VerifyCodeGateway verifyCodeGateway;
  private final VerifyCodeConvertor verifyCodeConvertor;

  @Autowired
  public VerifyCodeGeneratedCmdExe(
    baby.mumu.unique.domain.verification.gateway.VerifyCodeGateway verifyCodeGateway,
    VerifyCodeConvertor verifyCodeConvertor) {
    this.verifyCodeGateway = verifyCodeGateway;
    this.verifyCodeConvertor = verifyCodeConvertor;
  }

  public Long execute(VerifyCodeGeneratedCmd verifyCodeGeneratedCmd) {
    Assert.notNull(verifyCodeGeneratedCmd, "VerifyCodeGeneratedCmd cannot be null");
    return verifyCodeConvertor.toEntity(verifyCodeGeneratedCmd)
      .map(verifyCodeGateway::generate)
      .orElseThrow(() -> new MuMuException(
        ResponseCode.VERIFICATION_CODE_GENERATION_FAILED));
  }
}
