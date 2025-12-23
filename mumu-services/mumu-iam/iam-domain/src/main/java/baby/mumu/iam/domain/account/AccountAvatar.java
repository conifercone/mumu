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

package baby.mumu.iam.domain.account;

import baby.mumu.basis.domain.BasisDomainModel;
import baby.mumu.basis.enums.AccountAvatarSourceEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serial;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 账号头像领域模型
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.10.0
 */
@Data
@RequiredArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountAvatar extends BasisDomainModel {

  @Serial
  private static final long serialVersionUID = 2673950778392742518L;

  /**
   * 唯一主键
   */
  private String id;

  /**
   * 账号ID
   */
  private Long accountId;

  /**
   * 头像来源
   * <p>头像来源为{@link AccountAvatarSourceEnum#URL}时头像取值{@link AccountAvatar#url}</p>
   * <p>头像来源为{@link AccountAvatarSourceEnum#UPLOAD}时头像取值
   * {@link AccountAvatar#fileId}</p>
   */
  private AccountAvatarSourceEnum source;

  /**
   * 上传头像时的文件ID，填写URL或第三方时可为空
   */
  private String fileId;

  /**
   * 账号上传的URL地址
   */
  private String url;

  private Long version;

}

