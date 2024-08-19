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

package com.sky.centaur.authentication.application.role.executor;

import com.sky.centaur.authentication.client.dto.RoleRecoverFromArchiveByIdCmd;
import com.sky.centaur.authentication.domain.role.gateway.RoleGateway;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 角色删除指令执行器
 *
 * @author kaiyu.shan
 * @since 1.0.4
 */
@Component
@Observed(name = "RoleRecoverFromArchiveByIdCmdExe")
public class RoleRecoverFromArchiveByIdCmdExe {

  private final RoleGateway roleGateway;

  @Autowired
  public RoleRecoverFromArchiveByIdCmdExe(RoleGateway roleGateway) {
    this.roleGateway = roleGateway;
  }

  public void execute(RoleRecoverFromArchiveByIdCmd roleRecoverFromArchiveByIdCmd) {
    Optional.ofNullable(roleRecoverFromArchiveByIdCmd)
        .ifPresent(deleteCmd -> roleGateway.recoverFromArchiveById(deleteCmd.getId()));
  }
}
