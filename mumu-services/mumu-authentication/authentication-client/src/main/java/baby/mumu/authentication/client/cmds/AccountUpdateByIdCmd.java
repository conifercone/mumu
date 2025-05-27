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

import baby.mumu.basis.enums.GenderEnum;
import baby.mumu.basis.enums.LanguageEnum;
import baby.mumu.extension.annotations.NotBlankOrNull;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

/**
 * 账号根据id更新指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
public class AccountUpdateByIdCmd {

  @Schema(description = "账号ID", requiredMode = RequiredMode.REQUIRED)
  @NotNull(message = "{account.id.validation.not.null}")
  private Long id;

  @Schema(description = "用户名", requiredMode = RequiredMode.NOT_REQUIRED)
  @NotBlankOrNull(message = "{account.username.validation.not.blank}")
  private String username;

  @Schema(description = "国际电话区号", requiredMode = RequiredMode.NOT_REQUIRED)
  private String phoneCountryCode;

  @Schema(description = "手机号", requiredMode = RequiredMode.NOT_REQUIRED)
  private String phone;

  @Schema(description = "性别", requiredMode = RequiredMode.NOT_REQUIRED)
  private GenderEnum gender;

  @Schema(description = "邮箱", requiredMode = RequiredMode.NOT_REQUIRED)
  @NotBlankOrNull(message = "{account.email.validation.not.blank}")
  private String email;

  @Schema(description = "时区", requiredMode = RequiredMode.NOT_REQUIRED)
  private String timezone;

  @Schema(description = "语言偏好", requiredMode = RequiredMode.NOT_REQUIRED)
  private LanguageEnum language;

  @Schema(description = "出生日期", requiredMode = RequiredMode.NOT_REQUIRED)
  private LocalDate birthday;

  @Schema(description = "个性签名", requiredMode = RequiredMode.NOT_REQUIRED)
  private String bio;

  @Schema(description = "昵称", requiredMode = RequiredMode.NOT_REQUIRED)
  private String nickName;
}
