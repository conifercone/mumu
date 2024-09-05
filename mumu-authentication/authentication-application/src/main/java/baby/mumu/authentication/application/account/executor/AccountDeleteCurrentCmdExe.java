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
package baby.mumu.authentication.application.account.executor;

import baby.mumu.authentication.application.CaptchaVerify;
import baby.mumu.authentication.client.dto.AccountDeleteCurrentCmd;
import baby.mumu.authentication.domain.account.gateway.AccountGateway;
import baby.mumu.unique.client.api.CaptchaGrpcService;
import io.micrometer.observation.annotation.Observed;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 账户删除指定执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
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
