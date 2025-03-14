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

import baby.mumu.authentication.client.cmds.AccountFindAllCmd;
import baby.mumu.authentication.client.dto.AccountFindAllDTO;
import baby.mumu.authentication.domain.account.Account;
import baby.mumu.authentication.domain.account.gateway.AccountGateway;
import baby.mumu.authentication.infrastructure.account.convertor.AccountConvertor;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

/**
 * 分页查询所有账户指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 2.2.0
 */
@Component
@Observed(name = "AccountFindAllCmdExe")
public class AccountFindAllCmdExe {

  private final AccountGateway accountGateway;
  private final AccountConvertor accountConvertor;

  @Autowired
  public AccountFindAllCmdExe(AccountGateway accountGateway,
    AccountConvertor accountConvertor) {
    this.accountGateway = accountGateway;
    this.accountConvertor = accountConvertor;
  }

  public Page<AccountFindAllDTO> execute(@NotNull AccountFindAllCmd accountFindAllCmd) {
    Account account = accountConvertor.toEntity(accountFindAllCmd)
      .orElseGet(Account::new);
    Page<Account> accounts = accountGateway.findAll(account,
      accountFindAllCmd.getCurrent(), accountFindAllCmd.getPageSize());
    List<AccountFindAllDTO> accountFindAllDTOS = accounts.getContent().stream()
      .map(accountConvertor::toFindAllDTO)
      .filter(Optional::isPresent).map(Optional::get).toList();
    return new PageImpl<>(accountFindAllDTOS, accounts.getPageable(),
      accounts.getTotalElements());
  }
}
