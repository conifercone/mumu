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
package baby.mumu.basis.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse<T> implements Serializable {

  @Serial
  private static final long serialVersionUID = -630682120634308837L;
  /**
   * 响应代码
   */
  private String code;
  /**
   * 响应消息
   */
  private String message;
  /**
   * 响应结果
   */
  private T data;

  public ResultResponse(@NotNull BaseResultInterface resultCode) {
    this.code = resultCode.getResultCode();
    this.message = resultCode.getResultMsg();
  }

  public ResultResponse(@NotNull String code, @NotNull String message) {
    this.code = code;
    this.message = message;
  }

  @Contract(" -> new")
  public static @NotNull ResultResponse<?> success() {
    return new ResultResponse<>(ResultCode.SUCCESS);
  }

  public static <T> @NotNull ResultResponse<T> success(T t) {
    ResultResponse<T> resultResponse = new ResultResponse<>(ResultCode.SUCCESS);
    resultResponse.setData(t);
    return resultResponse;
  }

  @Contract("_ -> new")
  public static @NotNull ResultResponse<?> failure(BaseResultInterface resultCode) {
    return new ResultResponse<>(resultCode);
  }

  public static @NotNull ResultResponse<?> failure(String code, String message) {
    return new ResultResponse<>(code, message);
  }

  public static <T> @NotNull ResultResponse<T> failure(BaseResultInterface resultCode, T t) {
    ResultResponse<T> resultResponse = new ResultResponse<>(resultCode);
    resultResponse.setData(t);
    return resultResponse;
  }

  public static void exceptionResponse(@NotNull HttpServletResponse response,
      BaseResultInterface resultCode)
      throws IOException {
    ResultResponse<?> responseResult = ResultResponse.failure(resultCode);
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonResult = objectMapper.writeValueAsString(responseResult);
    applicationJsonResponse(response, jsonResult);
  }

  private static void applicationJsonResponse(@NotNull HttpServletResponse response,
      String jsonResult)
      throws IOException {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(Charsets.UTF_8.name());
    response.getWriter().print(jsonResult);
  }

  public static void exceptionResponse(@NotNull HttpServletResponse response,
      String code, String message)
      throws IOException {
    ResultResponse<?> responseResult = ResultResponse.failure(code, message);
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonResult = objectMapper.writeValueAsString(responseResult);
    applicationJsonResponse(response, jsonResult);
  }
}
