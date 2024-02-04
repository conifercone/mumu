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
package com.sky.centaur.log.client.api;

import com.sky.centaur.log.client.api.grpc.OperationLogServiceGrpc;
import com.sky.centaur.log.client.api.grpc.OperationLogServiceGrpc.OperationLogServiceFutureStub;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import io.grpc.ManagedChannel;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

/**
 * 操作日志对外提供grpc调用实例
 *
 * @author 单开宇
 * @since 2024-01-25
 */
@Component
public class OperationLogGrpcService extends LogGrpcService implements DisposableBean {

  private ManagedChannel channel;

  @Override
  public void destroy() {
    channel.shutdown();
  }

  public void submit(OperationLogSubmitGrpcCmd operationLogSubmitGrpcCmd) {
    if (channel == null) {
      getManagedChannelUsePlaintext().ifPresent(managedChannel -> {
        channel = managedChannel;
        extracted(operationLogSubmitGrpcCmd);
      });
    } else {
      extracted(operationLogSubmitGrpcCmd);
    }

  }

  private void extracted(OperationLogSubmitGrpcCmd operationLogSubmitGrpcCmd) {
    OperationLogServiceFutureStub operationLogServiceFutureStub = OperationLogServiceGrpc.newFutureStub(
        channel);
    //noinspection ResultOfMethodCallIgnored
    operationLogServiceFutureStub.submit(operationLogSubmitGrpcCmd);
  }

}
