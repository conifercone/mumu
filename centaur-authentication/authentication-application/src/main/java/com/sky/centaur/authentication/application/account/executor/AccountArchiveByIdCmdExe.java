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
package com.sky.centaur.authentication.application.account.executor;

import com.sky.centaur.authentication.client.dto.AccountArchiveByIdCmd;
import com.sky.centaur.authentication.domain.account.gateway.AccountGateway;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 根据id归档账户指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
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

  public void execute(@NotNull AccountArchiveByIdCmd accountArchiveByIdCmd) {
    Assert.notNull(accountArchiveByIdCmd, "AccountArchiveByIdCmd cannot be null");
    Optional.ofNullable(accountArchiveByIdCmd.getId())
        .ifPresent(accountGateway::archiveById);
  }
}
