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

import baby.mumu.authentication.domain.account.gateway.AccountGateway;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 根据id归档账号指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.4
 */
@Component
@Observed(name = "AccountArchiveByIdCmdExe")
public class AccountArchiveByIdCmdExe {

  private final AccountGateway accountGateway;

  @Autowired
  public AccountArchiveByIdCmdExe(AccountGateway accountGateway) {
    this.accountGateway = accountGateway;
  }

  public void execute(Long accountId) {
    Assert.notNull(accountId, "accountId cannot be null");
    Optional.of(accountId)
      .ifPresent(accountGateway::archiveById);
  }
}
