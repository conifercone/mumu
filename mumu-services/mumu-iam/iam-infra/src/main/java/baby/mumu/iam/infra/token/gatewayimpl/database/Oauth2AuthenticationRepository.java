/*
 * Copyright (c) 2024-2026, the original author or authors.
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

package baby.mumu.iam.infra.token.gatewayimpl.database;

import baby.mumu.iam.infra.token.gatewayimpl.database.po.Oauth2AuthorizationDO;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * oauth 2 授权操作类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.4.0
 */
public interface Oauth2AuthenticationRepository extends
    BaseJpaRepository<Oauth2AuthorizationDO, String>,
    JpaSpecificationExecutor<Oauth2AuthorizationDO> {

    Optional<Oauth2AuthorizationDO> findByRefreshTokenValue(String refreshTokenValue);
}
