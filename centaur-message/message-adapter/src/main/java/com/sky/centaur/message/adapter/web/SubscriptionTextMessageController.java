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
import com.sky.centaur.message.client.dto.SubscriptionTextMessageForwardCmd;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订阅文本消息管理
 *
 * @author kaiyu.shan
 * @since 1.0.2
 */
@RestController
@RequestMapping("/subscriptionTextMsg")
@Tag(name = "订阅文本消息管理")
public class SubscriptionTextMessageController {

  private final SubscriptionTextMessageService subscriptionTextMessageService;

  @Autowired
  public SubscriptionTextMessageController(
      SubscriptionTextMessageService subscriptionTextMessageService) {
    this.subscriptionTextMessageService = subscriptionTextMessageService;
  }

  @Operation(summary = "转发订阅文本消息")
  @PostMapping("/forward")
  @ResponseBody
  @API(status = Status.STABLE, since = "1.0.2")
  public void forward(
      @RequestBody SubscriptionTextMessageForwardCmd subscriptionTextMessageForwardCmd) {
    subscriptionTextMessageService.forwardMsg(subscriptionTextMessageForwardCmd);
  }
}
