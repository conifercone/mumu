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
package baby.mumu.log.client.api;

import baby.mumu.log.client.api.grpc.SystemLogServiceGrpc;
import baby.mumu.log.client.api.grpc.SystemLogServiceGrpc.SystemLogServiceFutureStub;
import baby.mumu.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import io.grpc.ManagedChannel;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import java.util.Optional;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * 系统日志对外提供grpc调用实例
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
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

  public void syncSubmit(SystemLogSubmitGrpcCmd systemLogSubmitGrpcCmd) {
    Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .ifPresent(ch -> {
        channel = ch;
        syncSubmitFromGrpc(systemLogSubmitGrpcCmd);
      });
  }

  private void syncSubmitFromGrpc(SystemLogSubmitGrpcCmd systemLogSubmitGrpcCmd) {
    SystemLogServiceFutureStub systemLogServiceFutureStub = SystemLogServiceGrpc.newFutureStub(
      channel);
    //noinspection ResultOfMethodCallIgnored
    systemLogServiceFutureStub.submit(systemLogSubmitGrpcCmd);
  }

}
