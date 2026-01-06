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

package baby.mumu.iam.client.config;

import baby.mumu.basis.grpc.resolvers.DiscoveryClientNameResolverProvider;
import baby.mumu.iam.client.api.AccountGrpcService;
import baby.mumu.iam.client.api.PermissionGrpcService;
import baby.mumu.iam.client.api.RoleGrpcService;
import baby.mumu.iam.client.api.TokenGrpcService;
import io.grpc.NameResolverRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

/**
 * api配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Configuration
public class AuthenticationClientConfiguration {

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
    public TokenGrpcService tokenGrpcService(DiscoveryClient discoveryClient,
                                             GrpcChannelFactory grpcChannelFactory) {
        return new TokenGrpcService(discoveryClient, grpcChannelFactory);
    }

    @Bean
    public AccountGrpcService accountGrpcService(DiscoveryClient discoveryClient,
                                                 GrpcChannelFactory grpcChannelFactory) {
        return new AccountGrpcService(discoveryClient, grpcChannelFactory);
    }

    @Bean
    public PermissionGrpcService permissionGrpcService(DiscoveryClient discoveryClient,
                                                       GrpcChannelFactory grpcChannelFactory) {
        return new PermissionGrpcService(discoveryClient, grpcChannelFactory);
    }

    @Bean
    public RoleGrpcService roleGrpcService(DiscoveryClient discoveryClient,
                                           GrpcChannelFactory grpcChannelFactory) {
        return new RoleGrpcService(discoveryClient, grpcChannelFactory);
    }

    @Bean
    public ProjectInformationPrint authenticationClientProjectInformationPrint() {
        return new ProjectInformationPrint();
    }
}
