package com.sky.centaur.authentication.infrastructure.token.gatewayimpl;

import com.sky.centaur.authentication.domain.token.gateway.TokenGateway;
import com.sky.centaur.authentication.infrastructure.token.redis.TokenRepository;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * token领域网关实现类
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
@Observed(name = "TokenGatewayImpl")
public class TokenGatewayImpl implements TokenGateway {

  private final TokenRepository tokenRepository;

  @Autowired
  public TokenGatewayImpl(TokenRepository tokenRepository) {
    this.tokenRepository = tokenRepository;
  }

  @Override
  public boolean validity(String token) {
    return Optional.ofNullable(token).map(res -> tokenRepository.existsById(res.hashCode()))
        .orElse(false);
  }
}
