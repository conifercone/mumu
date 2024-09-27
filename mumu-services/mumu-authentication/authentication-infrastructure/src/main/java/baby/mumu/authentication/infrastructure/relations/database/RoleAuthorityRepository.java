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
package baby.mumu.authentication.infrastructure.relations.database;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 角色权限关系管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.1.0
 */
public interface RoleAuthorityRepository extends
    BaseJpaRepository<RoleAuthorityDo, RoleAuthorityDoId>,
    JpaSpecificationExecutor<RoleAuthorityDo> {

  /**
   * 根据权限ID查询角色权限关联关系
   *
   * @param authorityId 权限ID
   * @return 角色权限关联关系列表
   */
  List<RoleAuthorityDo> findByAuthorityId(@NotNull final Long authorityId);

  /**
   * 根据角色ID删除角色权限关联关系
   *
   * @param roleId 角色ID
   */
  void deleteByRoleId(@NotNull final Long roleId);

  /**
   * 根据角色ID查询角色权限关联关系
   *
   * @param roleId 角色ID
   * @return 角色权限关联关系列表
   */
  List<RoleAuthorityDo> findByRoleId(@NotNull final Long roleId);
}
