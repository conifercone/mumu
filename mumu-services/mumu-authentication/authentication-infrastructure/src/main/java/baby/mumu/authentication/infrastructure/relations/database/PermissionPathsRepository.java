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
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 权限路径
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.3.0
 */
public interface PermissionPathsRepository extends
  BaseJpaRepository<PermissionPathsDo, PermissionPathsDoId>,
  JpaSpecificationExecutor<PermissionPathsDo> {

  /**
   * 根据后代权限ID获取所有权限路径
   *
   * @param descendantId 后代权限ID
   * @return 权限路径
   */
  List<PermissionPathsDo> findByDescendantId(Long descendantId);

  /**
   * 根据祖先ID和后代ID删除所有关系
   *
   * @param authorityId 祖先ID
   */
  @Modifying
  @Query("delete from PermissionPathsDo a where a.descendant.id=:authorityId or a.ancestor.id=:authorityId")
  void deleteAllPathsByPermissionId(@Param("authorityId") Long authorityId);

  /**
   * 获取所有根权限
   *
   * @return 根权限
   */
  @Query("""
      select a from PermissionPathsDo a where a.id.depth = 0 and not exists
         (select 1 from PermissionPathsDo b where b.descendant.id = a.ancestor.id and b.id.depth > 0)
    """)
  Page<PermissionPathsDo> findRootPermissions(Pageable pageable);

  /**
   * 获取直系后代权限
   *
   * @param ancestorId 祖先权限ID
   * @return 直系后代权限
   */
  @Query("select a from PermissionPathsDo a where a.id.depth = 1 and a.ancestor.id = :ancestorId")
  Page<PermissionPathsDo> findDirectPermissions(@Param("ancestorId") Long ancestorId,
    Pageable pageable);

  /**
   * 获取所有后代权限（不包含祖先自身）
   *
   * @param ancestorIds 祖先权限ID集合
   * @return 后代权限
   */
  @Query("select a from PermissionPathsDo a where a.id.depth != 0 and a.ancestor.id in :#{#ancestorIds}")
  List<PermissionPathsDo> findByAncestorIdIn(@Param("ancestorIds") Collection<Long> ancestorIds);

  /**
   * 是否存在后代权限
   *
   * @param ancestorId 祖先权限ID
   * @return 是否存在
   */
  @Query("SELECT COUNT(*) > 0 FROM PermissionPathsDo WHERE ancestor.id = :ancestorId AND id.depth = 1")
  boolean existsDescendantPermissions(@Param("ancestorId") Long ancestorId);


  /**
   * 删除所有不可达节点
   */
  @Modifying
  @Query("""
    DELETE FROM PermissionPathsDo ap1
    WHERE ap1.id.depth > 1
    AND NOT EXISTS (
        SELECT 1
        FROM PermissionPathsDo ap2
        JOIN PermissionPathsDo ap3
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
