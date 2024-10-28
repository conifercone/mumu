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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package baby.mumu.authentication.application.role.executor;

import baby.mumu.authentication.client.dto.RoleArchivedFindAllSliceCmd;
import baby.mumu.authentication.client.dto.co.RoleArchivedFindAllSliceCo;
import baby.mumu.authentication.domain.role.Role;
import baby.mumu.authentication.domain.role.gateway.RoleGateway;
import baby.mumu.authentication.infrastructure.role.convertor.RoleConvertor;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;

/**
 * 已归档角色查询指令执行器（不查询总数）
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Component
@Observed(name = "RoleArchivedFindAllSliceCmdExe")
public class RoleArchivedFindAllSliceCmdExe {

  private final RoleGateway roleGateway;
  private final RoleConvertor roleConvertor;

  @Autowired
  public RoleArchivedFindAllSliceCmdExe(RoleGateway roleGateway, RoleConvertor roleConvertor) {
    this.roleGateway = roleGateway;
    this.roleConvertor = roleConvertor;
  }

  public Slice<RoleArchivedFindAllSliceCo> execute(
    @NotNull RoleArchivedFindAllSliceCmd roleArchivedFindAllSliceCmd) {
    Role role = roleConvertor.toEntity(
        roleArchivedFindAllSliceCmd)
      .orElseGet(Role::new);
    Slice<Role> roles = roleGateway.findArchivedAllSlice(role,
      roleArchivedFindAllSliceCmd.getCurrent(), roleArchivedFindAllSliceCmd.getPageSize());
    List<RoleArchivedFindAllSliceCo> roleArchivedFindAllSliceCos = roles.getContent().stream()
      .map(roleConvertor::toArchivedFindAllSliceCo)
      .filter(Optional::isPresent).map(Optional::get).toList();
    return new SliceImpl<>(roleArchivedFindAllSliceCos, roles.getPageable(),
      roles.hasNext());
  }
}
