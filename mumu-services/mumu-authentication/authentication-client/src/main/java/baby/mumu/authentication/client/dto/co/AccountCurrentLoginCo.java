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

/**
 * 查询当前登录账户信息客户端对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountCurrentLoginCo extends BaseClientObject {

  @Serial
  private static final long serialVersionUID = -1195373340664104308L;

  private Long id;

  private String username;

  private String avatarUrl;

  private String phone;

  private SexEnum sex;

  private String email;

  private String timezone;

  private LanguageEnum language;

  private LocalDate birthday;

  private int age;

  private List<AccountRoleCurrentLoginQueryCo> roles;

  private List<AccountAddressCurrentLoginQueryCo> addresses;

  private List<AccountSystemSettingsCurrentLoginQueryCo> systemSettings;

  @Data
  public static class AccountAddressCurrentLoginQueryCo {

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
  public static class AccountRoleCurrentLoginQueryCo {

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
     * 角色权限
     */
    private List<AccountRoleAuthorityCurrentLoginQueryCo> authorities;
  }

  @Data
  public static class AccountRoleAuthorityCurrentLoginQueryCo {

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
  }

  @Data
  public static class AccountSystemSettingsCurrentLoginQueryCo {

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
     * 已启用
     */
    private Boolean enabled;
  }
}
