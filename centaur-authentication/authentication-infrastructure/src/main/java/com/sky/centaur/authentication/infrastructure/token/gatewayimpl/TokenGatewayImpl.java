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
