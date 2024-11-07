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
import org.springframework.transaction.annotation.Transactional;

/**
 * 权限路径
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.3.0
 */
public interface AuthorityPathsRepository extends
  BaseJpaRepository<AuthorityPathsDo, Long>,
  JpaSpecificationExecutor<AuthorityPathsDo> {

  /**
   * 根据后代权限ID获取所有权限路径
   *
   * @param descendantId 后代权限ID
   * @return 权限路径
   */
  List<AuthorityPathsDo> findByDescendantId(Long descendantId);

  /**
   * 根据祖先ID和后代ID删除所有关系
   *
   * @param authorityId 祖先ID
   */
  @Modifying
  @Query("delete from AuthorityPathsDo a where a.descendant.id=:authorityId or a.ancestor.id=:authorityId")
  @Transactional
  void deleteAllPathsByAuthorityId(@Param("authorityId") Long authorityId);

  /**
   * 获取所有根权限
   *
   * @return 根权限
   */
  @Query("select a from AuthorityPathsDo a where a.depth = 0 and not exists "
    + "(select 1 from AuthorityPathsDo b where b.descendant.id = a.ancestor.id and b.depth > 0)")
  Page<AuthorityPathsDo> findRootAuthorities(Pageable pageable);

  /**
   * 获取直系后代权限
   *
   * @param ancestorId 祖先权限ID
   * @return 直系后代权限
   */
  @Query("select a from AuthorityPathsDo a where a.depth = 1 and a.ancestor.id = :ancestorId")
  Page<AuthorityPathsDo> findDirectAuthorities(@Param("ancestorId") Long ancestorId,
    Pageable pageable);

  /**
   * 获取所有后代权限（不包含祖先自身）
   *
   * @param ancestorIds 祖先权限ID集合
   * @return 后代权限
   */
  @Query("select a from AuthorityPathsDo a where a.depth != 0 and a.ancestor.id in :#{#ancestorIds}")
  List<AuthorityPathsDo> findByAncestorIdIn(@Param("ancestorIds") Collection<Long> ancestorIds);

  /**
   * 是否存在后代权限
   *
   * @param ancestorId 祖先权限ID
   * @return 是否存在
   */
  @Query("SELECT COUNT(*) > 0 FROM AuthorityPathsDo WHERE ancestor.id = :ancestorId AND depth = 1")
  boolean existsDescendantAuthorities(@Param("ancestorId") Long ancestorId);


  /**
   * 路径是否已存在
   *
   * @param descendantId 后代ID
   * @param ancestorId   祖先ID
   * @return 是否已存在
   */
  @Query("SELECT COUNT(*) > 0 FROM AuthorityPathsDo WHERE ancestor.id = :ancestorId AND descendant.id = :descendantId AND depth = 1")
  boolean existsPath(@Param("descendantId") Long descendantId,
    @Param("ancestorId") Long ancestorId);

  /**
   * 路径是否已存在
   *
   * @param descendantId 后代ID
   * @param ancestorId   祖先ID
   * @param depth        路径深度
   * @return 是否已存在
   */
  @Query("SELECT COUNT(*) > 0 FROM AuthorityPathsDo WHERE ancestor.id = :ancestorId AND descendant.id = :descendantId AND depth = :depth")
  boolean existsPath(@Param("descendantId") Long descendantId,
    @Param("ancestorId") Long ancestorId,
    @Param("depth") Long depth);

  /**
   * 根据祖先ID和后代ID删除直系权限路径
   *
   * @param descendantId 后代ID
   * @param ancestorId   祖先ID
   */
  @Modifying
  @Query("delete from AuthorityPathsDo a where a.descendant.id=:descendantId and a.ancestor.id=:ancestorId and a.depth = 1")
  @Transactional
  void deleteDirectly(@Param("descendantId") Long descendantId,
    @Param("ancestorId") Long ancestorId);


  /**
   * 删除所有不可达节点
   */
  @Modifying
  @Query(value = "WITH RECURSIVE reachable_paths AS ( " +
    "    SELECT id AS ancestor_id, id AS descendant_id, 0 AS depth " +
    "    FROM authority_paths " +
    "    UNION ALL " +
    "    SELECT p.ancestor_id, a.descendant_id, p.depth + 1 AS depth " +
    "    FROM authority_paths p " +
    "    JOIN authority_paths a ON p.descendant_id = a.ancestor_id " +
    ") " +
    "DELETE FROM authority_paths " +
    "WHERE (ancestor_id, descendant_id, depth) NOT IN ( " +
    "    SELECT ancestor_id, descendant_id, depth FROM reachable_paths " +
    ") " +
    "AND ancestor_id != descendant_id", nativeQuery = true)
  @Transactional
  void deleteUnreachableData();
}
