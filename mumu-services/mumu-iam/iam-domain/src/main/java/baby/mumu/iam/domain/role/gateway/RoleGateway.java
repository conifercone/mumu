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

package baby.mumu.iam.domain.role.gateway;

import baby.mumu.iam.domain.role.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

/**
 * 角色领域网关
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
public interface RoleGateway {

  /**
   * 添加角色
   *
   * @param role 角色信息
   */
  void add(Role role);

  /**
   * 根据id删除角色
   *
   * @param id 角色id
   */
  void deleteById(Long id);

  /**
   * 根据code删除角色
   *
   * @param code 角色code
   */
  void deleteByCode(String code);

  /**
   * 根据id更新角色
   *
   * @param role 目标角色信息
   */
  void updateById(Role role);

  /**
   * 分页查询角色
   *
   * @param role     查询条件
   * @param current  页码
   * @param pageSize 当前页数量
   * @return 查询结果
   */
  Page<Role> findAll(Role role, int current, int pageSize);

  /**
   * 切片分页查询角色（不查询总数）
   *
   * @param role     查询条件
   * @param current  页码
   * @param pageSize 当前页数量
   * @return 查询结果
   */
  Slice<Role> findAllSlice(Role role, int current, int pageSize);

  /**
   * 切片分页查询已归档的角色（不查询总数）
   *
   * @param role     查询条件
   * @param current  页码
   * @param pageSize 当前页数量
   * @return 查询结果
   */
  Slice<Role> findArchivedAllSlice(Role role, int current, int pageSize);

  /**
   * 分页查询已归档的角色
   *
   * @param role     查询条件
   * @param current  页码
   * @param pageSize 每页数量
   * @return 查询结果
   */
  Page<Role> findArchivedAll(Role role, int current, int pageSize);

  /**
   * 查询角色(包含指定权限)
   *
   * @param permissionId 权限id
   * @return 查询结果
   */
  List<Role> findAllContainPermission(Long permissionId);

  /**
   * 根据id归档
   *
   * @param id 角色id
   */
  void archiveById(Long id);

  /**
   * 通过id从归档中恢复
   *
   * @param id 角色id
   */
  void recoverFromArchiveById(Long id);

  /**
   * 给指定后代角色添加祖先角色
   *
   * @param descendantId 后代角色ID
   * @param ancestorId   祖先角色ID
   */
  void addAncestor(Long descendantId, Long ancestorId);

  /**
   * 获取所有根角色
   *
   * @return 根角色列表
   */
  Page<Role> findRootRoles(int current, int pageSize);

  /**
   * 获取直系后代角色
   *
   * @param ancestorId 祖先ID
   * @param current    当前页
   * @param pageSize   每页数量
   * @return 直系后代
   */
  Page<Role> findDirectRoles(Long ancestorId, int current, int pageSize);


  /**
   * 删除角色路径
   *
   * @param ancestorId   祖先角色ID
   * @param descendantId 后代角色ID
   */
  void deletePath(Long ancestorId, Long descendantId);

  /**
   * 根据id查询角色详情
   *
   * @param id 角色id
   * @return 角色信息
   */
  Optional<Role> findById(Long id);

  /**
   * 根据code查询角色详情
   *
   * @param code 角色code
   * @return 角色信息
   */
  Optional<Role> findByCode(String code);
}
