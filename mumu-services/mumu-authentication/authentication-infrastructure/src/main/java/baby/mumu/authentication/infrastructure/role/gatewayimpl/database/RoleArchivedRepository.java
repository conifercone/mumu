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
package baby.mumu.authentication.infrastructure.role.gatewayimpl.database;

import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.dataobject.RoleArchivedDo;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

  /**
   * 切片分页查询已归档的角色（不查询总数）
   *
   * @param roleArchivedDo 查询条件
   * @param authoritiesIds 权限ID集合
   * @param pageable       分页条件
   * @return 查询结果
   */
  @Query(
      "select distinct r from RoleArchivedDo r left join RoleAuthorityDo ra on r.id =ra.id.roleId"
          + " where (:#{#roleArchivedDo.id} is null or r.id = :#{#roleArchivedDo.id}) "
          + "and (:#{#roleArchivedDo.name} is null or r.name like %:#{#roleArchivedDo.name}%) "
          + "and (:#{#authoritiesIds} is null or ra.id.authorityId in :#{#authoritiesIds}) "
          + "and (:#{#roleArchivedDo.code} is null or r.code like %:#{#roleArchivedDo.code}%) order by r.creationTime desc")
  Slice<RoleArchivedDo> findAllSlice(@Param("roleArchivedDo") RoleArchivedDo roleArchivedDo,
      @Param("authoritiesIds") Collection<Long> authoritiesIds, Pageable pageable);

  /**
   * 分页查询已归档的角色（查询总数）
   *
   * @param roleArchivedDo 查询条件
   * @param authoritiesIds 权限ID集合
   * @param pageable       分页条件
   * @return 查询结果
   */
  @Query(
      "select distinct r from RoleArchivedDo r left join RoleAuthorityDo ra on r.id =ra.id.roleId"
          + " where (:#{#roleArchivedDo.id} is null or r.id = :#{#roleArchivedDo.id}) "
          + "and (:#{#roleArchivedDo.name} is null or r.name like %:#{#roleArchivedDo.name}%) "
          + "and (:#{#authoritiesIds} is null or ra.id.authorityId in :#{#authoritiesIds}) "
          + "and (:#{#roleArchivedDo.code} is null or r.code like %:#{#roleArchivedDo.code}%) order by r.creationTime desc")
  Page<RoleArchivedDo> findAllPage(@Param("roleArchivedDo") RoleArchivedDo roleArchivedDo,
      @Param("authoritiesIds") Collection<Long> authoritiesIds, Pageable pageable);
}
