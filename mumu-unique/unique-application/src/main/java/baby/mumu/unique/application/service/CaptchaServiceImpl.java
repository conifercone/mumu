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
package baby.mumu.unique.application.service;

import baby.mumu.unique.application.captcha.executor.SimpleCaptchaGeneratedCmdExe;
import baby.mumu.unique.application.captcha.executor.SimpleCaptchaVerifyCmdExe;
import baby.mumu.unique.client.api.CaptchaService;
import baby.mumu.unique.client.api.grpc.CaptchaServiceGrpc.CaptchaServiceImplBase;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCmd;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCo;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCo.Builder;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcCmd;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcCo;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcResult;
import baby.mumu.unique.client.dto.SimpleCaptchaGeneratedCmd;
import baby.mumu.unique.client.dto.SimpleCaptchaVerifyCmd;
import baby.mumu.unique.client.dto.co.SimpleCaptchaGeneratedCo;
import baby.mumu.unique.client.dto.co.SimpleCaptchaVerifyCo;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.GRpcService;
import org.lognet.springboot.grpc.recovery.GRpcRuntimeExceptionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 验证码service实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class})
@Observed(name = "CaptchaServiceImpl")
public class CaptchaServiceImpl extends CaptchaServiceImplBase implements CaptchaService {

  private final SimpleCaptchaGeneratedCmdExe simpleCaptchaGeneratedCmdExe;
  private final SimpleCaptchaVerifyCmdExe simpleCaptchaVerifyCmdExe;

  @Autowired
  public CaptchaServiceImpl(SimpleCaptchaGeneratedCmdExe simpleCaptchaGeneratedCmdExe,
      SimpleCaptchaVerifyCmdExe simpleCaptchaVerifyCmdExe) {
    this.simpleCaptchaGeneratedCmdExe = simpleCaptchaGeneratedCmdExe;
    this.simpleCaptchaVerifyCmdExe = simpleCaptchaVerifyCmdExe;
  }

  @Override
  public SimpleCaptchaGeneratedCo generateSimpleCaptcha(
      SimpleCaptchaGeneratedCmd simpleCaptchaGeneratedCmd) {
    return simpleCaptchaGeneratedCmdExe.execute(simpleCaptchaGeneratedCmd);
  }

  @Override
  public boolean verifySimpleCaptcha(SimpleCaptchaVerifyCmd simpleCaptchaVerifyCmd) {
    return simpleCaptchaVerifyCmdExe.execute(simpleCaptchaVerifyCmd);
  }

  @NotNull
  private static SimpleCaptchaGeneratedCo getSimpleCaptchaGeneratedCo(
      @NotNull SimpleCaptchaGeneratedGrpcCmd request) {
    SimpleCaptchaGeneratedCo simpleCaptchaGeneratedCo = new SimpleCaptchaGeneratedCo();
    SimpleCaptchaGeneratedGrpcCo simpleCaptchaGeneratedGrpcCo = request.getSimpleCaptchaGeneratedGrpcCo();
    simpleCaptchaGeneratedCo.setId(
        simpleCaptchaGeneratedGrpcCo.hasId() ? simpleCaptchaGeneratedGrpcCo.getId().getValue()
            : null);
    simpleCaptchaGeneratedCo.setTtl(
        simpleCaptchaGeneratedGrpcCo.hasTtl() ? simpleCaptchaGeneratedGrpcCo.getTtl().getValue()
            : null);
    simpleCaptchaGeneratedCo.setLength(
        simpleCaptchaGeneratedGrpcCo.hasLength() ? simpleCaptchaGeneratedGrpcCo.getLength()
            .getValue() : null);
    simpleCaptchaGeneratedCo.setTarget(
        simpleCaptchaGeneratedGrpcCo.hasTarget() ? simpleCaptchaGeneratedGrpcCo.getTarget()
            .getValue() : null);
    return simpleCaptchaGeneratedCo;
  }

  @NotNull
  private static SimpleCaptchaVerifyCo getSimpleCaptchaVerifyCo(
      @NotNull SimpleCaptchaVerifyGrpcCmd request) {
    SimpleCaptchaVerifyCo simpleCaptchaVerifyCo = new SimpleCaptchaVerifyCo();
    SimpleCaptchaVerifyGrpcCo simpleCaptchaVerifyGrpcCo = request.getSimpleCaptchaVerifyGrpcCo();
    simpleCaptchaVerifyCo.setId(
        simpleCaptchaVerifyGrpcCo.hasId() ? simpleCaptchaVerifyGrpcCo.getId().getValue()
            : null);
    simpleCaptchaVerifyCo.setSource(
        simpleCaptchaVerifyGrpcCo.hasSource() ? simpleCaptchaVerifyGrpcCo.getSource()
            .getValue() : null);
    return simpleCaptchaVerifyCo;
  }

  @Override
  public void generateSimpleCaptcha(SimpleCaptchaGeneratedGrpcCmd request,
      StreamObserver<SimpleCaptchaGeneratedGrpcCo> responseObserver) {
    SimpleCaptchaGeneratedCmd simpleCaptchaGeneratedCmd = new SimpleCaptchaGeneratedCmd();
    SimpleCaptchaGeneratedCo simpleCaptchaGeneratedCo = getSimpleCaptchaGeneratedCo(
        request);
    simpleCaptchaGeneratedCmd.setSimpleCaptchaGeneratedCo(simpleCaptchaGeneratedCo);
    Builder builder = SimpleCaptchaGeneratedGrpcCo.newBuilder();
    try {
      SimpleCaptchaGeneratedCo captchaGeneratedCo = simpleCaptchaGeneratedCmdExe.execute(
          simpleCaptchaGeneratedCmd);
      if (captchaGeneratedCo != null) {
        builder.setId(Int64Value.of(captchaGeneratedCo.getId()))
            .setTtl(Int64Value.of(captchaGeneratedCo.getTtl())).setLength(
                Int32Value.of(captchaGeneratedCo.getLength()))
            .setTarget(StringValue.of(captchaGeneratedCo.getTarget()));
      }
    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void verifySimpleCaptcha(SimpleCaptchaVerifyGrpcCmd request,
      StreamObserver<SimpleCaptchaVerifyGrpcResult> responseObserver) {
    SimpleCaptchaVerifyCmd simpleCaptchaVerifyCmd = new SimpleCaptchaVerifyCmd();
    SimpleCaptchaVerifyCo simpleCaptchaVerifyCo = getSimpleCaptchaVerifyCo(
        request);
    simpleCaptchaVerifyCmd.setSimpleCaptchaVerifyCo(simpleCaptchaVerifyCo);
    SimpleCaptchaVerifyGrpcResult.Builder builder = SimpleCaptchaVerifyGrpcResult.newBuilder();
    try {
      builder.setResult(simpleCaptchaVerifyCmdExe.execute(
          simpleCaptchaVerifyCmd));
    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }
}
