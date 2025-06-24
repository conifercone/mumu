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

package baby.mumu.iam.infrastructure.role.gatewayimpl.database;

import baby.mumu.iam.infrastructure.role.gatewayimpl.database.po.RolePO;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 角色管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
public interface RoleRepository extends BaseJpaRepository<RolePO, Long>,
  JpaSpecificationExecutor<RolePO> {


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
   * 根据code集合查询所有角色
   *
   * @param codes code集合
   * @return 角色集合
   */
  List<RolePO> findByCodeIn(Collection<String> codes);

  /**
   * 切片分页查询角色（不查询总数）
   *
   * @param rolePO        查询条件
   * @param permissionIds 权限ID集合
   * @param pageable      分页条件
   * @return 查询结果
   */
  @Query("""
    select distinct r from RolePO r left join RolePermissionPO ra on r.id =ra.id.roleId
        where (:#{#rolePO.id} is null or r.id = :#{#rolePO.id})
        and (:#{#rolePO.name} is null or r.name like %:#{#rolePO.name}%)
        and (:#{#permissionIds} is null or ra.id.permissionId in :#{#permissionIds})
        and (:#{#rolePO.code} is null or r.code like %:#{#rolePO.code}%)
        and (:#{#rolePO.description} is null or r.description like %:#{#rolePO.description}%)
        order by r.creationTime desc
    """)
  Slice<RolePO> findAllSlice(@Param("rolePO") RolePO rolePO,
    @Param("permissionIds") Collection<Long> permissionIds, Pageable pageable);

  /**
   * 分页查询角色（查询总数）
   *
   * @param rolePO        查询条件
   * @param permissionIds 权限ID集合
   * @param pageable      分页条件
   * @return 查询结果
   */
  @Query("""
    select distinct r from RolePO r left join RolePermissionPO ra on r.id =ra.id.roleId
        where (:#{#rolePO.id} is null or r.id = :#{#rolePO.id})
        and (:#{#rolePO.name} is null or r.name like %:#{#rolePO.name}%)
        and (:#{#permissionIds} is null or ra.id.permissionId in :#{#permissionIds})
        and (:#{#rolePO.code} is null or r.code like %:#{#rolePO.code}%)
        and (:#{#rolePO.description} is null or r.description like %:#{#rolePO.description}%)
        order by r.creationTime desc
    """)
  Page<RolePO> findAllPage(@Param("rolePO") RolePO rolePO,
    @Param("permissionIds") Collection<Long> permissionIds, Pageable pageable);

  /**
   * 根据code查询角色
   *
   * @param code 角色code
   * @return 角色信息
   */
  Optional<RolePO> findByCode(String code);
}
