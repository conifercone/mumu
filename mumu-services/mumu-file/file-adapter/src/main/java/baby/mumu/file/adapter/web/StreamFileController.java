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
package baby.mumu.file.adapter.web;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.file.client.api.StreamFileService;
import baby.mumu.file.client.dto.StreamFileDownloadCmd;
import baby.mumu.file.client.dto.StreamFileRemoveCmd;
import baby.mumu.file.client.dto.StreamFileSyncUploadCmd;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * 流式文件管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@RestController
@RequestMapping("/stream")
@Tag(name = "流式文件管理")
public class StreamFileController {

  private final StreamFileService streamFileService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  public StreamFileController(StreamFileService streamFileService) {
    this.streamFileService = streamFileService;
  }

  @Operation(summary = "异步文件上传")
  @PostMapping("/sync/upload")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.1")
  public void syncUpload(@RequestParam("streamFileSyncUploadCmd") String streamFileSyncUploadCmd,
      @RequestParam("file") MultipartFile file) throws IOException {
    StreamFileSyncUploadCmd fileUploadCmd = objectMapper.readValue(streamFileSyncUploadCmd,
        StreamFileSyncUploadCmd.class);
    fileUploadCmd.getStreamFileSyncUploadCo().setContent(new ByteArrayInputStream(file.getBytes()));
    fileUploadCmd.getStreamFileSyncUploadCo().setOriginName(file.getOriginalFilename());
    fileUploadCmd.getStreamFileSyncUploadCo().setSize(file.getSize());
    streamFileService.syncUploadFile(fileUploadCmd);
  }

  @Operation(summary = "文件下载")
  @GetMapping("/download")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.1")
  public void download(@ModelAttribute StreamFileDownloadCmd streamFileDownloadCmd,
      HttpServletResponse response)
      throws IOException {
    Assert.notNull(streamFileDownloadCmd, "StreamFileDownloadCmd cannot be null");
    response.setHeader("Content-Disposition",
        "attachment;filename=" + (ObjectUtils.isEmpty(
            streamFileDownloadCmd.getRename())
            ? streamFileDownloadCmd.getName()
            : streamFileDownloadCmd.getRename()));
    response.setContentType("application/force-download");
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    IOUtils.copy(streamFileService.download(streamFileDownloadCmd), response.getOutputStream());
  }

  @Operation(summary = "获取字符串格式的文件内容")
  @GetMapping("/stringContent")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.1")
  public String getStringContent(@ModelAttribute StreamFileDownloadCmd streamFileDownloadCmd)
      throws IOException {
    return IOUtils.toString(streamFileService.download(streamFileDownloadCmd),
        StandardCharsets.UTF_8);
  }

  @Operation(summary = "删除文件")
  @DeleteMapping("/remove")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.1")
  public void removeFile(@RequestBody StreamFileRemoveCmd streamFileRemoveCmd) {
    streamFileService.removeFile(streamFileRemoveCmd);
  }
}
