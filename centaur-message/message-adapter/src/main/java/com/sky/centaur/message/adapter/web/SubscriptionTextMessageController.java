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

import com.sky.centaur.message.client.api.SubscriptionTextMessageService;
import com.sky.centaur.message.client.dto.SubscriptionTextMessageDeleteByIdCmd;
import com.sky.centaur.message.client.dto.SubscriptionTextMessageFindAllYouSendCmd;
import com.sky.centaur.message.client.dto.SubscriptionTextMessageForwardCmd;
import com.sky.centaur.message.client.dto.SubscriptionTextMessageReadByIdCmd;
import com.sky.centaur.message.client.dto.SubscriptionTextMessageUnreadByIdCmd;
import com.sky.centaur.message.client.dto.co.SubscriptionTextMessageFindAllYouSendCo;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文本订阅消息管理
 *
 * @author kaiyu.shan
 * @since 1.0.2
 */
@RestController
@RequestMapping("/subscriptionTextMsg")
@Tag(name = "文本订阅消息管理")
public class SubscriptionTextMessageController {

  private final SubscriptionTextMessageService subscriptionTextMessageService;

  @Autowired
  public SubscriptionTextMessageController(
      SubscriptionTextMessageService subscriptionTextMessageService) {
    this.subscriptionTextMessageService = subscriptionTextMessageService;
  }

  @Operation(summary = "转发文本订阅消息")
  @PostMapping("/forward")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.2")
  public void forward(
      @RequestBody @Valid SubscriptionTextMessageForwardCmd subscriptionTextMessageForwardCmd) {
    subscriptionTextMessageService.forwardMsg(subscriptionTextMessageForwardCmd);
  }

  @Operation(summary = "根据ID已读文本订阅消息")
  @PostMapping("/readMsgById")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.3")
  public void readMsgById(
      @RequestBody SubscriptionTextMessageReadByIdCmd subscriptionTextMessageReadByIdCmd) {
    subscriptionTextMessageService.readMsgById(subscriptionTextMessageReadByIdCmd);
  }

  @Operation(summary = "根据ID未读文本订阅消息")
  @PostMapping("/unreadMsgById")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.3")
  public void unreadMsgById(
      @RequestBody SubscriptionTextMessageUnreadByIdCmd subscriptionTextMessageUnreadByIdCmd) {
    subscriptionTextMessageService.unreadMsgById(subscriptionTextMessageUnreadByIdCmd);
  }

  @Operation(summary = "根据ID删除文本订阅消息")
  @DeleteMapping("/deleteMsgById")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.3")
  public void deleteMsgById(
      @RequestBody SubscriptionTextMessageDeleteByIdCmd subscriptionTextMessageDeleteByIdCmd) {
    subscriptionTextMessageService.deleteMsgById(subscriptionTextMessageDeleteByIdCmd);
  }

  @Operation(summary = "查询所有当前用户发送消息")
  @GetMapping("/findAllYouSend")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.3")
  public Page<SubscriptionTextMessageFindAllYouSendCo> findAllYouSend(
      @RequestBody @Valid SubscriptionTextMessageFindAllYouSendCmd subscriptionTextMessageFindAllYouSendCmd) {
    return subscriptionTextMessageService.findAllYouSend(subscriptionTextMessageFindAllYouSendCmd);
  }
}
