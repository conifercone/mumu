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

package baby.mumu.storage.infra.file.gatewayimpl.storage;

import baby.mumu.storage.domain.file.File;

import java.io.InputStream;

/**
 * 文件存储
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.12.0
 */
public interface FileStorageRepository {

    /**
     * 文件上传
     *
     * @param file 源文件
     */
    void upload(File file) throws Exception;

    /**
     * 文件删除
     *
     * @param file 目标文件
     */
    void delete(File file) throws Exception;

    /**
     * 文件下载
     *
     * @param file 目标文件
     */
    InputStream download(File file) throws Exception;


    /**
     * 创建目标文件对应的存储区
     *
     * @param file 目标文件
     */
    void createStorageZone(File file) throws Exception;


    /**
     * 判断目标文件对应的存储区是否存在
     *
     * @param file 目标文件
     */
    boolean storageZoneExists(File file) throws Exception;
}
