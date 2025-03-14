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
package baby.mumu.message.adapter.web;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.message.client.api.SubscriptionTextMessageService;
import baby.mumu.message.client.cmds.SubscriptionTextMessageFindAllWithSomeOneCmd;
import baby.mumu.message.client.cmds.SubscriptionTextMessageFindAllYouSendCmd;
import baby.mumu.message.client.cmds.SubscriptionTextMessageForwardCmd;
import baby.mumu.message.client.dto.SubscriptionTextMessageFindAllWithSomeOneDTO;
import baby.mumu.message.client.dto.SubscriptionTextMessageFindAllYouSendDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文本订阅消息管理
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
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
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.2")
  public void forward(
    @RequestBody @Valid SubscriptionTextMessageForwardCmd subscriptionTextMessageForwardCmd) {
    subscriptionTextMessageService.forwardMsg(subscriptionTextMessageForwardCmd);
  }

  @Operation(summary = "根据ID已读文本订阅消息")
  @PutMapping("/readMsgById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.3")
  public void readMsgById(@PathVariable(value = "id") Long id) {
    subscriptionTextMessageService.readMsgById(id);
  }

  @Operation(summary = "根据ID未读文本订阅消息")
  @PutMapping("/unreadMsgById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.3")
  public void unreadMsgById(@PathVariable(value = "id") Long id) {
    subscriptionTextMessageService.unreadMsgById(id);
  }

  @Operation(summary = "根据ID删除文本订阅消息")
  @DeleteMapping("/deleteMsgById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.3")
  public void deleteMsgById(@PathVariable(value = "id") Long id) {
    subscriptionTextMessageService.deleteMsgById(id);
  }

  @Operation(summary = "查询所有当前用户发送消息")
  @GetMapping("/findAllYouSend")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.3")
  public Page<SubscriptionTextMessageFindAllYouSendDTO> findAllYouSend(
    @ModelAttribute @Valid SubscriptionTextMessageFindAllYouSendCmd subscriptionTextMessageFindAllYouSendCmd) {
    return subscriptionTextMessageService.findAllYouSend(subscriptionTextMessageFindAllYouSendCmd);
  }

  @Operation(summary = "根据ID归档文本订阅消息")
  @PutMapping("/archiveMsgById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.3")
  public void archiveMsgById(@PathVariable(value = "id") Long id) {
    subscriptionTextMessageService.archiveMsgById(id);
  }

  @Operation(summary = "根据ID从存档中恢复消息指令")
  @PutMapping("/recoverMsgFromArchiveById/{id}")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.4")
  public void recoverMsgFromArchiveById(@PathVariable(value = "id") Long id) {
    subscriptionTextMessageService.recoverMsgFromArchiveById(
      id);
  }

  @Operation(summary = "查询所有和某人的消息记录")
  @GetMapping("/findAllWithSomeOne")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.3")
  public Page<SubscriptionTextMessageFindAllWithSomeOneDTO> findAllWithSomeOne(
    @ModelAttribute @Valid SubscriptionTextMessageFindAllWithSomeOneCmd subscriptionTextMessageFindAllWithSomeOneCmd) {
    return subscriptionTextMessageService.findAllMessageRecordWithSomeone(
      subscriptionTextMessageFindAllWithSomeOneCmd);
  }
}
