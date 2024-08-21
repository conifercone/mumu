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
package com.sky.centaur.message.adapter.web;

import com.sky.centaur.message.client.api.BroadcastTextMessageService;
import com.sky.centaur.message.client.dto.BroadcastTextMessageArchiveByIdCmd;
import com.sky.centaur.message.client.dto.BroadcastTextMessageDeleteByIdCmd;
import com.sky.centaur.message.client.dto.BroadcastTextMessageFindAllYouSendCmd;
import com.sky.centaur.message.client.dto.BroadcastTextMessageForwardCmd;
import com.sky.centaur.message.client.dto.BroadcastTextMessageReadByIdCmd;
import com.sky.centaur.message.client.dto.BroadcastTextMessageRecoverMsgFromArchiveByIdCmd;
import com.sky.centaur.message.client.dto.co.BroadcastTextMessageFindAllYouSendCo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文本广播消息管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.2
 */
@RestController
@RequestMapping("/broadcastTextMsg")
@Tag(name = "文本广播消息管理")
public class BroadcastTextMessageController {

  private final BroadcastTextMessageService broadcastTextMessageService;

  @Autowired
  public BroadcastTextMessageController(
      BroadcastTextMessageService broadcastTextMessageService) {
    this.broadcastTextMessageService = broadcastTextMessageService;
  }

  @Operation(summary = "转发文本广播消息")
  @PostMapping("/forward")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.2")
  public void forward(
      @RequestBody @Valid BroadcastTextMessageForwardCmd broadcastTextMessageForwardCmd) {
    broadcastTextMessageService.forwardMsg(broadcastTextMessageForwardCmd);
  }

  @Operation(summary = "根据ID已读文本广播消息")
  @PutMapping("/readMsgById")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.3")
  public void readMsgById(
      @RequestBody BroadcastTextMessageReadByIdCmd broadcastTextMessageReadByIdCmd) {
    broadcastTextMessageService.readMsgById(broadcastTextMessageReadByIdCmd);
  }

  @Operation(summary = "根据ID删除文本广播消息")
  @DeleteMapping("/deleteMsgById")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.3")
  public void deleteMsgById(
      @RequestBody BroadcastTextMessageDeleteByIdCmd broadcastTextMessageDeleteByIdCmd) {
    broadcastTextMessageService.deleteMsgById(broadcastTextMessageDeleteByIdCmd);
  }

  @Operation(summary = "查询所有当前用户发送消息")
  @GetMapping("/findAllYouSend")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.3")
  public Page<BroadcastTextMessageFindAllYouSendCo> findAllYouSend(
      @RequestBody @Valid BroadcastTextMessageFindAllYouSendCmd broadcastTextMessageFindAllYouSendCmd) {
    return broadcastTextMessageService.findAllYouSend(broadcastTextMessageFindAllYouSendCmd);
  }

  @Operation(summary = "根据ID归档文本广播消息")
  @PutMapping("/archiveMsgById")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.3")
  public void archiveMsgById(
      @RequestBody BroadcastTextMessageArchiveByIdCmd broadcastTextMessageArchiveByIdCmd) {
    broadcastTextMessageService.archiveMsgById(broadcastTextMessageArchiveByIdCmd);
  }

  @Operation(summary = "根据ID从存档中恢复消息指令")
  @PutMapping("/recoverMsgFromArchiveById")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.4")
  public void recoverMsgFromArchiveById(
      @RequestBody BroadcastTextMessageRecoverMsgFromArchiveByIdCmd broadcastTextMessageRecoverMsgFromArchiveByIdCmd) {
    broadcastTextMessageService.recoverMsgFromArchiveById(
        broadcastTextMessageRecoverMsgFromArchiveByIdCmd);
  }
}
