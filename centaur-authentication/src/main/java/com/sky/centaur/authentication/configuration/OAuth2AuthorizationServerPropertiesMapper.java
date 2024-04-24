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

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerProperties.Client;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerProperties.Registration;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

/**
 * OAuth2 授权服务器属性映射器
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@SuppressWarnings({"DuplicatedCode", "unused"})
public class OAuth2AuthorizationServerPropertiesMapper {

  private final OAuth2AuthorizationServerProperties properties;
  private final PasswordEncoder passwordEncoder;

  OAuth2AuthorizationServerPropertiesMapper(OAuth2AuthorizationServerProperties properties,
      PasswordEncoder passwordEncoder) {
    this.properties = properties;
    this.passwordEncoder = passwordEncoder;
  }

  public AuthorizationServerSettings asAuthorizationServerSettings() {
    PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
    OAuth2AuthorizationServerProperties.Endpoint endpoint = this.properties.getEndpoint();
    OAuth2AuthorizationServerProperties.OidcEndpoint oidc = endpoint.getOidc();
    AuthorizationServerSettings.Builder builder = AuthorizationServerSettings.builder();
    map.from(this.properties::getIssuer).to(builder::issuer);
    map.from(endpoint::getAuthorizationUri).to(builder::authorizationEndpoint);
    map.from(endpoint::getDeviceAuthorizationUri).to(builder::deviceAuthorizationEndpoint);
    map.from(endpoint::getDeviceVerificationUri).to(builder::deviceVerificationEndpoint);
    map.from(endpoint::getTokenUri).to(builder::tokenEndpoint);
    map.from(endpoint::getJwkSetUri).to(builder::jwkSetEndpoint);
    map.from(endpoint::getTokenRevocationUri).to(builder::tokenRevocationEndpoint);
    map.from(endpoint::getTokenIntrospectionUri).to(builder::tokenIntrospectionEndpoint);
    map.from(oidc::getLogoutUri).to(builder::oidcLogoutEndpoint);
    map.from(oidc::getClientRegistrationUri).to(builder::oidcClientRegistrationEndpoint);
    map.from(oidc::getUserInfoUri).to(builder::oidcUserInfoEndpoint);
    return builder.build();
  }

  public List<RegisteredClient> asRegisteredClients() {
    List<RegisteredClient> registeredClients = new ArrayList<>();
    this.properties.getClient()
        .forEach((registrationId, client) -> registeredClients.add(
            getRegisteredClient(registrationId, client)));
    return registeredClients;
  }

  private RegisteredClient getRegisteredClient(String registrationId, @NotNull Client client) {
    Registration registration = client.getRegistration();
    PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
    RegisteredClient.Builder builder = RegisteredClient.withId(registrationId);
    map.from(registration::getClientId).to(builder::clientId);
    map.from(passwordEncoder.encode(registration.getClientSecret())).to(builder::clientSecret);
    map.from(registration::getClientName).to(builder::clientName);
    registration.getClientAuthenticationMethods()
        .forEach((clientAuthenticationMethod) -> map.from(clientAuthenticationMethod)
            .as(ClientAuthenticationMethod::new)
            .to(builder::clientAuthenticationMethod));
    registration.getAuthorizationGrantTypes()
        .forEach((authorizationGrantType) -> map.from(authorizationGrantType)
            .as(AuthorizationGrantType::new)
            .to(builder::authorizationGrantType));
    registration.getRedirectUris()
        .forEach((redirectUri) -> map.from(redirectUri).to(builder::redirectUri));
    registration.getPostLogoutRedirectUris()
        .forEach((redirectUri) -> map.from(redirectUri).to(builder::postLogoutRedirectUri));
    registration.getScopes().forEach((scope) -> map.from(scope).to(builder::scope));
    builder.clientSettings(getClientSettings(client, map));
    builder.tokenSettings(getTokenSettings(client, map));
    return builder.build();
  }

  private @NotNull ClientSettings getClientSettings(@NotNull Client client,
      @NotNull PropertyMapper map) {
    ClientSettings.Builder builder = ClientSettings.builder();
    map.from(client::isRequireProofKey).to(builder::requireProofKey);
    map.from(client::isRequireAuthorizationConsent).to(builder::requireAuthorizationConsent);
    map.from(client::getJwkSetUri).to(builder::jwkSetUrl);
    map.from(client::getTokenEndpointAuthenticationSigningAlgorithm)
        .as(this::jwsAlgorithm)
        .to(builder::tokenEndpointAuthenticationSigningAlgorithm);
    return builder.build();
  }

  private @NotNull TokenSettings getTokenSettings(@NotNull Client client,
      @NotNull PropertyMapper map) {
    OAuth2AuthorizationServerProperties.Token token = client.getToken();
    TokenSettings.Builder builder = TokenSettings.builder();
    map.from(token::getAuthorizationCodeTimeToLive).to(builder::authorizationCodeTimeToLive);
    map.from(token::getAccessTokenTimeToLive).to(builder::accessTokenTimeToLive);
    map.from(token::getAccessTokenFormat).as(OAuth2TokenFormat::new).to(builder::accessTokenFormat);
    map.from(token::getDeviceCodeTimeToLive).to(builder::deviceCodeTimeToLive);
    map.from(token::isReuseRefreshTokens).to(builder::reuseRefreshTokens);
    map.from(token::getRefreshTokenTimeToLive).to(builder::refreshTokenTimeToLive);
    map.from(token::getIdTokenSignatureAlgorithm)
        .as(this::signatureAlgorithm)
        .to(builder::idTokenSignatureAlgorithm);
    return builder.build();
  }

  private JwsAlgorithm jwsAlgorithm(@NotNull String signingAlgorithm) {
    String name = signingAlgorithm.toUpperCase();
    JwsAlgorithm jwsAlgorithm = SignatureAlgorithm.from(name);
    if (jwsAlgorithm == null) {
      jwsAlgorithm = MacAlgorithm.from(name);
    }
    return jwsAlgorithm;
  }

  private SignatureAlgorithm signatureAlgorithm(@NotNull String signatureAlgorithm) {
    return SignatureAlgorithm.from(signatureAlgorithm.toUpperCase());
  }

}
