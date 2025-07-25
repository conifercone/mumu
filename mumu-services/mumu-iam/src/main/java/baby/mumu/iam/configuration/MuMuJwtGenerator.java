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

package baby.mumu.iam.configuration;

import static org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;

import baby.mumu.basis.enums.OAuth2Enum;
import baby.mumu.basis.enums.TokenClaimsEnum;
import baby.mumu.iam.domain.account.Account;
import baby.mumu.iam.infra.token.gatewayimpl.cache.AuthorizeCodeTokenCacheRepository;
import baby.mumu.iam.infra.token.gatewayimpl.cache.ClientTokenCacheRepository;
import baby.mumu.iam.infra.token.gatewayimpl.cache.OidcIdTokenCacheRepository;
import baby.mumu.iam.infra.token.gatewayimpl.cache.PasswordTokenCacheRepository;
import baby.mumu.iam.infra.token.gatewayimpl.cache.po.AuthorizeCodeTokenCacheablePO;
import baby.mumu.iam.infra.token.gatewayimpl.cache.po.ClientTokenCacheablePO;
import baby.mumu.iam.infra.token.gatewayimpl.cache.po.OidcIdTokenCacheablePO;
import baby.mumu.iam.infra.token.gatewayimpl.cache.po.PasswordTokenCacheablePO;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtClaimsSet.Builder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;


