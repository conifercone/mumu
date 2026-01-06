/*
 * Copyright (c) 2024-2026, the original author or authors.
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

package baby.mumu.storage.domain.file.gateway;

import baby.mumu.storage.domain.file.File;
import baby.mumu.storage.domain.file.FileMetadata;

import java.util.Optional;

/**
 * 文件领域网关
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.12.0
 */
public interface FileGateway {

    /**
     * 文件上传
     *
     * @param file 源文件
     * @return 文件元数据ID
     */
    Long upload(File file);

    /**
     * 根据文件元数据ID删除文件
     *
     * @param fileMetadataId 目标文件元数据ID
     */
    void deleteByMetadataId(Long fileMetadataId);

    /**
     * 根据文件元数据ID下载文件
     *
     * @param fileMetadataId 目标文件元数据ID
     */
    File downloadByMetadataId(Long fileMetadataId);

    /**
     * 根据文件元数据ID获取文件元数据信息
     *
     * @param fileMetadataId 文件元数据ID
     * @return 文件元数据
     */
    Optional<FileMetadata> findMetaByMetaId(Long fileMetadataId);
}
