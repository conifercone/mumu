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


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.sky.centaur.authentication.application.service.AccountUserDetailService;
import com.sky.centaur.authentication.domain.account.Account;
import com.sky.centaur.authentication.infrastructure.config.AuthenticationProperties;
import com.sky.centaur.basis.enums.TokenClaimsEnum;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

/**
 * 授权配置
 *
 * @author 单开宇
 * @since 2024-01-12
 */
@Configuration
@EnableWebSecurity
public class AuthorizationConfiguration {

  @Bean
  @Order(1)
  public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
      throws Exception {
    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
    http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
        .oidc(oidc -> oidc.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userInfoMapper(
            oidcUserInfoAuthenticationContext -> {
              OAuth2AccessToken accessToken = oidcUserInfoAuthenticationContext.getAccessToken();
              Map<String, Object> claims = new HashMap<>();
              claims.put("accessToken", accessToken);
              claims.put("sub",
                  oidcUserInfoAuthenticationContext.getAuthorization().getPrincipalName());
              return new OidcUserInfo(claims);
            }))
        );  // Enable OpenID Connect 1.0
    http
        // Redirect to the login page when not authenticated from the
        // authorization endpoint
        .exceptionHandling((exceptions) -> exceptions
            .defaultAuthenticationEntryPointFor(
                new LoginUrlAuthenticationEntryPoint("/login"),
                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
            )
        )
        // Accept access tokens for User Info and/or Client Registration
        .oauth2ResourceServer((resourceServer) -> resourceServer
            .jwt(Customizer.withDefaults()));

    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain defaultSecurityFilterChain(@NotNull HttpSecurity http)
      throws Exception {
    http
        .authorizeHttpRequests((authorize) -> authorize
            .anyRequest().authenticated()
        )
        // Form login handles the redirect to the login page from the
        // authorization server filter chain
        .formLogin(Customizer.withDefaults());

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

  /**
   * 密码加密策略
   *
   * @return bCrypt密码加解密
   */
  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return new AccountUserDetailService();
  }

  /**
   * 客户端信息注册
   *
   * @param jdbcTemplate jdbc模板
   * @param properties   客户端属性
   * @return 客户端信息注册实例
   */
  @Bean
  public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate,
      OAuth2AuthorizationServerProperties properties, PasswordEncoder passwordEncoder) {
    OAuth2AuthorizationServerPropertiesMapper oAuth2AuthorizationServerPropertiesMapper = new OAuth2AuthorizationServerPropertiesMapper(
        properties, passwordEncoder);
    List<RegisteredClient> registeredClients = oAuth2AuthorizationServerPropertiesMapper.asRegisteredClients();
    JdbcRegisteredClientRepository jdbcRegisteredClientRepository = new JdbcRegisteredClientRepository(
        jdbcTemplate);
    registeredClients.forEach(jdbcRegisteredClientRepository::save);
    return jdbcRegisteredClientRepository;
  }

  /**
   * 授权信息
   *
   * @param jdbcTemplate               jdbc实例
   * @param registeredClientRepository 客户端注册实例
   * @return 授权实例
   */
  @Bean
  public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate,
      RegisteredClientRepository registeredClientRepository) {
    JdbcOAuth2AuthorizationService jdbcOAuth2AuthorizationService = new JdbcOAuth2AuthorizationService(
        jdbcTemplate, registeredClientRepository);
    JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper rowMapper = new JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper(
        registeredClientRepository);
    ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModules(new CoreJackson2Module());
    objectMapper.registerModules(SecurityJackson2Modules.getModules(classLoader));
    objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
    rowMapper.setObjectMapper(objectMapper);
    jdbcOAuth2AuthorizationService.setAuthorizationRowMapper(rowMapper);
    return jdbcOAuth2AuthorizationService;
  }

  /**
   * 授权确认
   *
   * @param jdbcTemplate               jdbc实例
   * @param registeredClientRepository 客户端注册实例
   * @return 授权确认实例
   */
  @Bean
  public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate,
      RegisteredClientRepository registeredClientRepository) {
    return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
  }

  /**
   * jwks配置
   *
   * @return jwks实例
   */
  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    KeyPair keyPair = generateRsaKey();
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    RSAKey rsaKey = new RSAKey.Builder(publicKey)
        .privateKey(privateKey)
        .keyID(UUID.randomUUID().toString())
        .build();
    JWKSet jwkSet = new JWKSet(rsaKey);
    return new ImmutableJWKSet<>(jwkSet);
  }

  /**
   * 生成rsa密钥对
   *
   * @return rsa密钥对
   */
  private static KeyPair generateRsaKey() {
    KeyPair keyPair;
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      keyPair = keyPairGenerator.generateKeyPair();
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
    return keyPair;
  }

  /**
   * jwt解码配置
   *
   * @param jwkSource jwks实例
   * @return jwt解码实例
   */
  @Bean
  public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
  }

  /**
   * AuthorizationServer实例配置
   *
   * @return AuthorizationServer配置实例
   */
  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder().build();
  }

  /**
   * 自定义jwt，将权限信息放至jwt中
   *
   * @return OAuth2TokenCustomizer的实例
   */
  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> oAuth2TokenCustomizer() {
    return context -> {
      // 检查登录用户信息是不是UserDetails，排除掉没有用户参与的流程
      if (context.getPrincipal().getPrincipal() instanceof Account account) {
        // 获取申请的scopes
        Set<String> scopes = context.getAuthorizedScopes();
        // 获取用户的权限
        Collection<? extends GrantedAuthority> authorities = account.getAuthorities();
        // 提取权限并转为字符串
        Set<String> authoritySet = Optional.ofNullable(authorities).orElse(Collections.emptyList())
            .stream()
            // 获取权限字符串
            .map(GrantedAuthority::getAuthority)
            // 去重
            .collect(Collectors.toSet());

        // 合并scope与用户信息
        authoritySet.addAll(scopes);

        JwtClaimsSet.Builder claims = context.getClaims();
        // 将权限信息放入jwt的claims中（也可以生成一个以指定字符分割的字符串放入）
        claims.claim(TokenClaimsEnum.AUTHORITIES.name(), authoritySet);
        claims.claim(TokenClaimsEnum.USERNAME.name(), account.getUsername());
      }
    };
  }
}