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

package baby.mumu.iam.infra.relations.database;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 账号角色关系管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.1.0
 */
public interface AccountRoleRepository extends
  BaseJpaRepository<AccountRolePO, AccountRolePOId>,
  JpaSpecificationExecutor<AccountRolePO> {

  List<AccountRolePO> findByAccountId(Long accountId);

  List<AccountRolePO> findByRoleId(Long roleId);

  void deleteByAccountId(Long accountId);
}
