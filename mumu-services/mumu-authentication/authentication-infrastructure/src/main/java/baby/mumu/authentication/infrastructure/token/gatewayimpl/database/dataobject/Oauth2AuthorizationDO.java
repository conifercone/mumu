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
package baby.mumu.authentication.infrastructure.token.gatewayimpl.database.dataobject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

/**
 * oauth 2 授权数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.4.0
 */
@Getter
@Setter
@Entity
@Table(name = "oauth2_authorization")
public class Oauth2AuthorizationDO {

  @Id
  @Size(max = 100)
  @Column(name = "id", nullable = false, length = 100)
  private String id;

  @Size(max = 100)
  @NotNull
  @Column(name = "registered_client_id", nullable = false, length = 100)
  private String registeredClientId;

  @Size(max = 200)
  @NotNull
  @Column(name = "principal_name", nullable = false, length = 200)
  private String principalName;

  @Size(max = 100)
  @NotNull
  @Column(name = "authorization_grant_type", nullable = false, length = 100)
  private String authorizationGrantType;

  @Size(max = 1000)
  @ColumnDefault("NULL")
  @Column(name = "authorized_scopes", length = 1000)
  private String authorizedScopes;

  @Column(name = "attributes", length = Integer.MAX_VALUE)
  private String attributes;

  @Size(max = 500)
  @ColumnDefault("NULL")
  @Column(name = "state", length = 500)
  private String state;

  @Column(name = "authorization_code_value", length = Integer.MAX_VALUE)
  private String authorizationCodeValue;

  @Column(name = "authorization_code_issued_at")
  private Instant authorizationCodeIssuedAt;

  @Column(name = "authorization_code_expires_at")
  private Instant authorizationCodeExpiresAt;

  @Column(name = "authorization_code_metadata", length = Integer.MAX_VALUE)
  private String authorizationCodeMetadata;

  @Column(name = "access_token_value", length = Integer.MAX_VALUE)
  private String accessTokenValue;

  @Column(name = "access_token_issued_at")
  private Instant accessTokenIssuedAt;

  @Column(name = "access_token_expires_at")
  private Instant accessTokenExpiresAt;

  @Column(name = "access_token_metadata", length = Integer.MAX_VALUE)
  private String accessTokenMetadata;

  @Size(max = 100)
  @ColumnDefault("NULL")
  @Column(name = "access_token_type", length = 100)
  private String accessTokenType;

  @Size(max = 1000)
  @ColumnDefault("NULL")
  @Column(name = "access_token_scopes", length = 1000)
  private String accessTokenScopes;

  @Column(name = "oidc_id_token_value", length = Integer.MAX_VALUE)
  private String oidcIdTokenValue;

  @Column(name = "oidc_id_token_issued_at")
  private Instant oidcIdTokenIssuedAt;

  @Column(name = "oidc_id_token_expires_at")
  private Instant oidcIdTokenExpiresAt;

  @Column(name = "oidc_id_token_metadata", length = Integer.MAX_VALUE)
  private String oidcIdTokenMetadata;

  @Column(name = "refresh_token_value", length = Integer.MAX_VALUE)
  private String refreshTokenValue;

  @Column(name = "refresh_token_issued_at")
  private Instant refreshTokenIssuedAt;

  @Column(name = "refresh_token_expires_at")
  private Instant refreshTokenExpiresAt;

  @Column(name = "refresh_token_metadata", length = Integer.MAX_VALUE)
  private String refreshTokenMetadata;

  @Column(name = "user_code_value", length = Integer.MAX_VALUE)
  private String userCodeValue;

  @Column(name = "user_code_issued_at")
  private Instant userCodeIssuedAt;

  @Column(name = "user_code_expires_at")
  private Instant userCodeExpiresAt;

  @Column(name = "user_code_metadata", length = Integer.MAX_VALUE)
  private String userCodeMetadata;

  @Column(name = "device_code_value", length = Integer.MAX_VALUE)
  private String deviceCodeValue;

  @Column(name = "device_code_issued_at")
  private Instant deviceCodeIssuedAt;

  @Column(name = "device_code_expires_at")
  private Instant deviceCodeExpiresAt;

  @Column(name = "device_code_metadata", length = Integer.MAX_VALUE)
  private String deviceCodeMetadata;

}
