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

package baby.mumu.genix.application.service;

import baby.mumu.genix.application.captcha.executor.CaptchaCodeDeleteCmdExe;
import baby.mumu.genix.application.captcha.executor.CaptchaCodeGeneratedCmdExe;
import baby.mumu.genix.application.captcha.executor.CaptchaCodeVerifyCmdExe;
import baby.mumu.genix.client.api.CaptchaCodeService;
import baby.mumu.genix.client.cmds.CaptchaCodeGeneratedCmd;
import baby.mumu.genix.client.cmds.CaptchaCodeVerifyCmd;
import baby.mumu.genix.client.dto.CaptchaCodeGeneratedDTO;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 验证码service实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Service
@Observed(name = "CaptchaCodeServiceImpl")
public class CaptchaCodeServiceImpl implements CaptchaCodeService {

    private final CaptchaCodeGeneratedCmdExe captchaCodeGeneratedCmdExe;
    private final CaptchaCodeVerifyCmdExe captchaCodeVerifyCmdExe;
    private final CaptchaCodeDeleteCmdExe captchaCodeDeleteCmdExe;

    @Autowired
    public CaptchaCodeServiceImpl(CaptchaCodeGeneratedCmdExe captchaCodeGeneratedCmdExe,
                                  CaptchaCodeVerifyCmdExe captchaCodeVerifyCmdExe,
                                  CaptchaCodeDeleteCmdExe captchaCodeDeleteCmdExe) {
        this.captchaCodeGeneratedCmdExe = captchaCodeGeneratedCmdExe;
        this.captchaCodeVerifyCmdExe = captchaCodeVerifyCmdExe;
        this.captchaCodeDeleteCmdExe = captchaCodeDeleteCmdExe;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaptchaCodeGeneratedDTO generate(
        CaptchaCodeGeneratedCmd captchaCodeGeneratedCmd) {
        return captchaCodeGeneratedCmdExe.execute(captchaCodeGeneratedCmd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean verify(CaptchaCodeVerifyCmd captchaCodeVerifyCmd) {
        return captchaCodeVerifyCmdExe.execute(captchaCodeVerifyCmd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long captchaCodeId) {
        Optional.ofNullable(captchaCodeId).ifPresent(captchaCodeDeleteCmdExe::execute);
    }
}
