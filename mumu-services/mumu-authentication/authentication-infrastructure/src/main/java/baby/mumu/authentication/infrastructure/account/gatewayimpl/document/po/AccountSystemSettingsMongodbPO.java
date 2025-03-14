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
package baby.mumu.authentication.infrastructure.account.gatewayimpl.document.po;

import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.enums.SystemThemeEnum;
import baby.mumu.basis.enums.SystemThemeModeEnum;
import baby.mumu.basis.po.jpa.JpaMongodbBasisDefaultPersistentObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 账户系统设置
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 2.2.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Document("mumu-account-system-settings")
@Metamodel
public class AccountSystemSettingsMongodbPO extends JpaMongodbBasisDefaultPersistentObject {

  @Serial
  private static final long serialVersionUID = 6286174255794150761L;

  public AccountSystemSettingsMongodbPO(String id, Long userId, String profile, String name,
    boolean defaultSystemSettings, Long version) {
    this.id = id;
    this.userId = userId;
    this.profile = profile;
    this.name = name;
    this.defaultSystemSettings = defaultSystemSettings;
    this.version = version;
  }

  @Id
  @NotBlank
  private String id;

  /**
   * 账户ID
   */
  @NotNull
  @Indexed(background = true)
  private Long userId;

  /**
   * 系统设置标识
   */
  @NotBlank
  @Indexed(background = true)
  private String profile;

  /**
   * 系统设置名称
   */
  @NotBlank
  private String name;

  /**
   * 系统主题
   */
  private SystemThemeEnum systemTheme = SystemThemeEnum.DEFAULT;

  /**
   * 系统主题模式
   */
  private SystemThemeModeEnum systemThemeMode = SystemThemeModeEnum.SYNC_WITH_SYSTEM;

  /**
   * 是否是默认系统设置
   */
  @Indexed(background = true)
  private boolean defaultSystemSettings;

  @Version
  private Long version;
}
