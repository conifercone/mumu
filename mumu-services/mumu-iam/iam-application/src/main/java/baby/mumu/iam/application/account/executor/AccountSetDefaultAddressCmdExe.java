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

import baby.mumu.iam.domain.account.gateway.AccountGateway;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 设置当前登录账号默认地址指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.6.0
 */
@Component
@Observed(name = "AccountSetDefaultAddressCmdExe")
public class AccountSetDefaultAddressCmdExe {

  private final AccountGateway accountGateway;

  @Autowired
  public AccountSetDefaultAddressCmdExe(AccountGateway accountGateway) {
    this.accountGateway = accountGateway;
  }

  public void execute(String addressId) {
    Optional.ofNullable(addressId).filter(StringUtils::isNotBlank)
      .ifPresent(accountGateway::setDefaultAddress);
  }
}
