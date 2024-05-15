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

import com.sky.centaur.authentication.client.config.ResourceServerProperties.Policy;
import com.sky.centaur.basis.enums.TokenClaimsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * jwt类型资源服务器配置类
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties
@Import(ResourceServerProperties.class)
public class JWTSecurityConfig {

  private final ResourceServerProperties resourceServerProperties;

  @Autowired
  public JWTSecurityConfig(ResourceServerProperties resourceServerProperties) {
    this.resourceServerProperties = resourceServerProperties;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //noinspection DuplicatedCode
    if (!CollectionUtils.isEmpty(resourceServerProperties.getPolicies())) {
      for (Policy policy : resourceServerProperties.getPolicies()) {
        http.authorizeHttpRequests((authorize) -> {
              AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl = authorize
                  .requestMatchers(HttpMethod.valueOf(policy.getHttpMethod()),
                      policy.getMatcher());
              if (StringUtils.hasText(policy.getRole())) {
                authorizedUrl.hasRole(policy.getRole());
              } else if (StringUtils.hasText(policy.getAuthority())) {
                authorizedUrl.hasAuthority(policy.getAuthority());
              } else if (policy.isPermitAll()) {
                authorizedUrl.permitAll();
              }
            }
        );
      }
    }
    http.authorizeHttpRequests(
        (authorize) -> authorize.anyRequest()
            .authenticated());
    http.oauth2ResourceServer(
            resourceServerConfigurer -> resourceServerConfigurer.jwt(Customizer.withDefaults())
                .authenticationEntryPoint(new ResourceServerAuthenticationEntryPoint()))
        .csrf(csrf -> csrf.csrfTokenRepository(
                CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()));
    return http.build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    grantedAuthoritiesConverter.setAuthoritiesClaimName(TokenClaimsEnum.AUTHORITIES.name());
    grantedAuthoritiesConverter.setAuthorityPrefix("");
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }
}
