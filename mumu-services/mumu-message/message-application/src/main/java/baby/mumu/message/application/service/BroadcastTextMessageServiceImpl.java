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
package baby.mumu.message.application.service;

import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.message.application.broadcast.executor.BroadcastTextMessageArchiveByIdCmdExe;
import baby.mumu.message.application.broadcast.executor.BroadcastTextMessageDeleteByIdCmdExe;
import baby.mumu.message.application.broadcast.executor.BroadcastTextMessageFindAllYouSendCmdExe;
import baby.mumu.message.application.broadcast.executor.BroadcastTextMessageForwardCmdExe;
import baby.mumu.message.application.broadcast.executor.BroadcastTextMessageReadByIdCmdExe;
import baby.mumu.message.application.broadcast.executor.BroadcastTextMessageRecoverMsgFromArchiveByIdCmdExe;
import baby.mumu.message.client.api.BroadcastTextMessageService;
import baby.mumu.message.client.dto.BroadcastTextMessageFindAllYouSendCmd;
import baby.mumu.message.client.dto.BroadcastTextMessageForwardCmd;
import baby.mumu.message.client.dto.co.BroadcastTextMessageFindAllYouSendCo;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.2
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class, ClientIpInterceptor.class})
@Observed(name = "BroadcastTextMessageServiceImpl")
public class BroadcastTextMessageServiceImpl implements BroadcastTextMessageService {

  private final BroadcastTextMessageForwardCmdExe broadcastTextMessageForwardCmdExe;
  private final BroadcastTextMessageReadByIdCmdExe broadcastTextMessageReadByIdCmdExe;
  private final BroadcastTextMessageDeleteByIdCmdExe broadcastTextMessageDeleteByIdCmdExe;
  private final BroadcastTextMessageFindAllYouSendCmdExe broadcastTextMessageFindAllYouSendCmdExe;
  private final BroadcastTextMessageArchiveByIdCmdExe broadcastTextMessageArchiveByIdCmdExe;
  private final BroadcastTextMessageRecoverMsgFromArchiveByIdCmdExe broadcastTextMessageRecoverMsgFromArchiveByIdCmdExe;

  @Autowired
  public BroadcastTextMessageServiceImpl(
    BroadcastTextMessageForwardCmdExe broadcastTextMessageForwardCmdExe,
    BroadcastTextMessageReadByIdCmdExe broadcastTextMessageReadByIdCmdExe,
    BroadcastTextMessageDeleteByIdCmdExe broadcastTextMessageDeleteByIdCmdExe,
    BroadcastTextMessageFindAllYouSendCmdExe broadcastTextMessageFindAllYouSendCmdExe,
    BroadcastTextMessageArchiveByIdCmdExe broadcastTextMessageArchiveByIdCmdExe,
    BroadcastTextMessageRecoverMsgFromArchiveByIdCmdExe broadcastTextMessageRecoverMsgFromArchiveByIdCmdExe) {
    this.broadcastTextMessageForwardCmdExe = broadcastTextMessageForwardCmdExe;
    this.broadcastTextMessageReadByIdCmdExe = broadcastTextMessageReadByIdCmdExe;
    this.broadcastTextMessageDeleteByIdCmdExe = broadcastTextMessageDeleteByIdCmdExe;
    this.broadcastTextMessageFindAllYouSendCmdExe = broadcastTextMessageFindAllYouSendCmdExe;
    this.broadcastTextMessageArchiveByIdCmdExe = broadcastTextMessageArchiveByIdCmdExe;
    this.broadcastTextMessageRecoverMsgFromArchiveByIdCmdExe = broadcastTextMessageRecoverMsgFromArchiveByIdCmdExe;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void forwardMsg(BroadcastTextMessageForwardCmd broadcastTextMessageForwardCmd) {
    broadcastTextMessageForwardCmdExe.execute(broadcastTextMessageForwardCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void readMsgById(Long id) {
    broadcastTextMessageReadByIdCmdExe.execute(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteMsgById(Long id) {
    broadcastTextMessageDeleteByIdCmdExe.execute(id);
  }

  @Override
  public Page<BroadcastTextMessageFindAllYouSendCo> findAllYouSend(
    BroadcastTextMessageFindAllYouSendCmd broadcastTextMessageFindAllYouSendCmd) {
    return broadcastTextMessageFindAllYouSendCmdExe.execute(broadcastTextMessageFindAllYouSendCmd);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void archiveMsgById(
    Long id) {
    broadcastTextMessageArchiveByIdCmdExe.execute(id);
  }

  @Override
  public void recoverMsgFromArchiveById(
    Long id) {
    broadcastTextMessageRecoverMsgFromArchiveByIdCmdExe.execute(id);
  }
}
