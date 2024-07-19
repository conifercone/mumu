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
package com.sky.centaur.message.application.service;

import com.sky.centaur.message.application.broadcast.executor.BroadcastTextMessageForwardCmdExe;
import com.sky.centaur.message.client.api.BroadcastTextMessageService;
import com.sky.centaur.message.client.dto.BroadcastTextMessageForwardCmd;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文本广播消息service实现类
 *
 * @author kaiyu.shan
 * @since 1.0.2
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class})
@Observed(name = "BroadcastTextMessageServiceImpl")
public class BroadcastTextMessageServiceImpl implements BroadcastTextMessageService {

  private final BroadcastTextMessageForwardCmdExe broadcastTextMessageForwardCmdExe;

  @Autowired
  public BroadcastTextMessageServiceImpl(
      BroadcastTextMessageForwardCmdExe broadcastTextMessageForwardCmdExe) {
    this.broadcastTextMessageForwardCmdExe = broadcastTextMessageForwardCmdExe;
  }

  @Override
  @Transactional
  public void forwardMsg(BroadcastTextMessageForwardCmd broadcastTextMessageForwardCmd) {
    broadcastTextMessageForwardCmdExe.execute(broadcastTextMessageForwardCmd);
  }
}
