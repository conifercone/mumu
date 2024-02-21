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

package com.sky.centaur.authentication.configuration;


import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.INVALID_CLIENT;
import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.INVALID_GRANT;
import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.UNSUPPORTED_GRANT_TYPE;

import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.basis.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * 自定义异常处理
 *
 * @author 单开宇
 * @since 2024-02-21
 */
@Component
public class CentaurAuthenticationFailureHandler implements AuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException {
    if (exception instanceof OAuth2AuthenticationException oAuth2AuthenticationException) {
      OAuth2Error error = oAuth2AuthenticationException.getError();
      String errorCode = error.getErrorCode();
      switch (errorCode) {
        case UNSUPPORTED_GRANT_TYPE -> ResultResponse.exceptionResponse(response,
            ResultCode.UNSUPPORTED_GRANT_TYPE);
        case INVALID_CLIENT -> ResultResponse.exceptionResponse(response,
            ResultCode.INVALID_CLIENT);
        case INVALID_GRANT -> ResultResponse.exceptionResponse(response,
            ResultCode.INVALID_GRANT);
        default -> ResultResponse.exceptionResponse(response, errorCode, error.getDescription());
      }
    }

  }
}

