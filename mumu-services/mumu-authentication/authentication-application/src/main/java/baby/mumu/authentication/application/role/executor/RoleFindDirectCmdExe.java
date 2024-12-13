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
 * See the License for the specific language governing roles and
 * limitations under the License.
 */
package baby.mumu.authentication.application.role.executor;

import baby.mumu.authentication.client.cmds.RoleFindDirectCmd;
import baby.mumu.authentication.client.dto.RoleFindDirectDTO;
import baby.mumu.authentication.domain.role.Role;
import baby.mumu.authentication.domain.role.gateway.RoleGateway;
import baby.mumu.authentication.infrastructure.role.convertor.RoleConvertor;
import io.micrometer.observation.annotation.Observed;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

/**
 * 获取直系后代根角色指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.4.0
 */
@Component
@Observed(name = "RoleFindDirectCmdExe")
public class RoleFindDirectCmdExe {

  private final RoleGateway roleGateway;
  private final RoleConvertor roleConvertor;

  @Autowired
  public RoleFindDirectCmdExe(RoleGateway roleGateway,
    RoleConvertor roleConvertor) {
    this.roleGateway = roleGateway;
    this.roleConvertor = roleConvertor;
  }

  public Page<RoleFindDirectDTO> execute(RoleFindDirectCmd roleFindDirectCmd) {
    return Optional.ofNullable(roleFindDirectCmd).map(roleFindDirectCmdNotNull -> {
      Page<Role> roles = roleGateway.findDirectRoles(
        roleFindDirectCmd.getAncestorId(),
        roleFindDirectCmdNotNull.getCurrent(), roleFindDirectCmdNotNull.getPageSize());
      List<RoleFindDirectDTO> roleFindDirectDTOS = roles.getContent().stream()
        .map(roleConvertor::toRoleFindDirectDTO)
        .filter(Optional::isPresent).map(Optional::get).toList();
      return new PageImpl<>(roleFindDirectDTOS, roles.getPageable(),
        roles.getTotalElements());
    }).orElse(new PageImpl<>(new ArrayList<>()));
  }
}
