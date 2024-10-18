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
import baby.mumu.extension.annotations.NotBlankOrNull;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 账户信息更新客户端对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountUpdateByIdCo extends BaseClientObject {

  @Serial
  private static final long serialVersionUID = 1539139215341758634L;

  @Schema(description = "账户ID", requiredMode = RequiredMode.REQUIRED)
  @NotNull(message = "{account.id.validation.not.null}")
  private Long id;

  @Schema(description = "用户名", requiredMode = RequiredMode.NOT_REQUIRED)
  @NotBlankOrNull(message = "{account.username.validation.not.blank}")
  private String username;

  @Schema(description = "头像地址", requiredMode = RequiredMode.NOT_REQUIRED)
  private String avatarUrl;

  @Schema(description = "联系方式", requiredMode = RequiredMode.NOT_REQUIRED)
  private String phone;

  @Schema(description = "性别", requiredMode = RequiredMode.NOT_REQUIRED)
  private SexEnum sex;

  @Schema(description = "邮箱", requiredMode = RequiredMode.NOT_REQUIRED)
  @NotBlankOrNull(message = "{account.email.validation.not.blank}")
  private String email;

  @Schema(description = "时区", requiredMode = RequiredMode.NOT_REQUIRED)
  private String timezone;

  @Schema(description = "语言偏好", requiredMode = RequiredMode.NOT_REQUIRED)
  private LanguageEnum language;

  @Schema(description = "出生日期", requiredMode = RequiredMode.NOT_REQUIRED)
  private LocalDate birthday;

  @Schema(description = "地址", requiredMode = RequiredMode.NOT_REQUIRED)
  private List<AccountAddressUpdateByIdCo> addresses;

  @Data
  @Schema(description = "地址", requiredMode = RequiredMode.NOT_REQUIRED)
  public static class AccountAddressUpdateByIdCo {

    /**
     * 唯一主键
     */
    @Schema(description = "地址ID", requiredMode = RequiredMode.NOT_REQUIRED)
    private Long id;

    /**
     * 街道地址，包含门牌号和街道信息
     */
    @Schema(description = "街道地址，包含门牌号和街道信息", requiredMode = RequiredMode.NOT_REQUIRED)
    @Size(max = 255)
    private String street;

    /**
     * 城市信息
     */
    @Schema(description = "城市信息", requiredMode = RequiredMode.NOT_REQUIRED)
    @Size(max = 100)
    private String city;

    /**
     * 州或省的信息
     */
    @Schema(description = "州或省的信息", requiredMode = RequiredMode.NOT_REQUIRED)
    @Size(max = 100)
    private String state;

    /**
     * 邮政编码
     */
    @Schema(description = "邮政编码", requiredMode = RequiredMode.NOT_REQUIRED)
    @Size(max = 20)
    private String postalCode;

    /**
     * 国家信息
     */
    @Schema(description = "国家信息", requiredMode = RequiredMode.NOT_REQUIRED)
    @Size(max = 100)
    private String country;
  }
}
