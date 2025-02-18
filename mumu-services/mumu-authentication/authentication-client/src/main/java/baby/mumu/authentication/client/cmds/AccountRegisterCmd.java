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
package baby.mumu.authentication.client.cmds;

import baby.mumu.basis.constants.RegexpConstants;
import baby.mumu.basis.enums.LanguageEnum;
import baby.mumu.basis.enums.SexEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import org.springframework.data.geo.Point;

/**
 * 账户注册指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
@Schema(description = "账户注册指令")
public class AccountRegisterCmd {

  @Schema(description = "用户名", requiredMode = RequiredMode.REQUIRED)
  @NotBlank(message = "{account.username.validation.not.blank}")
  private String username;

  @Schema(description = "密码", requiredMode = RequiredMode.REQUIRED)
  @NotBlank(message = "{account.password.validation.not.blank}")
  @Pattern(regexp = RegexpConstants.PASSWORD_REGEXP, message = "{account.password.validation.pattern}")
  private String password;

  @Schema(description = "角色编码集合", requiredMode = RequiredMode.NOT_REQUIRED)
  private List<String> roleCodes;

  @Schema(description = "头像地址", requiredMode = RequiredMode.NOT_REQUIRED)
  private String avatarUrl;

  @Schema(description = "手机号", requiredMode = RequiredMode.NOT_REQUIRED)
  private String phone;

  @Schema(description = "性别", requiredMode = RequiredMode.NOT_REQUIRED)
  private SexEnum sex;

  @Schema(description = "邮箱地址", requiredMode = RequiredMode.REQUIRED)
  @NotBlank(message = "{account.email.validation.not.blank}")
  private String email;

  @Schema(description = "时区", requiredMode = RequiredMode.NOT_REQUIRED)
  private String timezone;

  @Schema(description = "语言偏好", requiredMode = RequiredMode.NOT_REQUIRED)
  private LanguageEnum language;

  @Schema(description = "出生日期", requiredMode = RequiredMode.REQUIRED)
  @NotNull(message = "{account.birthday.validation.not.null}")
  private LocalDate birthday;

  @Schema(description = "个性签名", requiredMode = RequiredMode.NOT_REQUIRED)
  private String bio;

  @Schema(description = "昵称", requiredMode = RequiredMode.NOT_REQUIRED)
  private String nickName;

  @Schema(description = "地址集合", requiredMode = RequiredMode.NOT_REQUIRED)
  private List<AccountAddressRegisterCmd> addresses;

  @Data
  public static class AccountAddressRegisterCmd {

    /**
     * 街道地址，包含门牌号和街道信息
     */
    @Size(max = 255)
    @Schema(description = "街道地址，包含门牌号和街道信息", requiredMode = RequiredMode.NOT_REQUIRED)
    private String street;

    /**
     * 城市信息
     */
    @Size(max = 100)
    @Schema(description = "城市信息", requiredMode = RequiredMode.NOT_REQUIRED)
    private String city;

    /**
     * 州或省的信息
     */
    @Size(max = 100)
    @Schema(description = "州或省的信息", requiredMode = RequiredMode.NOT_REQUIRED)
    private String state;

    /**
     * 邮政编码
     */
    @Size(max = 20)
    @Schema(description = "邮政编码", requiredMode = RequiredMode.NOT_REQUIRED)
    private String postalCode;

    /**
     * 国家信息
     */
    @Size(max = 100)
    @Schema(description = "国家信息", requiredMode = RequiredMode.NOT_REQUIRED)
    private String country;

    /**
     * 定位
     */
    @Schema(description = "定位", requiredMode = RequiredMode.NOT_REQUIRED)
    private Point location;
  }

  /**
   * 验证码ID
   */
  @Schema(description = "验证码ID", requiredMode = RequiredMode.REQUIRED)
  @NotNull(message = "{captcha.id.validation.not.null}")
  private Long captchaId;

  /**
   * 验证码内容
   */
  @Schema(description = "验证码内容", requiredMode = RequiredMode.REQUIRED)
  @NotBlank(message = "{captcha.validation.not.blank}")
  private String captcha;
}
