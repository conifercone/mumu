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

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.iam.client.dto.AccountCurrentLoginDTO;
import baby.mumu.iam.domain.account.gateway.AccountGateway;
import baby.mumu.iam.infra.account.convertor.AccountConvertor;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 查询当前登录账号信息指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "AccountCurrentLoginQueryCmdExe")
public class AccountCurrentLoginQueryCmdExe {

  private final AccountGateway accountGateway;
  private final AccountConvertor accountConvertor;

  @Autowired
  public AccountCurrentLoginQueryCmdExe(AccountGateway accountGateway,
    AccountConvertor accountConvertor) {
    this.accountGateway = accountGateway;
    this.accountConvertor = accountConvertor;
  }

  public AccountCurrentLoginDTO execute() {
    return accountGateway.queryCurrentLoginAccount()
      .flatMap(accountConvertor::toAccountCurrentLoginDTO)
      .orElseThrow(() -> new MuMuException(ResponseCode.ACCOUNT_DOES_NOT_EXIST));
  }
}
