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
package baby.mumu.authentication.infrastructure.account.gatewayimpl.document.po;

import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.enums.AccountAvatarSourceEnum;
import baby.mumu.basis.enums.AccountAvatarThirdPartyProviderEnum;
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
 * 账户头像存储对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.10.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Document("mumu-account-avatars")
@Metamodel
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
   * 账户ID
   */
  @NotNull
  @Indexed(background = true)
  private Long accountId;

  /**
   * 头像来源
   * <p>source为{@link AccountAvatarSourceEnum#URL}时头像取值{@link AccountAvatarDocumentPO#url}</p>
   * <p>source为{@link AccountAvatarSourceEnum#UPLOAD}时头像取值
   * {@link AccountAvatarDocumentPO#fileId}</p>
   * <p>source为{@link AccountAvatarSourceEnum#THIRD_PARTY}时头像取值
   * {@link AccountAvatarDocumentPO#thirdParty}</p>
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

  /**
   * 头像尺寸
   */
  private AccountAvatarDocumentSize size;

  /**
   * 三方头像
   */
  private AccountAvatarDocumentThirdParty thirdParty;

  /**
   * 是否默认头像
   */
  @NotNull
  @Indexed(background = true)
  private boolean isDefault;

  @Version
  private Long version;

  @Data
  public static class AccountAvatarDocumentSizeItem {

    /**
     * 头像地址
     */
    private String url;

    /**
     * 像素宽度
     */
    private int width;

    /**
     * 像素高度
     */
    private int height;
  }

  @Data
  public static class AccountAvatarDocumentSize {

    /**
     * 小尺寸 - 用于列表、评论、头像角标等，推荐大小 48x48 px
     */
    private AccountAvatarDocumentSizeItem small;

    /**
     * 中等尺寸 - 用于用户卡片、设置页面头像等，推荐大小 96x96 px
     */
    private AccountAvatarDocumentSizeItem medium;

    /**
     * 大尺寸 - 用于个人资料页大头像展示等，推荐大小 192x192 px
     */
    private AccountAvatarDocumentSizeItem large;
  }

  @Data
  public static class AccountAvatarDocumentThirdParty {

    /**
     * 提供者
     */
    private AccountAvatarThirdPartyProviderEnum provider;

    /**
     * 原始URL
     */
    private String rawUrl;

    /**
     * 链接的帐户ID
     */
    private String linkedAccountId;

    /**
     * 头像尺寸
     */
    private AccountAvatarDocumentSize size;
  }
}
