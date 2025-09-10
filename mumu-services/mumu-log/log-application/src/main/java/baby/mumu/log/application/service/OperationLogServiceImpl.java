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
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import baby.mumu.log.application.operation.executor.OperationLogFindAllCmdExe;
import baby.mumu.log.application.operation.executor.OperationLogQryCmdExe;
import baby.mumu.log.application.operation.executor.OperationLogSaveCmdExe;
import baby.mumu.log.application.operation.executor.OperationLogSubmitCmdExe;
import baby.mumu.log.client.api.OperationLogService;
import baby.mumu.log.client.api.grpc.OperationLogServiceGrpc.OperationLogServiceImplBase;
import baby.mumu.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import baby.mumu.log.client.cmds.OperationLogFindAllCmd;
import baby.mumu.log.client.cmds.OperationLogQryCmd;
import baby.mumu.log.client.cmds.OperationLogSaveCmd;
import baby.mumu.log.client.cmds.OperationLogSubmitCmd;
import baby.mumu.log.client.dto.OperationLogFindAllDTO;
import baby.mumu.log.client.dto.OperationLogQryDTO;
import baby.mumu.log.infra.operation.convertor.OperationLogConvertor;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

/**
 * 操作日志
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Service
@GrpcService(interceptors = {ObservationGrpcServerInterceptor.class, ClientIpInterceptor.class})
@Observed(name = "OperationLogServiceImpl")
public class OperationLogServiceImpl extends OperationLogServiceImplBase implements
  OperationLogService {

  private final OperationLogSubmitCmdExe operationLogSubmitCmdExe;

  private final OperationLogSaveCmdExe operationLogSaveCmdExe;

  private final OperationLogQryCmdExe operationLogQryCmdExe;

  private final OperationLogFindAllCmdExe operationLogFindAllCmdExe;
  private final OperationLogConvertor operationLogConvertor;

  @Autowired
  public OperationLogServiceImpl(OperationLogSubmitCmdExe operationLogSubmitCmdExe,
    OperationLogSaveCmdExe operationLogSaveCmdExe, OperationLogQryCmdExe operationLogQryCmdExe,
    OperationLogFindAllCmdExe operationLogFindAllCmdExe,
    OperationLogConvertor operationLogConvertor) {
    this.operationLogSubmitCmdExe = operationLogSubmitCmdExe;
    this.operationLogSaveCmdExe = operationLogSaveCmdExe;
    this.operationLogQryCmdExe = operationLogQryCmdExe;
    this.operationLogFindAllCmdExe = operationLogFindAllCmdExe;
    this.operationLogConvertor = operationLogConvertor;
  }

  @Override
  public void submit(OperationLogSubmitCmd operationLogSubmitCmd) {
    operationLogSubmitCmdExe.execute(operationLogSubmitCmd);
  }

  @Override
  public void save(OperationLogSaveCmd operationLogSaveCmd) {
    operationLogSaveCmdExe.execute(operationLogSaveCmd);
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void submit(@NonNull OperationLogSubmitGrpcCmd request,
    @NonNull StreamObserver<Empty> responseObserver) {
    operationLogConvertor.toOperationLogSubmitCmd(request)
      .ifPresentOrElse((operationLogSubmitCmd) -> {
        operationLogSubmitCmdExe.execute(operationLogSubmitCmd);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
      }, () -> {
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
      });
  }

  @Override
  public OperationLogQryDTO findOperationLogById(String id) {
    OperationLogQryCmd operationLogQryCmd = new OperationLogQryCmd();
    operationLogQryCmd.setId(id);
    return operationLogQryCmdExe.execute(operationLogQryCmd);
  }

  @Override
  public Page<OperationLogFindAllDTO> findAll(OperationLogFindAllCmd operationLogFindAllCmd) {
    return operationLogFindAllCmdExe.execute(operationLogFindAllCmd);
  }
}
