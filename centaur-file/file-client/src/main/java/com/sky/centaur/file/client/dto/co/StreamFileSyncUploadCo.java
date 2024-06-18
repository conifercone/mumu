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
package com.sky.centaur.file.client.dto.co;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sky.centaur.basis.constants.CommonConstants;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import java.io.InputStream;
import lombok.Data;
import org.springframework.util.ObjectUtils;

/**
 * 流式文件异步上传客户端对象
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamFileSyncUploadCo {

  /**
   * 文件内容
   */
  private InputStream content;

  /**
   * 存储地址
   */
  private String storageAddress;

  /**
   * 文件名(可以不包含文件拓展名,默认取当前上传文件的文件拓展名)
   */
  private String name;

  /**
   * 源文件名
   */
  private String originName;

  /**
   * 文件大小
   */
  private long size;

  public String getName() {
    if (ObjectUtils.isEmpty(name)) {
      return originName;
    } else if (!name.contains(CommonConstants.DOT)) {
      if (ObjectUtils.isEmpty(originName)) {
        throw new CentaurException(ResultCode.FILE_NAME_CANNOT_BE_EMPTY);
      }
      return name.concat(CommonConstants.DOT).concat(
          originName.substring(originName.lastIndexOf(CommonConstants.DOT) + 1));
    }
    return name;
  }
}
