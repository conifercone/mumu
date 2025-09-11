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

package baby.mumu.log.application.service;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import baby.mumu.log.application.system.executor.SystemLogFindAllCmdExe;
import baby.mumu.log.application.system.executor.SystemLogSaveCmdExe;
import baby.mumu.log.application.system.executor.SystemLogSubmitCmdExe;
import baby.mumu.log.client.api.SystemLogService;
import baby.mumu.log.client.api.grpc.SystemLogServiceGrpc.SystemLogServiceImplBase;
import baby.mumu.log.client.api.grpc.SystemLogSubmitGrpcCmd;
import baby.mumu.log.client.cmds.SystemLogFindAllCmd;
import baby.mumu.log.client.cmds.SystemLogSaveCmd;
import baby.mumu.log.client.cmds.SystemLogSubmitCmd;
import baby.mumu.log.client.dto.SystemLogFindAllDTO;
import baby.mumu.log.infra.system.convertor.SystemLogConvertor;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

/**
 * 系统日志
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Service
@GrpcService
public class SystemLogServiceImpl extends SystemLogServiceImplBase implements SystemLogService {

  private final SystemLogSubmitCmdExe systemLogSubmitCmdExe;

  private final SystemLogSaveCmdExe systemLogSaveCmdExe;
  private final SystemLogFindAllCmdExe systemLogFindAllCmdExe;
  private final SystemLogConvertor systemLogConvertor;

  @Autowired
  public SystemLogServiceImpl(SystemLogSubmitCmdExe systemLogSubmitCmdExe,
    SystemLogSaveCmdExe systemLogSaveCmdExe, SystemLogFindAllCmdExe systemLogFindAllCmdExe,
    SystemLogConvertor systemLogConvertor) {
    this.systemLogSubmitCmdExe = systemLogSubmitCmdExe;
    this.systemLogSaveCmdExe = systemLogSaveCmdExe;
    this.systemLogFindAllCmdExe = systemLogFindAllCmdExe;
    this.systemLogConvertor = systemLogConvertor;
  }

  @Override
  public void submit(SystemLogSubmitCmd systemLogSubmitCmd) {
    systemLogSubmitCmdExe.execute(systemLogSubmitCmd);
  }

  @Override
  public void save(SystemLogSaveCmd systemLogSaveCmd) {
    systemLogSaveCmdExe.execute(systemLogSaveCmd);
  }

  @Override
  public Page<SystemLogFindAllDTO> findAll(SystemLogFindAllCmd systemLogFindAllCmd) {
    return systemLogFindAllCmdExe.execute(systemLogFindAllCmd);
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void submit(SystemLogSubmitGrpcCmd request,
    StreamObserver<Empty> responseObserver) {
    systemLogConvertor.toSystemLogSubmitCmd(request).ifPresentOrElse((systemLogSubmitCmd) -> {
      systemLogSubmitCmdExe.execute(systemLogSubmitCmd);
      responseObserver.onNext(Empty.getDefaultInstance());
      responseObserver.onCompleted();
    }, () -> {
      responseObserver.onNext(Empty.getDefaultInstance());
      responseObserver.onCompleted();
    });
  }

}
