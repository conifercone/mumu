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

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

/**
 * 查询已归档角色指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
@Data
public class RoleArchivedFindAllCmd {

  private Long id;

  private String name;

  private String code;

  @Size(max = 500)
  private String description;

  private List<Long> permissionIds;

  @Min(value = 1, message = "{current.validation.min.size}")
  private Integer current = 1;

  @Min(value = 1, message = "{page.size.validation.min.size}")
  private Integer pageSize = 10;
}
