package com.sky.centaur.authentication.application.token.executor;

import com.sky.centaur.authentication.client.dto.TokenValidityCmd;
import com.sky.centaur.authentication.client.dto.co.TokenValidityCo;
import com.sky.centaur.authentication.domain.token.gateway.TokenGateway;
import io.micrometer.observation.annotation.Observed;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * token验证指令执行器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
@Observed(name = "TokenValidityCmdExe")
public class TokenValidityCmdExe {

  private final TokenGateway tokenGateway;

  @Autowired
  public TokenValidityCmdExe(TokenGateway tokenGateway) {
    this.tokenGateway = tokenGateway;
  }

  public TokenValidityCo execute(@NotNull TokenValidityCmd tokenValidityCmd) {
    TokenValidityCo tokenValidityCo = tokenValidityCmd.getTokenValidityCo();
    tokenValidityCo.setValidity(tokenGateway.validity(tokenValidityCo.getToken()));
    return tokenValidityCo;
  }
}
