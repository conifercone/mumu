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

import baby.mumu.iam.client.cmds.AccountChangePasswordCmd;
import baby.mumu.iam.domain.account.gateway.AccountGateway;
import io.micrometer.observation.annotation.Observed;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 账号修改密码指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "AccountChangePasswordCmdExe")
public class AccountChangePasswordCmdExe {

    private final AccountGateway accountGateway;

    @Autowired
    public AccountChangePasswordCmdExe(AccountGateway accountGateway) {
        this.accountGateway = accountGateway;
    }

    public void execute(@NonNull AccountChangePasswordCmd accountChangePasswordCmd) {
        Assert.notNull(accountChangePasswordCmd, "AccountChangePasswordCmd cannot be null");
        accountGateway.changePassword(accountChangePasswordCmd.getOriginalPassword(),
            accountChangePasswordCmd.getNewPassword());
    }
}
