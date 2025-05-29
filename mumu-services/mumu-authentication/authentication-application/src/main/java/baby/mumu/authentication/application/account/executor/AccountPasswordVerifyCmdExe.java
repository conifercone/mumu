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
package baby.mumu.authentication.application.account.executor;

import baby.mumu.authentication.client.cmds.AccountPasswordVerifyCmd;
import baby.mumu.authentication.domain.account.gateway.AccountGateway;
import io.micrometer.observation.annotation.Observed;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 账号密码校验指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "AccountPasswordVerifyCmdExe")
public class AccountPasswordVerifyCmdExe {

  private final AccountGateway accountGateway;

  @Autowired
  public AccountPasswordVerifyCmdExe(AccountGateway accountGateway) {
    this.accountGateway = accountGateway;
  }

  public boolean execute(@NotNull AccountPasswordVerifyCmd accountPasswordVerifyCmd) {
    Assert.notNull(accountPasswordVerifyCmd, "AccountPasswordVerifyCmd cannot be null");
    return accountGateway.verifyPassword(accountPasswordVerifyCmd.getPassword());
  }
}
