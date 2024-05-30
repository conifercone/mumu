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
package com.sky.centaur.authentication.adapter.web;

import com.sky.centaur.authentication.client.api.RoleService;
import com.sky.centaur.authentication.client.dto.RoleAddCmd;
import com.sky.centaur.authentication.client.dto.RoleDeleteByIdCmd;
import com.sky.centaur.authentication.client.dto.RoleFindAllCmd;
import com.sky.centaur.authentication.client.dto.RoleUpdateCmd;
import com.sky.centaur.authentication.client.dto.co.RoleAddCo;
import com.sky.centaur.authentication.client.dto.co.RoleFindAllCo;
import com.sky.centaur.authentication.client.dto.co.RoleUpdateCo;
import com.sky.centaur.basis.response.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * @author kaiyu.shan
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
  public RoleAddCo add(@RequestBody RoleAddCmd roleAddCmd) {
    return roleService.add(roleAddCmd);
  }

  @Operation(summary = "根据id删除角色")
  @DeleteMapping("/deleteById")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.0")
  public ResultResponse<?> deleteById(@RequestBody RoleDeleteByIdCmd roleDeleteByIdCmd) {
    roleService.deleteById(roleDeleteByIdCmd);
    return ResultResponse.success();
  }

  @Operation(summary = "更新角色")
  @PutMapping("/updateById")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.0")
  public RoleUpdateCo updateById(@RequestBody RoleUpdateCmd roleUpdateCmd) {
    return roleService.updateById(roleUpdateCmd);
  }

  @Operation(summary = "查询角色")
  @GetMapping("/findAll")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.0")
  public Page<RoleFindAllCo> findAll(@RequestBody RoleFindAllCmd roleFindAllCmd) {
    return roleService.findAll(roleFindAllCmd);
  }
}
