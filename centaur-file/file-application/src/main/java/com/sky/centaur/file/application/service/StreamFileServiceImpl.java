/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
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
package com.sky.centaur.file.application.service;

import com.sky.centaur.file.application.streamfile.executor.StreamFileDownloadCmdExe;
import com.sky.centaur.file.application.streamfile.executor.StreamFileSyncUploadCmdExe;
import com.sky.centaur.file.client.api.StreamFileService;
import com.sky.centaur.file.client.dto.StreamFileDownloadCmd;
import com.sky.centaur.file.client.dto.StreamFileSyncUploadCmd;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 流式文件接口实现类
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
@Service
public class StreamFileServiceImpl implements StreamFileService {

  private final StreamFileSyncUploadCmdExe streamFileSyncUploadCmdExe;
  private final StreamFileDownloadCmdExe streamFileDownloadCmdExe;

  @Autowired
  public StreamFileServiceImpl(StreamFileSyncUploadCmdExe streamFileSyncUploadCmdExe,
      StreamFileDownloadCmdExe streamFileDownloadCmdExe) {
    this.streamFileSyncUploadCmdExe = streamFileSyncUploadCmdExe;
    this.streamFileDownloadCmdExe = streamFileDownloadCmdExe;
  }

  @Override
  public void syncUploadFile(StreamFileSyncUploadCmd streamFileSyncUploadCmd) {
    streamFileSyncUploadCmdExe.execute(streamFileSyncUploadCmd);
  }

  @Override
  public InputStream download(StreamFileDownloadCmd streamFileDownloadCmd) {
    return streamFileDownloadCmdExe.execute(streamFileDownloadCmd);
  }

}
