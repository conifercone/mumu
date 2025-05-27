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
package baby.mumu.authentication.infrastructure.account.gatewayimpl.database;

import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.po.AccountPO;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 账号基本信息
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public interface AccountRepository extends BaseJpaRepository<AccountPO, Long>,
  JpaSpecificationExecutor<AccountPO> {

  /**
   * 根据用户名查询账号
   *
   * @param username 用户名
   * @return 账号数据对象
   */
  Optional<AccountPO> findByUsername(String username);

  /**
   * 根据邮箱查询账号
   *
   * @param email 邮箱地址
   * @return 账号数据对象
   */
  Optional<AccountPO> findByEmail(String email);

  /**
   * 根据id或者username或者email判断用户是否存在
   *
   * @param id       用户id
   * @param username 用户名
   * @param email    用户邮箱地址
   * @return 是否存在
   */
  boolean existsByIdOrUsernameOrEmail(Long id, @Size(max = 50) @NotNull String username,
    @Size(max = 200) String email);

  /**
   * 邮箱地址是否存在
   *
   * @param email 账号邮箱地址
   * @return true:邮箱地址已存在 false:邮箱地址不存在
   */
  boolean existsByEmail(String email);

  /**
   * 账号名是否存在
   *
   * @param username 账号名
   * @return true:账号名已存在 false:账号名不存在
   */
  boolean existsByUsername(String username);

  /**
   * 切片分页查询账号（不查询总数）
   *
   * @param accountPO 查询条件
   * @param roleIds   角色ID集合
   * @param pageable  分页条件
   * @return 查询结果
   */
  @Query("""
    select distinct r from AccountPO r left join AccountRolePO ra on r.id =ra.id.roleId
        where (:#{#accountPO.id} is null or r.id = :#{#accountPO.id})
        and (:#{#accountPO.username} is null or r.username like %:#{#accountPO.username}%)
        and (:#{#accountPO.phone} is null or r.phone like %:#{#accountPO.phone}%)
        and (:#{#roleIds} is null or ra.id.roleId in :#{#roleIds})
        and (:#{#accountPO.email} is null or r.email like %:#{#accountPO.email}%) order by r.creationTime desc
    """)
  Slice<AccountPO> findAllSlice(@Param("accountPO") AccountPO accountPO,
    @Param("roleIds") Collection<Long> roleIds, Pageable pageable);

  /**
   * 分页查询账号（查询总数）
   *
   * @param accountPO 查询条件
   * @param roleIds   角色ID集合
   * @param pageable  分页条件
   * @return 查询结果
   */
  @Query("""
    select distinct r from AccountPO r left join AccountRolePO ra on r.id =ra.id.roleId
        where (:#{#accountPO.id} is null or r.id = :#{#accountPO.id})
        and (:#{#accountPO.username} is null or r.username like %:#{#accountPO.username}%)
        and (:#{#accountPO.phone} is null or r.phone like %:#{#accountPO.phone}%)
        and (:#{#roleIds} is null or ra.id.roleId in :#{#roleIds})
        and (:#{#accountPO.email} is null or r.email like %:#{#accountPO.email}%) order by r.creationTime desc
    """)
  Page<AccountPO> findAllPage(@Param("accountPO") AccountPO accountPO,
    @Param("roleIds") Collection<Long> roleIds, Pageable pageable);
}
