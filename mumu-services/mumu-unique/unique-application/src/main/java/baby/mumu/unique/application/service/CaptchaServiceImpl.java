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

package baby.mumu.unique.application.service;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import baby.mumu.unique.application.captcha.executor.SimpleCaptchaGeneratedCmdExe;
import baby.mumu.unique.application.captcha.executor.SimpleCaptchaVerifyCmdExe;
import baby.mumu.unique.client.api.CaptchaService;
import baby.mumu.unique.client.api.grpc.CaptchaServiceGrpc.CaptchaServiceImplBase;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCmd;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcDTO;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcDTO.Builder;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcCmd;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcResult;
import baby.mumu.unique.client.cmds.SimpleCaptchaGeneratedCmd;
import baby.mumu.unique.client.cmds.SimpleCaptchaVerifyCmd;
import baby.mumu.unique.client.dto.SimpleCaptchaGeneratedDTO;
import baby.mumu.unique.infra.captcha.convertor.CaptchaConvertor;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import net.devh.boot.grpc.server.service.GrpcService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 验证码service实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Service
@GrpcService(interceptors = {ObservationGrpcServerInterceptor.class, ClientIpInterceptor.class})
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
  public SimpleCaptchaGeneratedDTO generateSimpleCaptcha(
    SimpleCaptchaGeneratedCmd simpleCaptchaGeneratedCmd) {
    return simpleCaptchaGeneratedCmdExe.execute(simpleCaptchaGeneratedCmd);
  }

  @Override
  public boolean verifySimpleCaptcha(SimpleCaptchaVerifyCmd simpleCaptchaVerifyCmd) {
    return simpleCaptchaVerifyCmdExe.execute(simpleCaptchaVerifyCmd);
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void generateSimpleCaptcha(@NotNull SimpleCaptchaGeneratedGrpcCmd request,
    StreamObserver<SimpleCaptchaGeneratedGrpcDTO> responseObserver) {
    SimpleCaptchaGeneratedCmd simpleCaptchaGeneratedCmd = new SimpleCaptchaGeneratedCmd();
    simpleCaptchaGeneratedCmd.setTtl(request.getTtl());
    simpleCaptchaGeneratedCmd.setLength(request.getLength());
    Builder builder = SimpleCaptchaGeneratedGrpcDTO.newBuilder();
    SimpleCaptchaGeneratedDTO captchaGeneratedCo = simpleCaptchaGeneratedCmdExe.execute(
      simpleCaptchaGeneratedCmd);
    if (captchaGeneratedCo != null) {
      builder.setId(captchaGeneratedCo.getId())
        .setTtl(captchaGeneratedCo.getTtl()).setLength(
          captchaGeneratedCo.getLength())
        .setTarget(captchaGeneratedCo.getTarget());
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
        builder.setResult(simpleCaptchaVerifyCmdExe.execute(
          simpleCaptchaVerifyCmdNotNull));
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();

      }, () -> {
        responseObserver.onNext(SimpleCaptchaVerifyGrpcResult.getDefaultInstance());
        responseObserver.onCompleted();
      });
  }
}
