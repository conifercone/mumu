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
package baby.mumu.authentication.infrastructure.permission.gatewayimpl.database;

import baby.mumu.authentication.infrastructure.permission.gatewayimpl.database.dataobject.PermissionArchivedDo;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;

/**
 * 权限归档基本信息
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.4
 */
@Validated
public interface PermissionArchivedRepository extends BaseJpaRepository<PermissionArchivedDo, Long>,
  JpaSpecificationExecutor<PermissionArchivedDo> {

  /**
   * 判断是否存在指定id和code的权限
   *
   * @param id   权限id
   * @param code 权限code
   * @return 是否存在
   */
  boolean existsByIdOrCode(Long id,
    @Size(max = 50, message = "{permission.code.validation.size}") @NotNull String code);

  /**
   * 判断权限编码是否已存在
   *
   * @param code 权限code
   * @return 是否存在
   */
  boolean existsByCode(
    @Size(max = 50, message = "{permission.code.validation.size}") @NotNull String code);


  /**
   * 切片分页查询已归档的权限（不查询总数）
   *
   * @param permissionArchivedDo 查询条件
   * @param pageable             分页条件
   * @return 查询结果
   */
  @Query(
    """
      select a from PermissionArchivedDo a where (:#{#permissionArchivedDo.id} is null or a.id = :#{#permissionArchivedDo.id})
            and (:#{#permissionArchivedDo.name} is null or a.name like %:#{#permissionArchivedDo.name}%)
            and (:#{#permissionArchivedDo.code} is null or a.code like %:#{#permissionArchivedDo.code}%) order by a.creationTime desc
      """)
  Slice<PermissionArchivedDo> findAllSlice(
    @Param("permissionArchivedDo") PermissionArchivedDo permissionArchivedDo, Pageable pageable);

  /**
   * 分页查询已归档的权限（查询总数）
   *
   * @param permissionArchivedDo 查询条件
   * @param pageable             分页条件
   * @return 查询结果
   */
  @Query(
    """
      select a from PermissionArchivedDo a where (:#{#permissionArchivedDo.id} is null or a.id = :#{#permissionArchivedDo.id})
            and (:#{#permissionArchivedDo.name} is null or a.name like %:#{#permissionArchivedDo.name}%)
            and (:#{#permissionArchivedDo.code} is null or a.code like %:#{#permissionArchivedDo.code}%) order by a.creationTime desc
      """)
  Page<PermissionArchivedDo> findAllPage(
    @Param("permissionArchivedDo") PermissionArchivedDo permissionArchivedDo, Pageable pageable);
}
