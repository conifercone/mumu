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

import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.iam.client.cmds.PermissionUpdateCmd;
import baby.mumu.iam.client.dto.PermissionUpdatedDataDTO;
import baby.mumu.iam.domain.permission.Permission;
import baby.mumu.iam.domain.permission.gateway.PermissionGateway;
import baby.mumu.iam.infra.permission.convertor.PermissionConvertor;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 更新权限指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Component
@Observed(name = "PermissionUpdateCmdExe")
public class PermissionUpdateCmdExe {

    private final PermissionGateway permissionGateway;
    private final PermissionConvertor permissionConvertor;

    @Autowired
    public PermissionUpdateCmdExe(PermissionGateway permissionGateway,
                                  PermissionConvertor permissionConvertor) {
        this.permissionGateway = permissionGateway;
        this.permissionConvertor = permissionConvertor;
    }

    public PermissionUpdatedDataDTO execute(PermissionUpdateCmd permissionUpdateCmd) {
        Permission permission = permissionConvertor.toEntity(permissionUpdateCmd)
            .orElseThrow(() -> new ApplicationException(
                ResponseCode.INVALID_PERMISSION_FORMAT));
        return permissionGateway.updateById(permission)
            .flatMap(permissionConvertor::toPermissionUpdatedDataDTO)
            .orElseThrow(() -> new ApplicationException(ResponseCode.INVALID_PERMISSION_FORMAT));

    }
}
