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

package baby.mumu.storage.client.cmds;

import baby.mumu.basis.constants.CommonConstants;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import java.io.InputStream;
import lombok.Data;
import org.springframework.util.ObjectUtils;

/**
 * 文件异步上传指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Data
public class FileSyncUploadCmd {

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
  private Long size;

  public String getName() {
    if (ObjectUtils.isEmpty(name)) {
      return originName;
    } else if (!name.contains(CommonConstants.DOT)) {
      if (ObjectUtils.isEmpty(originName)) {
        throw new MuMuException(ResponseCode.FILE_NAME_CANNOT_BE_EMPTY);
      }
      return name.concat(CommonConstants.DOT).concat(
        originName.substring(originName.lastIndexOf(CommonConstants.DOT) + 1));
    }
    return name;
  }
}
