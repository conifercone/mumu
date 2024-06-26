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

package com.sky.centaur.log.application.operation.executor;

import com.expediagroup.beans.BeanUtils;
import com.expediagroup.beans.transformer.BeanTransformer;
import com.sky.centaur.log.client.dto.OperationLogQryCmd;
import com.sky.centaur.log.client.dto.co.OperationLogQryCo;
import com.sky.centaur.log.domain.operation.gateway.OperationLogGateway;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 操作日志查询指令
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Component
public class OperationLogQryCmdExe {

  private final OperationLogGateway operationLogGateway;

  private static final BeanTransformer BEAN_TRANSFORMER = new BeanUtils().getTransformer()
      .setDefaultValueForMissingField(true)
      .setDefaultValueForMissingPrimitiveField(false);

  @Autowired
  public OperationLogQryCmdExe(OperationLogGateway operationLogGateway) {
    this.operationLogGateway = operationLogGateway;
  }

  public OperationLogQryCo execute(@NotNull OperationLogQryCmd operationLogQryCmd) {
    AtomicReference<OperationLogQryCo> operationLogQryCo = new AtomicReference<>();
    operationLogGateway.findOperationLogById(
        operationLogQryCmd.getId()).ifPresent(operationLog -> {
      BEAN_TRANSFORMER.resetFieldsTransformationSkip();
      OperationLogQryCo operationLogQryCoTmp = BEAN_TRANSFORMER.transform(operationLog,
          OperationLogQryCo.class);
      operationLogQryCo.set(operationLogQryCoTmp);
    });
    return operationLogQryCo.get();
  }

}
