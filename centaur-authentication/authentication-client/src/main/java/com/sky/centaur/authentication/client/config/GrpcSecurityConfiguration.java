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

import org.lognet.springboot.grpc.security.GrpcSecurity;
import org.lognet.springboot.grpc.security.GrpcSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

/**
 * grpc server端权限配置类
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Configuration
public class GrpcSecurityConfiguration extends GrpcSecurityConfigurerAdapter {

  private final JwtAuthenticationProvider jwtAuthenticationProvider;

  @Autowired
  public GrpcSecurityConfiguration(JwtAuthenticationProvider jwtAuthenticationProvider) {
    this.jwtAuthenticationProvider = jwtAuthenticationProvider;
  }

  @Override
  public void configure(GrpcSecurity builder) throws Exception {
    builder.authorizeRequests()
        .and()
        .authenticationProvider(jwtAuthenticationProvider);
  }
}
