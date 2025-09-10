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
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.security.core.AuthenticationException;

/**
 * grpc异常统一处理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Configuration
public class GrpcExceptionHandlersConfiguration {

  private static final Logger log = LoggerFactory.getLogger(
    GrpcExceptionHandlersConfiguration.class);

  @Bean
  public GrpcExceptionHandler globalGrpcExceptionHandler(
    SystemLogGrpcService systemLogGrpcService) {
    return exception -> {

      if (exception instanceof RateLimiterException e) {
        GrpcExceptionHandlersConfiguration.log.error(e.getMessage(), e);
        submit(systemLogGrpcService, "RL", e.getMessage(), ExceptionUtils.getStackTrace(e));
        return Status.RESOURCE_EXHAUSTED.withDescription(e.getMessage()).withCause(e).asException();
      }

      if (exception instanceof AuthenticationException e) {
        GrpcExceptionHandlersConfiguration.log.error(ResponseCode.UNAUTHORIZED.getMessage(), e);
        submit(systemLogGrpcService, "AUTHENTICATION",
          ResponseCode.UNAUTHORIZED.getMessage(),
          ResponseCode.UNAUTHORIZED.getMessage());
        return Status.UNAUTHENTICATED.withDescription(e.getMessage()).withCause(e).asException();
      }

      if (exception instanceof MuMuException e) {
        GrpcExceptionHandlersConfiguration.log.error(e.getMessage(), e);
        submit(systemLogGrpcService, "MUMU", e.getMessage(), ExceptionUtils.getStackTrace(e));
        return Status.INTERNAL.withDescription(e.getMessage()).withCause(e).asException();
      }

      // 兜底
      GrpcExceptionHandlersConfiguration.log.error(ResponseCode.INTERNAL_SERVER_ERROR.getMessage(),
        exception);
      submit(systemLogGrpcService, "EXCEPTION",
        ResponseCode.INTERNAL_SERVER_ERROR.getMessage(),
        ResponseCode.INTERNAL_SERVER_ERROR.getMessage());
      return Status.INTERNAL.withDescription(exception.getMessage()).withCause(exception)
        .asException();
    };
  }

  private void submit(SystemLogGrpcService systemLogGrpcService,
    String category,
    String content,
    String fail) {
    try {
      systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
        .setContent(content)
        .setCategory(category)
        .setFail(fail)
        .build());
    } catch (Exception ex) {
      GrpcExceptionHandlersConfiguration.log.warn("systemLogGrpcService.syncSubmit failed: {}",
        ex.getMessage(), ex);
    }
  }
}
