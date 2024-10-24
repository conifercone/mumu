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
package baby.mumu.unique.application.service;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import baby.mumu.unique.application.captcha.executor.SimpleCaptchaGeneratedCmdExe;
import baby.mumu.unique.application.captcha.executor.SimpleCaptchaVerifyCmdExe;
import baby.mumu.unique.client.api.CaptchaService;
import baby.mumu.unique.client.api.grpc.CaptchaServiceGrpc.CaptchaServiceImplBase;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCmd;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCo;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCo.Builder;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcCmd;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcResult;
import baby.mumu.unique.client.dto.SimpleCaptchaGeneratedCmd;
import baby.mumu.unique.client.dto.SimpleCaptchaVerifyCmd;
import baby.mumu.unique.client.dto.co.SimpleCaptchaGeneratedCo;
import baby.mumu.unique.infrastructure.captcha.convertor.CaptchaConvertor;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
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
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class, ClientIpInterceptor.class})
@Observed(name = "CaptchaServiceImpl")
public class CaptchaServiceImpl extends CaptchaServiceImplBase implements CaptchaService {

  private final SimpleCaptchaGeneratedCmdExe simpleCaptchaGeneratedCmdExe;
  private final SimpleCaptchaVerifyCmdExe simpleCaptchaVerifyCmdExe;
  private final CaptchaConvertor captchaConvertor;

  @Autowired
  public CaptchaServiceImpl(SimpleCaptchaGeneratedCmdExe simpleCaptchaGeneratedCmdExe,
    SimpleCaptchaVerifyCmdExe simpleCaptchaVerifyCmdExe, CaptchaConvertor captchaConvertor) {
    this.simpleCaptchaGeneratedCmdExe = simpleCaptchaGeneratedCmdExe;
    this.simpleCaptchaVerifyCmdExe = simpleCaptchaVerifyCmdExe;
    this.captchaConvertor = captchaConvertor;
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

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void generateSimpleCaptcha(SimpleCaptchaGeneratedGrpcCmd request,
    StreamObserver<SimpleCaptchaGeneratedGrpcCo> responseObserver) {
    SimpleCaptchaGeneratedCmd simpleCaptchaGeneratedCmd = new SimpleCaptchaGeneratedCmd();
    simpleCaptchaGeneratedCmd.setId(
      request.hasId() ? request.getId().getValue()
        : null);
    simpleCaptchaGeneratedCmd.setTtl(
      request.hasTtl() ? request.getTtl().getValue()
        : null);
    simpleCaptchaGeneratedCmd.setLength(
      request.hasLength() ? request.getLength()
        .getValue() : null);
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
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void verifySimpleCaptcha(SimpleCaptchaVerifyGrpcCmd request,
    StreamObserver<SimpleCaptchaVerifyGrpcResult> responseObserver) {
    captchaConvertor.toSimpleCaptchaVerifyCmd(request)
      .ifPresentOrElse((simpleCaptchaVerifyCmdNotNull) -> {
        SimpleCaptchaVerifyGrpcResult.Builder builder = SimpleCaptchaVerifyGrpcResult.newBuilder();
        try {
          builder.setResult(simpleCaptchaVerifyCmdExe.execute(
            simpleCaptchaVerifyCmdNotNull));
          responseObserver.onNext(builder.build());
          responseObserver.onCompleted();
        } catch (Exception e) {
          throw new GRpcRuntimeExceptionWrapper(e);
        }
      }, () -> {
        responseObserver.onNext(SimpleCaptchaVerifyGrpcResult.getDefaultInstance());
        responseObserver.onCompleted();
      });
  }
}
