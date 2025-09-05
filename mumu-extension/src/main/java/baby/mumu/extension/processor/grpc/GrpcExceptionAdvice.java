/*
 * Copyright (c) 2024-2025, the original author or authors.
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
import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;

/**
 * grpc异常统一处理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@GrpcAdvice
@SuppressWarnings("ClassCanBeRecord")
public class GrpcExceptionAdvice {

  private final SystemLogGrpcService systemLogGrpcService;

  @Autowired
  public GrpcExceptionAdvice(SystemLogGrpcService systemLogGrpcService) {
    this.systemLogGrpcService = systemLogGrpcService;
  }

  private static final Logger log = LoggerFactory.getLogger(GrpcExceptionAdvice.class);


  @SuppressWarnings("unused")
  @GrpcExceptionHandler
  public Status handle(MuMuException mumuException) {
    Status internal = Status.INTERNAL;
    if (mumuException != null) {
      GrpcExceptionAdvice.log.error(mumuException.getMessage(), mumuException);
      systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
        .setContent(mumuException.getMessage())
        .setCategory("MUMU")
        .setFail(ExceptionUtils.getStackTrace(mumuException))
        .build());
      internal = internal.withDescription(mumuException.getMessage()).withCause(mumuException);
    }
    return internal;
  }

  @SuppressWarnings("unused")
  @GrpcExceptionHandler
  public Status handle(RateLimiterException rateLimiterException) {
    Status resourceExhausted = Status.RESOURCE_EXHAUSTED;
    if (rateLimiterException != null) {
      GrpcExceptionAdvice.log.error(rateLimiterException.getMessage(), rateLimiterException);
      systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
        .setContent(rateLimiterException.getMessage())
        .setCategory("RL")
        .setFail(ExceptionUtils.getStackTrace(rateLimiterException))
        .build());
      resourceExhausted = resourceExhausted.withDescription(rateLimiterException.getMessage())
        .withCause(rateLimiterException);
    }
    return resourceExhausted;
  }

  @SuppressWarnings("unused")
  @GrpcExceptionHandler
  public Status handle(AuthenticationException authenticationException) {
    Status unauthenticated = Status.UNAUTHENTICATED;
    if (authenticationException != null) {
      GrpcExceptionAdvice.log.error(ResponseCode.UNAUTHORIZED.getMessage(),
        authenticationException);
      systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
        .setContent(ResponseCode.UNAUTHORIZED.getMessage())
        .setCategory("AUTHENTICATION")
        .setFail(ResponseCode.UNAUTHORIZED.getMessage())
        .build());
      unauthenticated = unauthenticated.withDescription(authenticationException.getMessage())
        .withCause(authenticationException);
    }
    return unauthenticated;
  }

  @SuppressWarnings("unused")
  @GrpcExceptionHandler
  public Status handle(Exception exception) {
    Status internal = Status.INTERNAL;
    if (exception != null) {
      GrpcExceptionAdvice.log.error(ResponseCode.INTERNAL_SERVER_ERROR.getMessage(),
        exception);
      systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
        .setContent(ResponseCode.INTERNAL_SERVER_ERROR.getMessage())
        .setCategory("EXCEPTION")
        .setFail(ResponseCode.INTERNAL_SERVER_ERROR.getMessage())
        .build());
      internal = internal.withDescription(exception.getMessage())
        .withCause(exception);
    }
    return internal;
  }
}
