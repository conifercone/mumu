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

package baby.mumu.genix.application.service;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import baby.mumu.genix.application.verification.executor.VerifyCodeGeneratedCmdExe;
import baby.mumu.genix.application.verification.executor.VerifyCodeVerifyCmdExe;
import baby.mumu.genix.client.api.VerifyCodeService;
import baby.mumu.genix.client.api.grpc.VerifyCodeGeneratedGrpcCmd;
import baby.mumu.genix.client.api.grpc.VerifyCodeServiceGrpc.VerifyCodeServiceImplBase;
import baby.mumu.genix.client.api.grpc.VerifyCodeVerifyGrpcCmd;
import baby.mumu.genix.client.cmds.VerifyCodeGeneratedCmd;
import baby.mumu.genix.client.cmds.VerifyCodeVerifyCmd;
import baby.mumu.genix.infra.verification.convertor.VerifyCodeConvertor;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Int64Value;
import io.grpc.stub.StreamObserver;
import io.micrometer.observation.annotation.Observed;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

/**
 * 验证码service实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Service
@GrpcService
@Observed(name = "VerifyCodeServiceImpl")
public class VerifyCodeServiceImpl extends VerifyCodeServiceImplBase implements VerifyCodeService {

  private final VerifyCodeGeneratedCmdExe verifyCodeGeneratedCmdExe;
  private final VerifyCodeVerifyCmdExe verifyCodeVerifyCmdExe;
  private final VerifyCodeConvertor verifyCodeConvertor;

  @Autowired
  public VerifyCodeServiceImpl(VerifyCodeGeneratedCmdExe verifyCodeGeneratedCmdExe,
    VerifyCodeVerifyCmdExe verifyCodeVerifyCmdExe, VerifyCodeConvertor verifyCodeConvertor) {
    this.verifyCodeGeneratedCmdExe = verifyCodeGeneratedCmdExe;
    this.verifyCodeVerifyCmdExe = verifyCodeVerifyCmdExe;
    this.verifyCodeConvertor = verifyCodeConvertor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long generate(
    VerifyCodeGeneratedCmd verifyCodeGeneratedCmd) {
    return verifyCodeGeneratedCmdExe.execute(verifyCodeGeneratedCmd);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean verify(VerifyCodeVerifyCmd verifyCodeVerifyCmd) {
    return verifyCodeVerifyCmdExe.execute(verifyCodeVerifyCmd);
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void generate(@NonNull VerifyCodeGeneratedGrpcCmd request,
    @NonNull StreamObserver<Int64Value> responseObserver) {
    VerifyCodeGeneratedCmd verifyCodeGeneratedCmd = new VerifyCodeGeneratedCmd();
    verifyCodeGeneratedCmd.setTtl(request.getTtl());
    verifyCodeGeneratedCmd.setLength(request.getLength());
    Long execute = verifyCodeGeneratedCmdExe.execute(
      verifyCodeGeneratedCmd);
    responseObserver.onNext(Int64Value.of(execute));
    responseObserver.onCompleted();
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void verify(VerifyCodeVerifyGrpcCmd request,
    StreamObserver<BoolValue> responseObserver) {
    verifyCodeConvertor.toVerifyCodeVerifyCmd(request)
      .ifPresentOrElse((verifyCodeVerifyCmd) -> {
        responseObserver.onNext(BoolValue.of(verifyCodeVerifyCmdExe.execute(
          verifyCodeVerifyCmd)));
        responseObserver.onCompleted();
      }, () -> {
        responseObserver.onNext(BoolValue.getDefaultInstance());
        responseObserver.onCompleted();
      });
  }
}
