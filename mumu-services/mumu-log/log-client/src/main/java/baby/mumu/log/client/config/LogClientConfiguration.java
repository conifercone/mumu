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

package baby.mumu.log.client.config;

import baby.mumu.basis.grpc.resolvers.DiscoveryClientNameResolverProvider;
import baby.mumu.log.client.api.OperationLogGrpcService;
import baby.mumu.log.client.api.SystemLogGrpcService;
import io.grpc.NameResolverRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

/**
 * 日志客户端配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Configuration
public class LogClientConfiguration {

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
  public OperationLogGrpcService operationLogGrpcService(DiscoveryClient discoveryClient,
    GrpcChannelFactory grpcChannelFactory) {
    return new OperationLogGrpcService(discoveryClient, grpcChannelFactory);
  }

  @Bean
  public SystemLogGrpcService systemLogGrpcService(DiscoveryClient discoveryClient,
    GrpcChannelFactory grpcChannelFactory) {
    return new SystemLogGrpcService(discoveryClient, grpcChannelFactory);
  }

  @Bean
  public ProjectInformationPrint logClientProjectInformationPrint() {
    return new ProjectInformationPrint();
  }
}
