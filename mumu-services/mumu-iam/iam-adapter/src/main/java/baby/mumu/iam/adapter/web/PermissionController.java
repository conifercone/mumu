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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springdoc.core.annotations.ParameterObject;
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

    @Operation(summary = "添加权限", description = "新增一条权限记录，返回新建权限ID。",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "权限新增命令对象",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = PermissionAddCmd.class))))
    @PostMapping("/add")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public ResponseWrapper<Long> add(@RequestBody @Validated PermissionAddCmd permissionAddCmd) {
        return ResponseWrapper.success(permissionService.add(permissionAddCmd));
    }

    @Operation(summary = "根据主键删除权限", description = "按权限ID删除权限记录。",
        parameters = {
            @Parameter(name = "id", description = "权限ID", required = true, in = ParameterIn.PATH)
        })
    @DeleteMapping("/deleteById/{id}")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public void deleteById(@PathVariable Long id) {
        permissionService.deleteById(id);
    }

    @Operation(summary = "根据编码删除权限", description = "按权限编码删除权限记录。",
        parameters = {
            @Parameter(name = "code", description = "权限编码", required = true, in = ParameterIn.PATH)
        })
    @DeleteMapping("/deleteByCode/{code}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.4.0")
    public void deleteByCode(@PathVariable String code) {
        permissionService.deleteByCode(code);
    }

    @Operation(summary = "修改权限", description = "根据命令对象更新权限信息，返回更新后的权限数据。",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "权限更新命令对象",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = PermissionUpdateCmd.class))))
    @PutMapping("/updateById")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public PermissionUpdatedDataDTO updateById(
        @RequestBody @Validated PermissionUpdateCmd permissionUpdateCmd) {
        return permissionService.updateById(permissionUpdateCmd);
    }

    @Operation(summary = "查询权限", description = "分页查询权限列表（包含总记录数）。")
    @GetMapping("/findAll")
    @API(status = Status.STABLE, since = "1.0.0")
    @RateLimiter
    public Page<PermissionFindAllDTO> findAll(
        @ParameterObject @ModelAttribute @Validated PermissionFindAllCmd permissionFindAllCmd) {
        return permissionService.findAll(permissionFindAllCmd);
    }

    @Operation(summary = "查询权限（不查询总数）", description = "分页查询权限列表（不统计总记录数，使用Slice返回）。")
    @GetMapping("/findAllSlice")
    @API(status = Status.STABLE, since = "2.2.0")
    @RateLimiter
    public Slice<PermissionFindAllSliceDTO> findAllSlice(
        @ParameterObject @ModelAttribute @Validated PermissionFindAllSliceCmd permissionFindAllSliceCmd) {
        return permissionService.findAllSlice(permissionFindAllSliceCmd);
    }

    @Operation(summary = "查询已归档权限", description = "分页查询已归档的权限列表（包含总记录数）。")
    @GetMapping("/findArchivedAll")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.0.0")
    public Page<PermissionArchivedFindAllDTO> findArchivedAll(
        @ParameterObject @ModelAttribute @Validated PermissionArchivedFindAllCmd permissionArchivedFindAllCmd) {
        return permissionService.findArchivedAll(permissionArchivedFindAllCmd);
    }

    @Operation(summary = "查询已归档权限（不查询总数）", description = "分页查询已归档权限列表（不统计总记录数，使用Slice返回）。")
    @GetMapping("/findArchivedAllSlice")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.2.0")
    public Slice<PermissionArchivedFindAllSliceDTO> findArchivedAllSlice(
        @ParameterObject @ModelAttribute @Validated PermissionArchivedFindAllSliceCmd permissionArchivedFindAllSliceCmd) {
        return permissionService.findArchivedAllSlice(permissionArchivedFindAllSliceCmd);
    }

    @Operation(summary = "根据id查询权限", description = "根据权限ID查询权限详情。",
        parameters = {
            @Parameter(name = "id", description = "权限ID", required = true, in = ParameterIn.PATH)
        })
    @GetMapping("/findById/{id}")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public PermissionFindByIdDTO findById(@PathVariable Long id) {
        return permissionService.findById(id);
    }

    @Operation(summary = "根据code查询权限", description = "根据权限编码查询权限详情。",
        parameters = {
            @Parameter(name = "code", description = "权限编码", required = true, in = ParameterIn.PATH)
        })
    @GetMapping("/findByCode/{code}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.4.0")
    public PermissionFindByCodeDTO findByCode(@PathVariable String code) {
        return permissionService.findByCode(code);
    }

    @Operation(summary = "根据id归档权限", description = "根据权限ID归档权限。",
        parameters = {
            @Parameter(name = "id", description = "权限ID", required = true, in = ParameterIn.PATH)
        })
    @PutMapping("/archiveById/{id}")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.4")
    public void archiveById(@PathVariable Long id) {
        permissionService.archiveById(id);
    }

    @Operation(summary = "根据id从归档恢复权限", description = "根据权限ID将权限从归档状态恢复。",
        parameters = {
            @Parameter(name = "id", description = "权限ID", required = true, in = ParameterIn.PATH)
        })
    @PutMapping("/recoverFromArchiveById/{id}")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.4")
    public void recoverFromArchiveById(@PathVariable Long id) {
        permissionService.recoverFromArchiveById(id);
    }

    @Operation(summary = "添加后代权限", description = "为指定祖先权限添加后代权限关系。",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "权限后代关系新增命令对象",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = PermissionAddDescendantCmd.class))))
    @PutMapping("/addDescendant")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.3.0")
    public void addDescendant(
        @RequestBody @Validated PermissionAddDescendantCmd permissionAddDescendantCmd) {
        permissionService.addDescendant(permissionAddDescendantCmd);
    }

    @Operation(summary = "获取所有根权限", description = "分页获取权限树中的根权限列表。")
    @GetMapping("/findRoot")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.3.0")
    public Page<PermissionFindRootDTO> findRoot(
        @ParameterObject @ModelAttribute PermissionFindRootCmd permissionFindRootCmd) {
        return permissionService.findRootPermissions(permissionFindRootCmd);
    }

    @Operation(summary = "获取直系后代权限", description = "分页获取指定权限的直系后代权限列表。")
    @GetMapping("/findDirect")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.3.0")
    public Page<PermissionFindDirectDTO> findDirect(
        @ParameterObject @ModelAttribute PermissionFindDirectCmd permissionFindDirectCmd) {
        return permissionService.findDirectPermissions(permissionFindDirectCmd);
    }

    @Operation(summary = "删除权限路径", description = "删除祖先权限与后代权限之间的路径关系。",
        parameters = {
            @Parameter(name = "ancestorId", description = "祖先权限ID", required = true, in = ParameterIn.PATH),
            @Parameter(name = "descendantId", description = "后代权限ID", required = true, in = ParameterIn.PATH)
        })
    @DeleteMapping("/deletePath/{ancestorId}/{descendantId}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.3.0")
    public void deletePath(@PathVariable Long ancestorId,
                           @PathVariable Long descendantId) {
        permissionService.deletePath(ancestorId, descendantId);
    }

    @Operation(summary = "移动权限路径", description = "将后代权限从原祖先路径迁移到目标祖先路径。",
        parameters = {
            @Parameter(name = "originalAncestorId", description = "原祖先权限ID", required = true,
                in = ParameterIn.PATH),
            @Parameter(name = "targetAncestorId", description = "目标祖先权限ID", required = true,
                in = ParameterIn.PATH),
            @Parameter(name = "descendantId", description = "后代权限ID", required = true, in = ParameterIn.PATH)
        })
    @PutMapping("/move/{originalAncestorId}/{targetAncestorId}/{descendantId}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.16.0")
    public void move(@PathVariable Long originalAncestorId, @PathVariable Long targetAncestorId,
                     @PathVariable Long descendantId) {
        permissionService.move(originalAncestorId, targetAncestorId, descendantId);
    }

    @Operation(summary = "下载所有权限数据（不包含权限关系数据）", description = "导出全部权限数据，不包含权限路径关系。")
    @GetMapping("/downloadAll")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.4.0")
    public void downloadAll(HttpServletResponse response) {
        permissionService.downloadAll(response);
    }

    @Operation(summary = "下载所有权限数据（包含权限关系数据）", description = "导出全部权限数据，包含权限路径关系。")
    @GetMapping("/downloadAllIncludePath")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.6.0")
    public void downloadAllIncludePath(HttpServletResponse response) {
        permissionService.downloadAllIncludePath(response);
    }

    @Operation(summary = "获取指定后代节点的所有祖先路径", description = "根据后代权限ID获取其所有祖先路径字符串列表。",
        parameters = {
            @Parameter(name = "descendantId", description = "后代权限ID", required = true, in = ParameterIn.PATH)
        })
    @GetMapping("/findAllAncestorPathStrings/{descendantId}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.16.0")
    public ResponseWrapper<List<String>> findAllAncestorPathStrings(@PathVariable Long descendantId) {
        return ResponseWrapper.success(permissionService.findAllAncestorPathStrings(descendantId));
    }
}
