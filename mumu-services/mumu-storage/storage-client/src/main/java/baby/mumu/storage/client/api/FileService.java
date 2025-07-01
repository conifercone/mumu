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

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.12.0
 */
public interface FileService {

  /**
   * 文件上传
   *
   * @param storageZone   存储区域
   * @param multipartFile 源文件
   * @return 文件元数据ID
   */
  Long upload(String storageZone, MultipartFile multipartFile);

  /**
   * 根据文件元数据ID删除文件
   *
   * @param metadataId 文件元数据ID
   */
  void deleteByMetadataId(Long metadataId);

  /**
   * 根据文件元数据ID下载文件
   *
   * @param metadataId          文件元数据ID
   * @param httpServletResponse 响应
   */
  void downloadByMetadataId(Long metadataId, HttpServletResponse httpServletResponse);
}
