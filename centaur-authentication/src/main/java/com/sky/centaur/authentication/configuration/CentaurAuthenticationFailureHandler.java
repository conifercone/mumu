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
import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.INVALID_SCOPE;
import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.UNSUPPORTED_GRANT_TYPE;

import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.basis.response.ResultResponse;
import com.sky.centaur.basis.tools.IpUtil;
import com.sky.centaur.log.client.api.OperationLogGrpcService;
import com.sky.centaur.log.client.api.SystemLogGrpcService;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCo;
import com.sky.centaur.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import com.sky.centaur.log.client.api.grpc.SystemLogSubmitGrpcCo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * 自定义异常处理
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
public class CentaurAuthenticationFailureHandler implements AuthenticationFailureHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      CentaurAuthenticationFailureHandler.class);

  @Resource
  OperationLogGrpcService operationLogGrpcService;

  @Resource
  SystemLogGrpcService systemLogGrpcService;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException {
    if (exception instanceof OAuth2AuthenticationException oAuth2AuthenticationException) {
      OAuth2Error error = oAuth2AuthenticationException.getError();
      String errorCode = error.getErrorCode();
      systemLogGrpcService.submit(SystemLogSubmitGrpcCmd.newBuilder()
          .setSystemLogSubmitCo(
              SystemLogSubmitGrpcCo.newBuilder().setContent(errorCode)
                  .setCategory("exception")
                  .setFail(ExceptionUtils.getStackTrace(exception)).build())
          .build());
      LOGGER.error(errorCode);
      response.setStatus(Integer.parseInt(ResultCode.UNAUTHORIZED.getResultCode()));
      switch (errorCode) {
        case UNSUPPORTED_GRANT_TYPE ->
            exceptionResponse(response, ResultCode.UNSUPPORTED_GRANT_TYPE, request);
        case INVALID_CLIENT -> exceptionResponse(response, ResultCode.INVALID_CLIENT, request);
        case INVALID_GRANT -> exceptionResponse(response, ResultCode.INVALID_GRANT, request);
        case INVALID_SCOPE -> exceptionResponse(response, ResultCode.INVALID_SCOPE, request);
        default -> {
          ResultResponse.exceptionResponse(response, errorCode, error.getDescription());
          operationFailLog(errorCode, error.getDescription(), IpUtil.getIpAddr(request));
        }
      }
    }
  }

  /**
   * 统一认证异常信息响应
   *
   * @param response   响应
   * @param resultCode 结果编码
   * @param request    请求信息
   * @throws IOException io异常
   */
  private void exceptionResponse(HttpServletResponse response, @NotNull ResultCode resultCode,
      HttpServletRequest request)
      throws IOException {
    ResultResponse.exceptionResponse(response,
        resultCode);
    operationFailLog(resultCode.getResultCode(),
        resultCode.getResultMsg(), IpUtil.getIpAddr(request));
  }

  /**
   * 发送操作日志（失败结果）
   *
   * @param category 类别
   * @param fail     失败信息
   * @param ip       ip地址
   */
  private void operationFailLog(String category, String fail, String ip) {
    operationLogGrpcService.submit(OperationLogSubmitGrpcCmd.newBuilder()
        .setOperationLogSubmitCo(
            OperationLogSubmitGrpcCo.newBuilder().setContent("AuthenticationFailureHandler")
                .setBizNo(ip)
                .setCategory(category)
                .setFail(fail).build())
        .build());
  }
}
