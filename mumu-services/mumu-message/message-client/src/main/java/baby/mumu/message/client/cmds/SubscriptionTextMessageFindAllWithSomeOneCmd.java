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
package baby.mumu.message.client.cmds;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 文本订阅消息查询所有和某人的消息记录指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.3
 */
@Data
public class SubscriptionTextMessageFindAllWithSomeOneCmd {

  @NotNull(message = "{receiver.id.validation.not.null}")
  private Long receiverId;
  @Min(value = 1, message = "{current.validation.min.size}")
  private Integer current = 1;
  @Min(value = 1, message = "{page.size.validation.min.size}")
  private Integer pageSize = 10;
}
