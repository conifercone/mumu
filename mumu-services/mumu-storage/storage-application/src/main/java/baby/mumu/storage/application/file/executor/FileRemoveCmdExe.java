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

import baby.mumu.storage.client.cmds.FileRemoveCmd;
import baby.mumu.storage.domain.file.gateway.FileGateway;
import baby.mumu.storage.infrastructure.file.convertor.FileConvertor;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 流式文件删除指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Component
public class FileRemoveCmdExe {

  private final FileGateway fileGateway;
  private final FileConvertor fileConvertor;

  @Autowired
  public FileRemoveCmdExe(FileGateway fileGateway,
    FileConvertor fileConvertor) {
    this.fileGateway = fileGateway;
    this.fileConvertor = fileConvertor;
  }

  public void execute(FileRemoveCmd fileRemoveCmd) {
    Optional.ofNullable(fileRemoveCmd).flatMap(fileConvertor::toEntity)
      .ifPresent(fileGateway::removeFile);
  }
}
