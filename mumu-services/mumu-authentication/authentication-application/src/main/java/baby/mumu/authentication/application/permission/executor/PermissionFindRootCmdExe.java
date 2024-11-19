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

import baby.mumu.authentication.client.dto.PermissionFindRootCmd;
import baby.mumu.authentication.client.dto.co.PermissionFindRootCo;
import baby.mumu.authentication.domain.permission.Permission;
import baby.mumu.authentication.domain.permission.gateway.PermissionGateway;
import baby.mumu.authentication.infrastructure.permission.convertor.PermissionConvertor;
import io.micrometer.observation.annotation.Observed;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

/**
 * 获取所有根权限指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
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

  public Page<PermissionFindRootCo> execute(PermissionFindRootCmd permissionFindRootCmd) {
    return Optional.ofNullable(permissionFindRootCmd).map(permissionFindRootCmdNotNull -> {
      Page<Permission> permissions = permissionGateway.findRootPermissions(
        permissionFindRootCmdNotNull.getCurrent(), permissionFindRootCmdNotNull.getPageSize());
      List<PermissionFindRootCo> permissionFindRootCos = permissions.getContent().stream()
        .map(permissionConvertor::toPermissionFindRootCo)
        .filter(Optional::isPresent).map(Optional::get).toList();
      return new PageImpl<>(permissionFindRootCos, permissions.getPageable(),
        permissions.getTotalElements());
    }).orElse(new PageImpl<>(new ArrayList<>()));
  }
}
