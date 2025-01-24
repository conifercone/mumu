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
package baby.mumu.authentication.client.config;

import baby.mumu.basis.constants.CommonConstants;
import io.grpc.MethodDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.devh.boot.grpc.server.security.authentication.BearerAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.CompositeGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.check.AccessPredicate;
import net.devh.boot.grpc.server.security.check.AccessPredicateVoter;
import net.devh.boot.grpc.server.security.check.GrpcSecurityMetadataSource;
import net.devh.boot.grpc.server.security.check.ManualGrpcSecurityMetadataSource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
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
public class GrpcSecurityConfiguration {

  private final JwtAuthenticationProvider jwtAuthenticationProvider;
  private final ResourcePoliciesProperties resourcePoliciesProperties;
  private final static String GRPC_GET_METHOD_TEMPLATE = "get%sMethod";

  @Autowired
  public GrpcSecurityConfiguration(JwtDecoder jwtDecoder,
    JwtAuthenticationConverter jwtAuthenticationConverter,
    ResourcePoliciesProperties resourcePoliciesProperties) {
    JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
    authenticationProvider.setJwtAuthenticationConverter(jwtAuthenticationConverter);
    this.jwtAuthenticationProvider = authenticationProvider;
    this.resourcePoliciesProperties = resourcePoliciesProperties;
  }

  @Bean
  AuthenticationManager authenticationManager() {
    final List<AuthenticationProvider> providers = new ArrayList<>();
    providers.add(jwtAuthenticationProvider);
    return new ProviderManager(providers);
  }

  @Bean
  GrpcAuthenticationReader authenticationReader() {
    final List<GrpcAuthenticationReader> readers = new ArrayList<>();
    readers.add(new BearerAuthenticationReader(
      BearerTokenAuthenticationToken::new));
    return new CompositeGrpcAuthenticationReader(readers);
  }

  @Bean
  GrpcSecurityMetadataSource grpcSecurityMetadataSource() {
    final ManualGrpcSecurityMetadataSource source = new ManualGrpcSecurityMetadataSource();
    if (CollectionUtils.isNotEmpty(resourcePoliciesProperties.getGrpc())) {
      resourcePoliciesProperties.getGrpc()
        .forEach(grpc -> {
          if (CollectionUtils.isNotEmpty(grpc.getGrpcPolicies())) {
            grpc.getGrpcPolicies().forEach(grpcPolicy -> {
              try {
                Class<?> clazz = Class.forName(grpc.getServiceFullPath());
                Method method = clazz.getDeclaredMethod(String.format(GRPC_GET_METHOD_TEMPLATE,
                  StringUtils.capitalize(grpcPolicy.getMethod())));
                MethodDescriptor<?, ?> methods =
                  (MethodDescriptor<?, ?>) method.invoke(null);
                if (StringUtils.isNotBlank(grpcPolicy.getRole())) {
                  source.set(methods, AccessPredicate.hasRole(grpcPolicy.getRole()));
                } else if (CollectionUtils.isNotEmpty(grpcPolicy.getAnyRole())) {
                  source.set(methods, AccessPredicate.hasAnyRole(grpcPolicy.getAnyRole()));
                } else if (StringUtils.isNotBlank(
                  grpcPolicy.getAuthority())) {
                  Assert.isTrue(
                    !grpcPolicy.getAuthority().startsWith(CommonConstants.AUTHORITY_PREFIX),
                    "Permission configuration cannot be empty and cannot start with SCOPE_");
                  source.set(methods, AccessPredicate.hasAuthority(new SimpleGrantedAuthority(
                    CommonConstants.AUTHORITY_PREFIX.concat(grpcPolicy.getAuthority()))));
                } else if (CollectionUtils.isNotEmpty(grpcPolicy.getAnyAuthority())) {
                  List<String> anyAuthority = grpcPolicy.getAnyAuthority();
                  anyAuthority.stream().filter(
                    authority -> StringUtils.isBlank(authority) || authority.startsWith(
                      CommonConstants.AUTHORITY_PREFIX)).findAny().ifPresent(authority -> {
                    throw new IllegalArgumentException(
                      "Permission configuration cannot be empty and cannot start with SCOPE_");
                  });
                  source.set(methods, AccessPredicate.hasAnyAuthority(
                    anyAuthority.stream().distinct().map(authority -> new SimpleGrantedAuthority(
                        CommonConstants.AUTHORITY_PREFIX.concat(authority)))
                      .collect(Collectors.toSet())));
                } else if (grpcPolicy.isPermitAll()) {
                  source.set(methods, AccessPredicate.permitAll());
                } else if (grpcPolicy.isDenyAll()) {
                  source.set(methods, AccessPredicate.denyAll());
                } else if (grpcPolicy.isAuthenticated()) {
                  source.set(methods, AccessPredicate.authenticated());
                }
              } catch (ClassNotFoundException | InvocationTargetException |
                       IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException(e);
              }
            });
          }
        });
    }
    source.setDefault(AccessPredicate.authenticated());
    return source;
  }

  @Bean
  @SuppressWarnings("deprecation")
  AccessDecisionManager accessDecisionManager() {
    final List<AccessDecisionVoter<?>> voters = new ArrayList<>();
    voters.add(new AccessPredicateVoter());
    return new UnanimousBased(voters);
  }
}
