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

import com.sky.centaur.authentication.client.dto.RoleAddCmd;
import com.sky.centaur.authentication.client.dto.RoleArchiveByIdCmd;
import com.sky.centaur.authentication.client.dto.RoleDeleteByIdCmd;
import com.sky.centaur.authentication.client.dto.RoleFindAllCmd;
import com.sky.centaur.authentication.client.dto.RoleRecoverFromArchiveByIdCmd;
import com.sky.centaur.authentication.client.dto.RoleUpdateCmd;
import com.sky.centaur.authentication.client.dto.co.RoleFindAllCo;
import org.springframework.data.domain.Page;

/**
 * 角色功能API
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
public interface RoleService {

  /**
   * 新增角色
   *
   * @param roleAddCmd 角色新增指令
   */
  void add(RoleAddCmd roleAddCmd);

  /**
   * 根据id删除角色
   *
   * @param roleDeleteByIdCmd 根据id删除角色指令
   */
  void deleteById(RoleDeleteByIdCmd roleDeleteByIdCmd);

  /**
   * 根据id更新角色
   *
   * @param roleUpdateCmd 根据id更新角色指令
   */
  void updateById(RoleUpdateCmd roleUpdateCmd);

  /**
   * 分页查询角色
   *
   * @param roleFindAllCmd 分页查询角色指令
   * @return 查询结果
   */
  Page<RoleFindAllCo> findAll(RoleFindAllCmd roleFindAllCmd);


  /**
   * 根据id归档角色
   *
   * @param roleArchiveByIdCmd 根据id归档角色指令
   */
  void archiveById(RoleArchiveByIdCmd roleArchiveByIdCmd);

  /**
   * 通过id从归档中恢复
   *
   * @param roleRecoverFromArchiveByIdCmd 通过id从归档中恢复指令
   */
  void recoverFromArchiveById(
      RoleRecoverFromArchiveByIdCmd roleRecoverFromArchiveByIdCmd);
}
