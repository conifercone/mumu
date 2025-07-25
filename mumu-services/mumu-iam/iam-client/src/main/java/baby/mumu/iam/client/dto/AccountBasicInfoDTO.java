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
import baby.mumu.basis.enums.GenderEnum;
import baby.mumu.basis.enums.LanguageEnum;
import java.io.Serial;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.javamoney.moneta.Money;
import org.springframework.data.geo.Point;

/**
 * 账号基本信息数据传输对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountBasicInfoDTO extends BaseDataTransferObject {

  @Serial
  private static final long serialVersionUID = -6255187696908573035L;

  /**
   * 账号id
   */
  private Long id;

  /**
   * 账号名
   */
  private String username;

  /**
   * 已启用
   */
  private boolean enabled;

  /**
   * 凭证未过期
   */
  private boolean credentialsNonExpired;

  /**
   * 帐户未锁定
   */
  private boolean accountNonLocked;

  /**
   * 帐号未过期
   */
  private boolean accountNonExpired;

  /**
   * 头像
   */
  private AccountAvatarBasicInfoDTO avatar;

  /**
   * 国际电话区号
   */
  private String phoneCountryCode;

  /**
   * 手机号
   */
  private String phone;

  /**
   * 性别
   */
  private GenderEnum gender;

  /**
   * 电子邮箱
   */
  private String email;

  /**
   * 时区
   */
  private String timezone;

  /**
   * 语言偏好
   */
  private LanguageEnum language;

  /**
   * 出生日期
   */
  private LocalDate birthday;

  /**
   * 个性签名
   */
  private String bio;

  /**
   * 昵称
   */
  private String nickName;

  /**
   * 余额
   */
  private Money balance;

  /**
   * 地址
   */
  private List<AccountAddressBasicInfoDTO> addresses;

  /**
   * 手机号已验证
   */
  private boolean phoneVerified;

  /**
   * 电子邮件已验证
   */
  private boolean emailVerified;


  @Data
  public static class AccountAddressBasicInfoDTO {

    /**
     * 唯一主键
     */
    private String id;

    /**
     * 账号ID
     */
    private Long accountId;

    /**
     * 街道地址，包含门牌号和街道信息
     */
    private String street;

    /**
     * 城市信息
     */
    private String city;

    /**
     * 州或省的信息
     */
    private String state;

    /**
     * 邮政编码
     */
    private String postalCode;

    /**
     * 国家信息
     */
    private String country;

    /**
     * 定位
     */
    private Point location;

    /**
     * 是否为默认地址
     */
    private boolean defaultAddress;
  }

  @Data
  public static class AccountAvatarBasicInfoDTO {

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

    private Long version;

  }
}
