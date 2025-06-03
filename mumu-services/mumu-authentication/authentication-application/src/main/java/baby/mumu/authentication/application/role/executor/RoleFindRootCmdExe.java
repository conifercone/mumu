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
 * See the License for the specific language governing roles and
 * limitations under the License.
 */

package baby.mumu.authentication.application.role.executor;

import baby.mumu.authentication.client.cmds.RoleFindRootCmd;
import baby.mumu.authentication.client.dto.RoleFindRootDTO;
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
 * 获取所有根角色指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.4.0
 */
@Component
@Observed(name = "RoleFindRootCmdExe")
public class RoleFindRootCmdExe {

  private final RoleGateway roleGateway;
  private final RoleConvertor roleConvertor;

  @Autowired
  public RoleFindRootCmdExe(RoleGateway roleGateway,
    RoleConvertor roleConvertor) {
    this.roleGateway = roleGateway;
    this.roleConvertor = roleConvertor;
  }

  public Page<RoleFindRootDTO> execute(RoleFindRootCmd roleFindRootCmd) {
    return Optional.ofNullable(roleFindRootCmd).map(roleFindRootCmdNotNull -> {
      Page<Role> roles = roleGateway.findRootRoles(
        roleFindRootCmdNotNull.getCurrent(), roleFindRootCmdNotNull.getPageSize());
      List<RoleFindRootDTO> roleFindRootDTOS = roles.getContent().stream()
        .map(roleConvertor::toRoleFindRootDTO)
        .filter(Optional::isPresent).map(Optional::get).toList();
      return new PageImpl<>(roleFindRootDTOS, roles.getPageable(),
        roles.getTotalElements());
    }).orElse(new PageImpl<>(new ArrayList<>()));
  }
}
