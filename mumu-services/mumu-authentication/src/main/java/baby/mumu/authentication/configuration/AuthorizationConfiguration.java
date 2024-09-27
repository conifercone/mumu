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

import baby.mumu.authentication.application.service.AccountUserDetailService;
import baby.mumu.authentication.client.config.MuMuAuthenticationEntryPoint;
import baby.mumu.authentication.client.config.ResourceServerProperties;
import baby.mumu.authentication.client.config.ResourceServerProperties.Policy;
import baby.mumu.authentication.domain.account.Account;
import baby.mumu.authentication.domain.account.gateway.AccountGateway;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityDo;
import baby.mumu.authentication.infrastructure.relations.database.RoleAuthorityDo;
import baby.mumu.authentication.infrastructure.relations.database.RoleAuthorityRepository;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.RoleRepository;
import baby.mumu.authentication.infrastructure.token.gatewayimpl.redis.ClientTokenRepository;
import baby.mumu.authentication.infrastructure.token.gatewayimpl.redis.OidcIdTokenRepository;
import baby.mumu.authentication.infrastructure.token.gatewayimpl.redis.RefreshTokenRepository;
import baby.mumu.authentication.infrastructure.token.gatewayimpl.redis.TokenRepository;
import baby.mumu.basis.constants.CommonConstants;
import baby.mumu.basis.enums.TokenClaimsEnum;
import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.authentication.AuthenticationProperties;
import baby.mumu.extension.authentication.AuthenticationProperties.Rsa;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

