/*
 * Copyright (c) 2024-2026, the original author or authors.
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
import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.exception.RateLimiterException;
import baby.mumu.basis.kotlin.tools.TraceIdUtils;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.basis.response.ResponseWrapper;
import baby.mumu.log.client.api.SystemLogGrpcService;
import baby.mumu.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.nio.charset.StandardCharsets;
import java.util.Objects;

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

    @Autowired
    public ResponseBodyProcessor(SystemLogGrpcService systemLogGrpcService) {
        this.systemLogGrpcService = systemLogGrpcService;
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseWrapper<?> handleApplicationException(
        @NonNull ApplicationException applicationException,
        @NonNull HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(
            applicationException.getResponseCode() != null ? applicationException.getResponseCode()
                .getStatus()
                : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ResponseBodyProcessor.log.error(applicationException.getMessage(), applicationException);
        if (applicationException.getThrowable() != null) {
            ResponseBodyProcessor.log.error(applicationException.getThrowable().getMessage(),
                applicationException.getThrowable());
        }
        systemLogGrpcService.syncSubmit(SystemLogSubmitGrpcCmd.newBuilder()
            .setContent(applicationException.getMessage())
            .setCategory("MUMU")
            .setFail(ExceptionUtils.getStackTrace(applicationException))
            .build());
        if (applicationException.getData() != null) {
            return ResponseWrapper.failure(applicationException.getResponseCode(),
                applicationException.getData());
        }
        return ResponseWrapper.failure(applicationException.getResponseCode());
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
            illegalArgumentException.getMessage()
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
        String traceId = TraceIdUtils.getTraceId();
        if (ResponseBodyProcessor.VOID.equals(getReturnName(returnType))) {
            ResponseWrapper<Object> responseWrapper = ResponseWrapper.success();
            responseWrapper.setTraceId(traceId);
            return responseWrapper;
        }
        return switch (body) {
            case ResponseWrapper<?> responseWrapper -> {
                responseWrapper.setTraceId(traceId);
                yield responseWrapper;
            }
            case DataTransferObject dataTransferObject -> {
                ResponseWrapper<DataTransferObject> responseWrapper = ResponseWrapper.success(
                    dataTransferObject);
                responseWrapper.setTraceId(traceId);
                yield responseWrapper;
            }
            case Page<?> page -> {
                ResponseWrapper<? extends Page<?>> responseWrapper = ResponseWrapper.success(page);
                responseWrapper.setTraceId(traceId);
                yield responseWrapper;
            }
            case Slice<?> slice -> {
                ResponseWrapper<? extends Slice<?>> responseWrapper = ResponseWrapper.success(slice);
                responseWrapper.setTraceId(traceId);
                yield responseWrapper;
            }
            case null -> {
                ResponseWrapper<Object> responseWrapper = ResponseWrapper.success();
                responseWrapper.setTraceId(traceId);
                yield responseWrapper;
            }
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
