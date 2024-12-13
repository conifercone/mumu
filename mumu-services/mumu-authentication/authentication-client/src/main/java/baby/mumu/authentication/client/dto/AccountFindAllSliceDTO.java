/*
 * Copyright (c) 2024-2024, the original author or authors.
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
package baby.mumu.authentication.client.dto;

import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.dto.BaseDataTransferObject;
import baby.mumu.basis.enums.LanguageEnum;
import baby.mumu.basis.enums.SexEnum;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.javamoney.moneta.Money;

/**
 * 账户分页查询数据传输对象（不查询总数）
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Metamodel
public class AccountFindAllSliceDTO extends BaseDataTransferObject {

  @Serial
  private static final long serialVersionUID = 2643022626369675463L;

  /**
   * 账户id
   */
  private Long id;

  /**
   * 账户名
   */
  private String username;

  /**
   * 是否启用
   */
  private Boolean enabled;

  /**
   * 凭证未过期
   */
  private Boolean credentialsNonExpired;

  /**
   * 帐户未锁定
   */
  private Boolean accountNonLocked;

  /**
   * 帐号未过期
   */
  private Boolean accountNonExpired;

  /**
   * 账户角色
   */
  private List<AccountFindAllRoleCo> roles;

  /**
   * 头像地址
   */
  private String avatarUrl;

  /**
   * 电话
   */
  private String phone;

  /**
   * 性别
   */
  private SexEnum sex;

  /**
   * 电子邮件
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
   * 生日
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
  private List<AccountFindAllSliceAddressCo> addresses;

  @Data
  public static class AccountFindAllSliceAddressCo {

    /**
     * 唯一主键
     */
    private Long id;

    /**
     * 账户ID
     */
    private Long userId;

    /**
     * 街道地址，包含门牌号和街道信息
     */
    @Size(max = 255)
    private String street;

    /**
     * 城市信息
     */
    @Size(max = 100)
    private String city;

    /**
     * 州或省的信息
     */
    @Size(max = 100)
    private String state;

    /**
     * 邮政编码
     */
    @Size(max = 20)
    private String postalCode;

    /**
     * 国家信息
     */
    @Size(max = 100)
    private String country;
  }

  @Data
  public static class AccountFindAllRoleCo {

    private Long id;

    private String name;

    private String code;

    private String description;

    private List<AccountFindAllPermissionCo> permissions;
  }

  @Data
  public static class AccountFindAllPermissionCo {

    /**
     * 权限id
     */
    private Long id;

    /**
     * 权限编码
     */
    private String code;

    /**
     * 权限名称
     */
    private String name;

    private String description;
  }
}