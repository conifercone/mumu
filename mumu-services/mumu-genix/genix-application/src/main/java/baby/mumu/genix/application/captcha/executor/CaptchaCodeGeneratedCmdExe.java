/*
 * Copyright (c) 2024-2026, the original author or authors.
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

package baby.mumu.genix.application.captcha.executor;

import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.genix.client.cmds.CaptchaCodeGeneratedCmd;
import baby.mumu.genix.client.dto.CaptchaCodeGeneratedDTO;
import baby.mumu.genix.domain.captcha.gateway.CaptchaCodeGateway;
import baby.mumu.genix.infra.captcha.convertor.CaptchaCodeConvertor;
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
public class CaptchaCodeGeneratedCmdExe {

    private final CaptchaCodeGateway captchaCodeGateway;
    private final CaptchaCodeConvertor captchaCodeConvertor;

    @Autowired
    public CaptchaCodeGeneratedCmdExe(
        CaptchaCodeGateway captchaCodeGateway,
        CaptchaCodeConvertor captchaCodeConvertor) {
        this.captchaCodeGateway = captchaCodeGateway;
        this.captchaCodeConvertor = captchaCodeConvertor;
    }

    public CaptchaCodeGeneratedDTO execute(CaptchaCodeGeneratedCmd captchaCodeGeneratedCmd) {
        Assert.notNull(captchaCodeGeneratedCmd, "CaptchaCodeGeneratedCmd cannot be null");
        return captchaCodeConvertor.toEntity(captchaCodeGeneratedCmd)
            .map(captchaCodeGateway::generate)
            .flatMap(captchaCodeConvertor::toCaptchaCodeGeneratedDTO)
            .orElseThrow(() -> new ApplicationException(
                ResponseCode.CAPTCHA_CODE_GENERATION_FAILED));
    }
}
