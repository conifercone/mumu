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

import baby.mumu.authentication.client.api.TokenGrpcService;
import baby.mumu.authentication.client.config.ResourceServerProperties.Policy;
import baby.mumu.basis.constants.CommonConstants;
import baby.mumu.basis.enums.TokenClaimsEnum;
import java.util.ArrayList;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.util.Assert;

/**
 * jwt类型资源服务器配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(ResourceServerProperties.class)
public class JWTSecurityConfig {

  private final ResourceServerProperties resourceServerProperties;

  @Autowired
  public JWTSecurityConfig(ResourceServerProperties resourceServerProperties) {
    this.resourceServerProperties = resourceServerProperties;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtDecoder jwtDecoder,
      TokenGrpcService tokenGrpcService) throws Exception {
    //noinspection DuplicatedCode
    ArrayList<String> csrfIgnoreUrls = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(resourceServerProperties.getPolicies())) {
      for (Policy policy : resourceServerProperties.getPolicies()) {
        if (policy.isPermitAll()) {
          csrfIgnoreUrls.add(policy.getMatcher());
        }
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
            .authenticated());
    http.oauth2ResourceServer(
            resourceServerConfigurer -> resourceServerConfigurer.jwt(
                    jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(
                    new MuMuAuthenticationEntryPoint(resourceServerProperties
                    )))
        .csrf(csrf -> csrf.csrfTokenRepository(
                CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            .ignoringRequestMatchers(csrfIgnoreUrls.toArray(new String[0])));
    http.addFilterBefore(new JwtAuthenticationTokenFilter(jwtDecoder, tokenGrpcService),
        UsernamePasswordAuthenticationFilter.class);
    return http.build();
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
