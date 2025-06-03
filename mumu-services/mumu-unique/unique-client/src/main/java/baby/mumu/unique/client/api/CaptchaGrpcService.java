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

package baby.mumu.unique.client.api;

import static baby.mumu.basis.response.ResponseCode.GRPC_SERVICE_NOT_FOUND;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.unique.client.api.grpc.CaptchaServiceGrpc;
import baby.mumu.unique.client.api.grpc.CaptchaServiceGrpc.CaptchaServiceBlockingStub;
import baby.mumu.unique.client.api.grpc.CaptchaServiceGrpc.CaptchaServiceFutureStub;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCmd;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcDTO;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcCmd;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcResult;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * 验证码生成对外提供grpc调用实例
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Observed(name = "CaptchaGrpcService")
public class CaptchaGrpcService extends UniqueGrpcService implements DisposableBean {

  private ManagedChannel channel;

  public CaptchaGrpcService(
    DiscoveryClient discoveryClient,
    ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    super(discoveryClient, grpcClientInterceptorObjectProvider);
  }

  @Override
  public void destroy() {
    Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdown);
  }

  public SimpleCaptchaGeneratedGrpcDTO generateSimpleCaptcha(
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return generateSimpleCaptchaFromGrpc(simpleCaptchaGeneratedGrpcCmd);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  public ListenableFuture<SimpleCaptchaGeneratedGrpcDTO> syncGenerateSimpleCaptcha(
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return syncGenerateSimpleCaptchaFromGrpc(simpleCaptchaGeneratedGrpcCmd);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  public SimpleCaptchaVerifyGrpcResult verifySimpleCaptcha(
    SimpleCaptchaVerifyGrpcCmd simpleCaptchaVerifyGrpcCmd) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return verifySimpleCaptchaFromGrpc(simpleCaptchaVerifyGrpcCmd);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  public ListenableFuture<SimpleCaptchaVerifyGrpcResult> syncVerifySimpleCaptcha(
    SimpleCaptchaVerifyGrpcCmd simpleCaptchaVerifyGrpcCmd) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return syncVerifySimpleCaptchaFromGrpc(simpleCaptchaVerifyGrpcCmd);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  private @Nullable SimpleCaptchaGeneratedGrpcDTO generateSimpleCaptchaFromGrpc(
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd) {
    CaptchaServiceBlockingStub captchaServiceBlockingStub = CaptchaServiceGrpc.newBlockingStub(
      channel);
    return captchaServiceBlockingStub.generateSimpleCaptcha(simpleCaptchaGeneratedGrpcCmd);
  }

  private @NotNull ListenableFuture<SimpleCaptchaGeneratedGrpcDTO> syncGenerateSimpleCaptchaFromGrpc(
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd) {
    CaptchaServiceFutureStub captchaServiceFutureStub = CaptchaServiceGrpc.newFutureStub(
      channel);
    return captchaServiceFutureStub.generateSimpleCaptcha(
      simpleCaptchaGeneratedGrpcCmd);
  }

  private @Nullable SimpleCaptchaVerifyGrpcResult verifySimpleCaptchaFromGrpc(
    SimpleCaptchaVerifyGrpcCmd simpleCaptchaVerifyGrpcCmd) {
    CaptchaServiceBlockingStub captchaServiceBlockingStub = CaptchaServiceGrpc.newBlockingStub(
      channel);
    return captchaServiceBlockingStub.verifySimpleCaptcha(simpleCaptchaVerifyGrpcCmd);
  }

  private @NotNull ListenableFuture<SimpleCaptchaVerifyGrpcResult> syncVerifySimpleCaptchaFromGrpc(
    SimpleCaptchaVerifyGrpcCmd simpleCaptchaVerifyGrpcCmd) {
    CaptchaServiceFutureStub captchaServiceFutureStub = CaptchaServiceGrpc.newFutureStub(
      channel);
    return captchaServiceFutureStub.verifySimpleCaptcha(
      simpleCaptchaVerifyGrpcCmd);
  }
}
