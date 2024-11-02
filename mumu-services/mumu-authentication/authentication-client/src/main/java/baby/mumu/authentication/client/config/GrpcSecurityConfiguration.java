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
package baby.mumu.authentication.client.config;

import baby.mumu.basis.constants.CommonConstants;
import io.grpc.MethodDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.security.GrpcSecurity;
import org.lognet.springboot.grpc.security.GrpcSecurityConfigurerAdapter;
import org.lognet.springboot.grpc.security.GrpcServiceAuthorizationConfigurer.AuthorizedMethod;
import org.lognet.springboot.grpc.security.GrpcServiceAuthorizationConfigurer.Registry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.util.Assert;

/**
 * grpc server端权限配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Configuration
public class GrpcSecurityConfiguration extends GrpcSecurityConfigurerAdapter {

  private final JwtAuthenticationProvider jwtAuthenticationProvider;
  private final ResourceServerProperties resourceServerProperties;
  private final String GRPC_GET_METHOD_TEMPLATE = "get%sMethod";

  @Autowired
  public GrpcSecurityConfiguration(JwtDecoder jwtDecoder,
    JwtAuthenticationConverter jwtAuthenticationConverter,
    ResourceServerProperties resourceServerProperties) {
    JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
    authenticationProvider.setJwtAuthenticationConverter(jwtAuthenticationConverter);
    this.jwtAuthenticationProvider = authenticationProvider;
    this.resourceServerProperties = resourceServerProperties;
  }

  @Override
  public void configure(@NotNull GrpcSecurity builder) throws Exception {
    Registry authorizeRequests = builder.authorizeRequests();
    if (CollectionUtils.isNotEmpty(resourceServerProperties.getGrpcs())) {
      resourceServerProperties.getGrpcs()
        .forEach(grpc -> {
          if (CollectionUtils.isNotEmpty(grpc.getGrpcPolicies())) {
            grpc.getGrpcPolicies().forEach(grpcPolicy -> {
              try {
                Class<?> clazz = Class.forName(grpc.getServiceFullPath());
                Method method = clazz.getDeclaredMethod(String.format(GRPC_GET_METHOD_TEMPLATE,
                  StringUtils.capitalize(grpcPolicy.getMethod())));
                AuthorizedMethod methods = authorizeRequests.methods(
                  (MethodDescriptor<?, ?>) method.invoke(null));
                if (StringUtils.isNotBlank(grpcPolicy.getRole())) {
                  methods.hasAnyRole(grpcPolicy.getRole());
                } else if (StringUtils.isNotBlank(
                  grpcPolicy.getAuthority())) {
                  Assert.isTrue(
                    !grpcPolicy.getAuthority().startsWith(CommonConstants.AUTHORITY_PREFIX),
                    "Permission configuration cannot be empty and cannot start with SCOPE_");
                  methods.hasAnyAuthority(
                    CommonConstants.AUTHORITY_PREFIX.concat(grpcPolicy.getAuthority()));
                }
              } catch (ClassNotFoundException | InvocationTargetException |
                       IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException(e);
              }
            });
          }
        });
    }
    authorizeRequests
      .and()
      .authenticationProvider(jwtAuthenticationProvider);
  }
}
