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

package baby.mumu.storage.adapter.web;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.response.ResponseWrapper;
import baby.mumu.storage.client.api.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.12.0
 */
@RestController
@Validated
@RequestMapping("/file")
@Tag(name = "文件管理")
public class FileController {

  private final FileService fileService;

  @Autowired
  public FileController(FileService fileService) {
    this.fileService = fileService;
  }

  @Operation(summary = "文件上传")
  @PostMapping("/upload")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.12.0")
  public ResponseWrapper<Long> upload(
    @Parameter(description = "文件存储区域", required = true) @RequestParam("storageZone") @NotNull String storageZone,
    @Parameter(description = "源文件", required = true) @RequestParam("file") MultipartFile file) {
    return ResponseWrapper.success(fileService.upload(storageZone, file));
  }

  @Operation(summary = "文件根据元数据ID删除")
  @DeleteMapping("/deleteByMetadataId/{metadataId}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "2.12.0")
  public void deleteByMetadataId(
    @Parameter(description = "文件元数据ID", required = true) @NotNull @PathVariable("metadataId") Long metadataId) {
    fileService.deleteByMetadataId(metadataId);
  }
}
