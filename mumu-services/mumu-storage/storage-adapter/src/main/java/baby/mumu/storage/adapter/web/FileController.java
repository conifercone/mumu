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
import baby.mumu.basis.kotlin.tools.FileDownloadUtils;
import baby.mumu.storage.client.api.FileService;
import baby.mumu.storage.client.cmds.StreamFileDownloadCmd;
import baby.mumu.storage.client.cmds.StreamFileRemoveCmd;
import baby.mumu.storage.client.cmds.StreamFileSyncUploadCmd;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@RestController
@Validated
@RequestMapping("/file")
@Tag(name = "流式文件管理")
public class FileController {

  private final FileService fileService;

  @Autowired
  public FileController(FileService fileService) {
    this.fileService = fileService;
  }

  @Operation(summary = "异步文件上传")
  @PostMapping("/sync/upload")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.1")
  public void syncUpload(@ModelAttribute StreamFileSyncUploadCmd fileUploadCmd,
    @RequestParam("file") MultipartFile file) throws IOException {
    fileUploadCmd.setContent(new ByteArrayInputStream(file.getBytes()));
    fileUploadCmd.setOriginName(file.getOriginalFilename());
    fileUploadCmd.setSize(file.getSize());
    fileService.syncUploadFile(fileUploadCmd);
  }

  @Operation(summary = "文件下载")
  @GetMapping("/download")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.1")
  public void download(@ModelAttribute StreamFileDownloadCmd streamFileDownloadCmd,
    HttpServletResponse response) {
    Assert.notNull(streamFileDownloadCmd, "StreamFileDownloadCmd cannot be null");
    FileDownloadUtils.download(response, ObjectUtils.isEmpty(
        streamFileDownloadCmd.getRename())
        ? streamFileDownloadCmd.getName()
        : streamFileDownloadCmd.getRename(), fileService.download(streamFileDownloadCmd),
      "application/force-download");
  }

  @Operation(summary = "获取字符串格式的文件内容")
  @GetMapping("/stringContent")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.1")
  public String getStringContent(@ModelAttribute StreamFileDownloadCmd streamFileDownloadCmd)
    throws IOException {
    return IOUtils.toString(fileService.download(streamFileDownloadCmd),
      StandardCharsets.UTF_8);
  }

  @Operation(summary = "删除文件")
  @DeleteMapping("/remove")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.1")
  public void removeFile(@RequestBody StreamFileRemoveCmd streamFileRemoveCmd) {
    fileService.removeFile(streamFileRemoveCmd);
  }
}
