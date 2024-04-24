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

import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.basis.response.ResultResponse;
import com.sky.centaur.log.client.api.OperationLogGrpcService;
import com.sky.centaur.log.client.api.SystemLogGrpcService;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCo;
import com.sky.centaur.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import com.sky.centaur.log.client.api.grpc.SystemLogSubmitGrpcCo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * 自定义AuthenticationEntryPoint
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public class CentaurAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      CentaurAuthenticationEntryPoint.class);


  private final OperationLogGrpcService operationLogGrpcService;

  private final SystemLogGrpcService systemLogGrpcService;

  public CentaurAuthenticationEntryPoint(String loginFormUrl,
      OperationLogGrpcService operationLogGrpcService, SystemLogGrpcService systemLogGrpcService) {
    super(loginFormUrl);
    this.operationLogGrpcService = operationLogGrpcService;
    this.systemLogGrpcService = systemLogGrpcService;
  }

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    if (authException instanceof UsernameNotFoundException usernameNotFoundException) {
      LOGGER.error(ResultCode.ACCOUNT_DOES_NOT_EXIST.getResultCode());
      systemLogGrpcService.submit(SystemLogSubmitGrpcCmd.newBuilder()
          .setSystemLogSubmitCo(
              SystemLogSubmitGrpcCo.newBuilder()
                  .setContent(ResultCode.ACCOUNT_DOES_NOT_EXIST.getResultCode())
                  .setCategory("exception")
                  .setFail(ExceptionUtils.getStackTrace(usernameNotFoundException)).build())
          .build());
      operationLogGrpcService.submit(OperationLogSubmitGrpcCmd.newBuilder()
          .setOperationLogSubmitCo(
              OperationLogSubmitGrpcCo.newBuilder().setContent("AuthenticationEntryPoint")
                  .setBizNo(ResultCode.ACCOUNT_DOES_NOT_EXIST.getResultCode())
                  .setFail(ResultCode.ACCOUNT_DOES_NOT_EXIST.getResultMsg()).build())
          .build());
      response.setStatus(Integer.parseInt(ResultCode.UNAUTHORIZED.getResultCode()));
      ResultResponse.exceptionResponse(response, ResultCode.ACCOUNT_DOES_NOT_EXIST);
    } else if (authException instanceof InvalidBearerTokenException invalidBearerTokenException) {
      LOGGER.error(ResultCode.INVALID_TOKEN.getResultCode());
      systemLogGrpcService.submit(SystemLogSubmitGrpcCmd.newBuilder()
          .setSystemLogSubmitCo(
              SystemLogSubmitGrpcCo.newBuilder()
                  .setContent(ResultCode.INVALID_TOKEN.getResultCode())
                  .setCategory("exception")
                  .setFail(ExceptionUtils.getStackTrace(invalidBearerTokenException)).build())
          .build());
      operationLogGrpcService.submit(OperationLogSubmitGrpcCmd.newBuilder()
          .setOperationLogSubmitCo(
              OperationLogSubmitGrpcCo.newBuilder().setContent("AuthenticationEntryPoint")
                  .setBizNo(ResultCode.INVALID_TOKEN.getResultCode())
                  .setFail(ResultCode.INVALID_TOKEN.getResultMsg()).build())
          .build());
      response.setStatus(Integer.parseInt(ResultCode.UNAUTHORIZED.getResultCode()));
      ResultResponse.exceptionResponse(response, ResultCode.INVALID_TOKEN);
    } else {
      super.commence(request, response, authException);
    }
  }
}
