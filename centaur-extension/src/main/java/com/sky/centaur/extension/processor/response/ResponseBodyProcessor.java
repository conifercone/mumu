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
import com.google.common.base.Charsets;
import com.sky.centaur.basis.client.dto.co.ClientObject;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.basis.response.ResultResponse;
import com.sky.centaur.log.client.api.SystemLogGrpcService;
import com.sky.centaur.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import com.sky.centaur.log.client.api.grpc.SystemLogSubmitGrpcCo;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import java.util.Objects;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

  private final SystemLogGrpcService systemLogGrpcService;

  @Autowired
  public ResponseBodyProcessor(SystemLogGrpcService systemLogGrpcService) {
    this.systemLogGrpcService = systemLogGrpcService;
  }

  @ExceptionHandler(CentaurException.class)
  public ResultResponse<?> handleCentaurException(@NotNull CentaurException centaurException,
      @NotNull HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(Charsets.UTF_8.name());
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

  @ExceptionHandler(ValidationException.class)
  public ResultResponse<?> handleException(@NotNull ValidationException validationException,
      @NotNull HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(Charsets.UTF_8.name());
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    LOGGER.error(validationException.getMessage());
    systemLogGrpcService.submit(SystemLogSubmitGrpcCmd.newBuilder()
        .setSystemLogSubmitCo(
            SystemLogSubmitGrpcCo.newBuilder().setContent(validationException.getMessage())
                .setCategory("validationException")
                .setFail(ExceptionUtils.getStackTrace(validationException)).build())
        .build());
    return ResultResponse.failure(String.valueOf(HttpServletResponse.SC_BAD_REQUEST),
        validationException.getMessage()
            .substring(validationException.getMessage().indexOf(": ") + 2));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResultResponse<?> handleException(
      @NotNull MethodArgumentNotValidException methodArgumentNotValidException,
      @NotNull HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(Charsets.UTF_8.name());
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    LOGGER.error(methodArgumentNotValidException.getMessage());
    systemLogGrpcService.submit(SystemLogSubmitGrpcCmd.newBuilder()
        .setSystemLogSubmitCo(
            SystemLogSubmitGrpcCo.newBuilder()
                .setContent(methodArgumentNotValidException.getMessage())
                .setCategory("methodArgumentNotValidException")
                .setFail(ExceptionUtils.getStackTrace(methodArgumentNotValidException)).build())
        .build());
    return ResultResponse.failure(String.valueOf(HttpServletResponse.SC_BAD_REQUEST),
        Objects.requireNonNull(methodArgumentNotValidException.getFieldError())
            .getDefaultMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResultResponse<?> handleException(@NotNull Exception exception,
      @NotNull HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(Charsets.UTF_8.name());
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
    return switch (body) {
      case ResultResponse<?> resultResponse -> resultResponse;
      case String string -> {
        try {
          yield objectMapper.writeValueAsString(ResultResponse.success(string));
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      }
      case ClientObject clientObject -> ResultResponse.success(clientObject);
      case Page<?> page -> ResultResponse.success(page);
      case null -> ResultResponse.success();
      default -> body;
    };
  }

  private @NotNull String getReturnName(MethodParameter returnType) {
    if (returnType == null || returnType.getMethod() == null) {
      return "";
    }
    return returnType.getMethod().getReturnType().getName();
  }
}
