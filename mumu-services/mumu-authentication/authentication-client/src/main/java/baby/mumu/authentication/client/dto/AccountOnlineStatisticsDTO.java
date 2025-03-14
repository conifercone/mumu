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
package baby.mumu.authentication.client.dto;

import baby.mumu.basis.dto.BaseDataTransferObject;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 账户在线统计数据传输对象
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountOnlineStatisticsDTO extends BaseDataTransferObject {

  @Serial
  private static final long serialVersionUID = 4656166892082871011L;

  private Long onlineCapacity;
}
