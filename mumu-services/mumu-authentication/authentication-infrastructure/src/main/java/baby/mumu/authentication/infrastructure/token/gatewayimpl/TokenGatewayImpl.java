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
import baby.mumu.authentication.infrastructure.token.gatewayimpl.redis.ClientTokenRepository;
import baby.mumu.authentication.infrastructure.token.gatewayimpl.redis.TokenRepository;
import baby.mumu.basis.enums.OAuth2Enum;
import baby.mumu.basis.enums.TokenClaimsEnum;
import baby.mumu.basis.response.ResponseCode;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private final TokenRepository tokenRepository;
  private final JwtDecoder jwtDecoder;
  private final ClientTokenRepository clientTokenRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(
      TokenGatewayImpl.class);

  @Autowired
  public TokenGatewayImpl(TokenRepository tokenRepository, JwtDecoder jwtDecoder,
      ClientTokenRepository clientTokenRepository) {
    this.tokenRepository = tokenRepository;
    this.jwtDecoder = jwtDecoder;
    this.clientTokenRepository = clientTokenRepository;
  }

  @Override
  public boolean validity(String token) {
    return Optional.ofNullable(token).map(tokenValue -> {
          try {
            Jwt jwt = jwtDecoder.decode(tokenValue);
            String claimAsString = jwt.getClaimAsString(
                TokenClaimsEnum.AUTHORIZATION_GRANT_TYPE.name());
            if (OAuth2Enum.GRANT_TYPE_PASSWORD.getName().equals(claimAsString)
                || AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(claimAsString)) {
              return tokenRepository.existsById(
                  Long.parseLong(jwt.getClaimAsString(TokenClaimsEnum.ACCOUNT_ID.name())));
            } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(claimAsString)) {
              return clientTokenRepository.existsById(jwt.getClaimAsString("sub"));
            }
            return false;
          } catch (Exception e) {
            LOGGER.error(ResponseCode.INVALID_TOKEN.getMessage(), e);
            return false;
          }
        })
        .orElse(false);
  }
}
