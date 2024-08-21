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
package com.sky.centaur.authentication.client.config;

import com.sky.centaur.authentication.client.api.AccountGrpcService;
import com.sky.centaur.authentication.client.api.AuthorityGrpcService;
import com.sky.centaur.authentication.client.api.RoleGrpcService;
import com.sky.centaur.authentication.client.api.TokenGrpcService;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * api配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Configuration
public class AuthenticationClientConfiguration {

  @Bean
  public TokenGrpcService tokenGrpcService(DiscoveryClient discoveryClient,
      ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    return new TokenGrpcService(discoveryClient, grpcClientInterceptorObjectProvider);
  }

  @Bean
  public AccountGrpcService accountGrpcService(DiscoveryClient discoveryClient,
      ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    return new AccountGrpcService(discoveryClient, grpcClientInterceptorObjectProvider);
  }

  @Bean
  public AuthorityGrpcService authorityGrpcService(DiscoveryClient discoveryClient,
      ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    return new AuthorityGrpcService(discoveryClient, grpcClientInterceptorObjectProvider);
  }

  @Bean
  public RoleGrpcService roleGrpcService(DiscoveryClient discoveryClient,
      ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    return new RoleGrpcService(discoveryClient, grpcClientInterceptorObjectProvider);
  }
}
