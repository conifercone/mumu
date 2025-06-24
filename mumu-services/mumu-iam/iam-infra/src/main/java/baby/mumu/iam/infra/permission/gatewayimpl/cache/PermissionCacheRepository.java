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

package baby.mumu.iam.infra.permission.gatewayimpl.cache;

import baby.mumu.iam.infra.permission.gatewayimpl.cache.po.PermissionCacheablePO;
import com.redis.om.spring.repository.RedisDocumentRepository;
import java.util.Optional;

/**
 * 权限缓存
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
public interface PermissionCacheRepository extends
  RedisDocumentRepository<PermissionCacheablePO, Long> {

  Optional<PermissionCacheablePO> findByCode(String code);
}
