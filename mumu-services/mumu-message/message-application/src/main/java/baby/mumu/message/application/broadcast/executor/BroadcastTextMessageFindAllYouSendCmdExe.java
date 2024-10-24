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
package baby.mumu.message.application.broadcast.executor;

import baby.mumu.message.client.dto.BroadcastTextMessageFindAllYouSendCmd;
import baby.mumu.message.client.dto.co.BroadcastTextMessageFindAllYouSendCo;
import baby.mumu.message.domain.broadcast.BroadcastTextMessage;
import baby.mumu.message.domain.broadcast.gateway.BroadcastTextMessageGateway;
import baby.mumu.message.infrastructure.broadcast.convertor.BroadcastTextMessageConvertor;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 文本广播消息查询所有当前用户发送消息指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.3
 */
@Component
public class BroadcastTextMessageFindAllYouSendCmdExe {

  private final BroadcastTextMessageGateway broadcastTextMessageGateway;
  private final BroadcastTextMessageConvertor broadcastTextMessageConvertor;

  @Autowired
  public BroadcastTextMessageFindAllYouSendCmdExe(
      BroadcastTextMessageGateway broadcastTextMessageGateway,
      BroadcastTextMessageConvertor broadcastTextMessageConvertor) {
    this.broadcastTextMessageGateway = broadcastTextMessageGateway;
    this.broadcastTextMessageConvertor = broadcastTextMessageConvertor;
  }

  public Page<BroadcastTextMessageFindAllYouSendCo> execute(
      @NotNull BroadcastTextMessageFindAllYouSendCmd broadcastTextMessageFindAllYouSendCmd) {
    Assert.notNull(broadcastTextMessageFindAllYouSendCmd,
        "BroadcastTextMessageFindAllYouSendCmd cannot null");
    Page<BroadcastTextMessage> allYouSend = broadcastTextMessageGateway.findAllYouSend(
        broadcastTextMessageConvertor.toEntity(broadcastTextMessageFindAllYouSendCmd)
            .orElse(null),
        broadcastTextMessageFindAllYouSendCmd.getCurrent(),
        broadcastTextMessageFindAllYouSendCmd.getPageSize());
    List<BroadcastTextMessageFindAllYouSendCo> broadcastTextMessageFindAllYouSendCos = allYouSend.getContent()
        .stream()
        .map(broadcastTextMessageConvertor::toFindAllYouSendCo)
        .filter(Optional::isPresent).map(Optional::get).toList();
    return new PageImpl<>(broadcastTextMessageFindAllYouSendCos, allYouSend.getPageable(),
        allYouSend.getTotalElements());
  }
}
