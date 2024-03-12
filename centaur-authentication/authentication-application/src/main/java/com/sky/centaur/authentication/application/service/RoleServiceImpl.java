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

package com.sky.centaur.authentication.application.service;

import com.sky.centaur.authentication.application.role.executor.RoleAddCmdExe;
import com.sky.centaur.authentication.application.role.executor.RoleDeleteCmdExe;
import com.sky.centaur.authentication.application.role.executor.RoleFindAllCmdExe;
import com.sky.centaur.authentication.application.role.executor.RoleUpdateCmdExe;
import com.sky.centaur.authentication.client.api.RoleService;
import com.sky.centaur.authentication.client.dto.RoleAddCmd;
import com.sky.centaur.authentication.client.dto.RoleDeleteCmd;
import com.sky.centaur.authentication.client.dto.RoleFindAllCmd;
import com.sky.centaur.authentication.client.dto.RoleUpdateCmd;
import com.sky.centaur.authentication.client.dto.co.RoleAddCo;
import com.sky.centaur.authentication.client.dto.co.RoleDeleteCo;
import com.sky.centaur.authentication.client.dto.co.RoleFindAllCo;
import com.sky.centaur.authentication.client.dto.co.RoleUpdateCo;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * 角色管理
 *
 * @author 单开宇
 * @since 2024-02-23
 */
@Service
@Observed(name = "RoleServiceImpl")
public class RoleServiceImpl implements RoleService {

  @Resource
  RoleAddCmdExe roleAddCmdExe;

  @Resource
  RoleDeleteCmdExe roleDeleteCmdExe;

  @Resource
  RoleUpdateCmdExe roleUpdateCmdExe;

  @Resource
  RoleFindAllCmdExe roleFindAllCmdExe;

  @Override
  public RoleAddCo add(RoleAddCmd roleAddCmd) {
    return roleAddCmdExe.execute(roleAddCmd);
  }

  @Override
  public RoleDeleteCo delete(RoleDeleteCmd roleDeleteCmd) {
    return roleDeleteCmdExe.execute(roleDeleteCmd);
  }

  @Override
  public RoleUpdateCo updateById(RoleUpdateCmd roleUpdateCmd) {
    return roleUpdateCmdExe.execute(roleUpdateCmd);
  }

  @Override
  public Page<RoleFindAllCo> findAll(RoleFindAllCmd roleFindAllCmd) {
    return roleFindAllCmdExe.execute(roleFindAllCmd);
  }
}
