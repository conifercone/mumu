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
import com.sky.centaur.authentication.client.dto.RoleDeleteCmd;
import com.sky.centaur.authentication.client.dto.RoleUpdateCmd;
import com.sky.centaur.authentication.client.dto.co.RoleAddCo;
import com.sky.centaur.authentication.client.dto.co.RoleDeleteCo;
import com.sky.centaur.authentication.client.dto.co.RoleUpdateCo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色管理
 *
 * @author 单开宇
 * @since 2024-01-10
 */
@RestController
@RequestMapping("/role")
@Tag(name = "角色管理")
public class RoleController {

  @Resource
  private RoleService roleService;

  @Operation(summary = "添加角色")
  @PostMapping("/add")
  @ResponseBody
  @API(status = Status.STABLE)
  public RoleAddCo add(@RequestBody RoleAddCmd roleAddCmd) {
    return roleService.add(roleAddCmd);
  }

  @Operation(summary = "删除角色")
  @DeleteMapping("/delete")
  @ResponseBody
  @API(status = Status.STABLE)
  public RoleDeleteCo delete(@RequestBody RoleDeleteCmd roleDeleteCmd) {
    return roleService.delete(roleDeleteCmd);
  }

  @Operation(summary = "更新角色")
  @PutMapping("/updateById")
  @ResponseBody
  @API(status = Status.STABLE)
  public RoleUpdateCo updateById(@RequestBody RoleUpdateCmd roleUpdateCmd) {
    return roleService.updateById(roleUpdateCmd);
  }
}
