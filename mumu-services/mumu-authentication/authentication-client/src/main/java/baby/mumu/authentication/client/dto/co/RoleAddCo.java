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
import jakarta.validation.constraints.NotBlank;
import java.io.Serial;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色添加客户端对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleAddCo extends BaseClientObject {

  @Serial
  private static final long serialVersionUID = 2187240003793734765L;

  private Long id;

  @NotBlank
  private String name;

  @NotBlank
  private String code;

  private List<Long> authorityIds;
}
