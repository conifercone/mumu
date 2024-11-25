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
package baby.mumu.authentication.infrastructure.token.gatewayimpl.database;

import baby.mumu.authentication.infrastructure.token.gatewayimpl.database.dataobject.Oauth2AuthorizationDo;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * oauth 2 授权操作类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.4.0
 */
public interface Oauth2AuthenticationRepository extends
  BaseJpaRepository<Oauth2AuthorizationDo, String>,
  JpaSpecificationExecutor<Oauth2AuthorizationDo> {

  Optional<Oauth2AuthorizationDo> findByRefreshTokenValue(String refreshTokenValue);
}
