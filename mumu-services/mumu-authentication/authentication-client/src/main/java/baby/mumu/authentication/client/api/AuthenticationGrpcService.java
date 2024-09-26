/*
 * Copyright (c) 2024-2024, the original author or authors.
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
package baby.mumu.authentication.client.api;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * 鉴权grpc服务
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
class AuthenticationGrpcService {

  private final DiscoveryClient discoveryClient;

  private final ObservationGrpcClientInterceptor observationGrpcClientInterceptor;
  private final AtomicInteger currentIndex = new AtomicInteger(0);

  public AuthenticationGrpcService(DiscoveryClient discoveryClient,
      @NotNull ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    this.discoveryClient = discoveryClient;
    this.observationGrpcClientInterceptor = grpcClientInterceptorObjectProvider.getIfAvailable();
  }

  protected Optional<ManagedChannel> getManagedChannelUsePlaintext() {
    //noinspection DuplicatedCode
    return getServiceInstance().map(
        serviceInstance -> {
          ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forAddress(
                  serviceInstance.getHost(),
                  serviceInstance.getPort())
              .usePlaintext();
          Optional.ofNullable(observationGrpcClientInterceptor).ifPresent(builder::intercept);
          return builder.build();
        });
  }

  protected Optional<ServiceInstance> getServiceInstance() {
    List<ServiceInstance> instances = discoveryClient.getInstances("grpc-authentication");
    return Optional.ofNullable(instances)
        .filter(list -> !list.isEmpty())
        .map(list -> list.get(currentIndex.getAndUpdate(i -> (i + 1) % list.size())));
  }
}
