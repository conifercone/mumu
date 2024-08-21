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
package com.sky.centaur.authentication.client.api;

import com.sky.centaur.authentication.client.dto.AuthorityAddCmd;
import com.sky.centaur.authentication.client.dto.AuthorityArchiveByIdCmd;
import com.sky.centaur.authentication.client.dto.AuthorityDeleteByIdCmd;
import com.sky.centaur.authentication.client.dto.AuthorityFindAllCmd;
import com.sky.centaur.authentication.client.dto.AuthorityFindByIdCmd;
import com.sky.centaur.authentication.client.dto.AuthorityRecoverFromArchiveByIdCmd;
import com.sky.centaur.authentication.client.dto.AuthorityUpdateCmd;
import com.sky.centaur.authentication.client.dto.co.AuthorityFindAllCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityFindByIdCo;
import org.springframework.data.domain.Page;

/**
 * 权限功能API
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public interface AuthorityService {

  /**
   * 新增权限
   *
   * @param authorityAddCmd 权限新增指令
   */
  void add(AuthorityAddCmd authorityAddCmd);

  /**
   * 根据id删除权限
   *
   * @param authorityDeleteByIdCmd 根据id删除权限指令
   */
  void deleteById(AuthorityDeleteByIdCmd authorityDeleteByIdCmd);

  /**
   * 根据id更新权限
   *
   * @param authorityUpdateCmd 权限更新指令
   */
  void updateById(AuthorityUpdateCmd authorityUpdateCmd);

  /**
   * 分页查询权限
   *
   * @param authorityFindAllCmd 分页查询权限指令
   * @return 查询结果
   */
  Page<AuthorityFindAllCo> findAll(AuthorityFindAllCmd authorityFindAllCmd);

  /**
   * 根据id查询权限
   *
   * @param authorityFindByIdCmd 根据id查询权限指令
   * @return 查询结果
   */
  AuthorityFindByIdCo findById(AuthorityFindByIdCmd authorityFindByIdCmd);

  /**
   * 根据id归档权限
   *
   * @param authorityArchiveByIdCmd 根据id归档权限指令
   */
  void archiveById(AuthorityArchiveByIdCmd authorityArchiveByIdCmd);

  /**
   * 通过id从归档中恢复
   *
   * @param authorityRecoverFromArchiveByIdCmd 通过id从归档中恢复指令
   */
  void recoverFromArchiveById(
      AuthorityRecoverFromArchiveByIdCmd authorityRecoverFromArchiveByIdCmd);
}
