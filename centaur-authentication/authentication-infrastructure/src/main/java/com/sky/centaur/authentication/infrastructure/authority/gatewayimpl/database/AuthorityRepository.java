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

package com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database;

import com.sky.centaur.authentication.infrastructure.authority.gatewayimpl.database.dataobject.AuthorityDo;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 权限基本信息
 *
 * @author 单开宇
 * @since 2024-01-12
 */
public interface AuthorityRepository extends BaseJpaRepository<AuthorityDo, Long>,
    JpaSpecificationExecutor<AuthorityDo> {

  List<AuthorityDo> findAuthorityDoByIdIn(Collection<Long> ids);
}
