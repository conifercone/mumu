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
package com.sky.centaur.authentication.application.service;

import com.sky.centaur.authentication.domain.account.Account;
import com.sky.centaur.authentication.domain.account.gateway.AccountGateway;
import com.sky.centaur.basis.kotlin.tools.CommonUtil;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

/**
 * spring security authentication server 用户信息service
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Observed(name = "AccountUserDetailService")
public final class AccountUserDetailService implements UserDetailsService {

  private final AccountGateway accountGateway;

  public AccountUserDetailService(AccountGateway accountGateway) {
    this.accountGateway = accountGateway;
  }

  @Override
  public @NotNull UserDetails loadUserByUsername(String usernameOrEmail)
      throws UsernameNotFoundException {
    Assert.hasText(usernameOrEmail, "username or email is required");
    Optional<Account> optionalAccount =
        CommonUtil.isValidEmail(usernameOrEmail) ? accountGateway.findAccountByEmail(
            usernameOrEmail)
            : accountGateway.findAccountByUsername(usernameOrEmail);
    if (optionalAccount.isPresent()) {
      return optionalAccount.get();
    }
    throw new UsernameNotFoundException(usernameOrEmail);
  }
}
