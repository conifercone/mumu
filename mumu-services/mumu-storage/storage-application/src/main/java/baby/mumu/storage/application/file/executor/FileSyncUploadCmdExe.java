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

package baby.mumu.storage.application.file.executor;

import baby.mumu.storage.client.cmds.StreamFileSyncUploadCmd;
import baby.mumu.storage.domain.stream.gateway.StreamFileGateway;
import baby.mumu.storage.infrastructure.streamfile.convertor.StreamFileConvertor;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 流式文件异步上传指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Component
public class FileSyncUploadCmdExe {

  private final StreamFileGateway streamFileGateway;
  private final StreamFileConvertor streamFileConvertor;

  @Autowired
  public FileSyncUploadCmdExe(StreamFileGateway streamFileGateway,
    StreamFileConvertor streamFileConvertor) {
    this.streamFileGateway = streamFileGateway;
    this.streamFileConvertor = streamFileConvertor;
  }

  @Async
  public void execute(StreamFileSyncUploadCmd streamFileSyncUploadCmd) {
    Optional.ofNullable(streamFileSyncUploadCmd).flatMap(streamFileConvertor::toEntity)
      .ifPresent(streamFileGateway::uploadFile);
  }
}
