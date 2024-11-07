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
  @Query("delete from AuthorityPathsDo a where a.ancestor.id=:ancestorId and a.descendant.id=:descendantId and a.depth = 1")
  @Transactional
  void deleteDirectly(@Param("descendantId") Long descendantId,
    @Param("ancestorId") Long ancestorId);


  /**
   * 删除所有不可达节点
   */
  @Modifying
  @Query("DELETE FROM AuthorityPathsDo ap1 " +
    "WHERE ap1.depth > 1 " +
    "AND NOT EXISTS (" +
    "   SELECT 1 " +
    "   FROM AuthorityPathsDo ap2 " +
    "   JOIN AuthorityPathsDo ap3 " +
    "       ON ap2.descendant.id = ap3.ancestor.id " +  // ap2 的后代是 ap3 的祖先
    "   WHERE ap2.ancestor.id = ap1.ancestor.id " +  // 同一个祖先
    "   AND ap3.descendant.id = ap1.descendant.id " +  // 同一个后代
    "   AND ap2.depth = 1 " +
    "   AND ap3.depth = 1 " +
    ") " +
    "AND ap1.ancestor.id != ap1.descendant.id")  // 确保不删除自身的路径
  @Transactional
  void deleteUnreachableData();
}
