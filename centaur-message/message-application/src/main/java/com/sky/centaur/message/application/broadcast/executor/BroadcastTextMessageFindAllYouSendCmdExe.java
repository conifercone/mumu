/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
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
package com.sky.centaur.message.application.broadcast.executor;

import com.sky.centaur.message.client.dto.BroadcastTextMessageFindAllYouSendCmd;
import com.sky.centaur.message.client.dto.co.BroadcastTextMessageFindAllYouSendCo;
import com.sky.centaur.message.domain.broadcast.BroadcastTextMessage;
import com.sky.centaur.message.domain.broadcast.gateway.BroadcastTextMessageGateway;
import com.sky.centaur.message.infrastructure.broadcast.convertor.BroadcastTextMessageConvertor;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 文本广播消息查询所有当前用户发送消息指令执行器
 *
 * @author kaiyu.shan
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
        broadcastTextMessageConvertor.toEntity(
                broadcastTextMessageFindAllYouSendCmd.getBroadcastTextMessageFindAllYouSendCo())
            .orElse(null),
        broadcastTextMessageFindAllYouSendCmd.getPageNo(),
        broadcastTextMessageFindAllYouSendCmd.getPageSize());
    List<BroadcastTextMessageFindAllYouSendCo> broadcastTextMessageFindAllYouSendCos = allYouSend.getContent()
        .stream()
        .map(broadcastTextMessage -> broadcastTextMessageConvertor.toFindAllYouSendCo(
                broadcastTextMessage)
            .orElse(null))
        .filter(
            Objects::nonNull).toList();
    return new PageImpl<>(broadcastTextMessageFindAllYouSendCos, allYouSend.getPageable(),
        allYouSend.getTotalElements());
  }
}
