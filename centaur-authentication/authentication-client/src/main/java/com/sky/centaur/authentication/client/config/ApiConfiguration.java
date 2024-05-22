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
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * api配置类
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Configuration
public class ApiConfiguration {

  @Bean
  public TokenGrpcService tokenGrpcService(DiscoveryClient consulDiscoveryClient) {
    return new TokenGrpcService(consulDiscoveryClient);
  }

  @Bean
  public AccountGrpcService accountGrpcService(DiscoveryClient consulDiscoveryClient) {
    return new AccountGrpcService(consulDiscoveryClient);
  }

  @Bean
  public AuthorityGrpcService authorityGrpcService(DiscoveryClient consulDiscoveryClient) {
    return new AuthorityGrpcService(consulDiscoveryClient);
  }

  @Bean
  public RoleGrpcService roleGrpcService(DiscoveryClient consulDiscoveryClient) {
    return new RoleGrpcService(consulDiscoveryClient);
  }
}