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

import baby.mumu.iam.client.cmds.PermissionFindRootCmd;
import baby.mumu.iam.client.dto.PermissionFindRootDTO;
import baby.mumu.iam.domain.permission.Permission;
import baby.mumu.iam.domain.permission.gateway.PermissionGateway;
import baby.mumu.iam.infra.permission.convertor.PermissionConvertor;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 获取所有根权限指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.3.0
 */
@Component
@Observed(name = "PermissionFindRootCmdExe")
public class PermissionFindRootCmdExe {

    private final PermissionGateway permissionGateway;
    private final PermissionConvertor permissionConvertor;

    @Autowired
    public PermissionFindRootCmdExe(PermissionGateway permissionGateway,
                                    PermissionConvertor permissionConvertor) {
        this.permissionGateway = permissionGateway;
        this.permissionConvertor = permissionConvertor;
    }

    public Page<PermissionFindRootDTO> execute(PermissionFindRootCmd permissionFindRootCmd) {
        return Optional.ofNullable(permissionFindRootCmd).map(permissionFindRootCmdNotNull -> {
            Page<Permission> permissions = permissionGateway.findRootPermissions(
                permissionFindRootCmdNotNull.getCurrent(), permissionFindRootCmdNotNull.getPageSize());
            List<PermissionFindRootDTO> permissionFindRootDTOS = permissions.getContent().stream()
                .map(permissionConvertor::toPermissionFindRootDTO)
                .filter(Optional::isPresent).map(Optional::get).toList();
            return new PageImpl<>(permissionFindRootDTOS, permissions.getPageable(),
                permissions.getTotalElements());
        }).orElse(new PageImpl<>(new ArrayList<>()));
    }
}
