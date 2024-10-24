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

import baby.mumu.authentication.client.api.AuthorityService;
import baby.mumu.authentication.client.dto.AuthorityAddCmd;
import baby.mumu.authentication.client.dto.AuthorityArchivedFindAllCmd;
import baby.mumu.authentication.client.dto.AuthorityArchivedFindAllSliceCmd;
import baby.mumu.authentication.client.dto.AuthorityFindAllCmd;
import baby.mumu.authentication.client.dto.AuthorityFindAllSliceCmd;
import baby.mumu.authentication.client.dto.AuthorityUpdateCmd;
import baby.mumu.authentication.client.dto.co.AuthorityArchivedFindAllCo;
import baby.mumu.authentication.client.dto.co.AuthorityArchivedFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindAllCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindAllSliceCo;
import baby.mumu.authentication.client.dto.co.AuthorityFindByIdCo;
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
@RequestMapping("/authority")
@Tag(name = "权限管理")
public class AuthorityController {

  private final AuthorityService authorityService;

  @Autowired
  public AuthorityController(AuthorityService authorityService) {
    this.authorityService = authorityService;
  }

  @Operation(summary = "添加权限")
  @PostMapping("/add")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void add(@RequestBody @Valid AuthorityAddCmd authorityAddCmd) {
    authorityService.add(authorityAddCmd);
  }

  @Operation(summary = "根据主键删除权限")
  @DeleteMapping("/deleteById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void deleteById(@PathVariable(value = "id") Long id) {
    authorityService.deleteById(id);
  }

  @Operation(summary = "修改权限")
  @PutMapping("/updateById")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public void updateById(@RequestBody @Valid AuthorityUpdateCmd authorityUpdateCmd) {
    authorityService.updateById(authorityUpdateCmd);
  }

  @Operation(summary = "查询权限")
  @GetMapping("/findAll")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.0")
  @RateLimiter
  public Page<AuthorityFindAllCo> findAll(
      @ModelAttribute @Valid AuthorityFindAllCmd authorityFindAllCmd) {
    return authorityService.findAll(authorityFindAllCmd);
  }

  @Operation(summary = "查询权限（不查询总数）")
  @GetMapping("/findAllSlice")
  @ResponseBody
  @API(status = Status.STABLE, since = "2.2.0")
  @RateLimiter
  public Slice<AuthorityFindAllSliceCo> findAllSlice(
      @ModelAttribute @Valid AuthorityFindAllSliceCmd authorityFindAllSliceCmd) {
    return authorityService.findAllSlice(authorityFindAllSliceCmd);
  }

  @Operation(summary = "查询已归档权限")
  @GetMapping("/findArchivedAll")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.0.0")
  public Page<AuthorityArchivedFindAllCo> findArchivedAll(
      @ModelAttribute @Valid AuthorityArchivedFindAllCmd authorityArchivedFindAllCmd) {
    return authorityService.findArchivedAll(authorityArchivedFindAllCmd);
  }

  @Operation(summary = "查询已归档权限（不查询总数）")
  @GetMapping("/findArchivedAllSlice")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.2.0")
  public Slice<AuthorityArchivedFindAllSliceCo> findArchivedAllSlice(
      @ModelAttribute @Valid AuthorityArchivedFindAllSliceCmd authorityArchivedFindAllSliceCmd) {
    return authorityService.findArchivedAllSlice(authorityArchivedFindAllSliceCmd);
  }

  @Operation(summary = "根据id查询权限")
  @GetMapping("/findById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.0")
  public AuthorityFindByIdCo findById(@PathVariable(value = "id") Long id) {
    return authorityService.findById(id);
  }

  @Operation(summary = "根据id归档权限")
  @PutMapping("/archiveById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.4")
  public void archiveById(@PathVariable(value = "id") Long id) {
    authorityService.archiveById(id);
  }

  @Operation(summary = "根据id从归档恢复权限")
  @PutMapping("/recoverFromArchiveById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.4")
  public void recoverFromArchiveById(@PathVariable(value = "id") Long id) {
    authorityService.recoverFromArchiveById(id);
  }
}
