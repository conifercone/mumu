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

package baby.mumu.storage.adapter.web;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.response.ResponseWrapper;
import baby.mumu.storage.client.api.FileService;
import baby.mumu.storage.client.dto.FileFindMetaByMetaIdDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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

    @Operation(summary = "上传文件",
        parameters = {
            @Parameter(name = "storageZoneId", description = "存储区域ID", required = true, in = ParameterIn.PATH),
            @Parameter(name = "file", description = "源文件", required = true, in = ParameterIn.QUERY)
        })
    @PostMapping("/upload/{storageZoneId}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.12.0")
    public ResponseWrapper<Long> upload(
        @PathVariable @NotNull Long storageZoneId,
        @RequestParam("file") MultipartFile file) {
        return ResponseWrapper.success(fileService.upload(storageZoneId, file));
    }

    @Operation(summary = "根据元数据ID删除文件",
        parameters = {
            @Parameter(name = "metadataId", description = "文件元数据ID", required = true, in = ParameterIn.PATH)
        })
    @DeleteMapping("/deleteByMetadataId/{metadataId}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.12.0")
    public void deleteByMetadataId(
        @PathVariable @NotNull Long metadataId) {
        fileService.deleteByMetadataId(metadataId);
    }

    @Operation(summary = "根据元数据ID下载文件",
        parameters = {
            @Parameter(name = "metadataId", description = "文件元数据ID", required = true, in = ParameterIn.PATH)
        })
    @GetMapping("/downloadByMetadataId/{metadataId}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.12.0")
    public void downloadByMetadataId(
        @PathVariable @NotNull Long metadataId,
        HttpServletResponse httpServletResponse) {
        fileService.downloadByMetadataId(metadataId, httpServletResponse);
    }

    @Operation(summary = "根据元数据ID获取文件元数据信息",
        parameters = {
            @Parameter(name = "metadataId", description = "文件元数据ID", required = true, in = ParameterIn.PATH)
        })
    @GetMapping("/findMetaByMetaId/{metadataId}")
    @RateLimiter
    @API(status = Status.STABLE, since = "2.13.0")
    public FileFindMetaByMetaIdDTO findMetaByMetaId(
        @PathVariable @NotNull Long metadataId) {
        return fileService.findMetaByMetaId(metadataId);
    }
}
