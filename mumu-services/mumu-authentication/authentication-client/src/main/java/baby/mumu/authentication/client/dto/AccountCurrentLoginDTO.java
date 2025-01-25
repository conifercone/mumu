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
package baby.mumu.authentication.client.dto;

import baby.mumu.basis.dto.BaseDataTransferObject;
import baby.mumu.basis.enums.DigitalPreferenceEnum;
import baby.mumu.basis.enums.LanguageEnum;
import baby.mumu.basis.enums.SexEnum;
import baby.mumu.basis.enums.SystemThemeEnum;
import baby.mumu.basis.enums.SystemThemeModeEnum;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.javamoney.moneta.Money;
import org.springframework.data.geo.Point;

/**
 * 查询当前登录账户信息数据传输对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountCurrentLoginDTO extends BaseDataTransferObject {

  @Serial
  private static final long serialVersionUID = -1195373340664104308L;

  private Long id;

  /**
   * 用户名
   */
  private String username;

  /**
   * 头像地址
   */
  private String avatarUrl;

  /**
   * 手机号
   */
  private String phone;

  /**
   * 性别
   */
  private SexEnum sex;

  /**
   * 邮箱
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
   * 年龄
   */
  private int age;

  /**
   * 余额
   */
  private Money balance;

  /**
   * 数字偏好
   */
  private DigitalPreferenceEnum digitalPreference;

  /**
   * 账户所属角色
   */
  private List<AccountRoleCurrentLoginQueryDTO> roles;

  /**
   * 账户地址
   */
  private List<AccountAddressCurrentLoginQueryDTO> addresses;

  /**
   * 账户系统设置
   */
  private List<AccountSystemSettingsCurrentLoginQueryDTO> systemSettings;

  @Data
  public static class AccountAddressCurrentLoginQueryDTO {

    /**
     * 唯一主键
     */
    private String id;

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
  public static class AccountRoleCurrentLoginQueryDTO {

    /**
     * 角色id
     */
    private Long id;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 角色权限
     */
    private List<AccountRolePermissionCurrentLoginQueryDTO> permissions;
  }

  @Data
  public static class AccountRolePermissionCurrentLoginQueryDTO {

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

    /**
     * 权限描述
     */
    private String description;
  }

  @Data
  public static class AccountSystemSettingsCurrentLoginQueryDTO {

    /**
     * 唯一主键
     */
    private String id;

    /**
     * 系统设置标识
     */
    private String profile;

    /**
     * 系统设置名称
     */
    private String name;

    /**
     * 系统主题
     */
    private SystemThemeEnum systemTheme;

    /**
     * 系统主题模式
     */
    private SystemThemeModeEnum systemThemeMode;

    /**
     * 默认系统设置
     */
    private boolean defaultSystemSettings;

    /**
     * 版本号
     */
    private Long version;
  }
}
