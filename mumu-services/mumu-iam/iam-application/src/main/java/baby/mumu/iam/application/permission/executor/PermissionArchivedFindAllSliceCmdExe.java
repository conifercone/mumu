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

package baby.mumu.iam.application.permission.executor;

import baby.mumu.iam.client.cmds.PermissionArchivedFindAllSliceCmd;
import baby.mumu.iam.client.dto.PermissionArchivedFindAllSliceDTO;
import baby.mumu.iam.domain.permission.Permission;
import baby.mumu.iam.domain.permission.gateway.PermissionGateway;
import baby.mumu.iam.infra.permission.convertor.PermissionConvertor;
import io.micrometer.observation.annotation.Observed;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 查询权限已归档指令执行器（不查询总数）
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
@Component
@Observed(name = "PermissionArchivedFindAllSliceCmdExe")
public class PermissionArchivedFindAllSliceCmdExe {

  private final PermissionGateway permissionGateway;
  private final PermissionConvertor permissionConvertor;

  @Autowired
  public PermissionArchivedFindAllSliceCmdExe(PermissionGateway permissionGateway,
    PermissionConvertor permissionConvertor) {
    this.permissionGateway = permissionGateway;
    this.permissionConvertor = permissionConvertor;
  }

  public Slice<PermissionArchivedFindAllSliceDTO> execute(
    PermissionArchivedFindAllSliceCmd permissionArchivedFindAllSliceCmd) {
    Assert.notNull(permissionArchivedFindAllSliceCmd,
      "PermissionArchivedFindAllSliceCmd cannot be null");
    Permission permission = permissionConvertor.toEntity(permissionArchivedFindAllSliceCmd)
      .orElseGet(Permission::new);
    Slice<Permission> permissions = permissionGateway.findArchivedAllSlice(permission,
      permissionArchivedFindAllSliceCmd.getCurrent(),
      permissionArchivedFindAllSliceCmd.getPageSize());
    List<PermissionArchivedFindAllSliceDTO> permissionArchivedFindAllSliceDTOS = permissions.getContent()
      .stream()
      .map(permissionConvertor::toPermissionArchivedFindAllSliceDTO)
      .filter(Optional::isPresent).map(Optional::get).toList();
    return new SliceImpl<>(permissionArchivedFindAllSliceDTOS, permissions.getPageable(),
      permissions.hasNext());
  }
}
