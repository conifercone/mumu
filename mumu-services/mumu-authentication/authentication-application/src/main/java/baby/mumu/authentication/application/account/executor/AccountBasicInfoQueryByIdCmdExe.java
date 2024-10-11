/*
 * Copyright (c) 2024-2024, the original author or authors.
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

import baby.mumu.authentication.client.dto.AccountBasicInfoByIdCmd;
import baby.mumu.authentication.client.dto.co.AccountBasicInfoCo;
import baby.mumu.authentication.domain.account.gateway.AccountGateway;
import baby.mumu.authentication.infrastructure.account.convertor.AccountConvertor;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResultCode;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 根据ID查询账户基本信息指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Component
@Observed(name = "AccountBasicInfoQueryByIdCmdExe")
public class AccountBasicInfoQueryByIdCmdExe {

  private final AccountGateway accountGateway;
  private final AccountConvertor accountConvertor;

  @Autowired
  public AccountBasicInfoQueryByIdCmdExe(AccountGateway accountGateway,
      AccountConvertor accountConvertor) {
    this.accountGateway = accountGateway;
    this.accountConvertor = accountConvertor;
  }

  public AccountBasicInfoCo execute(AccountBasicInfoByIdCmd accountBasicInfoByIdCmd) {
    return Optional.ofNullable(accountBasicInfoByIdCmd).map(AccountBasicInfoByIdCmd::getId)
        .flatMap(accountGateway::getAccountBasicInfoById)
        .flatMap(accountConvertor::toBasicInfoCo)
        .orElseThrow(() -> new MuMuException(ResultCode.ACCOUNT_DOES_NOT_EXIST));
  }
}
