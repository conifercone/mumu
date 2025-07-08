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

package baby.mumu.iam.client.dto;

import baby.mumu.basis.dto.BaseDataTransferObject;
import baby.mumu.basis.enums.AccountAvatarSourceEnum;
import baby.mumu.basis.enums.LanguageEnum;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 附近的账号数据传输对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.6.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountNearbyDTO extends BaseDataTransferObject {

  @Serial
  private static final long serialVersionUID = -5128372525687244372L;

  /**
   * 账号名
   */
  private String username;

  /**
   * 头像地址
   */
  private AccountAvatarDTO avatar;

  /**
   * 时区
   */
  private String timezone;

  /**
   * 语言偏好
   */
  private LanguageEnum language;

  /**
   * 个性签名
   */
  private String bio;

  /**
   * 昵称
   */
  private String nickName;

  @Data
  public static class AccountAvatarDTO {

    /**
     * 唯一主键
     */
    private String id;

    /**
     * 头像来源
     */
    private AccountAvatarSourceEnum source;

    /**
     * 上传头像时的文件ID，填写URL或第三方时可为空
     */
    private String fileId;

    /**
     * 用户上传的URL地址
     */
    private String url;

  }
}
