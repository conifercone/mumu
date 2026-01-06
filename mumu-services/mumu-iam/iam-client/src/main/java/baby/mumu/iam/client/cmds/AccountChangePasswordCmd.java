/*
 * Copyright (c) 2024-2026, the original author or authors.
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

package baby.mumu.iam.client.cmds;

import baby.mumu.basis.constants.RegexpConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 账号修改密码指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Data
public class AccountChangePasswordCmd {

    @NotBlank(message = "{account.password.validation.not.blank}")
    private String originalPassword;

    @NotBlank(message = "{account.password.validation.not.blank}")
    @Pattern(regexp = RegexpConstants.PASSWORD_REGEXP, message = "{account.password.validation.pattern}")
    private String newPassword;
}
