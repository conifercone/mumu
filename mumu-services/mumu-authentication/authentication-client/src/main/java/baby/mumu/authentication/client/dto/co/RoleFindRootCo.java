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
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取所有根角色客户端对象
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.4.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleFindRootCo extends BaseClientObject {

  @Serial
  private static final long serialVersionUID = -3809817450467517181L;

  private Long id;

  private String code;

  private String name;

  private boolean hasDescendant;
}
