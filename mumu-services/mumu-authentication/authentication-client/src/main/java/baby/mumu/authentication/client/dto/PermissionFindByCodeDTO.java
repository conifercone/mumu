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
package baby.mumu.authentication.client.dto;

import baby.mumu.basis.dto.BaseDataTransferObject;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 根据code查询权限数据传输对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.4.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionFindByCodeDTO extends BaseDataTransferObject {

  @Serial
  private static final long serialVersionUID = -6426137725606848297L;

  private Long id;

  private String code;

  private String name;

  @Size(max = 500)
  private String description;

  private boolean hasDescendant;
}
