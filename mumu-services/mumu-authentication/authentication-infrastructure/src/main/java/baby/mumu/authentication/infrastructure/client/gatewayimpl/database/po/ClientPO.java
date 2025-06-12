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

package baby.mumu.authentication.infrastructure.client.gatewayimpl.database.po;

import baby.mumu.basis.po.jpa.JpaBasisDefaultPersistentObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.Instant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

/**
 * 客户端数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.5.0
 */
@Getter
@Setter
@Entity
@Table(name = "oauth2_registered_client")
@DynamicInsert
@RequiredArgsConstructor
public class ClientPO extends JpaBasisDefaultPersistentObject {

  @Serial
  private static final long serialVersionUID = 8276445490560814514L;

  @Id
  private String id;

  private String clientId;

  private Instant clientIdIssuedAt;

  private String clientSecret;

  private Instant clientSecretExpiresAt;

  private String clientName;

  @Column(length = 1000)
  private String clientAuthenticationMethods;

  @Column(length = 1000)
  private String authorizationGrantTypes;

  @Column(length = 1000)
  private String redirectUris;

  @Column(length = 1000)
  private String postLogoutRedirectUris;

  @Column(length = 1000)
  private String scopes;

  @Column(length = 2000)
  private String clientSettings;

  @Column(length = 2000)
  private String tokenSettings;
}
