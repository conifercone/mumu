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
package baby.mumu.extension.processor.grpc;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.exception.RateLimiterException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.log.client.api.SystemLogGrpcService;
import baby.mumu.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import baby.mumu.log.client.api.grpc.SystemLogSubmitGrpcCo;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
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


  @SuppressWarnings("unused")
  @GRpcExceptionHandler
  public Status handle(MuMuException mumuException,
      @SuppressWarnings("unused") GRpcExceptionScope gRpcExceptionScope) {
    Status internal = Status.INTERNAL;
    if (mumuException != null) {
      LOGGER.error(mumuException.getMessage());
      systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
          .setSystemLogSubmitCo(
              SystemLogSubmitGrpcCo.newBuilder().setContent(mumuException.getMessage())
                  .setCategory("mumuException")
                  .setFail(ExceptionUtils.getStackTrace(mumuException)).build())
          .build());
      internal = internal.withDescription(mumuException.getMessage()).withCause(mumuException);
    }
    return internal;
  }

  @SuppressWarnings("unused")
  @GRpcExceptionHandler
  public Status handle(RateLimiterException rateLimiterException,
      @SuppressWarnings("unused") GRpcExceptionScope gRpcExceptionScope) {
    Status resourceExhausted = Status.RESOURCE_EXHAUSTED;
    if (rateLimiterException != null) {
      LOGGER.error(rateLimiterException.getMessage());
      systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
          .setSystemLogSubmitCo(
              SystemLogSubmitGrpcCo.newBuilder().setContent(rateLimiterException.getMessage())
                  .setCategory("rateLimiterException")
                  .setFail(ExceptionUtils.getStackTrace(rateLimiterException)).build())
          .build());
      resourceExhausted = resourceExhausted.withDescription(rateLimiterException.getMessage())
          .withCause(rateLimiterException);
    }
    return resourceExhausted;
  }

  @SuppressWarnings("unused")
  @GRpcExceptionHandler
  public Status handle(AuthenticationException authenticationException,
      @SuppressWarnings("unused") GRpcExceptionScope scope) {
    Status unauthenticated = Status.UNAUTHENTICATED;
    if (authenticationException != null) {
      LOGGER.error(ResponseCode.UNAUTHORIZED.getMessage());
      systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
          .setSystemLogSubmitCo(
            SystemLogSubmitGrpcCo.newBuilder().setContent(ResponseCode.UNAUTHORIZED.getMessage())
                  .setCategory("exception")
              .setFail(ResponseCode.UNAUTHORIZED.getMessage()).build())
          .build());
      unauthenticated = unauthenticated.withDescription(authenticationException.getMessage())
          .withCause(authenticationException);
    }
    return unauthenticated;
  }
}
