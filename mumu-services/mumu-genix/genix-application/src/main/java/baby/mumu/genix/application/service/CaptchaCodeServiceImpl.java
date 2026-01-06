/*
 * Copyright (c) 2024-2026, the original author or authors.
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
import baby.mumu.genix.application.captcha.executor.CaptchaCodeDeleteCmdExe;
import baby.mumu.genix.application.captcha.executor.CaptchaCodeGeneratedCmdExe;
import baby.mumu.genix.application.captcha.executor.CaptchaCodeVerifyCmdExe;
import baby.mumu.genix.client.api.CaptchaCodeService;
import baby.mumu.genix.client.api.grpc.CaptchaCodeGeneratedGrpcCmd;
import baby.mumu.genix.client.api.grpc.CaptchaCodeGeneratedGrpcDTO;
import baby.mumu.genix.client.api.grpc.CaptchaCodeServiceGrpc.CaptchaCodeServiceImplBase;
import baby.mumu.genix.client.api.grpc.CaptchaCodeVerifyGrpcCmd;
import baby.mumu.genix.client.cmds.CaptchaCodeGeneratedCmd;
import baby.mumu.genix.client.cmds.CaptchaCodeVerifyCmd;
import baby.mumu.genix.client.dto.CaptchaCodeGeneratedDTO;
import baby.mumu.genix.infra.captcha.convertor.CaptchaCodeConvertor;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import io.grpc.stub.StreamObserver;
import io.micrometer.observation.annotation.Observed;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 验证码service实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Service
@GrpcService
@Observed(name = "CaptchaCodeServiceImpl")
public class CaptchaCodeServiceImpl extends CaptchaCodeServiceImplBase implements
    CaptchaCodeService {

    private final CaptchaCodeGeneratedCmdExe captchaCodeGeneratedCmdExe;
    private final CaptchaCodeVerifyCmdExe captchaCodeVerifyCmdExe;
    private final CaptchaCodeConvertor captchaCodeConvertor;
    private final CaptchaCodeDeleteCmdExe captchaCodeDeleteCmdExe;

    @Autowired
    public CaptchaCodeServiceImpl(CaptchaCodeGeneratedCmdExe captchaCodeGeneratedCmdExe,
                                  CaptchaCodeVerifyCmdExe captchaCodeVerifyCmdExe, CaptchaCodeConvertor captchaCodeConvertor,
                                  CaptchaCodeDeleteCmdExe captchaCodeDeleteCmdExe) {
        this.captchaCodeGeneratedCmdExe = captchaCodeGeneratedCmdExe;
        this.captchaCodeVerifyCmdExe = captchaCodeVerifyCmdExe;
        this.captchaCodeConvertor = captchaCodeConvertor;
        this.captchaCodeDeleteCmdExe = captchaCodeDeleteCmdExe;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaptchaCodeGeneratedDTO generate(
        CaptchaCodeGeneratedCmd captchaCodeGeneratedCmd) {
        return captchaCodeGeneratedCmdExe.execute(captchaCodeGeneratedCmd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean verify(CaptchaCodeVerifyCmd captchaCodeVerifyCmd) {
        return captchaCodeVerifyCmdExe.execute(captchaCodeVerifyCmd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long captchaCodeId) {
        Optional.ofNullable(captchaCodeId).ifPresent(captchaCodeDeleteCmdExe::execute);
    }

    @Override
    @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
    public void generate(@NonNull CaptchaCodeGeneratedGrpcCmd request,
                         @NonNull StreamObserver<CaptchaCodeGeneratedGrpcDTO> responseObserver) {
        CaptchaCodeGeneratedCmd captchaCodeGeneratedCmd = new CaptchaCodeGeneratedCmd();
        captchaCodeGeneratedCmd.setTtl(request.getTtl());
        captchaCodeGeneratedCmd.setLength(request.getLength());
        CaptchaCodeGeneratedDTO captchaCodeGeneratedDTO = captchaCodeGeneratedCmdExe.execute(
            captchaCodeGeneratedCmd);
        responseObserver.onNext(
            captchaCodeConvertor.toCaptchaCodeGeneratedGrpcDTO(captchaCodeGeneratedDTO)
                .orElse(CaptchaCodeGeneratedGrpcDTO.getDefaultInstance()));
        responseObserver.onCompleted();
    }

    @Override
    @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
    public void verify(CaptchaCodeVerifyGrpcCmd request,
                       StreamObserver<BoolValue> responseObserver) {
        captchaCodeConvertor.toCaptchaCodeVerifyCmd(request)
            .ifPresentOrElse((captchaCodeVerifyCmd) -> {
                responseObserver.onNext(BoolValue.of(captchaCodeVerifyCmdExe.execute(
                    captchaCodeVerifyCmd)));
                responseObserver.onCompleted();
            }, () -> {
                responseObserver.onNext(BoolValue.getDefaultInstance());
                responseObserver.onCompleted();
            });
    }

    @Override
    @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
    public void delete(Int64Value request, StreamObserver<Empty> responseObserver) {
        Optional.ofNullable(request).map(Int64Value::getValue).ifPresent(
            captchaCodeDeleteCmdExe::execute);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
