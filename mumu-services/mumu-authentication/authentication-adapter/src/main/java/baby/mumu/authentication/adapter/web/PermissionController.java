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
package baby.mumu.authentication.adapter.web;

import baby.mumu.authentication.client.api.PermissionService;
import baby.mumu.authentication.client.cmds.PermissionAddAncestorCmd;
import baby.mumu.authentication.client.cmds.PermissionAddCmd;
import baby.mumu.authentication.client.cmds.PermissionArchivedFindAllCmd;
import baby.mumu.authentication.client.cmds.PermissionArchivedFindAllSliceCmd;
import baby.mumu.authentication.client.cmds.PermissionFindAllCmd;
import baby.mumu.authentication.client.cmds.PermissionFindAllSliceCmd;
import baby.mumu.authentication.client.cmds.PermissionFindDirectCmd;
import baby.mumu.authentication.client.cmds.PermissionFindRootCmd;
import baby.mumu.authentication.client.cmds.PermissionUpdateCmd;
import baby.mumu.authentication.client.dto.PermissionArchivedFindAllDTO;
import baby.mumu.authentication.client.dto.PermissionArchivedFindAllSliceDTO;
import baby.mumu.authentication.client.dto.PermissionFindAllDTO;
import baby.mumu.authentication.client.dto.PermissionFindAllSliceDTO;
import baby.mumu.authentication.client.dto.PermissionFindByCodeDTO;
import baby.mumu.authentication.client.dto.PermissionFindByIdDTO;
import baby.mumu.authentication.client.dto.PermissionFindDirectDTO;
import baby.mumu.authentication.client.dto.PermissionFindRootDTO;
import baby.mumu.basis.annotations.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.validation.annotation.Validated;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@RestController
@Validated
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
  public void add(@RequestBody @Validated PermissionAddCmd permissionAddCmd) {
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

  @Operation(summary = "根据编码删除权限")
  @DeleteMapping("/deleteByCode/{code}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.4.0")
  public void deleteByCode(@PathVariable(value = "code") String code) {
    permissionService.deleteByCode(code);
  }

  @Operation(summary = "修改权限")
  @PutMapping("/updateById")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateById(@RequestBody @Validated PermissionUpdateCmd permissionUpdateCmd) {
    permissionService.updateById(permissionUpdateCmd);
  }

  @Operation(summary = "查询权限")
  @GetMapping("/findAll")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.0")
  @RateLimiter
  public Page<PermissionFindAllDTO> findAll(
    @ModelAttribute @Validated PermissionFindAllCmd permissionFindAllCmd) {
    return permissionService.findAll(permissionFindAllCmd);
  }

  @Operation(summary = "查询权限（不查询总数）")
  @GetMapping("/findAllSlice")
  @ResponseBody
  @API(status = Status.STABLE, since = "2.2.0")
  @RateLimiter
  public Slice<PermissionFindAllSliceDTO> findAllSlice(
    @ModelAttribute @Validated PermissionFindAllSliceCmd permissionFindAllSliceCmd) {
    return permissionService.findAllSlice(permissionFindAllSliceCmd);
  }

  @Operation(summary = "查询已归档权限")
  @GetMapping("/findArchivedAll")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.0.0")
  public Page<PermissionArchivedFindAllDTO> findArchivedAll(
    @ModelAttribute @Validated PermissionArchivedFindAllCmd permissionArchivedFindAllCmd) {
    return permissionService.findArchivedAll(permissionArchivedFindAllCmd);
  }

  @Operation(summary = "查询已归档权限（不查询总数）")
  @GetMapping("/findArchivedAllSlice")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public Slice<PermissionArchivedFindAllSliceDTO> findArchivedAllSlice(
    @ModelAttribute @Validated PermissionArchivedFindAllSliceCmd permissionArchivedFindAllSliceCmd) {
    return permissionService.findArchivedAllSlice(permissionArchivedFindAllSliceCmd);
  }

  @Operation(summary = "根据id查询权限")
  @GetMapping("/findById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public PermissionFindByIdDTO findById(@PathVariable(value = "id") Long id) {
    return permissionService.findById(id);
  }

  @Operation(summary = "根据code查询权限")
  @GetMapping("/findByCode/{code}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.4.0")
  public PermissionFindByCodeDTO findByCode(@PathVariable(value = "code") String code) {
    return permissionService.findByCode(code);
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
  public void addAncestor(
    @RequestBody @Validated PermissionAddAncestorCmd permissionAddAncestorCmd) {
    permissionService.addAncestor(permissionAddAncestorCmd);
  }

  @Operation(summary = "获取所有根权限")
  @GetMapping("/findRoot")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.3.0")
  public Page<PermissionFindRootDTO> findRoot(
    @ModelAttribute PermissionFindRootCmd permissionFindRootCmd) {
    return permissionService.findRootPermissions(permissionFindRootCmd);
  }

  @Operation(summary = "获取直系后代权限")
  @GetMapping("/findDirect")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.3.0")
  public Page<PermissionFindDirectDTO> findDirect(
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

  @Operation(summary = "下载所有权限数据（不包含权限关系数据）")
  @GetMapping("/downloadAll")
  @RateLimiter
  @API(status = Status.STABLE, since = "2.4.0")
  public void downloadAll(HttpServletResponse response) {
    permissionService.downloadAll(response);
  }

  @Operation(summary = "下载所有权限数据（包含权限关系数据）")
  @GetMapping("/downloadAllIncludePath")
  @RateLimiter
  @API(status = Status.STABLE, since = "2.6.0")
  public void downloadAllIncludePath(HttpServletResponse response) {
    permissionService.downloadAllIncludePath(response);
  }
}
