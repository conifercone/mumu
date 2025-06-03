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
 * See the License for the specific language governing Roles and
 * limitations under the License.
 */

package baby.mumu.authentication.infrastructure.relations.database;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 角色路径
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.4.0
 */
public interface RolePathRepository extends
  BaseJpaRepository<RolePathPO, RolePathPOId>,
  JpaSpecificationExecutor<RolePathPO> {

  /**
   * 根据后代角色ID获取所有角色路径
   *
   * @param descendantId 后代角色ID
   * @return 角色路径
   */
  List<RolePathPO> findByDescendantId(Long descendantId);

  /**
   * 根据祖先ID和后代ID删除所有关系
   *
   * @param roleId 祖先ID
   */
  @Modifying
  @Query("delete from RolePathPO a where a.descendant.id=:roleId or a.ancestor.id=:roleId")
  void deleteAllPathsByRoleId(@Param("roleId") Long roleId);

  /**
   * 获取所有根角色
   *
   * @return 根角色
   */
  @Query("""
      select a from RolePathPO a where a.id.depth = 0 and not exists
         (select 1 from RolePathPO b where b.descendant.id = a.ancestor.id and b.id.depth > 0)
    """)
  Page<RolePathPO> findRootRoles(Pageable pageable);

  /**
   * 获取直系后代角色
   *
   * @param ancestorId 祖先角色ID
   * @return 直系后代角色
   */
  @Query("select a from RolePathPO a where a.id.depth = 1 and a.ancestor.id = :ancestorId")
  Page<RolePathPO> findDirectRoles(@Param("ancestorId") Long ancestorId,
    Pageable pageable);

  /**
   * 获取所有后代角色（不包含祖先自身）
   *
   * @param ancestorIds 祖先角色ID集合
   * @return 后代角色
   */
  @Query("select a from RolePathPO a where a.id.depth != 0 and a.ancestor.id in :#{#ancestorIds}")
  List<RolePathPO> findByAncestorIdIn(@Param("ancestorIds") Collection<Long> ancestorIds);

  /**
   * 是否存在后代角色
   *
   * @param ancestorId 祖先角色ID
   * @return 是否存在
   */
  @Query("SELECT COUNT(*) > 0 FROM RolePathPO WHERE ancestor.id = :ancestorId AND id.depth = 1")
  boolean existsDescendantRoles(@Param("ancestorId") Long ancestorId);


  /**
   * 删除所有不可达节点
   */
  @Modifying
  @Query("""
    DELETE FROM RolePathPO ap1
    WHERE ap1.id.depth > 1
    AND NOT EXISTS (
        SELECT 1
        FROM RolePathPO ap2
        JOIN RolePathPO ap3
            ON ap2.descendant.id = ap3.ancestor.id
        WHERE ap2.ancestor.id = ap1.ancestor.id
        AND ap3.descendant.id = ap1.descendant.id
        AND ap2.id.depth = 1
        AND ap3.id.depth = 1
    )
    AND ap1.ancestor.id != ap1.descendant.id
    """)
  void deleteUnreachableData();
}
