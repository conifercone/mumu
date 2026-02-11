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

import baby.mumu.basis.enums.SystemThemeEnum;
import baby.mumu.basis.enums.SystemThemeModeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 添加账号系统设置指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
@Data
public class AccountAddSystemSettingsCmd {

    /**
     * 系统设置标识
     */
    @Schema(description = "系统设置标识", requiredMode = RequiredMode.REQUIRED)
    @NotBlank
    private String profile;

    /**
     * 系统设置名称
     */
    @Schema(description = "系统设置名称", requiredMode = RequiredMode.REQUIRED)
    @NotBlank
    private String name;

    /**
     * 系统主题
     */
    @Schema(description = "系统主题", requiredMode = RequiredMode.REQUIRED)
    @NotNull
    private SystemThemeEnum systemTheme = SystemThemeEnum.DEFAULT;

    /**
     * 系统主题模式
     */
    @Schema(description = "系统主题模式", requiredMode = RequiredMode.REQUIRED)
    @NotNull
    private SystemThemeModeEnum systemThemeMode = SystemThemeModeEnum.SYNC_WITH_SYSTEM;
}
