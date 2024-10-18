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

import baby.mumu.authentication.client.dto.co.AccountRegisterCo;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 账户注册指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
@Schema(description = "账户注册指令")
public class AccountRegisterCmd {

  /**
   * 账户注册信息
   */
  @Valid
  @Schema(description = "账户注册信息", requiredMode = RequiredMode.REQUIRED)
  private AccountRegisterCo accountRegisterCo;

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
