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
package baby.mumu.authentication.configuration;


import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.INVALID_CLIENT;
import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.INVALID_GRANT;
import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.INVALID_SCOPE;
import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.UNSUPPORTED_GRANT_TYPE;

import baby.mumu.basis.kotlin.tools.IpUtil;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.basis.response.ResponseWrapper;
import baby.mumu.log.client.api.OperationLogGrpcService;
import baby.mumu.log.client.api.SystemLogGrpcService;
import baby.mumu.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import baby.mumu.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * 自定义异常处理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
public class MuMuAuthenticationFailureHandler implements AuthenticationFailureHandler {

  private static final Logger logger = LoggerFactory.getLogger(
    MuMuAuthenticationFailureHandler.class);

  private final OperationLogGrpcService operationLogGrpcService;

  private final SystemLogGrpcService systemLogGrpcService;

  @Autowired
  public MuMuAuthenticationFailureHandler(OperationLogGrpcService operationLogGrpcService,
    SystemLogGrpcService systemLogGrpcService) {
    this.operationLogGrpcService = operationLogGrpcService;
    this.systemLogGrpcService = systemLogGrpcService;
  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
    AuthenticationException exception) throws IOException {
    if (exception instanceof OAuth2AuthenticationException oAuth2AuthenticationException) {
      OAuth2Error error = oAuth2AuthenticationException.getError();
      String errorCode = error.getErrorCode();
      systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
        .setContent(errorCode)
        .setCategory("exception")
        .setFail(ExceptionUtils.getStackTrace(exception))
        .build());
      logger.error(errorCode);
      response.setStatus(Integer.parseInt(ResponseCode.UNAUTHORIZED.getCode()));
      switch (errorCode) {
        case UNSUPPORTED_GRANT_TYPE ->
          exceptionResponse(response, ResponseCode.UNSUPPORTED_GRANT_TYPE, request);
        case INVALID_CLIENT -> exceptionResponse(response, ResponseCode.INVALID_CLIENT, request);
        case INVALID_GRANT -> exceptionResponse(response, ResponseCode.INVALID_GRANT, request);
        case INVALID_SCOPE -> exceptionResponse(response, ResponseCode.INVALID_SCOPE, request);
        default -> {
          operationFailLog(errorCode, error.getDescription(), IpUtil.getIpAddr(request));
          ResponseWrapper.exceptionResponse(response, errorCode, error.getDescription());
        }
      }
    }
  }

  /**
   * 统一认证异常信息响应
   *
   * @param response     响应
   * @param responseCode 结果编码
   * @param request      请求信息
   * @throws IOException io异常
   */
  private void exceptionResponse(HttpServletResponse response, @NotNull ResponseCode responseCode,
    HttpServletRequest request)
    throws IOException {
    operationFailLog(responseCode.getCode(),
      responseCode.getMessage(), IpUtil.getIpAddr(request));
    ResponseWrapper.exceptionResponse(response,
      responseCode);
  }

  /**
   * 发送操作日志（失败结果）
   *
   * @param category 类别
   * @param fail     失败信息
   * @param ip       ip地址
   */
  private void operationFailLog(String category, String fail, String ip) {
    operationLogGrpcService.syncSubmit(OperationLogSubmitGrpcCmd.newBuilder()
      .setContent("AuthenticationFailureHandler")
      .setBizNo(ip)
      .setCategory(category)
      .setFail(fail)
      .build());
  }
}
