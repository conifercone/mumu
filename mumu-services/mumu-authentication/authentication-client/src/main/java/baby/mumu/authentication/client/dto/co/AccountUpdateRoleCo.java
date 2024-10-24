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
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 账户更新角色客户端对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountUpdateRoleCo extends BaseClientObject {

  @Serial
  private static final long serialVersionUID = 6110957444309849374L;

  @NotNull
  @Schema(description = "账户ID", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Schema(description = "角色编码集合", requiredMode = RequiredMode.NOT_REQUIRED)
  private List<String> roleCodes;
}
