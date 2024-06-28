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

import com.sky.centaur.log.client.api.grpc.SystemLogServiceGrpc;
import com.sky.centaur.log.client.api.grpc.SystemLogServiceGrpc.SystemLogServiceFutureStub;
import com.sky.centaur.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import io.grpc.ManagedChannel;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import java.util.Optional;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * 系统日志对外提供grpc调用实例
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public class SystemLogGrpcService extends LogGrpcService implements DisposableBean {

  private ManagedChannel channel;

  public SystemLogGrpcService(
      DiscoveryClient discoveryClient,
      ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    super(discoveryClient, grpcClientInterceptorObjectProvider);
  }


  @Override
  public void destroy() {
    Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdown);
  }

  public void submit(SystemLogSubmitGrpcCmd systemLogSubmitGrpcCmd) {
    if (channel == null) {
      getManagedChannelUsePlaintext().ifPresent(managedChannel -> {
        channel = managedChannel;
        submitFromGrpc(systemLogSubmitGrpcCmd);
      });
    } else {
      submitFromGrpc(systemLogSubmitGrpcCmd);
    }
  }

  private void submitFromGrpc(SystemLogSubmitGrpcCmd systemLogSubmitGrpcCmd) {
    SystemLogServiceFutureStub systemLogServiceFutureStub = SystemLogServiceGrpc.newFutureStub(
        channel);
    //noinspection ResultOfMethodCallIgnored
    systemLogServiceFutureStub.submit(systemLogSubmitGrpcCmd);
  }

}
