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

package baby.mumu.iam.infra.account.gatewayimpl.document.po;

import baby.mumu.basis.enums.AccountAvatarSourceEnum;
import baby.mumu.basis.po.jpa.JpaDocumentBasisDefaultPersistentObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 账号头像存储对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.10.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Document("mumu-account-avatars")
public class AccountAvatarDocumentPO extends JpaDocumentBasisDefaultPersistentObject {

  @Serial
  private static final long serialVersionUID = -976481457561712450L;

  /**
   * 唯一主键
   */
  @Id
  @NotBlank
  private String id;

  /**
   * 账号ID
   */
  @NotNull
  @Indexed(background = true)
  private Long accountId;

  /**
   * 头像来源
   * <p>source为{@link AccountAvatarSourceEnum#URL}时头像取值{@link AccountAvatarDocumentPO#url}</p>
   * <p>source为{@link AccountAvatarSourceEnum#UPLOAD}时头像取值
   * {@link AccountAvatarDocumentPO#fileId}</p>
   */
  @Indexed(background = true)
  private AccountAvatarSourceEnum source;

  /**
   * 上传头像时的文件ID，填写URL或第三方时可为空
   */
  @Indexed(background = true)
  private String fileId;

  /**
   * 用户上传的URL地址
   */
  @Indexed(background = true)
  private String url;

  @Version
  private Long version;
}
