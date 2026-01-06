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

package baby.mumu.genix.client.api;

import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.genix.client.api.grpc.CaptchaCodeGeneratedGrpcCmd;
import baby.mumu.genix.client.api.grpc.CaptchaCodeGeneratedGrpcDTO;
import baby.mumu.genix.client.api.grpc.CaptchaCodeServiceGrpc;
import baby.mumu.genix.client.api.grpc.CaptchaCodeServiceGrpc.CaptchaCodeServiceBlockingStub;
import baby.mumu.genix.client.api.grpc.CaptchaCodeServiceGrpc.CaptchaCodeServiceFutureStub;
import baby.mumu.genix.client.api.grpc.CaptchaCodeVerifyGrpcCmd;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Int64Value;
import io.grpc.ManagedChannel;
import io.micrometer.observation.annotation.Observed;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.grpc.client.GrpcChannelFactory;

import java.util.Optional;

import static baby.mumu.basis.response.ResponseCode.GRPC_SERVICE_NOT_FOUND;

/**
 * 验证码生成对外提供grpc调用实例
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Observed(name = "CaptchaCodeGrpcService")
public class CaptchaCodeGrpcService extends GenixGrpcService implements DisposableBean {

    private ManagedChannel channel;

    public CaptchaCodeGrpcService(
        DiscoveryClient discoveryClient,
        GrpcChannelFactory grpcChannelFactory) {
        super(discoveryClient, grpcChannelFactory);
    }

    @Override
    public void destroy() {
        Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdown);
    }

    public CaptchaCodeGeneratedGrpcDTO generate(
        CaptchaCodeGeneratedGrpcCmd captchaCodeGeneratedGrpcCmd) {
        return Optional.ofNullable(channel)
            .or(this::getManagedChannel)
            .map(ch -> {
                channel = ch;
                return generateFromGrpc(captchaCodeGeneratedGrpcCmd);
            })
            .orElseThrow(() -> new ApplicationException(GRPC_SERVICE_NOT_FOUND));
    }

    public ListenableFuture<CaptchaCodeGeneratedGrpcDTO> syncGenerate(
        CaptchaCodeGeneratedGrpcCmd captchaCodeGeneratedGrpcCmd) {
        return Optional.ofNullable(channel)
            .or(this::getManagedChannel)
            .map(ch -> {
                channel = ch;
                return syncGenerateFromGrpc(captchaCodeGeneratedGrpcCmd);
            })
            .orElseThrow(() -> new ApplicationException(GRPC_SERVICE_NOT_FOUND));
    }

    public BoolValue verify(
        CaptchaCodeVerifyGrpcCmd captchaCodeVerifyGrpcCmd) {
        return Optional.ofNullable(channel)
            .or(this::getManagedChannel)
            .map(ch -> {
                channel = ch;
                return verifyFromGrpc(captchaCodeVerifyGrpcCmd);
            })
            .orElseThrow(() -> new ApplicationException(GRPC_SERVICE_NOT_FOUND));
    }

    @SuppressWarnings("unused")
    public ListenableFuture<BoolValue> syncVerify(
        CaptchaCodeVerifyGrpcCmd captchaCodeVerifyGrpcCmd) {
        return Optional.ofNullable(channel)
            .or(this::getManagedChannel)
            .map(ch -> {
                channel = ch;
                return syncVerifyFromGrpc(captchaCodeVerifyGrpcCmd);
            })
            .orElseThrow(() -> new ApplicationException(GRPC_SERVICE_NOT_FOUND));
    }

    public void delete(Int64Value captchaCodeId) {
        Optional.ofNullable(channel)
            .or(this::getManagedChannel)
            .ifPresentOrElse(ch -> {
                channel = ch;
                deleteFromGrpc(captchaCodeId);
            }, () -> {
                throw new ApplicationException(GRPC_SERVICE_NOT_FOUND);
            });
    }

    @SuppressWarnings("unused")
    public void syncDelete(Int64Value captchaCodeId) {
        Optional.ofNullable(channel)
            .or(this::getManagedChannel)
            .ifPresentOrElse(ch -> {
                channel = ch;
                syncDeleteFromGrpc(captchaCodeId);
            }, () -> {
                throw new ApplicationException(GRPC_SERVICE_NOT_FOUND);
            });
    }

    private CaptchaCodeGeneratedGrpcDTO generateFromGrpc(
        CaptchaCodeGeneratedGrpcCmd captchaCodeGeneratedGrpcCmd) {
        CaptchaCodeServiceBlockingStub captchaCodeServiceBlockingStub = CaptchaCodeServiceGrpc.newBlockingStub(
            channel);
        return captchaCodeServiceBlockingStub.generate(captchaCodeGeneratedGrpcCmd);
    }

    private @NonNull ListenableFuture<CaptchaCodeGeneratedGrpcDTO> syncGenerateFromGrpc(
        CaptchaCodeGeneratedGrpcCmd captchaCodeGeneratedGrpcCmd) {
        CaptchaCodeServiceFutureStub captchaCodeServiceFutureStub = CaptchaCodeServiceGrpc.newFutureStub(
            channel);
        return captchaCodeServiceFutureStub.generate(
            captchaCodeGeneratedGrpcCmd);
    }

    private BoolValue verifyFromGrpc(
        CaptchaCodeVerifyGrpcCmd captchaCodeVerifyGrpcCmd) {
        CaptchaCodeServiceBlockingStub captchaCodeServiceBlockingStub = CaptchaCodeServiceGrpc.newBlockingStub(
            channel);
        return captchaCodeServiceBlockingStub.verify(captchaCodeVerifyGrpcCmd);
    }

    private @NonNull ListenableFuture<BoolValue> syncVerifyFromGrpc(
        CaptchaCodeVerifyGrpcCmd captchaCodeVerifyGrpcCmd) {
        CaptchaCodeServiceFutureStub captchaCodeServiceFutureStub = CaptchaCodeServiceGrpc.newFutureStub(
            channel);
        return captchaCodeServiceFutureStub.verify(
            captchaCodeVerifyGrpcCmd);
    }

    private void deleteFromGrpc(Int64Value captchaCodeId) {
        CaptchaCodeServiceBlockingStub captchaCodeServiceBlockingStub = CaptchaCodeServiceGrpc.newBlockingStub(
            channel);
        // noinspection ResultOfMethodCallIgnored
        captchaCodeServiceBlockingStub.delete(captchaCodeId);
    }

    private void syncDeleteFromGrpc(Int64Value captchaCodeId) {
        CaptchaCodeServiceFutureStub captchaCodeServiceFutureStub = CaptchaCodeServiceGrpc.newFutureStub(
            channel);
        // noinspection ResultOfMethodCallIgnored
        captchaCodeServiceFutureStub.delete(captchaCodeId);
    }
}
