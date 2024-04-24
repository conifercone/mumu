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
package com.sky.centaur.log.application.service;

import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.log.application.operation.executor.OperationLogQryCmdExe;
import com.sky.centaur.log.application.operation.executor.OperationLogSaveCmdExe;
import com.sky.centaur.log.application.operation.executor.OperationLogSubmitCmdExe;
import com.sky.centaur.log.client.api.OperationLogService;
import com.sky.centaur.log.client.api.grpc.OperationLogServiceEmptyResult;
import com.sky.centaur.log.client.api.grpc.OperationLogServiceGrpc.OperationLogServiceImplBase;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCo;
import com.sky.centaur.log.client.dto.OperationLogQryCmd;
import com.sky.centaur.log.client.dto.OperationLogSaveCmd;
import com.sky.centaur.log.client.dto.OperationLogSubmitCmd;
import com.sky.centaur.log.client.dto.co.OperationLogQryCo;
import com.sky.centaur.log.client.dto.co.OperationLogSubmitCo;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.GRpcService;
import org.lognet.springboot.grpc.recovery.GRpcRuntimeExceptionWrapper;
import org.springframework.stereotype.Service;

/**
 * 操作日志
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class})
@Observed(name = "OperationLogServiceImpl")
public class OperationLogServiceImpl extends OperationLogServiceImplBase implements
    OperationLogService {

  @Resource
  private OperationLogSubmitCmdExe operationLogSubmitCmdExe;

  @Resource
  private OperationLogSaveCmdExe operationLogSaveCmdExe;

  @Resource
  private OperationLogQryCmdExe operationLogQryCmdExe;

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
    } catch (CentaurException e) {
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
