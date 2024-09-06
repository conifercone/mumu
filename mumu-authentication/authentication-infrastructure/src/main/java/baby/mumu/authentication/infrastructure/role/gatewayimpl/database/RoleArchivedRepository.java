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

package baby.mumu.authentication.infrastructure.role.gatewayimpl.database;

import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleArchivedDo;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 角色归档管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.4
 */
public interface RoleArchivedRepository extends BaseJpaRepository<RoleArchivedDo, Long>,
    JpaSpecificationExecutor<RoleArchivedDo> {

  /**
   * 根据id或code判断角色是否已存在
   *
   * @param id   角色id
   * @param code 角色code
   * @return 是否存在
   */
  boolean existsByIdOrCode(Long id, @Size(max = 100) @NotNull String code);

  /**
   * 角色code是否存在
   *
   * @param code 角色code
   * @return 是否存在
   */
  boolean existsByCode(String code);
}
