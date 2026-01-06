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
import baby.mumu.iam.client.api.RoleService;
import baby.mumu.iam.client.cmds.*;
import baby.mumu.iam.client.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 角色管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@RestController
@Validated
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
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public ResponseWrapper<Long> add(@RequestBody @Validated RoleAddCmd roleAddCmd) {
        return ResponseWrapper.success(roleService.add(roleAddCmd));
    }

    @Operation(summary = "根据id删除角色")
    @DeleteMapping("/deleteById/{id}")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public void deleteById(@PathVariable Long id) {
        roleService.deleteById(id);
    }

    @Operation(summary = "根据code删除角色")
    @DeleteMapping("/deleteByCode/{code}")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "2.4.0")
    public void deleteByCode(@PathVariable String code) {
        roleService.deleteByCode(code);
    }

    @Operation(summary = "更新角色")
    @PutMapping("/updateById")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public RoleUpdatedDataDTO updateById(@RequestBody @Validated RoleUpdateCmd roleUpdateCmd) {
        return roleService.updateById(roleUpdateCmd);
    }

    @Operation(summary = "查询角色")
    @GetMapping("/findAll")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.0")
    public Page<RoleFindAllDTO> findAll(@ModelAttribute @Validated RoleFindAllCmd roleFindAllCmd) {
        return roleService.findAll(roleFindAllCmd);
    }

    @Operation(summary = "查询角色(不查询总数)")
    @GetMapping("/findAllSlice")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "2.2.0")
    public Slice<RoleFindAllSliceDTO> findAllSlice(
        @ModelAttribute @Validated RoleFindAllSliceCmd roleFindAllSliceCmd) {
        return roleService.findAllSlice(roleFindAllSliceCmd);
    }

    @Operation(summary = "查询已归档角色")
    @GetMapping("/findArchivedAll")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "2.2.0")
    public Page<RoleArchivedFindAllDTO> findArchivedAll(
        @ModelAttribute @Validated RoleArchivedFindAllCmd roleArchivedFindAllCmd) {
        return roleService.findArchivedAll(roleArchivedFindAllCmd);
    }

    @Operation(summary = "查询已归档角色(不查询总数)")
    @GetMapping("/findArchivedAllSlice")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "2.2.0")
    public Slice<RoleArchivedFindAllSliceDTO> findArchivedAllSlice(
        @ModelAttribute @Validated RoleArchivedFindAllSliceCmd roleArchivedFindAllSliceCmd) {
        return roleService.findArchivedAllSlice(roleArchivedFindAllSliceCmd);
    }

    @Operation(summary = "根据id归档角色")
    @PutMapping("/archiveById/{id}")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.4")
    public void archiveById(@PathVariable Long id) {
        roleService.archiveById(id);
    }

    @Operation(summary = "根据id从归档中恢复角色")
    @PutMapping("/recoverFromArchiveById/{id}")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.4")
    public void recoverFromArchiveById(@PathVariable Long id) {
        roleService.recoverFromArchiveById(id);
    }

    @Operation(summary = "添加后代角色")
    @PutMapping("/addDescendant")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "2.4.0")
    public void addDescendant(@RequestBody @Validated RoleAddDescendantCmd roleAddDescendantCmd) {
        roleService.addDescendant(roleAddDescendantCmd);
    }

    @Operation(summary = "获取所有根角色")
    @GetMapping("/findRoot")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "2.4.0")
    public Page<RoleFindRootDTO> findRoot(
        @ModelAttribute RoleFindRootCmd roleFindRootCmd) {
        return roleService.findRootRoles(roleFindRootCmd);
    }

    @Operation(summary = "获取直系后代角色")
    @GetMapping("/findDirect")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "2.4.0")
    public Page<RoleFindDirectDTO> findDirect(
        @ModelAttribute RoleFindDirectCmd roleFindDirectCmd) {
        return roleService.findDirectRoles(roleFindDirectCmd);
    }

    @Operation(summary = "删除角色路径")
    @DeleteMapping("/deletePath/{ancestorId}/{descendantId}")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "2.4.0")
    public void deletePath(@PathVariable Long ancestorId,
                           @PathVariable Long descendantId) {
        roleService.deletePath(ancestorId, descendantId);
    }

    @Operation(summary = "根据id查询角色")
    @GetMapping("/findById/{id}")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "2.4.0")
    public RoleFindByIdDTO findById(@PathVariable Long id) {
        return roleService.findById(id);
    }

    @Operation(summary = "根据code查询角色")
    @GetMapping("/findByCode/{code}")
    @ResponseBody
    @RateLimiter
    @API(status = Status.STABLE, since = "2.5.0")
    public RoleFindByCodeDTO findByCode(@PathVariable String code) {
        return roleService.findByCode(code);
    }
}
