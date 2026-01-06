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

import baby.mumu.basis.enums.ServiceEnum;
import io.grpc.ManagedChannel;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.grpc.client.ChannelBuilderOptions;
import org.springframework.grpc.client.GrpcChannelFactory;

import java.time.Duration;
import java.util.Optional;

/**
 * 日志grpc服务
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
class LogGrpcService {

    public static final String GRPC_LOG = ServiceEnum.LOG.getName();
    private final DiscoveryClient discoveryClient;
    private final GrpcChannelFactory grpcChannelFactory;

    public LogGrpcService(DiscoveryClient discoveryClient, GrpcChannelFactory grpcChannelFactory) {
        this.discoveryClient = discoveryClient;
        this.grpcChannelFactory = grpcChannelFactory;
    }

    protected Optional<ManagedChannel> getManagedChannel() {
        if (!serviceAvailable()) {
            return Optional.empty();
        }
        ChannelBuilderOptions opts = ChannelBuilderOptions.defaults()
            .withInterceptorsMerge(true)
            .withShutdownGracePeriod(Duration.ofSeconds(10));

        // 这里传入的是“通道名”，会去读取 spring.grpc.client.channels.storage.* 的配置
        ManagedChannel ch = grpcChannelFactory.createChannel(LogGrpcService.GRPC_LOG, opts);
        return Optional.of(ch);
    }

    protected boolean serviceAvailable() {
        return !discoveryClient.getInstances(LogGrpcService.GRPC_LOG).isEmpty();
    }

}
