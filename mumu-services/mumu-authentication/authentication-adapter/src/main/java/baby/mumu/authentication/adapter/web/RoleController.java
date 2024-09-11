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
package baby.mumu.authentication.adapter.web;

import baby.mumu.authentication.client.api.RoleService;
import baby.mumu.authentication.client.dto.RoleAddCmd;
import baby.mumu.authentication.client.dto.RoleArchiveByIdCmd;
import baby.mumu.authentication.client.dto.RoleDeleteByIdCmd;
import baby.mumu.authentication.client.dto.RoleFindAllCmd;
import baby.mumu.authentication.client.dto.RoleRecoverFromArchiveByIdCmd;
import baby.mumu.authentication.client.dto.RoleUpdateCmd;
import baby.mumu.authentication.client.dto.co.RoleFindAllCo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@RestController
@RequestMapping("/role")
@Tag(name = "角色管理")
public class RoleController {

  private final RoleService roleService;

  @Autowired
  public RoleController(RoleService roleService) {
    this.roleService = roleService;
  }

  @Operation(summary = "添加角色")
  @PostMapping("/add")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.0")
  public void add(@RequestBody @Valid RoleAddCmd roleAddCmd) {
    roleService.add(roleAddCmd);
  }

  @Operation(summary = "根据id删除角色")
  @DeleteMapping("/deleteById")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.0")
  public void deleteById(@RequestBody @Valid RoleDeleteByIdCmd roleDeleteByIdCmd) {
    roleService.deleteById(roleDeleteByIdCmd);
  }

  @Operation(summary = "更新角色")
  @PutMapping("/updateById")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateById(@RequestBody @Valid RoleUpdateCmd roleUpdateCmd) {
    roleService.updateById(roleUpdateCmd);
  }

  @Operation(summary = "查询角色")
  @GetMapping("/findAll")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.0")
  public Page<RoleFindAllCo> findAll(@RequestBody @Valid RoleFindAllCmd roleFindAllCmd) {
    return roleService.findAll(roleFindAllCmd);
  }

  @Operation(summary = "根据id归档角色")
  @PutMapping("/archiveById")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.4")
  public void archiveById(@RequestBody @Valid RoleArchiveByIdCmd roleArchiveByIdCmd) {
    roleService.archiveById(roleArchiveByIdCmd);
  }

  @Operation(summary = "根据id从归档中恢复角色")
  @PutMapping("/recoverFromArchiveById")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.4")
  public void recoverFromArchiveById(
      @RequestBody @Valid RoleRecoverFromArchiveByIdCmd roleRecoverFromArchiveByIdCmd) {
    roleService.recoverFromArchiveById(roleRecoverFromArchiveByIdCmd);
  }
}
