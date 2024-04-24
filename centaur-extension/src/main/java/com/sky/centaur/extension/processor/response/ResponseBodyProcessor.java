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
package com.sky.centaur.extension.processor.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.centaur.basis.client.dto.co.ClientObject;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.basis.response.ResultResponse;
import com.sky.centaur.log.client.api.SystemLogGrpcService;
import com.sky.centaur.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import com.sky.centaur.log.client.api.grpc.SystemLogSubmitGrpcCo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 响应处理器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@RestControllerAdvice(annotations = RestController.class)
public class ResponseBodyProcessor implements ResponseBodyAdvice<Object> {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final String VOID = "void";
  private static final Logger LOGGER = LoggerFactory.getLogger(ResponseBodyProcessor.class);

  @Resource
  private SystemLogGrpcService systemLogGrpcService;


  @ExceptionHandler(CentaurException.class)
  public ResultResponse<?> handleCentaurException(@NotNull CentaurException centaurException,
      @NotNull HttpServletResponse response) {
    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    LOGGER.error(centaurException.getMessage());
    systemLogGrpcService.submit(SystemLogSubmitGrpcCmd.newBuilder()
        .setSystemLogSubmitCo(
            SystemLogSubmitGrpcCo.newBuilder().setContent(centaurException.getMessage())
                .setCategory("centaurException")
                .setFail(ExceptionUtils.getStackTrace(centaurException)).build())
        .build());
    if (centaurException.getData() != null) {
      return ResultResponse.failure(centaurException.getResultCode(), centaurException.getData());
    }
    return ResultResponse.failure(centaurException.getResultCode());
  }

  @ExceptionHandler(Exception.class)
  public ResultResponse<?> handleException(@NotNull Exception exception,
      @NotNull HttpServletResponse response) {
    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    LOGGER.error(exception.getMessage());
    systemLogGrpcService.submit(SystemLogSubmitGrpcCmd.newBuilder()
        .setSystemLogSubmitCo(
            SystemLogSubmitGrpcCo.newBuilder().setContent(exception.getMessage())
                .setCategory("exception")
                .setFail(ExceptionUtils.getStackTrace(exception)).build())
        .build());
    return ResultResponse.failure(ResultCode.INTERNAL_SERVER_ERROR);
  }

  @Override
  public boolean supports(@NotNull MethodParameter returnType,
      @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(Object body, @NotNull MethodParameter returnType,
      @NotNull MediaType selectedContentType,
      @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
      @NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response) {
    if (VOID.equals(getReturnName(returnType))) {
      return ResultResponse.success();
    }
    if (body instanceof ResultResponse) {
      return body;
    } else if (body instanceof String) {
      // string类型返回要单独json序列化返回一下，不然会报转换异常
      try {
        return objectMapper.writeValueAsString(ResultResponse.success(body));
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    } else if (body != null) {
      if (body instanceof ClientObject) {
        return ResultResponse.success(body);
      } else if (body instanceof Page<?>) {
        return ResultResponse.success(body);
      }
      return body;
    } else {
      return ResultResponse.success();
    }
  }

  private @NotNull String getReturnName(MethodParameter returnType) {
    if (returnType == null || returnType.getMethod() == null) {
      return "";
    }
    return returnType.getMethod().getReturnType().getName();
  }
}
