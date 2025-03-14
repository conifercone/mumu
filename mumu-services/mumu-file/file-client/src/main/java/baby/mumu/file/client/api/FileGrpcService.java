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
package baby.mumu.file.client.api;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * 文件grpc服务
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 1.0.1
 */
class FileGrpcService {

  public static final String GRPC_FILE = "file";
  private final DiscoveryClient discoveryClient;

  private final ObservationGrpcClientInterceptor observationGrpcClientInterceptor;

  public FileGrpcService(DiscoveryClient discoveryClient,
    @NotNull ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    this.discoveryClient = discoveryClient;
    this.observationGrpcClientInterceptor = grpcClientInterceptorObjectProvider.getIfAvailable();
  }

  protected Optional<ManagedChannel> getManagedChannelUsePlaintext() {
    //noinspection DuplicatedCode
    return Optional.of(serviceAvailable()).filter(Boolean::booleanValue).map(
      _ -> {
        ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forTarget(
            "discovery-client://" + GRPC_FILE)
          .defaultLoadBalancingPolicy("round_robin")
          .usePlaintext();
        Optional.ofNullable(observationGrpcClientInterceptor).ifPresent(builder::intercept);
        return builder.build();
      });
  }

  protected boolean serviceAvailable() {
    return !discoveryClient.getInstances(GRPC_FILE).isEmpty();
  }

}
