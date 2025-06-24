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

import baby.mumu.iam.client.cmds.AccountFindAllSliceCmd;
import baby.mumu.iam.client.dto.AccountFindAllSliceDTO;
import baby.mumu.iam.domain.account.Account;
import baby.mumu.iam.domain.account.gateway.AccountGateway;
import baby.mumu.iam.infra.account.convertor.AccountConvertor;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;

/**
 * 分页查询所有账号指令执行器(不查询总数)
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
@Component
@Observed(name = "AccountFindAllSliceCmdExe")
public class AccountFindAllSliceCmdExe {

  private final AccountGateway accountGateway;
  private final AccountConvertor accountConvertor;

  @Autowired
  public AccountFindAllSliceCmdExe(AccountGateway accountGateway,
    AccountConvertor accountConvertor) {
    this.accountGateway = accountGateway;
    this.accountConvertor = accountConvertor;
  }

  public Slice<AccountFindAllSliceDTO> execute(
    @NotNull AccountFindAllSliceCmd accountFindAllSliceCmd) {
    Account account = accountConvertor.toEntity(accountFindAllSliceCmd).orElseGet(Account::new);
    Slice<Account> accounts = accountGateway.findAllSlice(account,
      accountFindAllSliceCmd.getCurrent(), accountFindAllSliceCmd.getPageSize());
    List<AccountFindAllSliceDTO> accountFindAllSliceDTOS = accounts.getContent().stream()
      .map(accountConvertor::toFindAllSliceDTO)
      .filter(Optional::isPresent).map(Optional::get).toList();
    return new SliceImpl<>(accountFindAllSliceDTOS, accounts.getPageable(),
      accounts.hasNext());
  }
}
