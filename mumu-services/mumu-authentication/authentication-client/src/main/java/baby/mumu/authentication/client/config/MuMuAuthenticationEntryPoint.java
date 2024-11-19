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
package baby.mumu.authentication.client.config;

import baby.mumu.basis.response.ResponseCode;
import baby.mumu.basis.response.ResponseWrapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * 自定义AuthenticationEntryPoint
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public class MuMuAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

  private static final Logger logger = LoggerFactory.getLogger(
    MuMuAuthenticationEntryPoint.class);


  public MuMuAuthenticationEntryPoint(@NotNull ResourceServerProperties resourceServerProperties) {
    super(resourceServerProperties.getLoginAddress());
  }

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
    AuthenticationException authException) throws IOException, ServletException {
    switch (authException) {
      case UsernameNotFoundException usernameNotFoundException -> {
        logger.error(ResponseCode.ACCOUNT_DOES_NOT_EXIST.getMessage(), usernameNotFoundException);
        response.setStatus(Integer.parseInt(ResponseCode.UNAUTHORIZED.getCode()));
        ResponseWrapper.exceptionResponse(response, ResponseCode.ACCOUNT_DOES_NOT_EXIST);
      }
      case InvalidBearerTokenException invalidBearerTokenException -> {
        logger.error(ResponseCode.INVALID_TOKEN.getMessage(), invalidBearerTokenException);
        response.setStatus(Integer.parseInt(ResponseCode.UNAUTHORIZED.getCode()));
        ResponseWrapper.exceptionResponse(response, ResponseCode.INVALID_TOKEN);
      }
      case InsufficientAuthenticationException insufficientAuthenticationException -> {
        logger.error(ResponseCode.INSUFFICIENT_AUTHENTICATION.getMessage(),
          insufficientAuthenticationException);
        response.setStatus(Integer.parseInt(ResponseCode.UNAUTHORIZED.getCode()));
        ResponseWrapper.exceptionResponse(response, ResponseCode.INSUFFICIENT_AUTHENTICATION);
      }
      case null, default -> super.commence(request, response, authException);
    }
  }
}
