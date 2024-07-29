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

import com.sky.centaur.message.application.broadcast.executor.BroadcastTextMessageArchiveByIdCmdExe;
import com.sky.centaur.message.application.broadcast.executor.BroadcastTextMessageDeleteByIdCmdExe;
import com.sky.centaur.message.application.broadcast.executor.BroadcastTextMessageFindAllYouSendCmdExe;
import com.sky.centaur.message.application.broadcast.executor.BroadcastTextMessageForwardCmdExe;
import com.sky.centaur.message.application.broadcast.executor.BroadcastTextMessageReadByIdCmdExe;
import com.sky.centaur.message.client.api.BroadcastTextMessageService;
import com.sky.centaur.message.client.dto.BroadcastTextMessageArchiveByIdCmd;
import com.sky.centaur.message.client.dto.BroadcastTextMessageDeleteByIdCmd;
import com.sky.centaur.message.client.dto.BroadcastTextMessageFindAllYouSendCmd;
import com.sky.centaur.message.client.dto.BroadcastTextMessageForwardCmd;
import com.sky.centaur.message.client.dto.BroadcastTextMessageReadByIdCmd;
import com.sky.centaur.message.client.dto.co.BroadcastTextMessageFindAllYouSendCo;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
  private final BroadcastTextMessageReadByIdCmdExe broadcastTextMessageReadByIdCmdExe;
  private final BroadcastTextMessageDeleteByIdCmdExe broadcastTextMessageDeleteByIdCmdExe;
  private final BroadcastTextMessageFindAllYouSendCmdExe broadcastTextMessageFindAllYouSendCmdExe;
  private final BroadcastTextMessageArchiveByIdCmdExe broadcastTextMessageArchiveByIdCmdExe;

  @Autowired
  public BroadcastTextMessageServiceImpl(
      BroadcastTextMessageForwardCmdExe broadcastTextMessageForwardCmdExe,
      BroadcastTextMessageReadByIdCmdExe broadcastTextMessageReadByIdCmdExe,
      BroadcastTextMessageDeleteByIdCmdExe broadcastTextMessageDeleteByIdCmdExe,
      BroadcastTextMessageFindAllYouSendCmdExe broadcastTextMessageFindAllYouSendCmdExe,
      BroadcastTextMessageArchiveByIdCmdExe broadcastTextMessageArchiveByIdCmdExe) {
    this.broadcastTextMessageForwardCmdExe = broadcastTextMessageForwardCmdExe;
    this.broadcastTextMessageReadByIdCmdExe = broadcastTextMessageReadByIdCmdExe;
    this.broadcastTextMessageDeleteByIdCmdExe = broadcastTextMessageDeleteByIdCmdExe;
    this.broadcastTextMessageFindAllYouSendCmdExe = broadcastTextMessageFindAllYouSendCmdExe;
    this.broadcastTextMessageArchiveByIdCmdExe = broadcastTextMessageArchiveByIdCmdExe;
  }

  @Override
  @Transactional
  public void forwardMsg(BroadcastTextMessageForwardCmd broadcastTextMessageForwardCmd) {
    broadcastTextMessageForwardCmdExe.execute(broadcastTextMessageForwardCmd);
  }

  @Override
  @Transactional
  public void readMsgById(BroadcastTextMessageReadByIdCmd broadcastTextMessageReadByIdCmd) {
    broadcastTextMessageReadByIdCmdExe.execute(broadcastTextMessageReadByIdCmd);
  }

  @Override
  @Transactional
  public void deleteMsgById(BroadcastTextMessageDeleteByIdCmd broadcastTextMessageDeleteByIdCmd) {
    broadcastTextMessageDeleteByIdCmdExe.execute(broadcastTextMessageDeleteByIdCmd);
  }

  @Override
  public Page<BroadcastTextMessageFindAllYouSendCo> findAllYouSend(
      BroadcastTextMessageFindAllYouSendCmd broadcastTextMessageFindAllYouSendCmd) {
    return broadcastTextMessageFindAllYouSendCmdExe.execute(broadcastTextMessageFindAllYouSendCmd);
  }

  @Override
  @Transactional
  public void archiveMsgById(
      BroadcastTextMessageArchiveByIdCmd broadcastTextMessageArchiveByIdCmd) {
    broadcastTextMessageArchiveByIdCmdExe.execute(broadcastTextMessageArchiveByIdCmd);
  }
}
