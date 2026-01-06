/*
 * Copyright (c) 2024-2026, the original author or authors.
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

import baby.mumu.log.client.api.grpc.OperationLogServiceGrpc;
import baby.mumu.log.client.api.grpc.OperationLogServiceGrpc.OperationLogServiceFutureStub;
import baby.mumu.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import io.grpc.ManagedChannel;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.grpc.client.GrpcChannelFactory;

import java.util.Optional;

/**
 * 操作日志对外提供grpc调用实例
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Observed(name = "OperationLogGrpcService")
public class OperationLogGrpcService extends LogGrpcService implements DisposableBean {

    private ManagedChannel channel;

    public OperationLogGrpcService(
        DiscoveryClient discoveryClient,
        GrpcChannelFactory grpcChannelFactory) {
        super(discoveryClient, grpcChannelFactory);
    }

    @Override
    public void destroy() {
        Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdown);
    }

    public void syncSubmit(OperationLogSubmitGrpcCmd operationLogSubmitGrpcCmd) {
        Optional.ofNullable(channel)
            .or(this::getManagedChannel)
            .ifPresent(ch -> {
                channel = ch;
                syncSubmitFromGrpc(operationLogSubmitGrpcCmd);
            });
    }

    private void syncSubmitFromGrpc(OperationLogSubmitGrpcCmd operationLogSubmitGrpcCmd) {
        OperationLogServiceFutureStub operationLogServiceFutureStub = OperationLogServiceGrpc.newFutureStub(
            channel);
        // noinspection ResultOfMethodCallIgnored
        operationLogServiceFutureStub.submit(operationLogSubmitGrpcCmd);
    }

}
