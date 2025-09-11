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

package baby.mumu.extension.processor.response;

import baby.mumu.basis.dto.DataTransferObject;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.exception.RateLimiterException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.basis.response.ResponseWrapper;
import baby.mumu.extension.translation.SimpleTextTranslation;
import baby.mumu.log.client.api.SystemLogGrpcService;
import baby.mumu.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jspecify.annotations.NonNull;
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
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 响应处理器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@RestControllerAdvice(annotations = RestController.class)
public class ResponseBodyProcessor implements ResponseBodyAdvice<Object> {

  private static final String VOID = "void";
  private static final Logger log = LoggerFactory.getLogger(ResponseBodyProcessor.class);

  private final SystemLogGrpcService systemLogGrpcService;
  private final SimpleTextTranslation simpleTextTranslation;

  @Autowired
  public ResponseBodyProcessor(SystemLogGrpcService systemLogGrpcService,
    ObjectProvider<SimpleTextTranslation> simpleTextTranslations) {
    this.systemLogGrpcService = systemLogGrpcService;
    this.simpleTextTranslation = simpleTextTranslations.getIfAvailable();
  }

  @ExceptionHandler(MuMuException.class)
  public ResponseWrapper<?> handleMuMuException(@NonNull MuMuException mumuException,
    @NonNull HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setStatus(
      mumuException.getResponseCode() != null ? mumuException.getResponseCode().getStatus()
        : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    ResponseBodyProcessor.log.error(mumuException.getMessage(), mumuException);
    if (mumuException.getThrowable() != null) {
      ResponseBodyProcessor.log.error(mumuException.getThrowable().getMessage(),
        mumuException.getThrowable());
    }
    systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
      .setContent(mumuException.getMessage())
      .setCategory("MUMU")
      .setFail(ExceptionUtils.getStackTrace(mumuException))
      .build());
    if (mumuException.getData() != null) {
      return ResponseWrapper.failure(mumuException.getResponseCode(), mumuException.getData());
    }
    return ResponseWrapper.failure(mumuException.getResponseCode());
  }

