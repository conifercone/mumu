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

import baby.mumu.basis.kotlin.tools.TimeUtils;
import baby.mumu.basis.kotlin.tools.TraceIdUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.Data;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import tools.jackson.databind.json.JsonMapper;

/**
 * 响应信息
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Data
public class ResponseWrapper<T> implements Serializable {

  @Serial
  private static final long serialVersionUID = -630682120634308837L;

  /**
   * 是否成功
   */
  private boolean successful;

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
  private OffsetDateTime timestamp = TimeUtils.convertToAccountZone(
    OffsetDateTime.now(ZoneOffset.UTC));

  /**
   * 响应结果
   */
  private T data;

  /**
   * 全局链路追踪ID
   */
  private String traceId;

  private static final JsonMapper JSON_MAPPER = JsonMapper.builder()
    .build();


  private ResponseWrapper(@NonNull BaseResponse resultCode, boolean successful) {
    this.code = resultCode.getCode();
    this.message = resultCode.getMessage();
    this.successful = successful;
  }

  private ResponseWrapper(@NonNull String code, @NonNull String message, boolean successful) {
    this.code = code;
    this.message = message;
    this.successful = successful;
  }

  public static @NonNull <T> ResponseWrapper<T> success() {
    return new ResponseWrapper<>(ResponseCode.SUCCESS, true);
  }

  public static <T> @NonNull ResponseWrapper<T> success(T t) {
    ResponseWrapper<T> responseWrapper = new ResponseWrapper<>(ResponseCode.SUCCESS, true);
    responseWrapper.setData(t);
    return responseWrapper;
  }

  public static @NonNull <T> ResponseWrapper<T> failure(BaseResponse resultCode) {
    return new ResponseWrapper<>(resultCode, false);
  }

  public static @NonNull <T> ResponseWrapper<T> failure(String code, String message) {
    return new ResponseWrapper<>(code, message, false);
  }

  public static <T> @NonNull ResponseWrapper<T> failure(BaseResponse resultCode, T t) {
    ResponseWrapper<T> responseWrapper = new ResponseWrapper<>(resultCode, false);
    responseWrapper.setData(t);
    return responseWrapper;
  }

  public static void exceptionResponse(@NonNull HttpServletResponse response,
    BaseResponse resultCode)
    throws IOException {
    ResponseWrapper<?> responseResult = ResponseWrapper.failure(resultCode);
    responseResult.setTraceId(TraceIdUtils.getTraceId());
    String jsonResult = ResponseWrapper.JSON_MAPPER.writeValueAsString(responseResult);
    ResponseWrapper.applicationJsonResponse(response, jsonResult);
  }

  private static void applicationJsonResponse(@NonNull HttpServletResponse response,
    String jsonResult)
    throws IOException {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.getWriter().print(jsonResult);
  }

  public static void exceptionResponse(@NonNull HttpServletResponse response,
    String code, String message)
    throws IOException {
    ResponseWrapper<?> responseResult = ResponseWrapper.failure(code, message);
    responseResult.setTraceId(TraceIdUtils.getTraceId());
    String jsonResult = ResponseWrapper.JSON_MAPPER.writeValueAsString(responseResult);
    ResponseWrapper.applicationJsonResponse(response, jsonResult);
  }
}
