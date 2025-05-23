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
package baby.mumu.authentication.domain.account;

import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.domain.BasisDomainModel;
import baby.mumu.basis.enums.AccountAvatarSourceEnum;
import baby.mumu.basis.enums.AccountAvatarThirdPartyProviderEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 账户头像领域模型
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.10.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@Metamodel
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountAvatar extends BasisDomainModel {

  @Serial
  private static final long serialVersionUID = 2673950778392742518L;

  /**
   * 唯一主键
   */
  private String id;

  /**
   * 账户ID
   */
  private Long accountId;

  /**
   * 头像来源
   * <p>头像来源为{@link AccountAvatarSourceEnum#URL}时头像取值{@link AccountAvatar#url}</p>
   * <p>头像来源为{@link AccountAvatarSourceEnum#UPLOAD}时头像取值
   * {@link AccountAvatar#fileId}</p>
   * <p>头像来源为{@link AccountAvatarSourceEnum#THIRD_PARTY}时头像取值
   * {@link AccountAvatar#thirdParty}</p>
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

  /**
   * 头像尺寸
   */
  private AccountAvatarSize size;

  /**
   * 三方头像
   */
  private AccountAvatarThirdParty thirdParty;

  /**
   * 是否默认头像
   */
  private boolean isDefault;

  private Long version;


  @Data
  public static class AccountAvatarSize {

    /**
     * 小尺寸 - 用于列表、评论、头像角标等，推荐大小 48x48 px
     */
    private String small;

    /**
     * 中等尺寸 - 用于用户卡片、设置页面头像等，推荐大小 96x96 px
     */
    private String medium;

    /**
     * 大尺寸 - 用于个人资料页大头像展示等，推荐大小 192x192 px
     */
    private String large;
  }

  @Data
  public static class AccountAvatarThirdParty {

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
    private AccountAvatarSize size;
  }
}

