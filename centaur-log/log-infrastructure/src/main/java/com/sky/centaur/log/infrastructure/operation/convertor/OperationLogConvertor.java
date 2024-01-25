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
package com.sky.centaur.log.infrastructure.operation.convertor;

import com.sky.centaur.log.client.dto.co.OperationLogSubmitCo;
import com.sky.centaur.log.domain.operation.OperationLog;
import com.sky.centaur.log.infrastructure.operation.gatewayimpl.kafka.dataobject.OperationLogDo;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;

/**
 * 操作日志转换器
 *
 * @author 单开宇
 * @since 2024-01-16
 */
public class OperationLogConvertor {


  @Contract("_ -> new")
  public static @NotNull OperationLogDo toDataObject(@NotNull OperationLog operationLog) {
    OperationLogDo operationLogDo = new OperationLogDo();
    BeanUtils.copyProperties(operationLog, operationLogDo);
    return operationLogDo;
  }

  @Contract("_ -> new")
  public static @NotNull OperationLog toEntity(@NotNull OperationLogSubmitCo operationLogSubmitCo) {
    OperationLog operationLog = new OperationLog();
    BeanUtils.copyProperties(operationLogSubmitCo, operationLog);
    return operationLog;
  }

}
