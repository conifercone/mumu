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

import baby.mumu.authentication.domain.permission.gateway.PermissionGateway;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 删除权限路径指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.3.0
 */
@Component
@Observed(name = "PermissionDeletePathCmdExe")
public class PermissionDeletePathCmdExe {

  private final PermissionGateway permissionGateway;

  @Autowired
  public PermissionDeletePathCmdExe(PermissionGateway permissionGateway) {
    this.permissionGateway = permissionGateway;
  }

  public void execute(Long ancestorId, Long descendantId) {
    permissionGateway.deletePath(ancestorId, descendantId);
  }
}
