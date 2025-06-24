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

import baby.mumu.iam.client.cmds.AccountAddAddressCmd;
import baby.mumu.iam.domain.account.gateway.AccountGateway;
import baby.mumu.iam.infrastructure.account.convertor.AccountConvertor;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 账号添加地址指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.0.0
 */
@Component
@Observed(name = "AccountAddAddressCmdExe")
public class AccountAddAddressCmdExe {

  private final AccountGateway accountGateway;
  private final AccountConvertor accountConvertor;

  @Autowired
  public AccountAddAddressCmdExe(AccountGateway accountGateway, AccountConvertor accountConvertor) {
    this.accountGateway = accountGateway;
    this.accountConvertor = accountConvertor;
  }

  public void execute(AccountAddAddressCmd accountAddAddressCmd) {
    Optional.ofNullable(accountAddAddressCmd)
      .flatMap(accountConvertor::toEntity)
      .ifPresent(accountGateway::addAddress);
  }
}
