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

package baby.mumu.iam.application.account.executor;

import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.genix.client.api.CaptchaCodeGrpcService;
import baby.mumu.genix.client.api.CaptchaCodeVerify;
import baby.mumu.iam.client.cmds.AccountRegisterCmd;
import baby.mumu.iam.domain.account.Account;
import baby.mumu.iam.domain.account.gateway.AccountGateway;
import baby.mumu.iam.infra.account.convertor.AccountConvertor;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 账号注册指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "AccountRegisterCmdExe")
public class AccountRegisterCmdExe extends CaptchaCodeVerify {

    private final AccountGateway accountGateway;
    private final AccountConvertor accountConvertor;

    @Autowired
    public AccountRegisterCmdExe(AccountGateway accountGateway,
                                 CaptchaCodeGrpcService captchaCodeGrpcService, AccountConvertor accountConvertor) {
        super(captchaCodeGrpcService);
        this.accountGateway = accountGateway;
        this.accountConvertor = accountConvertor;
    }

    public Long execute(AccountRegisterCmd accountRegisterCmd) {
        Account account = accountConvertor.toEntity(accountRegisterCmd)
            .orElseThrow(() -> new ApplicationException(ResponseCode.INVALID_ACCOUNT_FORMAT));
        return verify(accountRegisterCmd.getCaptchaCodeId(), accountRegisterCmd.getCaptchaCode(),
            () -> accountGateway.register(account));
    }
}
