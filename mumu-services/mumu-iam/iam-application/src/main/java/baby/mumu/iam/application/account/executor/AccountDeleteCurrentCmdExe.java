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

package baby.mumu.iam.application.account.executor;

import baby.mumu.iam.client.cmds.AccountDeleteCurrentCmd;
import baby.mumu.iam.domain.account.gateway.AccountGateway;
import baby.mumu.unique.client.api.CaptchaGrpcService;
import baby.mumu.unique.client.api.CaptchaVerify;
import io.micrometer.observation.annotation.Observed;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 账号删除指定执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "AccountDeleteCurrentCmdExe")
public class AccountDeleteCurrentCmdExe extends CaptchaVerify {

  private final AccountGateway accountGateway;

  @Autowired
  public AccountDeleteCurrentCmdExe(AccountGateway accountGateway,
    CaptchaGrpcService captchaGrpcService) {
    super(captchaGrpcService);
    this.accountGateway = accountGateway;
  }

  public void execute(@NotNull AccountDeleteCurrentCmd accountDeleteCurrentCmd) {
    verifyCaptcha(accountDeleteCurrentCmd.getCaptchaId(), accountDeleteCurrentCmd.getCaptcha());
    accountGateway.deleteCurrentAccount();
  }
}
