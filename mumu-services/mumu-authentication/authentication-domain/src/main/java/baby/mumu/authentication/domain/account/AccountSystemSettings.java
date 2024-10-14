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
package baby.mumu.authentication.domain.account;

import baby.mumu.basis.annotations.GenerateDescription;
import baby.mumu.basis.domain.BasisDomainModel;
import baby.mumu.basis.enums.SystemThemeEnum;
import baby.mumu.basis.enums.SystemThemeModeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serial;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 账户系统设置
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@GenerateDescription
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountSystemSettings extends BasisDomainModel {

  @Serial
  private static final long serialVersionUID = 3580954151681556830L;

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
  private Boolean enabled;
}
