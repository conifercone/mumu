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
package baby.mumu.authentication.infrastructure.token.gatewayimpl;

import baby.mumu.authentication.domain.token.gateway.TokenGateway;
import baby.mumu.authentication.infrastructure.token.gatewayimpl.redis.AuthorizeCodeTokenRepository;
import baby.mumu.authentication.infrastructure.token.gatewayimpl.redis.ClientTokenRepository;
import baby.mumu.authentication.infrastructure.token.gatewayimpl.redis.PasswordTokenRepository;
import baby.mumu.basis.enums.OAuth2Enum;
import baby.mumu.basis.enums.TokenClaimsEnum;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

/**
 * token领域网关实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "TokenGatewayImpl")
public class TokenGatewayImpl implements TokenGateway {

  private final PasswordTokenRepository passwordTokenRepository;
  private final JwtDecoder jwtDecoder;
  private final ClientTokenRepository clientTokenRepository;
  private final AuthorizeCodeTokenRepository authorizeCodeTokenRepository;

  @Autowired
  public TokenGatewayImpl(PasswordTokenRepository passwordTokenRepository, JwtDecoder jwtDecoder,
    ClientTokenRepository clientTokenRepository,
    AuthorizeCodeTokenRepository authorizeCodeTokenRepository) {
    this.passwordTokenRepository = passwordTokenRepository;
    this.jwtDecoder = jwtDecoder;
    this.clientTokenRepository = clientTokenRepository;
    this.authorizeCodeTokenRepository = authorizeCodeTokenRepository;
  }

  @Override
  public boolean validity(String token) {
    return Optional.ofNullable(token).map(tokenValue -> {
        try {
          Jwt jwt = jwtDecoder.decode(tokenValue);
          String claimAsString = jwt.getClaimAsString(
            TokenClaimsEnum.AUTHORIZATION_GRANT_TYPE.getClaimName());
          if (OAuth2Enum.GRANT_TYPE_PASSWORD.getName().equals(claimAsString)) {
            return passwordTokenRepository.existsById(
              Long.parseLong(jwt.getClaimAsString(TokenClaimsEnum.ACCOUNT_ID.getClaimName())));
          } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(claimAsString)) {
            return clientTokenRepository.existsById(jwt.getClaimAsString("sub"));
          } else if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(claimAsString)) {
            return authorizeCodeTokenRepository.existsById(
              Long.parseLong(jwt.getClaimAsString(TokenClaimsEnum.ACCOUNT_ID.getClaimName())));

          }
          return true;
        } catch (Exception ignore) {
          return false;
        }
      })
      .orElse(false);
  }
}
