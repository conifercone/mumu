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

package com.sky.centaur.authentication.application.account.executor;

import com.sky.centaur.authentication.client.dto.RoleAddCmd;
import com.sky.centaur.authentication.client.dto.co.RoleAddCo;
import com.sky.centaur.authentication.domain.account.Role;
import com.sky.centaur.authentication.domain.account.gateway.RoleGateway;
import com.sky.centaur.authentication.infrastructure.account.convertor.RoleConvertor;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * 角色添加指令执行器
 *
 * @author 单开宇
 * @since 2024-02-23
 */
@Component
public class RoleAddCmdExe {

  @Resource
  private RoleGateway roleGateway;

  public RoleAddCo execute(@NotNull RoleAddCmd roleAddCmd) {
    Role role = RoleConvertor.toEntity(roleAddCmd.getRoleAddCo());
    roleGateway.add(role);
    return roleAddCmd.getRoleAddCo();
  }
}
