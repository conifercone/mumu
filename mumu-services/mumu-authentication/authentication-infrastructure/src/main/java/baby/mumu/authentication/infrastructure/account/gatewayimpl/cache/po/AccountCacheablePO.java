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
package baby.mumu.authentication.infrastructure.account.gatewayimpl.cache.po;

import baby.mumu.basis.enums.CacheLevelEnum;
import baby.mumu.basis.enums.DigitalPreferenceEnum;
import baby.mumu.basis.enums.GenderEnum;
import baby.mumu.basis.enums.LanguageEnum;
import baby.mumu.basis.enums.SystemThemeEnum;
import baby.mumu.basis.enums.SystemThemeModeEnum;
import baby.mumu.basis.po.jpa.JpaCacheableBasisArchivablePersistentObject;
import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import java.io.Serial;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.javamoney.moneta.Money;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.TimeToLive;

/**
 * 账户基本信息缓存数据对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(value = "mumu:authentication:account")
public class AccountCacheablePO extends JpaCacheableBasisArchivablePersistentObject {

  @Serial
  private static final long serialVersionUID = -2322179892503865278L;

  /**
   * 账户id
   */
  @Id
  @Indexed
  private Long id;

  /**
   * 账户名
   */
  @Indexed
  private String username;

  /**
   * 账户密码
   */
  private String password;

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
   * 头像地址
   */
  private String avatarUrl;

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
  @Indexed
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
   * 手机号已验证
   */
  private boolean phoneVerified;

  /**
   * 电子邮件已验证
   */
  private boolean emailVerified;

  /**
   * 数字偏好
   */
  private DigitalPreferenceEnum digitalPreference;

  /**
   * 地址
   */
  private List<AccountAddressCacheablePO> addresses;

  /**
   * 系统设置
   */
  private List<AccountSystemSettingsCacheablePO> systemSettings;

  /**
   * 存活时间
   * <p>中等级别变化数据：默认缓存时长为1小时</p>
   */
  @TimeToLive
  private Long ttl = CacheLevelEnum.MEDIUM.getSecondTtl();

  @Data
  public static class AccountAddressCacheablePO {

    /**
     * 唯一主键
     */
    private String id;

    /**
     * 账户ID
     */
    private Long userId;

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

    private Long version;
  }

  @Data
  public static class AccountSystemSettingsCacheablePO {

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
     * 账户ID
     */
    private Long userId;

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

    private Long version;
  }
}
