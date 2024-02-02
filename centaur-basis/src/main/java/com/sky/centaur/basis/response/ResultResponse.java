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
package com.sky.centaur.basis.response;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 响应信息
 *
 * @author 单开宇
 * @since 2024-01-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse<T> implements Serializable {

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

  public static <T> @NotNull ResultResponse<T> failure(BaseResultInterface resultCode, T t) {
    ResultResponse<T> resultResponse = new ResultResponse<>(resultCode);
    resultResponse.setData(t);
    return resultResponse;
  }
}
