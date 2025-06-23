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

package baby.mumu.iam.application.role.executor;

import baby.mumu.iam.client.cmds.RoleArchivedFindAllCmd;
import baby.mumu.iam.client.dto.RoleArchivedFindAllDTO;
import baby.mumu.iam.domain.role.Role;
import baby.mumu.iam.domain.role.gateway.RoleGateway;
import baby.mumu.iam.infrastructure.role.convertor.RoleConvertor;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

/**
 * 已归档角色查询指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
@Component
@Observed(name = "RoleArchivedFindAllCmdExe")
public class RoleArchivedFindAllCmdExe {

  private final RoleGateway roleGateway;
  private final RoleConvertor roleConvertor;

  @Autowired
  public RoleArchivedFindAllCmdExe(RoleGateway roleGateway, RoleConvertor roleConvertor) {
    this.roleGateway = roleGateway;
    this.roleConvertor = roleConvertor;
  }

  public Page<RoleArchivedFindAllDTO> execute(
    @NotNull RoleArchivedFindAllCmd roleArchivedFindAllCmd) {
    Role role = roleConvertor.toEntity(roleArchivedFindAllCmd)
      .orElseGet(Role::new);
    Page<Role> roles = roleGateway.findArchivedAll(role,
      roleArchivedFindAllCmd.getCurrent(), roleArchivedFindAllCmd.getPageSize());
    List<RoleArchivedFindAllDTO> roleArchivedFindAllDTOS = roles.getContent().stream()
      .map(roleConvertor::toArchivedFindAllDTO)
      .filter(Optional::isPresent).map(Optional::get).toList();
    return new PageImpl<>(roleArchivedFindAllDTOS, roles.getPageable(),
      roles.getTotalElements());
  }
}
