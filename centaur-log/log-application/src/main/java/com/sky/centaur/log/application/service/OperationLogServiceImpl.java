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

import com.sky.centaur.log.application.operation.executor.OperationLogSaveCmdExe;
import com.sky.centaur.log.application.operation.executor.OperationLogSubmitCmdExe;
import com.sky.centaur.log.client.api.OperationLogService;
import com.sky.centaur.log.client.api.grpc.Empty;
import com.sky.centaur.log.client.api.grpc.OperationLogServiceGrpc.OperationLogServiceImplBase;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCmd;
import com.sky.centaur.log.client.api.grpc.OperationLogSubmitGrpcCo;
import com.sky.centaur.log.client.dto.OperationLogSaveCmd;
import com.sky.centaur.log.client.dto.OperationLogSubmitCmd;
import com.sky.centaur.log.client.dto.co.OperationLogSubmitCo;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.stereotype.Service;

/**
 * 操作日志
 *
 * @author 单开宇
 * @since 2024-01-25
 */
@Service
@GRpcService
public class OperationLogServiceImpl extends OperationLogServiceImplBase implements
    OperationLogService {

  @Resource
  private OperationLogSubmitCmdExe operationLogSubmitCmdExe;

  @Resource
  private OperationLogSaveCmdExe operationLogSaveCmdExe;

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
      @NotNull StreamObserver<Empty> responseObserver) {
    OperationLogSubmitCmd operationLogSubmitCmd = new OperationLogSubmitCmd();
    OperationLogSubmitCo operationLogSubmitCo = new OperationLogSubmitCo();
    OperationLogSubmitGrpcCo operationLogSubmitGrpcCo = request.getOperationLogSubmitCo();
    operationLogSubmitCo.setContent(operationLogSubmitGrpcCo.getContent());
    operationLogSubmitCo.setOperator(operationLogSubmitGrpcCo.getOperator());
    operationLogSubmitCo.setBizNo(operationLogSubmitGrpcCo.getBizNo());
    operationLogSubmitCo.setCategory(operationLogSubmitGrpcCo.getCategory());
    operationLogSubmitCo.setDetail(operationLogSubmitGrpcCo.getDetail());
    operationLogSubmitCo.setSuccess(operationLogSubmitGrpcCo.getSuccess());
    operationLogSubmitCo.setFail(operationLogSubmitGrpcCo.getFail());
    operationLogSubmitCmd.setOperationLogSubmitCo(operationLogSubmitCo);
    operationLogSubmitCmdExe.execute(operationLogSubmitCmd);
    Empty build = Empty.newBuilder().build();
    responseObserver.onNext(build);
    responseObserver.onCompleted();
  }
}
