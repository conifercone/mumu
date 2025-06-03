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

package baby.mumu.authentication.infrastructure.token.gatewayimpl;

import baby.mumu.authentication.domain.token.gateway.TokenGateway;
import baby.mumu.authentication.infrastructure.token.gatewayimpl.cache.AuthorizeCodeTokenCacheRepository;
import baby.mumu.authentication.infrastructure.token.gatewayimpl.cache.ClientTokenCacheRepository;
import baby.mumu.authentication.infrastructure.token.gatewayimpl.cache.PasswordTokenCacheRepository;
import baby.mumu.basis.enums.OAuth2Enum;
import baby.mumu.basis.enums.TokenClaimsEnum;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

/**
 * token领域网关实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "TokenGatewayImpl")
public class TokenGatewayImpl implements TokenGateway {

  private final PasswordTokenCacheRepository passwordTokenCacheRepository;
  private final JwtDecoder jwtDecoder;
  private final ClientTokenCacheRepository clientTokenCacheRepository;
  private final AuthorizeCodeTokenCacheRepository authorizeCodeTokenCacheRepository;

  @Autowired
  public TokenGatewayImpl(PasswordTokenCacheRepository passwordTokenCacheRepository,
    JwtDecoder jwtDecoder,
    ClientTokenCacheRepository clientTokenCacheRepository,
    AuthorizeCodeTokenCacheRepository authorizeCodeTokenCacheRepository) {
    this.passwordTokenCacheRepository = passwordTokenCacheRepository;
    this.jwtDecoder = jwtDecoder;
    this.clientTokenCacheRepository = clientTokenCacheRepository;
    this.authorizeCodeTokenCacheRepository = authorizeCodeTokenCacheRepository;
  }

  @Override
  public boolean validity(String token) {
    return Optional.ofNullable(token).map(tokenValue -> {
        try {
          Jwt jwt = jwtDecoder.decode(tokenValue);
          String claimAsString = jwt.getClaimAsString(
            TokenClaimsEnum.AUTHORIZATION_GRANT_TYPE.getClaimName());
          if (OAuth2Enum.GRANT_TYPE_PASSWORD.getName().equals(claimAsString)) {
            return passwordTokenCacheRepository.existsById(
              Long.parseLong(jwt.getClaimAsString(TokenClaimsEnum.ACCOUNT_ID.getClaimName())));
          } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(claimAsString)) {
            return clientTokenCacheRepository.existsById(jwt.getClaimAsString(JwtClaimNames.SUB));
          } else if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(claimAsString)) {
            return authorizeCodeTokenCacheRepository.existsById(
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
