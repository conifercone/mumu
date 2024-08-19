/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
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
package com.sky.centaur.authentication.domain.role.gateway;

import com.sky.centaur.authentication.domain.role.Role;
import org.springframework.data.domain.Page;

/**
 * 角色领域网关
 *
 * @author kaiyu.shan
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
   * 根据id更新角色
   *
   * @param role 目标角色信息
   */
  void updateById(Role role);

  /**
   * 分页查询角色
   *
   * @param role     查询条件
   * @param pageNo   页码
   * @param pageSize 当前页数量
   * @return 查询结果
   */
  Page<Role> findAll(Role role, int pageNo, int pageSize);

  /**
   * 分页查询角色(包含指定权限)
   *
   * @param authorityId 权限id
   * @param pageNo      页码
   * @param pageSize    当前页数量
   * @return 查询结果
   */
  Page<Role> findAllContainAuthority(Long authorityId, int pageNo, int pageSize);

  /**
   * 根据id归档
   *
   * @param id 权限id
   */
  void archiveById(Long id);

  /**
   * 通过id从存档中恢复
   *
   * @param id 权限id
   */
  void recoverFromArchiveById(Long id);
}
