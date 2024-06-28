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
package com.sky.centaur.file.application.streamfile.executor;

import com.sky.centaur.file.client.dto.StreamFileRemoveCmd;
import com.sky.centaur.file.domain.stream.gateway.StreamFileGateway;
import com.sky.centaur.file.infrastructure.streamfile.convertor.StreamFileConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 流式文件删除指令执行器
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
@Component
public class StreamFileRemoveCmdExe {

  private final StreamFileGateway streamFileGateway;

  @Autowired
  public StreamFileRemoveCmdExe(StreamFileGateway streamFileGateway) {
    this.streamFileGateway = streamFileGateway;
  }

  public void execute(StreamFileRemoveCmd streamFileRemoveCmd) {
    Assert.notNull(streamFileRemoveCmd, "StreamFileRemoveCmd cannot be null");
    StreamFileConvertor.toEntity(streamFileRemoveCmd.getStreamFileRemoveCo())
        .ifPresent(streamFileGateway::removeFile);
  }
}
