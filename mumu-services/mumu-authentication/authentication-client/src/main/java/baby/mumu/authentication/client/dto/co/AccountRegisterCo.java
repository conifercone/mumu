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
package baby.mumu.authentication.client.dto.co;

import baby.mumu.basis.client.dto.co.BaseClientObject;
import baby.mumu.basis.constants.AccountSystemSettingsDefaultValueConstants;
import baby.mumu.basis.enums.LanguageEnum;
import baby.mumu.basis.enums.SexEnum;
import baby.mumu.basis.enums.SystemThemeEnum;
import baby.mumu.basis.enums.SystemThemeModeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 账户信息注册客户端对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountRegisterCo extends BaseClientObject {

  @Serial
  private static final long serialVersionUID = 4447904589683822023L;

  private Long id;

  @NotBlank(message = "{account.username.validation.not.blank}")
  private String username;

  @NotBlank(message = "{account.password.validation.not.blank}")
  private String password;

  private List<String> roleCodes;

  private String avatarUrl;

  private String phone;

  private SexEnum sex;

  @NotBlank(message = "{account.email.validation.not.blank}")
  private String email;

  private String timezone;

  private LanguageEnum language;

  @NotNull(message = "{account.birthday.validation.not.null}")
  private LocalDate birthday;

  private List<AccountAddressRegisterCo> addresses;

  private List<AccountSystemSettingsRegisterCo> systemSettings = Collections.singletonList(
      AccountSystemSettingsRegisterCo.builder().profile(
              AccountSystemSettingsDefaultValueConstants.DEFAULT_ACCOUNT_SYSTEM_SETTINGS_PROFILE_VALUE)
          .name(
              AccountSystemSettingsDefaultValueConstants.DEFAULT_ACCOUNT_SYSTEM_SETTINGS_NAME_VALUE)
          .enabled(true).build());

  @Data
  public static class AccountAddressRegisterCo {

    /**
     * 唯一主键
     */
    private Long id;

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
  @Builder
  public static class AccountSystemSettingsRegisterCo {

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
    @Builder.Default
    private SystemThemeEnum systemTheme = SystemThemeEnum.DEFAULT;

    /**
     * 系统主题模式
     */
    @Builder.Default
    private SystemThemeModeEnum systemThemeMode = SystemThemeModeEnum.SYNC_WITH_SYSTEM;

    /**
     * 已启用
     */
    @Builder.Default
    private Boolean enabled = true;

    private Long version;
  }
}
