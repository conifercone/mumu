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
package baby.mumu.message.application.broadcast.executor;

import baby.mumu.message.client.cmds.BroadcastTextMessageForwardCmd;
import baby.mumu.message.domain.broadcast.gateway.BroadcastTextMessageGateway;
import baby.mumu.message.infrastructure.broadcast.convertor.BroadcastTextMessageConvertor;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 文本广播消息转发指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 1.0.2
 */
@Component
public class BroadcastTextMessageForwardCmdExe {

  private final BroadcastTextMessageGateway broadcastTextMessageGateway;
  private final BroadcastTextMessageConvertor broadcastTextMessageConvertor;

  @Autowired
  public BroadcastTextMessageForwardCmdExe(
    BroadcastTextMessageGateway broadcastTextMessageGateway,
    BroadcastTextMessageConvertor broadcastTextMessageConvertor) {
    this.broadcastTextMessageGateway = broadcastTextMessageGateway;
    this.broadcastTextMessageConvertor = broadcastTextMessageConvertor;
  }

  public void execute(BroadcastTextMessageForwardCmd broadcastTextMessageForwardCmd) {
    Optional.ofNullable(broadcastTextMessageForwardCmd)
      .flatMap(broadcastTextMessageConvertor::toEntity)
      .ifPresent(broadcastTextMessageGateway::forwardMsg);
  }
}
