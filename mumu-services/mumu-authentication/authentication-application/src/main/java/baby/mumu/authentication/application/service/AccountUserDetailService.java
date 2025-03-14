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
package baby.mumu.authentication.application.service;

import baby.mumu.authentication.domain.account.gateway.AccountGateway;
import baby.mumu.basis.kotlin.tools.EmailUtils;
import baby.mumu.basis.response.ResponseCode;
import io.micrometer.observation.annotation.Observed;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.util.Assert;

/**
 * spring security authentication server 用户信息service
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 1.0.0
 */
@Observed(name = "AccountUserDetailService")
public class AccountUserDetailService implements UserDetailsService {

  private final AccountGateway accountGateway;

  public AccountUserDetailService(AccountGateway accountGateway) {
    this.accountGateway = accountGateway;
  }

  @Override
  public @NotNull UserDetails loadUserByUsername(String usernameOrEmail)
    throws UsernameNotFoundException {
    Assert.hasText(usernameOrEmail, "username or email is required");
    Supplier<OAuth2AuthenticationException> usernameNotFoundExceptionSupplier = () -> {
      OAuth2Error error = new OAuth2Error(ResponseCode.ACCOUNT_DOES_NOT_EXIST.getCode(),
        ResponseCode.ACCOUNT_DOES_NOT_EXIST.getMessage(),
        StringUtils.EMPTY);
      return new OAuth2AuthenticationException(error);
    };
    return EmailUtils.isValidEmailFormat(usernameOrEmail) ? accountGateway.findAccountByEmail(
      usernameOrEmail).orElseThrow(usernameNotFoundExceptionSupplier)
      : accountGateway.findAccountByUsername(usernameOrEmail)
        .orElseThrow(usernameNotFoundExceptionSupplier);
  }
}
