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

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.storage.client.dto.FileFindFileMetaByMetaIdDTO;
import baby.mumu.storage.domain.file.FileMetadata;
import baby.mumu.storage.domain.file.gateway.FileGateway;
import baby.mumu.storage.infra.file.convertor.FileConvertor;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 文件根据元数据ID获取文件元数据信息文件指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.13.0
 */
@Component
@Observed(name = "FileFindFileMetaByMetaIdCmdExe")
public class FileFindFileMetaByMetaIdCmdExe {

  private final FileGateway fileGateway;
  private final FileConvertor fileConvertor;

  @Autowired
  public FileFindFileMetaByMetaIdCmdExe(FileGateway fileGateway,
    FileConvertor fileConvertor) {
    this.fileGateway = fileGateway;
    this.fileConvertor = fileConvertor;
  }

  public FileFindFileMetaByMetaIdDTO execute(Long metadataId) {
    FileMetadata fileMetadata = fileGateway.findFileMetaByMetaId(metadataId)
      .orElseThrow(() -> new MuMuException(ResponseCode.FILE_DOES_NOT_EXIST));
    return fileConvertor.toFileFindFileMetaByMetaIdDTO(fileMetadata)
      .orElseThrow(() -> new MuMuException(ResponseCode.FILE_METADATA_INVALID));
  }
}