/**
 * 授权配置
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(ExtensionProperties.class)
public class AuthorizationConfiguration {

  /**
   * 授权服务安全过滤链配置
   *
   * @param http                             请求
   * @param authorizationService             认证服务
   * @param tokenGenerator                   token生成器
   * @param mumuAuthenticationFailureHandler 自定义认证失败处理器
   * @return 授权服务安全过滤链实例
   * @throws Exception 异常信息
   */
  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public SecurityFilterChain authorizationServerSecurityFilterChain(@NotNull HttpSecurity http,
      OAuth2AuthorizationService authorizationService,
      OAuth2TokenGenerator<?> tokenGenerator,
      MuMuAuthenticationFailureHandler mumuAuthenticationFailureHandler,
      ResourceServerProperties resourceServerProperties, UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder)
      throws Exception {
    //noinspection DuplicatedCode
    if (CollectionUtils.isNotEmpty(resourceServerProperties.getPolicies())) {
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
    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
    http.getConfigurer(OAuth2AuthorizationServerConfigurer.class).clientAuthentication(
            oAuth2ClientAuthenticationConfigurer -> oAuth2ClientAuthenticationConfigurer.errorResponseHandler(
                mumuAuthenticationFailureHandler))
        //设置自定义密码模式
        .tokenEndpoint(tokenEndpoint ->
            tokenEndpoint
                .errorResponseHandler(mumuAuthenticationFailureHandler)
                .accessTokenRequestConverter(
                    new PasswordGrantAuthenticationConverter())
                .authenticationProvider(
                    new PasswordGrantAuthenticationProvider(
                        authorizationService, tokenGenerator, userDetailsService, passwordEncoder)))
        .oidc(oidc -> oidc.userInfoEndpoint(
            userInfoEndpoint -> userInfoEndpoint.userInfoMapper(
                oidcUserInfoAuthenticationContext -> {
                  OAuth2AccessToken accessToken = oidcUserInfoAuthenticationContext.getAccessToken();
                  Map<String, Object> claims = new HashMap<>();
                  claims.put("accessToken", accessToken);
                  claims.put("sub",
                      oidcUserInfoAuthenticationContext.getAuthorization()
                          .getPrincipalName());
                  return new OidcUserInfo(claims);
                })));

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
    http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()));

    return http.build();
  }

  /**
   * 配置token生成器
   */
  @Bean
  OAuth2TokenGenerator<?> tokenGenerator(JWKSource<SecurityContext> jwkSource,
      OAuth2TokenCustomizer<JwtEncodingContext> oAuth2TokenCustomizer,
      TokenRepository tokenRepository,
      OidcIdTokenRepository oidcIdTokenRepository,
      ClientTokenRepository clientTokenRepository,
      RefreshTokenRepository refreshTokenRepository) {
    MuMuJwtGenerator jwtGenerator = new MuMuJwtGenerator(new NimbusJwtEncoder(jwkSource));
    jwtGenerator.setJwtCustomizer(oAuth2TokenCustomizer);
    jwtGenerator.setTokenRepository(tokenRepository);
    jwtGenerator.setOidcIdTokenRepository(oidcIdTokenRepository);
    jwtGenerator.setClientTokenRepository(clientTokenRepository);
    OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
    MuMuOAuth2RefreshTokenGenerator refreshTokenGenerator = new MuMuOAuth2RefreshTokenGenerator();
    refreshTokenGenerator.setRefreshTokenRepository(refreshTokenRepository);
    return new DelegatingOAuth2TokenGenerator(
        jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
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

  /**
   * 账户详细信息
   *
   * @return 账户详细信息实例
   */
  @Bean
  public UserDetailsService userDetailsService(AccountGateway accountGateway) {
    return new AccountUserDetailService(accountGateway);
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
    objectMapper.addMixIn(Long.class, LongMixin.class);
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
  public JWKSource<SecurityContext> jwkSource(ExtensionProperties extensionProperties) {
    AuthenticationProperties authentication = extensionProperties.getAuthentication();
    Rsa rsa = authentication.getRsa();
    KeyPair keyPair;
    if (rsa.isAutomaticGenerated()) {
      keyPair = generateRsaKey();
    } else {
      keyPair = loadKeyPair(rsa.getJksKeyPath(), rsa.getJksKeyPassword(), rsa.getJksKeyPair());
    }
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
  public AuthorizationServerSettings authorizationServerSettings(
      @Value("${server.port}") Integer port) {
    return AuthorizationServerSettings.builder().issuer("http://localhost:" + port).build();
  }

  /**
   * 自定义jwt，将权限信息放至jwt中
   *
   * @return OAuth2TokenCustomizer的实例
   */
  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer(RoleRepository roleRepository,
      RoleAuthorityRepository roleAuthorityRepository) {
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
        claims.claim(TokenClaimsEnum.AUTHORITIES.name(), authoritySet);
        claims.claim(TokenClaimsEnum.ACCOUNT_NAME.name(), account.getUsername());
        claims.claim(TokenClaimsEnum.ACCOUNT_ID.name(), account.getId());
        claims.claim(TokenClaimsEnum.AUTHORIZATION_GRANT_TYPE.name(),
            context.getAuthorizationGrantType().getValue());
        if (StringUtils.isNotBlank(account.getTimezone())) {
          claims.claim(TokenClaimsEnum.TIMEZONE.name(), account.getTimezone());
        }
        Optional.ofNullable(account.getLanguage())
            .ifPresent(
                languageEnum -> claims.claim(TokenClaimsEnum.LANGUAGE.name(), languageEnum.name()));
      } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(
          context.getAuthorizationGrantType())) {
        JwtClaimsSet.Builder claims = context.getClaims();
        claims.claim(TokenClaimsEnum.AUTHORIZATION_GRANT_TYPE.name(),
            context.getAuthorizationGrantType().getValue());
        Set<String> authoritySet = Optional.ofNullable(
                ((OAuth2ClientAuthenticationToken) context.getAuthorizationGrant()
                    .getPrincipal()).getRegisteredClient()
            )
            .map(RegisteredClient::getScopes)
            .map(scopes -> {
              Set<String> roles = scopes.stream()
                  .filter(scope -> scope.startsWith(CommonConstants.ROLE_PREFIX))
                  .map(scope -> scope.substring(CommonConstants.ROLE_PREFIX.length()))
                  .collect(Collectors.toSet());
              Set<String> authorityCodesFromRoles = roleRepository.findByCodeIn(
                      new ArrayList<>(roles))
                  .stream()
                  .flatMap(
                      opt -> opt.stream().flatMap(obj -> roleAuthorityRepository.findByRoleId(
                              obj.getId()).stream()
                          .map(RoleAuthorityDo::getAuthority)))
                  .collect(Collectors.toMap(AuthorityDo::getId, authorityDo -> authorityDo,
                      (existing, replacement) -> existing))
                  .values()
                  .stream().map(AuthorityDo::getCode)
                  .collect(Collectors.toSet());
              authorityCodesFromRoles.addAll(scopes);
              return authorityCodesFromRoles;
            })
            .orElse(Collections.emptySet());
        claims.claim(TokenClaimsEnum.AUTHORITIES.name(), authoritySet);
      }
    };
  }

  @Bean
  public com.fasterxml.jackson.databind.Module dateTimeModule() {
    return new JavaTimeModule();
  }

  private KeyPair loadKeyPair(String keyPath, @NotNull String keyPassword, String keyPair) {
    KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
        new FileSystemResource(keyPath), keyPassword.toCharArray());
    return keyStoreKeyFactory.getKeyPair(keyPair, keyPassword.toCharArray());
  }
}
