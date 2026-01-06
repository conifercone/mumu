/*
 * Copyright (c) 2024-2026, the original author or authors.
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

package baby.mumu.iam.application.permission.executor;

import baby.mumu.basis.kotlin.tools.FileDownloadUtils;
import baby.mumu.iam.domain.permission.gateway.PermissionGateway;
import baby.mumu.iam.infra.permission.convertor.PermissionConvertor;
import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 下载所有权限（包含权限路径）指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.6.0
 */
@Component
@Observed(name = "PermissionIncludePathDownloadAllCmdExe")
public class PermissionIncludePathDownloadAllCmdExe {

    private final PermissionGateway permissionGateway;
    private final PermissionConvertor permissionConvertor;

    @Autowired
    public PermissionIncludePathDownloadAllCmdExe(PermissionGateway permissionGateway,
                                                  PermissionConvertor permissionConvertor) {
        this.permissionGateway = permissionGateway;
        this.permissionConvertor = permissionConvertor;
    }

    public void execute(HttpServletResponse response) {
        FileDownloadUtils.downloadJson(response, "permissions",
            permissionGateway.findAllIncludePath()
                .flatMap(
                    permission -> permissionConvertor.toPermissionIncludePathDownloadAllDTO(permission)
                        .stream()));
    }
}
