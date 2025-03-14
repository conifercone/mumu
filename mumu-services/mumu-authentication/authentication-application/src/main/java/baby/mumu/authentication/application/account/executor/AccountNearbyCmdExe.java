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

import baby.mumu.authentication.client.dto.AccountNearbyDTO;
import baby.mumu.authentication.domain.account.gateway.AccountGateway;
import baby.mumu.authentication.infrastructure.account.convertor.AccountConvertor;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 附近的账户
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 2.6.0
 */
@Component
@Observed(name = "AccountNearbyCmdExe")
public class AccountNearbyCmdExe {

  private final AccountGateway accountGateway;
  private final AccountConvertor accountConvertor;

  @Autowired
  public AccountNearbyCmdExe(AccountGateway accountGateway,
    AccountConvertor accountConvertor) {
    this.accountGateway = accountGateway;
    this.accountConvertor = accountConvertor;
  }

  public List<AccountNearbyDTO> execute(double radiusInMeters) {
    return accountGateway.nearby(radiusInMeters).stream()
      .flatMap(account -> accountConvertor.toAccountNearbyDTO(account).stream()).collect(
        Collectors.toList());
  }
}
