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

import baby.mumu.authentication.client.dto.co.RoleFindAllCo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 查询角色指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
public class RoleFindAllCmd {

  @Valid
  private RoleFindAllCo roleFindAllCo;
  @Min(value = 0, message = "{page.no.validation.min.size}")
  private int pageNo = 0;
  @Min(value = 1, message = "{page.size.validation.min.size}")
  private int pageSize = 10;
}
