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

import baby.mumu.authentication.client.api.PermissionService;
import baby.mumu.authentication.client.dto.PermissionAddAncestorCmd;
import baby.mumu.authentication.client.dto.PermissionAddCmd;
import baby.mumu.authentication.client.dto.PermissionArchivedFindAllCmd;
import baby.mumu.authentication.client.dto.PermissionArchivedFindAllSliceCmd;
import baby.mumu.authentication.client.dto.PermissionFindAllCmd;
import baby.mumu.authentication.client.dto.PermissionFindAllSliceCmd;
import baby.mumu.authentication.client.dto.PermissionFindDirectCmd;
import baby.mumu.authentication.client.dto.PermissionFindRootCmd;
import baby.mumu.authentication.client.dto.PermissionUpdateCmd;
import baby.mumu.authentication.client.dto.co.PermissionArchivedFindAllCo;
import baby.mumu.authentication.client.dto.co.PermissionArchivedFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.PermissionFindAllCo;
import baby.mumu.authentication.client.dto.co.PermissionFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.PermissionFindByIdCo;
import baby.mumu.authentication.client.dto.co.PermissionFindDirectCo;
import baby.mumu.authentication.client.dto.co.PermissionFindRootCo;
import baby.mumu.basis.annotations.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@RestController
@RequestMapping("/permission")
@Tag(name = "权限管理")
public class PermissionController {

  private final PermissionService permissionService;

  @Autowired
  public PermissionController(PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  @Operation(summary = "添加权限")
  @PostMapping("/add")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void add(@RequestBody @Valid PermissionAddCmd permissionAddCmd) {
    permissionService.add(permissionAddCmd);
  }

  @Operation(summary = "根据主键删除权限")
  @DeleteMapping("/deleteById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void deleteById(@PathVariable(value = "id") Long id) {
    permissionService.deleteById(id);
  }

  @Operation(summary = "修改权限")
  @PutMapping("/updateById")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateById(@RequestBody @Valid PermissionUpdateCmd permissionUpdateCmd) {
    permissionService.updateById(permissionUpdateCmd);
  }

  @Operation(summary = "查询权限")
  @GetMapping("/findAll")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.0")
  @RateLimiter
  public Page<PermissionFindAllCo> findAll(
    @ModelAttribute @Valid PermissionFindAllCmd permissionFindAllCmd) {
    return permissionService.findAll(permissionFindAllCmd);
  }

  @Operation(summary = "查询权限（不查询总数）")
  @GetMapping("/findAllSlice")
  @ResponseBody
  @API(status = Status.STABLE, since = "2.2.0")
  @RateLimiter
  public Slice<PermissionFindAllSliceCo> findAllSlice(
    @ModelAttribute @Valid PermissionFindAllSliceCmd permissionFindAllSliceCmd) {
    return permissionService.findAllSlice(permissionFindAllSliceCmd);
  }

  @Operation(summary = "查询已归档权限")
  @GetMapping("/findArchivedAll")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.0.0")
  public Page<PermissionArchivedFindAllCo> findArchivedAll(
    @ModelAttribute @Valid PermissionArchivedFindAllCmd permissionArchivedFindAllCmd) {
    return permissionService.findArchivedAll(permissionArchivedFindAllCmd);
  }

  @Operation(summary = "查询已归档权限（不查询总数）")
  @GetMapping("/findArchivedAllSlice")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public Slice<PermissionArchivedFindAllSliceCo> findArchivedAllSlice(
    @ModelAttribute @Valid PermissionArchivedFindAllSliceCmd permissionArchivedFindAllSliceCmd) {
    return permissionService.findArchivedAllSlice(permissionArchivedFindAllSliceCmd);
  }

  @Operation(summary = "根据id查询权限")
  @GetMapping("/findById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public PermissionFindByIdCo findById(@PathVariable(value = "id") Long id) {
    return permissionService.findById(id);
  }

  @Operation(summary = "根据id归档权限")
  @PutMapping("/archiveById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.4")
  public void archiveById(@PathVariable(value = "id") Long id) {
    permissionService.archiveById(id);
  }

  @Operation(summary = "根据id从归档恢复权限")
  @PutMapping("/recoverFromArchiveById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.4")
  public void recoverFromArchiveById(@PathVariable(value = "id") Long id) {
    permissionService.recoverFromArchiveById(id);
  }

  @Operation(summary = "添加祖先权限")
  @PutMapping("/addAncestor")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.3.0")
  public void addAncestor(@RequestBody @Valid PermissionAddAncestorCmd permissionAddAncestorCmd) {
    permissionService.addAncestor(permissionAddAncestorCmd);
  }

  @Operation(summary = "获取所有根权限")
  @GetMapping("/findRoot")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.3.0")
  public Page<PermissionFindRootCo> findRoot(
    @ModelAttribute PermissionFindRootCmd permissionFindRootCmd) {
    return permissionService.findRootPermissions(permissionFindRootCmd);
  }

  @Operation(summary = "获取直系后代权限")
  @GetMapping("/findDirect")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.3.0")
  public Page<PermissionFindDirectCo> findDirect(
    @ModelAttribute PermissionFindDirectCmd permissionFindDirectCmd) {
    return permissionService.findDirectPermissions(permissionFindDirectCmd);
  }

  @Operation(summary = "删除权限路径")
  @DeleteMapping("/deletePath/{ancestorId}/{descendantId}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.3.0")
  public void deletePath(@PathVariable(value = "ancestorId") Long ancestorId,
    @PathVariable(value = "descendantId") Long descendantId) {
    permissionService.deletePath(ancestorId, descendantId);
  }
}
