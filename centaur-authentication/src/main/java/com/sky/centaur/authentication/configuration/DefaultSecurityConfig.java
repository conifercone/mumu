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

package com.sky.centaur.authentication.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import com.sky.centaur.authentication.infrastructure.config.AuthenticationProperties;
import com.sky.centaur.authentication.infrastructure.token.redis.TokenRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

/**
 * 默认安全配置
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class DefaultSecurityConfig {

  @Bean
  @Order(0)
  public SecurityFilterChain defaultSecurityFilterChain(@NotNull HttpSecurity http,
      UserDetailsService userDetailsService, JwtDecoder jwtDecoder, TokenRepository tokenRepository)
      throws Exception {
    http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
        .authorizeHttpRequests((authorize) -> authorize
            .anyRequest().authenticated()
        )
        // Form login handles the redirect to the login page from the
        // authorization server filter chain
        .formLogin(withDefaults());
    http.addFilterBefore(
        new JwtAuthenticationTokenFilter(userDetailsService, jwtDecoder, tokenRepository),
        UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }


  /**
   * 使用WebSecurity.ignoring()忽略某些URL请求，这些请求将被Spring Security忽略
   */
  @Bean
  public WebSecurityCustomizer webSecurityCustomizer(
      AuthenticationProperties authenticationProperties) {
    return web -> {
      // 读取配置文件auth.security.excludeUrls下的链接进行忽略（白名单）
      web.ignoring().requestMatchers(
          authenticationProperties.getSecurity().getExcludeUrls().toArray(new String[]{}));
    };
  }
}