  @ExceptionHandler(RateLimiterException.class)
  public ResponseWrapper<?> handleRateLimitingException(
    @NonNull RateLimiterException rateLimiterException,
    @NonNull HttpServletResponse response) {
    ResponseCode responseCode = rateLimiterException.getResponseCode();
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setStatus(responseCode.getStatus());
    ResponseBodyProcessor.log.error(rateLimiterException.getMessage(), rateLimiterException);
    systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
      .setContent(rateLimiterException.getMessage())
      .setCategory("RL")
      .setFail(ExceptionUtils.getStackTrace(rateLimiterException))
      .build());
    return ResponseWrapper.failure(rateLimiterException.getResponseCode(),
      rateLimiterException.getData());
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseWrapper<?> handleException(@NonNull ValidationException validationException,
    @NonNull HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    ResponseBodyProcessor.log.error(validationException.getMessage(), validationException);
    systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
      .setContent(validationException.getMessage())
      .setCategory("VALIDATION")
      .setFail(ExceptionUtils.getStackTrace(validationException))
      .build());
    return ResponseWrapper.failure(String.valueOf(HttpServletResponse.SC_BAD_REQUEST),
      validationException.getMessage()
        .substring(validationException.getMessage().indexOf(": ") + 2));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseWrapper<?> handleException(
    @NonNull HttpMessageNotReadableException httpMessageNotReadableException,
    @NonNull HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setStatus(ResponseCode.PARAMS_IS_INVALID.getStatus());
    ResponseBodyProcessor.log.error(httpMessageNotReadableException.getMessage(),
      httpMessageNotReadableException);
    systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
      .setContent(httpMessageNotReadableException.getMessage())
      .setCategory("HTTP_MESSAGE_NOT_READABLE")
      .setFail(ExceptionUtils.getStackTrace(httpMessageNotReadableException))
      .build());
    return ResponseWrapper.failure(ResponseCode.PARAMS_IS_INVALID);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseWrapper<?> handleException(
    @NonNull MethodArgumentNotValidException methodArgumentNotValidException,
    @NonNull HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    ResponseBodyProcessor.log.error(methodArgumentNotValidException.getMessage(),
      methodArgumentNotValidException);
    systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
      .setContent(methodArgumentNotValidException.getMessage())
      .setCategory("METHOD_ARGUMENT_NOT_VALID")
      .setFail(ExceptionUtils.getStackTrace(methodArgumentNotValidException))
      .build());
    return ResponseWrapper.failure(String.valueOf(HttpServletResponse.SC_BAD_REQUEST),
      Objects.requireNonNull(methodArgumentNotValidException.getFieldError())
        .getDefaultMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseWrapper<?> handleException(
    @NonNull IllegalArgumentException illegalArgumentException,
    @NonNull HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    ResponseBodyProcessor.log.error(illegalArgumentException.getMessage(),
      illegalArgumentException);
    systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
      .setContent(illegalArgumentException.getMessage())
      .setCategory("ILLEGAL_ARGUMENT")
      .setFail(ExceptionUtils.getStackTrace(illegalArgumentException))
      .build());
    return ResponseWrapper.failure(String.valueOf(HttpServletResponse.SC_BAD_REQUEST),
      Optional.ofNullable(simpleTextTranslation).flatMap(
          textTranslation -> textTranslation.translateToAccountLanguageIfPossible(
            illegalArgumentException.getMessage()))
        .orElse(illegalArgumentException.getMessage())
    );
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseWrapper<?> handleException(
    @NonNull MissingServletRequestParameterException missingServletRequestParameterException,
    @NonNull HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setStatus(ResponseCode.REQUEST_MISSING_NECESSARY_PARAMETERS.getStatus());
    ResponseBodyProcessor.log.error(missingServletRequestParameterException.getMessage(),
      missingServletRequestParameterException);
    systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
      .setContent(missingServletRequestParameterException.getMessage())
      .setCategory("MISSING_SERVLET_REQUEST_PARAMETER")
      .setFail(ExceptionUtils.getStackTrace(missingServletRequestParameterException))
      .build());
    return ResponseWrapper.failure(ResponseCode.REQUEST_MISSING_NECESSARY_PARAMETERS,
      missingServletRequestParameterException.getParameterName());
  }

  @ExceptionHandler(Exception.class)
  public ResponseWrapper<?> handleException(@NonNull Exception exception,
    @NonNull HttpServletResponse response) {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setStatus(ResponseCode.INTERNAL_SERVER_ERROR.getStatus());
    ResponseBodyProcessor.log.error(exception.getMessage(), exception);
    systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
      .setContent(exception.getMessage())
      .setCategory("EXCEPTION")
      .setFail(ExceptionUtils.getStackTrace(exception))
      .build());
    return ResponseWrapper.failure(ResponseCode.INTERNAL_SERVER_ERROR);
  }

  @Override
  public boolean supports(@NonNull MethodParameter returnType,
    @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
    // 只对本项目生效
    Class<?> controllerClass = returnType.getContainingClass();
    return controllerClass.getPackageName().startsWith("baby.mumu");
  }

  @Override
  public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType,
    @NonNull MediaType selectedContentType,
    @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
    @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
    if (ResponseBodyProcessor.VOID.equals(getReturnName(returnType))) {
      return ResponseWrapper.success();
    }
    return switch (body) {
      case ResponseWrapper<?> responseWrapper -> responseWrapper;
      case DataTransferObject dataTransferObject -> ResponseWrapper.success(dataTransferObject);
      case Page<?> page -> ResponseWrapper.success(page);
      case Slice<?> slice -> ResponseWrapper.success(slice);
      case null -> ResponseWrapper.success();
      default -> body;
    };
  }

  private @NonNull String getReturnName(MethodParameter returnType) {
    if (returnType == null || returnType.getMethod() == null) {
      return StringUtils.EMPTY;
    }
    return returnType.getMethod().getReturnType().getName();
  }
}
