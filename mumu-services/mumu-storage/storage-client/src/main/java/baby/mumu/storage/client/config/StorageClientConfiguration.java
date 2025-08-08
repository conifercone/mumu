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

package baby.mumu.storage.client.config;

import baby.mumu.basis.grpc.resolvers.DiscoveryClientNameResolverProvider;
import baby.mumu.storage.client.api.FileGrpcService;
import io.grpc.NameResolverRegistry;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 存储服务客户端配置
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.14.0
 */
@Configuration
public class StorageClientConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public DiscoveryClientNameResolverProvider discoveryClientNameResolverProvider(
    DiscoveryClient discoveryClient) {
    DiscoveryClientNameResolverProvider discoveryClientNameResolverProvider = new DiscoveryClientNameResolverProvider(
      discoveryClient);
    NameResolverRegistry.getDefaultRegistry().register(discoveryClientNameResolverProvider);
    return discoveryClientNameResolverProvider;
  }

  @Bean
  public FileGrpcService fileGrpcService(DiscoveryClient discoveryClient,
    ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    return new FileGrpcService(discoveryClient, grpcClientInterceptorObjectProvider);
  }

  @Bean
  public ProjectInformationPrint storageClientProjectInformationPrint() {
    return new ProjectInformationPrint();
  }
}
