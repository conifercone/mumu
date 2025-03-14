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
package baby.mumu.file.client.api;

import baby.mumu.file.client.cmds.StreamFileDownloadCmd;
import baby.mumu.file.client.cmds.StreamFileRemoveCmd;
import baby.mumu.file.client.cmds.StreamFileSyncUploadCmd;
import java.io.InputStream;

/**
 * 流式文件接口
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 1.0.1
 */
public interface StreamFileService {

  /**
   * 异步文件上传
   *
   * @param streamFileSyncUploadCmd 流文件异步上传指令
   */
  void syncUploadFile(StreamFileSyncUploadCmd streamFileSyncUploadCmd);

  /**
   * 下载
   *
   * @param streamFileDownloadCmd 流文件下载指令
   * @return 文件流
   */
  InputStream download(StreamFileDownloadCmd streamFileDownloadCmd);

  /**
   * 删除文件
   *
   * @param streamFileRemoveCmd 流文件删除指令
   */
  void removeFile(StreamFileRemoveCmd streamFileRemoveCmd);
}
