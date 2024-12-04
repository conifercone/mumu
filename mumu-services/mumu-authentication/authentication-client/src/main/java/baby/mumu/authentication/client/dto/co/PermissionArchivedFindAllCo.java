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

import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.co.BaseClientObject;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限查询已归档客户端对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Metamodel
public class PermissionArchivedFindAllCo extends BaseClientObject {

  @Serial
  private static final long serialVersionUID = 8850768735487730429L;

  private Long id;

  @Size(max = 50, message = "{permission.code.validation.size}")
  private String code;

  @Size(max = 200, message = "{permission.name.validation.size}")
  private String name;

  @Size(max = 500)
  private String description;
}
