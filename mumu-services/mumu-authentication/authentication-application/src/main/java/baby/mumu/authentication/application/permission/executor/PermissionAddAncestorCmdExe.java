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
package baby.mumu.authentication.application.permission.executor;

import baby.mumu.authentication.client.cmds.PermissionAddAncestorCmd;
import baby.mumu.authentication.domain.permission.gateway.PermissionGateway;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 给指定后代权限添加祖先权限指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.3.0
 */
@Component
@Observed(name = "PermissionAddAncestorCmdExe")
public class PermissionAddAncestorCmdExe {

  private final PermissionGateway permissionGateway;

  @Autowired
  public PermissionAddAncestorCmdExe(PermissionGateway permissionGateway) {
    this.permissionGateway = permissionGateway;
  }

  public void execute(
    PermissionAddAncestorCmd permissionAddAncestorCmd) {
    Optional.ofNullable(permissionAddAncestorCmd)
      .ifPresent(_ -> permissionGateway.addAncestor(
        permissionAddAncestorCmd.getDescendantId(), permissionAddAncestorCmd.getAncestorId()));
  }
}
