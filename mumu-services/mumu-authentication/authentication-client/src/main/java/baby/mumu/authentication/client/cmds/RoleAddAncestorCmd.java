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

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 给指定后代角色添加祖先角色指令
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 2.4.0
 */
@Data
public class RoleAddAncestorCmd {

  /**
   * 后代角色ID
   */
  @NotNull
  private Long descendantId;

  /**
   * 祖先角色ID
   */
  @NotNull
  private Long ancestorId;

}
