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
package baby.mumu.authentication.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import baby.mumu.authentication.client.api.TokenGrpcService;
import baby.mumu.authentication.client.config.JwtAuthenticationTokenFilter;
import baby.mumu.authentication.client.config.ResourceServerProperties;
import baby.mumu.authentication.client.config.ResourceServerProperties.Policy;
import baby.mumu.log.client.api.OperationLogGrpcService;
import baby.mumu.log.client.api.SystemLogGrpcService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.util.CollectionUtils;

/**
 * 默认安全配置
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class DefaultSecurityConfig {

  @Bean
  @Order(0)
  public SecurityFilterChain defaultSecurityFilterChain(@NotNull HttpSecurity http,
      JwtDecoder jwtDecoder, TokenGrpcService tokenGrpcService,
      ResourceServerProperties resourceServerProperties,
      OperationLogGrpcService operationLogGrpcService,
      SystemLogGrpcService systemLogGrpcService, @Value("${server.port}") Integer port,
      SwaggerUiConfigProperties swaggerUiConfigProperties)
      throws Exception {
    //noinspection DuplicatedCode
    if (!CollectionUtils.isEmpty(resourceServerProperties.getPolicies())) {
      for (Policy policy : resourceServerProperties.getPolicies()) {
        http.authorizeHttpRequests((authorize) -> {
              AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl = authorize
                  .requestMatchers(HttpMethod.valueOf(policy.getHttpMethod()),
                      policy.getMatcher());
          if (StringUtils.isNotBlank(policy.getRole())) {
                authorizedUrl.hasRole(policy.getRole());
          } else if (StringUtils.isNotBlank(policy.getAuthority())) {
                authorizedUrl.hasAuthority(policy.getAuthority());
              } else if (policy.isPermitAll()) {
                authorizedUrl.permitAll();
              }
            }
        );
      }
    }
    http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
        .authorizeHttpRequests((authorize) -> authorize
            .anyRequest().authenticated()
        )
        // Form login handles the redirect to the login page from the
        // authorization server filter chain
        .formLogin(withDefaults());
    http.addFilterBefore(
        new JwtAuthenticationTokenFilter(jwtDecoder, tokenGrpcService),
        UsernamePasswordAuthenticationFilter.class);
    http.exceptionHandling((exceptions) -> exceptions
        .defaultAuthenticationEntryPointFor(
            new MuMuAuthenticationEntryPoint(String.format("http://localhost:%s/login", port),
                operationLogGrpcService,
                systemLogGrpcService, swaggerUiConfigProperties),
            new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
        )
    );
    return http.build();
  }
}