/**
 * mumu jwt generator
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
public class MuMuJwtGenerator implements OAuth2TokenGenerator<Jwt> {

  private final JwtEncoder jwtEncoder;
  private OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer;
  private PasswordTokenCacheRepository passwordTokenCacheRepository;
  @Setter
  private OidcIdTokenCacheRepository oidcIdTokenCacheRepository;
  @Setter
  private ClientTokenCacheRepository clientTokenCacheRepository;
  @Setter
  private AuthorizeCodeTokenCacheRepository authorizeCodeTokenCacheRepository;

  /**
   * Constructs a {@code JwtGenerator} using the provided parameters.
   *
   * @param jwtEncoder the jwt encoder
   */
  public MuMuJwtGenerator(JwtEncoder jwtEncoder) {
    Assert.notNull(jwtEncoder, "jwtEncoder cannot be null");
    this.jwtEncoder = jwtEncoder;
  }

  @Nullable
  @Override
  public Jwt generate(@NotNull OAuth2TokenContext context) {
    if (MuMuJwtGenerator.calibration(context)) {
      return null;
    }

    String issuer = null;
    if (context.getAuthorizationServerContext() != null) {
      issuer = context.getAuthorizationServerContext().getIssuer();
    }
    RegisteredClient registeredClient = context.getRegisteredClient();

    Instant issuedAt = Instant.now();
    Instant expiresAt;
    JwsAlgorithm jwsAlgorithm = SignatureAlgorithm.RS256;
    if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
      expiresAt = issuedAt.plus(30, ChronoUnit.MINUTES);
      if (registeredClient.getTokenSettings().getIdTokenSignatureAlgorithm() != null) {
        jwsAlgorithm = registeredClient.getTokenSettings().getIdTokenSignatureAlgorithm();
      }
    } else {
      expiresAt = issuedAt.plus(registeredClient.getTokenSettings().getAccessTokenTimeToLive());
    }

    Builder claimsBuilder = MuMuJwtGenerator.getClaimsBuilder(context,
      issuer, registeredClient, issuedAt, expiresAt);

    JwsHeader.Builder jwsHeaderBuilder = JwsHeader.with(jwsAlgorithm);

    if (this.jwtCustomizer != null) {
      JwtEncodingContext.Builder jwtContextBuilder = MuMuJwtGenerator.getJwtContextBuilder(
        context, jwsHeaderBuilder, claimsBuilder);

      JwtEncodingContext jwtContext = jwtContextBuilder.build();
      this.jwtCustomizer.customize(jwtContext);
    }

    JwsHeader jwsHeader = jwsHeaderBuilder.build();
    JwtClaimsSet claims = claimsBuilder.build();

    Jwt jwt = this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims));
    cachedToken(context, jwt);
    return jwt;
  }

  private static boolean calibration(@NotNull OAuth2TokenContext context) {
    if (context.getTokenType() == null ||
      (!OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType()) &&
        !OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue()))) {
      return true;
    }
    return OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType()) &&
      !OAuth2TokenFormat.SELF_CONTAINED.equals(
        context.getRegisteredClient().getTokenSettings().getAccessTokenFormat());
  }

  private static JwtEncodingContext.Builder getJwtContextBuilder(
    @NotNull OAuth2TokenContext context,
    JwsHeader.Builder jwsHeaderBuilder, Builder claimsBuilder) {
    // @formatter:off
      JwtEncodingContext.Builder jwtContextBuilder = JwtEncodingContext.with(jwsHeaderBuilder, claimsBuilder)
          .registeredClient(context.getRegisteredClient())
          .principal(context.getPrincipal())
          .authorizationServerContext(context.getAuthorizationServerContext())
          .authorizedScopes(context.getAuthorizedScopes())
          .tokenType(context.getTokenType())
          .authorizationGrantType(context.getAuthorizationGrantType());
      if (context.getAuthorization() != null) {
        jwtContextBuilder.authorization(context.getAuthorization());
      }
      if (context.getAuthorizationGrant() != null) {
        jwtContextBuilder.authorizationGrant(context.getAuthorizationGrant());
      }
      if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
        SessionInformation sessionInformation = context.get(SessionInformation.class);
        if (sessionInformation != null) {
          jwtContextBuilder.put(SessionInformation.class, sessionInformation);
        }
      }
      // @formatter:on
    return jwtContextBuilder;
  }

  private static @NotNull JwtClaimsSet.Builder getClaimsBuilder(@NotNull OAuth2TokenContext context,
    String issuer, RegisteredClient registeredClient, Instant issuedAt, Instant expiresAt) {
    // @formatter:off
    Builder claimsBuilder = JwtClaimsSet.builder();
    if (StringUtils.isNotBlank(issuer)) {
      claimsBuilder.issuer(issuer);
    }
    claimsBuilder
        .subject(context.getPrincipal().getName())
        .audience(Collections.singletonList(registeredClient.getClientId()))
        .issuedAt(issuedAt)
        .expiresAt(expiresAt)
        .id(UUID.randomUUID().toString());
    if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
      claimsBuilder.notBefore(issuedAt);
      if (CollectionUtils.isNotEmpty(context.getAuthorizedScopes())) {
        claimsBuilder.claim(OAuth2ParameterNames.SCOPE, context.getAuthorizedScopes());
      }
    } else if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
      claimsBuilder.claim(IdTokenClaimNames.AZP, registeredClient.getClientId());
      if (AUTHORIZATION_CODE.equals(context.getAuthorizationGrantType())) {
        OAuth2AuthorizationRequest authorizationRequest = Objects.requireNonNull(context.getAuthorization()).getAttribute(
            OAuth2AuthorizationRequest.class.getName());
        assert authorizationRequest != null;
        String nonce = (String) authorizationRequest.getAdditionalParameters().get(OidcParameterNames.NONCE);
        if (StringUtils.isNotBlank(nonce)) {
          claimsBuilder.claim(IdTokenClaimNames.NONCE, nonce);
        }
        SessionInformation sessionInformation = context.get(SessionInformation.class);
        if (sessionInformation != null) {
          claimsBuilder.claim("sid", sessionInformation.getSessionId());
          claimsBuilder.claim(IdTokenClaimNames.AUTH_TIME, sessionInformation.getLastRequest());
        }
      } else if (AuthorizationGrantType.REFRESH_TOKEN.equals(context.getAuthorizationGrantType())) {
        OidcIdToken currentIdToken = Objects.requireNonNull(Objects.requireNonNull(context.getAuthorization()).getToken(OidcIdToken.class)).getToken();
        if (currentIdToken.hasClaim("sid")) {
          claimsBuilder.claim("sid", currentIdToken.getClaim("sid"));
        }
        if (currentIdToken.hasClaim(IdTokenClaimNames.AUTH_TIME)) {
          claimsBuilder.claim(IdTokenClaimNames.AUTH_TIME, currentIdToken.<Date>getClaim(IdTokenClaimNames.AUTH_TIME));
        }
      }
    }
    // @formatter:on
    return claimsBuilder;
  }

  private void cachedToken(@NotNull OAuth2TokenContext context, @NotNull Jwt jwt) {
    String tokenValue = jwt.getTokenValue();
    Instant start = Instant.now();
    Instant jwtExpiresAt = jwt.getExpiresAt();
    String authorizationGrantType = String.valueOf(
      jwt.getClaims().get(TokenClaimsEnum.AUTHORIZATION_GRANT_TYPE.getClaimName()));
    Duration between = Duration.between(start, jwtExpiresAt);
    if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
      if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(authorizationGrantType)) {
        ClientTokenCacheablePO clientTokenCacheablePO = new ClientTokenCacheablePO();
        clientTokenCacheablePO.setId(jwt.getClaimAsString(JwtClaimNames.SUB));
        clientTokenCacheablePO.setClientTokenValue(tokenValue);
        clientTokenCacheablePO.setTtl(between.toSeconds());
        clientTokenCacheRepository.save(clientTokenCacheablePO);
      } else if (context.getPrincipal().getPrincipal() instanceof Account account) {
        if (AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
          // 缓存授权码模式token
          AuthorizeCodeTokenCacheablePO authorizeCodeTokenCacheablePO = new AuthorizeCodeTokenCacheablePO();
          authorizeCodeTokenCacheablePO.setId(account.getId());
          authorizeCodeTokenCacheablePO.setTokenValue(tokenValue);
          authorizeCodeTokenCacheablePO.setTtl(between.toSeconds());
          authorizeCodeTokenCacheRepository.save(authorizeCodeTokenCacheablePO);
        } else if (OAuth2Enum.GRANT_TYPE_PASSWORD.getName()
          .equals(authorizationGrantType)) {
          // 缓存密码模式token
          PasswordTokenCacheablePO passwordTokenCacheablePO = new PasswordTokenCacheablePO();
          passwordTokenCacheablePO.setId(account.getId());
          passwordTokenCacheablePO.setTokenValue(tokenValue);
          passwordTokenCacheablePO.setTtl(between.toSeconds());
          passwordTokenCacheRepository.save(passwordTokenCacheablePO);
        }
      }
    } else if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
      OidcIdTokenCacheablePO oidcIdTokenCacheablePO = new OidcIdTokenCacheablePO();
      oidcIdTokenCacheablePO.setId(
        Long.parseLong(jwt.getClaimAsString(TokenClaimsEnum.ACCOUNT_ID.getClaimName())));
      oidcIdTokenCacheablePO.setTokenValue(tokenValue);
      oidcIdTokenCacheablePO.setTtl(between.toSeconds());
      oidcIdTokenCacheRepository.save(oidcIdTokenCacheablePO);
    }
  }

  /**
   * Sets the {@link OAuth2TokenCustomizer} that customizes the
   * {@link JwtEncodingContext#getJwsHeader() JWS headers} and/or
   * {@link JwtEncodingContext#getClaims() claims} for the generated {@link Jwt}.
   *
   * @param jwtCustomizer the {@link OAuth2TokenCustomizer} that customizes the headers and/or
   *                      claims for the generated {@code Jwt}
   */
  public void setJwtCustomizer(OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer) {
    Assert.notNull(jwtCustomizer, "jwtCustomizer cannot be null");
    this.jwtCustomizer = jwtCustomizer;
  }

  public void setPasswordTokenCacheRepository(
    PasswordTokenCacheRepository passwordTokenCacheRepository) {
    Assert.notNull(passwordTokenCacheRepository, "passwordTokenCacheRepository cannot be null");
    this.passwordTokenCacheRepository = passwordTokenCacheRepository;
  }

}

