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
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 自定义AuthenticationEntryPoint
 *
 * @author 单开宇
 * @since 2024-02-21
 */
@Component
public class CentaurAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Resource
  OperationLogGrpcService operationLogGrpcService;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {
    if (authException instanceof UsernameNotFoundException) {
      operationLogGrpcService.submit(OperationLogSubmitGrpcCmd.newBuilder()
          .setOperationLogSubmitCo(
              OperationLogSubmitGrpcCo.newBuilder().setContent("AuthenticationEntryPoint")
                  .setBizNo(ResultCode.ACCOUNT_DOES_NOT_EXIST.getResultCode())
                  .setFail(ResultCode.ACCOUNT_DOES_NOT_EXIST.getResultMsg()).build())
          .build());
      ResultResponse.exceptionResponse(response, ResultCode.ACCOUNT_DOES_NOT_EXIST);
    }
  }
}
