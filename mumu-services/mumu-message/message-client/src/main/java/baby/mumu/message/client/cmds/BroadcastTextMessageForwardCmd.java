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
package baby.mumu.message.client.cmds;

import baby.mumu.basis.enums.MessageStatusEnum;
import jakarta.validation.constraints.NotBlank;
import java.util.Collection;
import lombok.Data;

/**
 * 文本广播消息转发指令
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.2
 */
@Data
public class BroadcastTextMessageForwardCmd {

  private Collection<Long> receiverIds;

  @NotBlank(message = "{text.message.validation.not.blank}")
  private String message;

  private MessageStatusEnum messageStatus;
}
