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

/**
 * 数据转换异常
 *
 * @author 单开宇
 * @since 2024-01-20
 */
public class DataConversionException extends CentaurException {

  public DataConversionException() {
    super(ResultCode.OPERATION_LOG_KAFKA_MESSAGE_CONVERSION_FAILED);
  }
}
