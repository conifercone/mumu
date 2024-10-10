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
package baby.mumu.extension.processor.response;

import baby.mumu.basis.client.dto.co.ClientObject;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.exception.RateLimiterException;
import baby.mumu.basis.response.ResultCode;
import baby.mumu.basis.response.ResultResponse;
import baby.mumu.extension.translation.SimpleTextTranslation;
import baby.mumu.log.client.api.SystemLogGrpcService;
import baby.mumu.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import baby.mumu.log.client.api.grpc.SystemLogSubmitGrpcCo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@RestControllerAdvice(annotations = RestController.class)
public class ResponseBodyProcessor implements ResponseBodyAdvice<Object> {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final String VOID = "void";
  private static final Logger LOGGER = LoggerFactory.getLogger(ResponseBodyProcessor.class);

  private final SystemLogGrpcService systemLogGrpcService;
  private final SimpleTextTranslation simpleTextTranslation;

  @Autowired
  public ResponseBodyProcessor(SystemLogGrpcService systemLogGrpcService,
      ObjectProvider<SimpleTextTranslation> simpleTextTranslations) {
    this.systemLogGrpcService = systemLogGrpcService;
    this.simpleTextTranslation = simpleTextTranslations.getIfAvailable();
  }

  @ExceptionHandler(MuMuException.class)
  public ResultResponse<?> handleMuMuException(@NotNull MuMuException mumuException,
      @NotNull HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(Charsets.UTF_8.name());
    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    LOGGER.error(mumuException.getMessage(), mumuException);
    systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
        .setSystemLogSubmitCo(
            SystemLogSubmitGrpcCo.newBuilder().setContent(mumuException.getMessage())
                .setCategory("mumuException")
                .setFail(ExceptionUtils.getStackTrace(mumuException)).build())
        .build());
    if (mumuException.getData() != null) {
      return ResultResponse.failure(mumuException.getResultCode(), mumuException.getData());
    }
    return ResultResponse.failure(mumuException.getResultCode());
  }

  @ExceptionHandler(RateLimiterException.class)
  public ResultResponse<?> handleRateLimitingException(
      @NotNull RateLimiterException rateLimiterException,
      @NotNull HttpServletResponse response) {
    ResultCode resultCode = rateLimiterException.getResultCode();
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(Charsets.UTF_8.name());
    response.setStatus(Integer.parseInt(resultCode.getResultCode()));
    LOGGER.error(rateLimiterException.getMessage(), rateLimiterException);
    systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
        .setSystemLogSubmitCo(
            SystemLogSubmitGrpcCo.newBuilder().setContent(rateLimiterException.getMessage())
                .setCategory("rateLimiterException")
                .setFail(ExceptionUtils.getStackTrace(rateLimiterException)).build())
        .build());
    return ResultResponse.failure(rateLimiterException.getResultCode(),
        rateLimiterException.getData());
  }

  @ExceptionHandler(ValidationException.class)
  public ResultResponse<?> handleException(@NotNull ValidationException validationException,
      @NotNull HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(Charsets.UTF_8.name());
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    LOGGER.error(validationException.getMessage(), validationException);
    systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
        .setSystemLogSubmitCo(
            SystemLogSubmitGrpcCo.newBuilder().setContent(validationException.getMessage())
                .setCategory("validationException")
                .setFail(ExceptionUtils.getStackTrace(validationException)).build())
        .build());
    return ResultResponse.failure(String.valueOf(HttpServletResponse.SC_BAD_REQUEST),
        validationException.getMessage()
            .substring(validationException.getMessage().indexOf(": ") + 2));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResultResponse<?> handleException(
      @NotNull HttpMessageNotReadableException httpMessageNotReadableException,
      @NotNull HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(Charsets.UTF_8.name());
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    LOGGER.error(httpMessageNotReadableException.getMessage(), httpMessageNotReadableException);
    systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
        .setSystemLogSubmitCo(
            SystemLogSubmitGrpcCo.newBuilder()
                .setContent(httpMessageNotReadableException.getMessage())
                .setCategory("httpMessageNotReadableException")
                .setFail(ExceptionUtils.getStackTrace(httpMessageNotReadableException)).build())
        .build());
    return ResultResponse.failure(ResultCode.PARAMS_IS_INVALID);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResultResponse<?> handleException(
      @NotNull MethodArgumentNotValidException methodArgumentNotValidException,
      @NotNull HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(Charsets.UTF_8.name());
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    LOGGER.error(methodArgumentNotValidException.getMessage(), methodArgumentNotValidException);
    systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
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

  @ExceptionHandler(IllegalArgumentException.class)
  public ResultResponse<?> handleException(
      @NotNull IllegalArgumentException illegalArgumentException,
      @NotNull HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(Charsets.UTF_8.name());
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    LOGGER.error(illegalArgumentException.getMessage(), illegalArgumentException);
    systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
        .setSystemLogSubmitCo(
            SystemLogSubmitGrpcCo.newBuilder()
                .setContent(illegalArgumentException.getMessage())
                .setCategory("illegalArgumentException")
                .setFail(ExceptionUtils.getStackTrace(illegalArgumentException)).build())
        .build());
    return ResultResponse.failure(String.valueOf(HttpServletResponse.SC_BAD_REQUEST),
        Optional.ofNullable(simpleTextTranslation).flatMap(
                textTranslation -> textTranslation.translateToAccountLanguageIfPossible(
                    illegalArgumentException.getMessage()))
            .orElse(illegalArgumentException.getMessage())
    );
  }

  @ExceptionHandler(Exception.class)
  public ResultResponse<?> handleException(@NotNull Exception exception,
      @NotNull HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(Charsets.UTF_8.name());
    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    LOGGER.error(exception.getMessage(), exception);
    systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
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
      case Slice<?> slice -> ResultResponse.success(slice);
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
