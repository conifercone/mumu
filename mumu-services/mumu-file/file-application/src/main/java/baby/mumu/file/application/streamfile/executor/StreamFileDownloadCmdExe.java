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
package baby.mumu.file.application.streamfile.executor;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.file.client.dto.StreamFileDownloadCmd;
import baby.mumu.file.domain.stream.gateway.StreamFileGateway;
import baby.mumu.file.infrastructure.streamfile.convertor.StreamFileConvertor;
import java.io.InputStream;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 流式文件下载指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Component
public class StreamFileDownloadCmdExe {

  private final StreamFileGateway streamFileGateway;
  private final StreamFileConvertor streamFileConvertor;

  @Autowired
  public StreamFileDownloadCmdExe(StreamFileGateway streamFileGateway,
    StreamFileConvertor streamFileConvertor) {
    this.streamFileGateway = streamFileGateway;
    this.streamFileConvertor = streamFileConvertor;
  }

  public InputStream execute(StreamFileDownloadCmd streamFileDownloadCmd) {
    Assert.notNull(streamFileDownloadCmd, "StreamFileDownloadCmd cannot be null");
    Supplier<MuMuException> downloadFailed = () -> new MuMuException(
      ResponseCode.FILE_DOWNLOAD_FAILED);
    return streamFileConvertor.toEntity(streamFileDownloadCmd)
      .map(streamFile -> streamFileGateway.download(streamFile)
        .orElseThrow(downloadFailed)
      ).orElseThrow(downloadFailed);
  }
}
