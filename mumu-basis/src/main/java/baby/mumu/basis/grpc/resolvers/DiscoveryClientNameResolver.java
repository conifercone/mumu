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

package baby.mumu.basis.grpc.resolvers;

import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.StatusOr;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * 服务发现客户端名称解析器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.1.0
 */
public class DiscoveryClientNameResolver extends NameResolver {

  private final String serviceName;
  private Listener2 listener;
  private final DiscoveryClient discoveryClient;
  private final String GRPC_PORT_META_KEY = "gRPC_port";

  public DiscoveryClientNameResolver(String serviceName, DiscoveryClient discoveryClient) {
    this.serviceName = serviceName;
    this.discoveryClient = discoveryClient;
  }

  @Override
  public String getServiceAuthority() {
    return serviceName;
  }

  @Override
  public void start(Listener2 listener) {
    this.listener = listener;
    init();
  }

  @Override
  public void refresh() {
    init();
  }

  private void init() {
    listener.onResult(ResolutionResult.newBuilder()
      .setAddressesOrError(StatusOr.fromValue(fetchAddresses()))
      .setAttributes(Attributes.EMPTY)
      .build());
  }

  /**
   * This method is intentionally left blank because No operation needed for this implementation.
   */
  @Override
  public void shutdown() {
    // No operation needed for this implementation.
  }

  @Contract(" -> new")
  private @NotNull @Unmodifiable List<EquivalentAddressGroup> fetchAddresses() {
    return List.of(new EquivalentAddressGroup(Optional.ofNullable(
        discoveryClient.getInstances(serviceName)).map(
        serviceInstances -> serviceInstances.stream().map(
            serviceInstance -> (SocketAddress) new InetSocketAddress(serviceInstance.getHost(),
              Integer.parseInt(serviceInstance.getMetadata().get(GRPC_PORT_META_KEY))))
          .collect(Collectors.toList()))
      .orElse(new ArrayList<>())));
  }
}
