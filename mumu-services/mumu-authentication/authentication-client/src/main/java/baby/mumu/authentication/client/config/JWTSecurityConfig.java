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

import static org.springframework.security.config.Customizer.withDefaults;

import baby.mumu.authentication.client.api.TokenGrpcService;
import baby.mumu.authentication.client.config.ResourceServerProperties.Policy;
import baby.mumu.basis.constants.CommonConstants;
import baby.mumu.basis.enums.TokenClaimsEnum;
import io.micrometer.tracing.Tracer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.util.Assert;

/**
 * jwt类型资源服务器配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@EnableWebSecurity
@Configuration
@EnableConfigurationProperties(ResourceServerProperties.class)
public class JWTSecurityConfig {

  private final ResourceServerProperties resourceServerProperties;

  @Autowired
  public JWTSecurityConfig(ResourceServerProperties resourceServerProperties) {
    this.resourceServerProperties = resourceServerProperties;
  }

  @Bean
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder,
    TokenGrpcService tokenGrpcService, ObjectProvider<Tracer> tracers) throws Exception {
    if (CollectionUtils.isNotEmpty(resourceServerProperties.getPolicies())) {
      for (Policy policy : resourceServerProperties.getPolicies()) {
        http.authorizeHttpRequests((authorize) -> {
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl = authorize
              .requestMatchers(HttpMethod.valueOf(policy.getHttpMethod()),
                policy.getMatcher());
            if (StringUtils.isNotBlank(policy.getRole())) {
              authorizedUrl.hasRole(policy.getRole());
            } else if (StringUtils.isNotBlank(policy.getAuthority())) {
              Assert.isTrue(!policy.getAuthority().startsWith(CommonConstants.AUTHORITY_PREFIX),
                "Permission configuration cannot be empty and cannot start with SCOPE_");
              authorizedUrl.hasAuthority(
                CommonConstants.AUTHORITY_PREFIX.concat(policy.getAuthority()));
            } else if (policy.isPermitAll()) {
              authorizedUrl.permitAll();
            }
          }
        );
      }
    }
    http.authorizeHttpRequests(
      (authorize) -> authorize.anyRequest()
        .authenticated()).formLogin(withDefaults());
    http.addFilterBefore(
      new JwtAuthenticationTokenFilter(jwtDecoder, tokenGrpcService, tracers.getIfAvailable()),
      UsernamePasswordAuthenticationFilter.class);
    // Redirect to the login page when not authenticated from the
    // authorization endpoint
    http.exceptionHandling((exceptions) -> exceptions
        .defaultAuthenticationEntryPointFor(
          new MuMuAuthenticationEntryPoint(
            resourceServerProperties
          ),
          new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
        ).authenticationEntryPoint(
          new MuMuAuthenticationEntryPoint(resourceServerProperties
          ))
      )
      // Accept access tokens for User Info and/or Client Registration
      .oauth2ResourceServer((resourceServer) -> resourceServer
        .jwt(withDefaults())
        .authenticationEntryPoint(
          new MuMuAuthenticationEntryPoint(
            resourceServerProperties
          )));
    return http.cors(Customizer.withDefaults()).build();
  }


  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    MuMuJwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new MuMuJwtGrantedAuthoritiesConverter();
    grantedAuthoritiesConverter.setAuthoritiesClaimName(TokenClaimsEnum.AUTHORITIES.name());
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }
}
