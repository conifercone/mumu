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
package baby.mumu.file.infrastructure.streamfile.gatewayimpl.minio.po;

import baby.mumu.basis.annotations.Metamodel;
import java.io.InputStream;
import lombok.Data;

/**
 * 流式文件minio数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Data
@Metamodel
public class StreamFileMinioPO {

  /**
   * 文件内容
   */
  private InputStream content;

  /**
   * 存储地址
   */
  private String storageAddress;

  /**
   * 文件名
   */
  private String name;

  /**
   * 文件大小
   */
  private Long size;
}
