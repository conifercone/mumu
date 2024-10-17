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
package baby.mumu.log.client.dto;

import baby.mumu.log.client.dto.co.OperationLogFindAllQueryCo;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 操作日志查询所有指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
public class OperationLogFindAllCmd {

  private OperationLogFindAllQueryCo operationLogFindAllQueryCo;

  /**
   * 当前页码
   */
  @Min(value = 0, message = "{page.no.validation.min.size}")
  private int current = 0;
  /**
   * 每页数量
   */
  @Min(value = 1, message = "{page.size.validation.min.size}")
  private int pageSize = 10;
}
