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

import baby.mumu.basis.enums.SystemThemeEnum;
import baby.mumu.basis.enums.SystemThemeModeEnum;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新账号系统设置指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
@Data
public class AccountModifySystemSettingsBySettingsIdCmd {

  /**
   * 唯一主键
   */
  @NotBlank
  private String id;

  /**
   * 系统设置标识
   */
  @Nullable
  @Size(min = 1)
  private String profile;

  /**
   * 系统设置名称
   */
  @Nullable
  @Size(min = 1)
  private String name;

  /**
   * 系统主题
   */
  @Nullable
  private SystemThemeEnum systemTheme;

  /**
   * 系统主题模式
   */
  @Nullable
  private SystemThemeModeEnum systemThemeMode;
}
