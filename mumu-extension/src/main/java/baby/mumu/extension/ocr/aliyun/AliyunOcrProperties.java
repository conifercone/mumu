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
package baby.mumu.extension.ocr.aliyun;

import lombok.Data;

/**
 * 阿里云ocr属性
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
@Data
public class AliyunOcrProperties {

  /**
   * 区域
   */
  private String endpoint = "ocr-api.cn-hangzhou.aliyuncs.com";

  /**
   * 密钥ID
   */
  private String accessKeyId;

  /**
   * 密钥
   */
  private String accessKeySecret;

  /**
   * 已启用
   */
  private boolean enabled;

}
