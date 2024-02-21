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

import jakarta.annotation.Resource;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClaimAccessor;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * 密码模式
 *
 * @author 单开宇
 * @since 2024-02-21
 */
public class PasswordGrantAuthenticationProvider implements AuthenticationProvider {


  private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";
  @Resource
  private UserDetailsService userDetailsService;
  @Resource
  private PasswordEncoder passwordEncoder;

  private final OAuth2AuthorizationService authorizationService;
  private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

  public PasswordGrantAuthenticationProvider(OAuth2AuthorizationService authorizationService,
      OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
    Assert.notNull(authorizationService, "authorizationService cannot be null");
    Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
    this.authorizationService = authorizationService;
    this.tokenGenerator = tokenGenerator;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    PasswordGrantAuthenticationToken passwordGrantAuthenticationToken =
        (PasswordGrantAuthenticationToken) authentication;

    Map<String, Object> additionalParameters = passwordGrantAuthenticationToken.getAdditionalParameters();
    //授权类型
    AuthorizationGrantType authorizationGrantType = passwordGrantAuthenticationToken.getGrantType();
    //用户名
    String username = (String) additionalParameters.get(OAuth2ParameterNames.USERNAME);
    //密码
    String password = (String) additionalParameters.get(OAuth2ParameterNames.PASSWORD);
    //请求参数权限范围
    String requestScopesStr = (String) additionalParameters.get(OAuth2ParameterNames.SCOPE);
    //请求参数权限范围专场集合
    Set<String> requestScopeSet = Stream.of(requestScopesStr.split(" "))
        .collect(Collectors.toSet());

    // Ensure the client is authenticated
    OAuth2ClientAuthenticationToken clientPrincipal =
        getAuthenticatedClientElseThrowInvalidClient(passwordGrantAuthenticationToken);
    RegisteredClient registeredClient = getRegisteredClient(
        clientPrincipal, authorizationGrantType);

    //校验用户名信息
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    if (!passwordEncoder.matches(password, userDetails.getPassword())) {
      throw new OAuth2AuthenticationException("密码不正确！");
    }

    //由于在上面已验证过用户名、密码，现在构建一个已认证的对象UsernamePasswordAuthenticationToken
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken.authenticated(
        userDetails, clientPrincipal, userDetails.getAuthorities());

    // Initialize the DefaultOAuth2TokenContext
    DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
        .registeredClient(registeredClient)
        .principal(usernamePasswordAuthenticationToken)
        .authorizationServerContext(AuthorizationServerContextHolder.getContext())
        .authorizationGrantType(authorizationGrantType)
        .authorizedScopes(requestScopeSet)
        .authorizationGrant(passwordGrantAuthenticationToken);

    // Initialize the OAuth2Authorization
    OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(
            registeredClient)
        .principalName(clientPrincipal.getName())
        .authorizedScopes(requestScopeSet)
        .attribute(Principal.class.getName(), usernamePasswordAuthenticationToken)
        .authorizationGrantType(authorizationGrantType);

    // ----- Access token -----
    OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN)
        .build();
    OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
    if (generatedAccessToken == null) {
      OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
          "The token generator failed to generate the access token.", ERROR_URI);
      throw new OAuth2AuthenticationException(error);
    }

    OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
        generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
        generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());
    if (generatedAccessToken instanceof ClaimAccessor) {
      authorizationBuilder.token(accessToken, (metadata) ->
          metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME,
              ((ClaimAccessor) generatedAccessToken).getClaims()));
    } else {
      authorizationBuilder.accessToken(accessToken);
    }

    // ----- Refresh token -----
    OAuth2RefreshToken refreshToken = null;
    if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)
        &&
        // Do not issue refresh token to public client
        !clientPrincipal.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.NONE)) {

      tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
      OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
      if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
        OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
            "The token generator failed to generate the refresh token.", ERROR_URI);
        throw new OAuth2AuthenticationException(error);
      }

      refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
      authorizationBuilder.refreshToken(refreshToken);
    }
    // ----- ID token -----
    OidcIdToken idToken;
    if (requestScopeSet.contains(OidcScopes.OPENID)) {
      // @formatter:off
      tokenContext = tokenContextBuilder
          .tokenType(new OAuth2TokenType(OidcParameterNames.ID_TOKEN))
          .authorization(authorizationBuilder.build())	// ID token customizer may need access to the access token and/or refresh token
          .build();
      // @formatter:on
      OAuth2Token generatedIdToken = this.tokenGenerator.generate(tokenContext);
      if (!(generatedIdToken instanceof Jwt)) {
        OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
            "The token generator failed to generate the ID token.", ERROR_URI);
        throw new OAuth2AuthenticationException(error);
      }
      idToken = new OidcIdToken(generatedIdToken.getTokenValue(), generatedIdToken.getIssuedAt(),
          generatedIdToken.getExpiresAt(), ((Jwt) generatedIdToken).getClaims());
      authorizationBuilder.token(idToken, (metadata) ->
          metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, idToken.getClaims()));
    } else {
      idToken = null;
    }

    //保存认证信息
    OAuth2Authorization authorization = authorizationBuilder.build();
    this.authorizationService.save(authorization);
    //删除敏感信息
    Map<String, Object> additionalParametersFinal = new HashMap<>();
    additionalParametersFinal.put(OAuth2ParameterNames.SCOPE, requestScopesStr);
    if (idToken != null) {
      additionalParametersFinal.put(OidcParameterNames.ID_TOKEN, idToken.getTokenValue());
    }
    return new OAuth2AccessTokenAuthenticationToken(
        registeredClient, clientPrincipal, accessToken, refreshToken, additionalParametersFinal);
  }

  @NotNull
  private static RegisteredClient getRegisteredClient(
      @NotNull OAuth2ClientAuthenticationToken clientPrincipal,
      AuthorizationGrantType authorizationGrantType) {
    RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
    if (registeredClient == null || CollectionUtils.isEmpty(
        registeredClient.getAuthorizationGrantTypes())) {
      throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
    }
    // Ensure the client is configured to use this authorization grant type
    if (!registeredClient.getAuthorizationGrantTypes().contains(authorizationGrantType)) {
      throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
    }
    return registeredClient;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return PasswordGrantAuthenticationToken.class.isAssignableFrom(authentication);
  }

  private static @NotNull OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(
      @NotNull Authentication authentication) {
    OAuth2ClientAuthenticationToken clientPrincipal = null;
    if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(
        authentication.getPrincipal().getClass())) {
      clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
    }
    if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
      return clientPrincipal;
    }
    throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
  }
}
