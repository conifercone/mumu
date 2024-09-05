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

package baby.mumu.authentication.application.role.executor;

import baby.mumu.authentication.client.dto.RoleDeleteByIdCmd;
import baby.mumu.authentication.domain.role.gateway.RoleGateway;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 角色删除指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "RoleDeleteByIdCmdExe")
public class RoleDeleteByIdCmdExe {

  private final RoleGateway roleGateway;

  @Autowired
  public RoleDeleteByIdCmdExe(RoleGateway roleGateway) {
    this.roleGateway = roleGateway;
  }

  public void execute(RoleDeleteByIdCmd roleDeleteByIdCmd) {
    Optional.ofNullable(roleDeleteByIdCmd)
        .ifPresent(deleteCmd -> roleGateway.deleteById(deleteCmd.getId()));
  }
}
