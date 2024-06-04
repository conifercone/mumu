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
package com.sky.centaur.basis.exception;

import com.sky.centaur.basis.response.ResultCode;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

/**
 * 顶级异常
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CentaurException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 5646742276995362775L;

  private ResultCode resultCode;

  private Object data;

  public CentaurException(@NotNull ResultCode resultCode) {
    super(resultCode.getResultMsg());
    this.resultCode = resultCode;
  }

  public CentaurException(@NotNull ResultCode resultCode, Object data) {
    super(resultCode.getResultMsg());
    this.resultCode = resultCode;
    this.data = data;
  }
}
