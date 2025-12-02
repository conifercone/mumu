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

package baby.mumu.iam.client.config;

import static org.springframework.security.config.Customizer.withDefaults;

import baby.mumu.basis.constants.CommonConstants;
import baby.mumu.basis.enums.TokenClaimsEnum;
import baby.mumu.iam.client.api.TokenGrpcService;
import baby.mumu.iam.client.config.ResourcePoliciesProperties.HttpPolicy;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.util.Assert;

/**
 * IAM资源服务器配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(ResourcePoliciesProperties.class)
@EnableWebSecurity
public class IAMResourceServerConfig {

  private final ResourcePoliciesProperties resourcePoliciesProperties;

  @Autowired
  public IAMResourceServerConfig(ResourcePoliciesProperties resourcePoliciesProperties) {
    this.resourcePoliciesProperties = resourcePoliciesProperties;
  }

  @Bean
  public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http,
    JwtDecoder jwtDecoder,
    TokenGrpcService tokenGrpcService) {
    // noinspection DuplicatedCode
    ArrayList<String> csrfIgnoreUrls = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(resourcePoliciesProperties.getHttp())) {
      for (HttpPolicy httpPolicy : resourcePoliciesProperties.getHttp()) {
        if (httpPolicy.isPermitAll()) {
          csrfIgnoreUrls.add(httpPolicy.getMatcher());
        }
        http.authorizeHttpRequests((authorize) -> {
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl = authorize
              .requestMatchers(HttpMethod.valueOf(httpPolicy.getHttpMethod()),
                httpPolicy.getMatcher());
            if (StringUtils.isNotBlank(httpPolicy.getRole())) {
              authorizedUrl.hasRole(httpPolicy.getRole());
            } else if (CollectionUtils.isNotEmpty(httpPolicy.getAnyRole())) {
              authorizedUrl.hasAnyRole(
                httpPolicy.getAnyRole().stream().distinct().toArray(String[]::new));
            } else if (StringUtils.isNotBlank(httpPolicy.getAuthority())) {
              Assert.isTrue(!httpPolicy.getAuthority().startsWith(CommonConstants.AUTHORITY_PREFIX),
                "Permission configuration cannot be empty and cannot start with SCOPE_");
              authorizedUrl.hasAuthority(
                CommonConstants.AUTHORITY_PREFIX.concat(httpPolicy.getAuthority()));
            } else if (CollectionUtils.isNotEmpty(httpPolicy.getAnyAuthority())) {
              List<String> anyAuthority = httpPolicy.getAnyAuthority();
              anyAuthority.stream().filter(
                authority -> StringUtils.isBlank(authority) || authority.startsWith(
                  CommonConstants.AUTHORITY_PREFIX)).findAny().ifPresent(_ -> {
                throw new IllegalArgumentException(
                  "Permission configuration cannot be empty and cannot start with SCOPE_");
              });
              authorizedUrl.hasAnyAuthority(
                anyAuthority.stream().distinct().map(CommonConstants.AUTHORITY_PREFIX::concat)
                  .toArray(String[]::new));
            } else if (httpPolicy.isPermitAll()) {
              authorizedUrl.permitAll();
            } else if (httpPolicy.isDenyAll()) {
              authorizedUrl.denyAll();
            } else if (httpPolicy.isAuthenticated()) {
              authorizedUrl.authenticated();
            }
          }
        );
      }
    }
    http.authorizeHttpRequests(
      (authorize) -> authorize.anyRequest()
        .denyAll());
    http.oauth2ResourceServer(
        resourceServerConfigurer -> resourceServerConfigurer.jwt(
          jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
      )
      .csrf(csrf -> csrf.csrfTokenRepository(
          CookieCsrfTokenRepository.withHttpOnlyFalse())
        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
        .ignoringRequestMatchers(csrfIgnoreUrls.toArray(new String[0])));
    http.addFilterBefore(
      new JwtAuthenticationTokenFilter(jwtDecoder, tokenGrpcService),
      UsernamePasswordAuthenticationFilter.class);
    http.exceptionHandling(exceptionHandling -> exceptionHandling
      .accessDeniedHandler(iamAccessDeniedHandler()));
    return http.formLogin(withDefaults()).cors(Customizer.withDefaults()).build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    IAMJwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new IAMJwtGrantedAuthoritiesConverter();
    grantedAuthoritiesConverter.setAuthoritiesClaimName(TokenClaimsEnum.AUTHORITIES.getClaimName());
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }

  @Bean
  public IAMAccessDeniedHandler iamAccessDeniedHandler() {
    return new IAMAccessDeniedHandler();
  }

}
