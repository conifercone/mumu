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

package baby.mumu.storage.client.api;

import baby.mumu.storage.client.cmds.FileDownloadCmd;
import baby.mumu.storage.client.cmds.FileRemoveCmd;
import baby.mumu.storage.client.cmds.FileSyncUploadCmd;
import java.io.InputStream;

/**
 * 流式文件接口
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
public interface FileService {

  /**
   * 异步文件上传
   *
   * @param fileSyncUploadCmd 流文件异步上传指令
   */
  void syncUploadFile(FileSyncUploadCmd fileSyncUploadCmd);

  /**
   * 下载
   *
   * @param fileDownloadCmd 流文件下载指令
   * @return 文件流
   */
  InputStream download(FileDownloadCmd fileDownloadCmd);

  /**
   * 删除文件
   *
   * @param fileRemoveCmd 流文件删除指令
   */
  void removeFile(FileRemoveCmd fileRemoveCmd);
}
