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
package com.sky.centaur.extension.processor.grpc;

import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.log.client.api.SystemLogGrpcService;
import com.sky.centaur.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import com.sky.centaur.log.client.api.grpc.SystemLogSubmitGrpcCo;
import io.grpc.Status;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.lognet.springboot.grpc.recovery.GRpcExceptionHandler;
import org.lognet.springboot.grpc.recovery.GRpcExceptionScope;
import org.lognet.springboot.grpc.recovery.GRpcServiceAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;

/**
 * grpc异常统一处理
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@GRpcServiceAdvice
public class GrpcExceptionAdvice {

  private final SystemLogGrpcService systemLogGrpcService;

  @Autowired
  public GrpcExceptionAdvice(SystemLogGrpcService systemLogGrpcService) {
    this.systemLogGrpcService = systemLogGrpcService;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(GrpcExceptionAdvice.class);


  @GRpcExceptionHandler
  public Status handle(CentaurException centaurException,
      @SuppressWarnings("unused") GRpcExceptionScope gRpcExceptionScope) {
    if (centaurException != null) {
      LOGGER.error(centaurException.getMessage());
      systemLogGrpcService.submit(SystemLogSubmitGrpcCmd.newBuilder()
          .setSystemLogSubmitCo(
              SystemLogSubmitGrpcCo.newBuilder().setContent("CentaurException")
                  .setCategory("centaurException")
                  .setFail(ExceptionUtils.getStackTrace(centaurException)).build())
          .build());
    }
    return Status.INTERNAL;
  }

  @GRpcExceptionHandler
  public Status handle(AuthenticationException authenticationException,
      @SuppressWarnings("unused") GRpcExceptionScope scope) {
    if (authenticationException != null) {
      LOGGER.error(ResultCode.UNAUTHORIZED.getResultMsg());
      systemLogGrpcService.submit(SystemLogSubmitGrpcCmd.newBuilder()
          .setSystemLogSubmitCo(
              SystemLogSubmitGrpcCo.newBuilder().setContent(ResultCode.UNAUTHORIZED.getResultCode())
                  .setCategory("exception")
                  .setFail(ResultCode.UNAUTHORIZED.getResultMsg()).build())
          .build());
    }
    return Status.UNAUTHENTICATED;
  }
}
