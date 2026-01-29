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

package baby.mumu.iam.adapter.web;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.response.ResponseWrapper;
import baby.mumu.iam.client.api.PermissionService;
import baby.mumu.iam.client.cmds.*;
import baby.mumu.iam.client.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseWrapper<Long> add(@RequestBody @Validated PermissionAddCmd permissionAddCmd) {
        return ResponseWrapper.success(permissionService.add(permissionAddCmd));
    }

    @Operation(summary = "根据主键删除权限")
    @DeleteMapping("/deleteById/{id}")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public void deleteById(@PathVariable Long id) {
        permissionService.deleteById(id);
    }

    @Operation(summary = "根据编码删除权限")
    @DeleteMapping("/deleteByCode/{code}")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "2.4.0")
    public void deleteByCode(@PathVariable String code) {
        permissionService.deleteByCode(code);
    }

    @Operation(summary = "修改权限")
    @PutMapping("/updateById")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public PermissionUpdatedDataDTO updateById(
        @RequestBody @Validated PermissionUpdateCmd permissionUpdateCmd) {
        return permissionService.updateById(permissionUpdateCmd);
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
    public PermissionFindByIdDTO findById(@PathVariable Long id) {
        return permissionService.findById(id);
    }

    @Operation(summary = "根据code查询权限")
    @GetMapping("/findByCode/{code}")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "2.4.0")
    public PermissionFindByCodeDTO findByCode(@PathVariable String code) {
        return permissionService.findByCode(code);
    }

    @Operation(summary = "根据id归档权限")
    @PutMapping("/archiveById/{id}")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.4")
    public void archiveById(@PathVariable Long id) {
        permissionService.archiveById(id);
    }

    @Operation(summary = "根据id从归档恢复权限")
    @PutMapping("/recoverFromArchiveById/{id}")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.4")
    public void recoverFromArchiveById(@PathVariable Long id) {
        permissionService.recoverFromArchiveById(id);
    }

    @Operation(summary = "添加后代权限")
    @PutMapping("/addDescendant")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "2.3.0")
    public void addDescendant(
        @RequestBody @Validated PermissionAddDescendantCmd permissionAddDescendantCmd) {
        permissionService.addDescendant(permissionAddDescendantCmd);
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
    public void deletePath(@PathVariable Long ancestorId,
                           @PathVariable Long descendantId) {
        permissionService.deletePath(ancestorId, descendantId);
    }

    @Operation(summary = "移动权限路径")
    @PutMapping("/move/{originalAncestorId}/{targetAncestorId}/{descendantId}")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "2.16.0")
    public void move(@PathVariable Long originalAncestorId, @PathVariable Long targetAncestorId,
                     @PathVariable Long descendantId) {
        permissionService.move(originalAncestorId, targetAncestorId, descendantId);
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

    @Operation(summary = "获取指定后代节点的所有祖先路径")
    @GetMapping("/findAllAncestorPathStrings/{descendantId}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.16.0")
    public ResponseWrapper<List<String>> findAllAncestorPathStrings(@PathVariable Long descendantId) {
        return ResponseWrapper.success(permissionService.findAllAncestorPathStrings(descendantId));
    }
}
