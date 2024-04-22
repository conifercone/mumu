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

import com.sky.centaur.authentication.client.api.AuthorityService;
import com.sky.centaur.authentication.client.dto.AuthorityAddCmd;
import com.sky.centaur.authentication.client.dto.AuthorityDeleteCmd;
import com.sky.centaur.authentication.client.dto.AuthorityFindAllCmd;
import com.sky.centaur.authentication.client.dto.AuthorityUpdateCmd;
import com.sky.centaur.authentication.client.dto.co.AuthorityAddCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityDeleteCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityFindAllCo;
import com.sky.centaur.authentication.client.dto.co.AuthorityUpdateCo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
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
 * 权限管理
 *
 * @author kaiyu.shan
 * @since 2024-01-10
 */
@RestController
@RequestMapping("/authority")
@Tag(name = "权限管理")
public class AuthorityController {

  @Resource
  private AuthorityService authorityService;

  @Operation(summary = "添加权限")
  @PostMapping("/add")
  @ResponseBody
  @API(status = Status.STABLE)
  public AuthorityAddCo add(@RequestBody AuthorityAddCmd authorityAddCmd) {
    return authorityService.add(authorityAddCmd);
  }

  @Operation(summary = "删除权限")
  @DeleteMapping("/delete")
  @ResponseBody
  @API(status = Status.STABLE)
  public AuthorityDeleteCo delete(@RequestBody AuthorityDeleteCmd authorityDeleteCmd) {
    return authorityService.delete(authorityDeleteCmd);
  }

  @Operation(summary = "修改权限")
  @PutMapping("/updateById")
  @ResponseBody
  @API(status = Status.STABLE)
  public AuthorityUpdateCo updateById(@RequestBody AuthorityUpdateCmd authorityUpdateCmd) {
    return authorityService.updateById(authorityUpdateCmd);
  }

  @Operation(summary = "查询权限")
  @GetMapping("/findAll")
  @ResponseBody
  @API(status = Status.STABLE)
  public Page<AuthorityFindAllCo> findAll(@RequestBody AuthorityFindAllCmd authorityFindAllCmd) {
    return authorityService.findAll(authorityFindAllCmd);
  }
}
