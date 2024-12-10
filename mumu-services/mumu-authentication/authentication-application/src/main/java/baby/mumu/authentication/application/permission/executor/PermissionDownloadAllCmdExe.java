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
package baby.mumu.authentication.application.permission.executor;

import baby.mumu.authentication.domain.permission.gateway.PermissionGateway;
import baby.mumu.authentication.infrastructure.permission.convertor.PermissionConvertor;
import baby.mumu.basis.kotlin.tools.FileDownloadUtil;
import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 下载所有权限指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.4.0
 */
@Component
@Observed(name = "PermissionDownloadAllCmdExe")
public class PermissionDownloadAllCmdExe {

  private final PermissionGateway permissionGateway;
  private final PermissionConvertor permissionConvertor;

  @Autowired
  public PermissionDownloadAllCmdExe(PermissionGateway permissionGateway,
    PermissionConvertor permissionConvertor) {
    this.permissionGateway = permissionGateway;
    this.permissionConvertor = permissionConvertor;
  }

  public void execute(HttpServletResponse response) {
    FileDownloadUtil.downloadCSV(response, "permissions.csv",
      permissionGateway.downloadAll()
        .flatMap(permission -> permissionConvertor.toPermissionDownloadAllCo(permission).stream()));
  }
}
