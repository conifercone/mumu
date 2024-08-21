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
package com.sky.centaur.unique.client.api;

import static com.sky.centaur.basis.response.ResultCode.GRPC_SERVICE_NOT_FOUND;

import com.google.common.util.concurrent.ListenableFuture;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.unique.client.api.grpc.CaptchaServiceGrpc;
import com.sky.centaur.unique.client.api.grpc.CaptchaServiceGrpc.CaptchaServiceBlockingStub;
import com.sky.centaur.unique.client.api.grpc.CaptchaServiceGrpc.CaptchaServiceFutureStub;
import com.sky.centaur.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCmd;
import com.sky.centaur.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCo;
import com.sky.centaur.unique.client.api.grpc.SimpleCaptchaVerifyGrpcCmd;
import com.sky.centaur.unique.client.api.grpc.SimpleCaptchaVerifyGrpcResult;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
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

  public SimpleCaptchaGeneratedGrpcCo generateSimpleCaptcha(
      SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd) {
    if (channel != null) {
      return generateSimpleCaptchaFromGrpc(simpleCaptchaGeneratedGrpcCmd);
    } else {
      Optional<ManagedChannel> managedChannelUsePlaintext = getManagedChannelUsePlaintext();
      if (managedChannelUsePlaintext.isPresent()) {
        channel = managedChannelUsePlaintext.get();
        return generateSimpleCaptchaFromGrpc(simpleCaptchaGeneratedGrpcCmd);
      } else {
        throw new CentaurException(GRPC_SERVICE_NOT_FOUND);
      }
    }
  }

  public ListenableFuture<SimpleCaptchaGeneratedGrpcCo> syncGenerateSimpleCaptcha(
      SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd) {
    if (channel != null) {
      return syncGenerateSimpleCaptchaFromGrpc(simpleCaptchaGeneratedGrpcCmd);
    } else {
      return getManagedChannelUsePlaintext().map(managedChannel -> {
        channel = managedChannel;
        return syncGenerateSimpleCaptchaFromGrpc(simpleCaptchaGeneratedGrpcCmd);
      }).orElseThrow(() -> new CentaurException(GRPC_SERVICE_NOT_FOUND));
    }
  }

  public SimpleCaptchaVerifyGrpcResult verifySimpleCaptcha(
      SimpleCaptchaVerifyGrpcCmd simpleCaptchaVerifyGrpcCmd) {
    if (channel != null) {
      return verifySimpleCaptchaFromGrpc(simpleCaptchaVerifyGrpcCmd);
    } else {
      Optional<ManagedChannel> managedChannelUsePlaintext = getManagedChannelUsePlaintext();
      if (managedChannelUsePlaintext.isPresent()) {
        channel = managedChannelUsePlaintext.get();
        return verifySimpleCaptchaFromGrpc(simpleCaptchaVerifyGrpcCmd);
      } else {
        throw new CentaurException(GRPC_SERVICE_NOT_FOUND);
      }
    }
  }

  public ListenableFuture<SimpleCaptchaVerifyGrpcResult> syncVerifySimpleCaptcha(
      SimpleCaptchaVerifyGrpcCmd simpleCaptchaVerifyGrpcCmd) {
    if (channel != null) {
      return syncVerifySimpleCaptchaFromGrpc(simpleCaptchaVerifyGrpcCmd);
    } else {
      return getManagedChannelUsePlaintext().map(managedChannel -> {
        channel = managedChannel;
        return syncVerifySimpleCaptchaFromGrpc(simpleCaptchaVerifyGrpcCmd);
      }).orElseThrow(() -> new CentaurException(GRPC_SERVICE_NOT_FOUND));
    }
  }

  private @Nullable SimpleCaptchaGeneratedGrpcCo generateSimpleCaptchaFromGrpc(
      SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd) {
    CaptchaServiceBlockingStub captchaServiceBlockingStub = CaptchaServiceGrpc.newBlockingStub(
        channel);
    return captchaServiceBlockingStub.generateSimpleCaptcha(simpleCaptchaGeneratedGrpcCmd);
  }

  private @NotNull ListenableFuture<SimpleCaptchaGeneratedGrpcCo> syncGenerateSimpleCaptchaFromGrpc(
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
