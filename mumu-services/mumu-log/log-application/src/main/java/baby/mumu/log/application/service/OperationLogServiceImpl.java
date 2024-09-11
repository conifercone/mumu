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
package baby.mumu.log.application.service;

import baby.mumu.log.application.operation.executor.OperationLogFindAllCmdExe;
import baby.mumu.log.application.operation.executor.OperationLogQryCmdExe;
import baby.mumu.log.application.operation.executor.OperationLogSaveCmdExe;
import baby.mumu.log.application.operation.executor.OperationLogSubmitCmdExe;
import baby.mumu.log.client.api.OperationLogService;
import baby.mumu.log.client.api.grpc.OperationLogServiceEmptyResult;
import baby.mumu.log.client.api.grpc.OperationLogServiceGrpc.OperationLogServiceImplBase;
import baby.mumu.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import baby.mumu.log.client.api.grpc.OperationLogSubmitGrpcCo;
import baby.mumu.log.client.dto.OperationLogFindAllCmd;
import baby.mumu.log.client.dto.OperationLogQryCmd;
import baby.mumu.log.client.dto.OperationLogSaveCmd;
import baby.mumu.log.client.dto.OperationLogSubmitCmd;
import baby.mumu.log.client.dto.co.OperationLogFindAllCo;
import baby.mumu.log.client.dto.co.OperationLogQryCo;
import baby.mumu.log.client.dto.co.OperationLogSubmitCo;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.GRpcService;
import org.lognet.springboot.grpc.recovery.GRpcRuntimeExceptionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * 操作日志
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class})
@Observed(name = "OperationLogServiceImpl")
public class OperationLogServiceImpl extends OperationLogServiceImplBase implements
    OperationLogService {

  private final OperationLogSubmitCmdExe operationLogSubmitCmdExe;

  private final OperationLogSaveCmdExe operationLogSaveCmdExe;

  private final OperationLogQryCmdExe operationLogQryCmdExe;

  private final OperationLogFindAllCmdExe operationLogFindAllCmdExe;

  @Autowired
  public OperationLogServiceImpl(OperationLogSubmitCmdExe operationLogSubmitCmdExe,
      OperationLogSaveCmdExe operationLogSaveCmdExe, OperationLogQryCmdExe operationLogQryCmdExe,
      OperationLogFindAllCmdExe operationLogFindAllCmdExe) {
    this.operationLogSubmitCmdExe = operationLogSubmitCmdExe;
    this.operationLogSaveCmdExe = operationLogSaveCmdExe;
    this.operationLogQryCmdExe = operationLogQryCmdExe;
    this.operationLogFindAllCmdExe = operationLogFindAllCmdExe;
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
  public void submit(@NotNull OperationLogSubmitGrpcCmd request,
      @NotNull StreamObserver<OperationLogServiceEmptyResult> responseObserver) {
    OperationLogSubmitCmd operationLogSubmitCmd = new OperationLogSubmitCmd();
    OperationLogSubmitCo operationLogSubmitCo = getOperationLogSubmitCo(
        request);
    operationLogSubmitCmd.setOperationLogSubmitCo(operationLogSubmitCo);
    try {
      operationLogSubmitCmdExe.execute(operationLogSubmitCmd);
    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    OperationLogServiceEmptyResult build = OperationLogServiceEmptyResult.newBuilder().build();
    responseObserver.onNext(build);
    responseObserver.onCompleted();
  }

  @Override
  public OperationLogQryCo findOperationLogById(String id) {
    OperationLogQryCmd operationLogQryCmd = new OperationLogQryCmd();
    operationLogQryCmd.setId(id);
    return operationLogQryCmdExe.execute(operationLogQryCmd);
  }

  @Override
  public Page<OperationLogFindAllCo> findAll(OperationLogFindAllCmd operationLogFindAllCmd) {
    return operationLogFindAllCmdExe.execute(operationLogFindAllCmd);
  }

  @NotNull
  private static OperationLogSubmitCo getOperationLogSubmitCo(
      @NotNull OperationLogSubmitGrpcCmd request) {
    OperationLogSubmitCo operationLogSubmitCo = new OperationLogSubmitCo();
    OperationLogSubmitGrpcCo operationLogSubmitGrpcCo = request.getOperationLogSubmitCo();
    operationLogSubmitCo.setContent(operationLogSubmitGrpcCo.getContent());
    operationLogSubmitCo.setOperator(operationLogSubmitGrpcCo.getOperator());
    operationLogSubmitCo.setBizNo(operationLogSubmitGrpcCo.getBizNo());
    operationLogSubmitCo.setCategory(operationLogSubmitGrpcCo.getCategory());
    operationLogSubmitCo.setDetail(operationLogSubmitGrpcCo.getDetail());
    operationLogSubmitCo.setSuccess(operationLogSubmitGrpcCo.getSuccess());
    operationLogSubmitCo.setFail(operationLogSubmitGrpcCo.getFail());
    return operationLogSubmitCo;
  }
}
