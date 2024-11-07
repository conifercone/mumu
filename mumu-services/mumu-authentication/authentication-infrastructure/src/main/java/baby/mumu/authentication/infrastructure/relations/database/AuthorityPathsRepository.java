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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 权限路径
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.3.0
 */
public interface AuthorityPathsRepository extends
  BaseJpaRepository<AuthorityPathsDo, AuthorityPathsDoId>,
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
   * @param descendantId 后代ID
   * @param ancestorId   祖先ID
   */
  void deleteByDescendantIdOrAncestorId(Long descendantId, Long ancestorId);

  /**
   * 获取所有根权限
   *
   * @return 根权限
   */
  @Query("select a from AuthorityPathsDo a where a.depth = 0 and not exists "
    + "(select 1 from AuthorityPathsDo b where b.id.descendantId = a.id.ancestorId and b.depth > 0)")
  Page<AuthorityPathsDo> findRootAuthorities(Pageable pageable);

  /**
   * 获取直系后代权限
   *
   * @param ancestorId 祖先权限ID
   * @return 直系后代权限
   */
  @Query("select a from AuthorityPathsDo a where a.depth = 1 and a.id.ancestorId = :ancestorId")
  Page<AuthorityPathsDo> findDirectAuthorities(@Param("ancestorId") Long ancestorId,
    Pageable pageable);

  /**
   * 获取所有后代权限（不包含祖先自身）
   *
   * @param ancestorIds 祖先权限ID集合
   * @return 后代权限
   */
  @Query("select a from AuthorityPathsDo a where a.depth != 0 and a.id.ancestorId in :#{#ancestorIds}")
  List<AuthorityPathsDo> findByAncestorIdIn(@Param("ancestorIds") Collection<Long> ancestorIds);

  /**
   * 是否存在后代权限
   *
   * @param ancestorId 祖先权限ID
   * @return 是否存在
   */
  @Query("SELECT COUNT(*) > 0 FROM AuthorityPathsDo WHERE id.ancestorId = :ancestorId AND depth != 0")
  boolean existsDescendantAuthorities(@Param("ancestorId") Long ancestorId);
}
