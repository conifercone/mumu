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
package baby.mumu.basis.response;

import baby.mumu.basis.filters.TraceIdFilter;
import baby.mumu.basis.kotlin.tools.CommonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.Data;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;

/**
 * 响应信息
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
public class ResponseWrapper<T> implements Serializable {

  @Serial
  private static final long serialVersionUID = -630682120634308837L;

  /**
   * 是否成功
   */
  private boolean success;

  /**
   * 响应代码
   */
  private String code;

  /**
   * 响应消息
   */
  private String message;

  /**
   * 响应时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime timestamp = CommonUtil.convertToAccountZone(
    OffsetDateTime.now(ZoneOffset.UTC));

  /**
   * 响应结果
   */
  private T data;

  /**
   * traceId
   */
  private String traceId = TraceIdFilter.getTraceId();


  private ResponseWrapper(@NotNull BaseResponse resultCode, boolean success) {
    this.code = resultCode.getCode();
    this.message = resultCode.getMessage();
    this.success = success;
  }

  private ResponseWrapper(@NotNull String code, @NotNull String message, boolean success) {
    this.code = code;
    this.message = message;
    this.success = success;
  }

  @Contract(" -> new")
  public static @NotNull <T> ResponseWrapper<T> success() {
    return new ResponseWrapper<>(ResponseCode.SUCCESS, true);
  }

  public static <T> @NotNull ResponseWrapper<T> success(T t) {
    ResponseWrapper<T> responseWrapper = new ResponseWrapper<>(ResponseCode.SUCCESS, true);
    responseWrapper.setData(t);
    return responseWrapper;
  }

  @Contract("_ -> new")
  public static @NotNull <T> ResponseWrapper<T> failure(BaseResponse resultCode) {
    return new ResponseWrapper<>(resultCode, false);
  }

  public static @NotNull <T> ResponseWrapper<T> failure(String code, String message) {
    return new ResponseWrapper<>(code, message, false);
  }

  public static <T> @NotNull ResponseWrapper<T> failure(BaseResponse resultCode, T t) {
    ResponseWrapper<T> responseWrapper = new ResponseWrapper<>(resultCode, false);
    responseWrapper.setData(t);
    return responseWrapper;
  }

  public static void exceptionResponse(@NotNull HttpServletResponse response,
    BaseResponse resultCode)
    throws IOException {
    ResponseWrapper<?> responseResult = ResponseWrapper.failure(resultCode);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String jsonResult = objectMapper.writeValueAsString(responseResult);
    applicationJsonResponse(response, jsonResult);
  }

  private static void applicationJsonResponse(@NotNull HttpServletResponse response,
    String jsonResult)
    throws IOException {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.getWriter().print(jsonResult);
  }

  public static void exceptionResponse(@NotNull HttpServletResponse response,
    String code, String message)
    throws IOException {
    ResponseWrapper<?> responseResult = ResponseWrapper.failure(code, message);
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonResult = objectMapper.writeValueAsString(responseResult);
    applicationJsonResponse(response, jsonResult);
  }
}
