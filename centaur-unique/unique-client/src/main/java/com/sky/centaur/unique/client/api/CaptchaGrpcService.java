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
import com.sky.centaur.unique.client.api.grpc.CaptchaServiceGrpc.CaptchaServiceFutureStub;
import com.sky.centaur.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCmd;
import com.sky.centaur.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCo;
import com.sky.centaur.unique.client.api.grpc.SimpleCaptchaVerifyGrpcCmd;
import com.sky.centaur.unique.client.api.grpc.SimpleCaptchaVerifyGrpcResult;
import io.grpc.ManagedChannel;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * 验证码生成对外提供grpc调用实例
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
@Observed(name = "CaptchaGrpcService")
public class CaptchaGrpcService extends UniqueGrpcService implements DisposableBean {

  private ManagedChannel channel;

  private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaGrpcService.class);

  public CaptchaGrpcService(
      DiscoveryClient discoveryClient,
      ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    super(discoveryClient, grpcClientInterceptorObjectProvider);
  }

  @Override
  public void destroy() {
    if (channel != null) {
      channel.shutdown();
    }
  }

  public SimpleCaptchaGeneratedGrpcCo generateSimpleCaptcha(
      SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd)
      throws ExecutionException, InterruptedException, TimeoutException {
    if (channel != null) {
      return generateSimpleCaptchaFromGrpc(simpleCaptchaGeneratedGrpcCmd);
    } else {
      Optional<ManagedChannel> managedChannelUsePlaintext = getManagedChannelUsePlaintext();
      if (managedChannelUsePlaintext.isPresent()) {
        channel = managedChannelUsePlaintext.get();
        return generateSimpleCaptchaFromGrpc(simpleCaptchaGeneratedGrpcCmd);
      } else {
        LOGGER.error(GRPC_SERVICE_NOT_FOUND.getResultMsg());
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
      }).orElse(null);
    }
  }

  public SimpleCaptchaVerifyGrpcResult verifySimpleCaptcha(
      SimpleCaptchaVerifyGrpcCmd simpleCaptchaVerifyGrpcCmd)
      throws ExecutionException, InterruptedException, TimeoutException {
    if (channel != null) {
      return verifySimpleCaptchaFromGrpc(simpleCaptchaVerifyGrpcCmd);
    } else {
      Optional<ManagedChannel> managedChannelUsePlaintext = getManagedChannelUsePlaintext();
      if (managedChannelUsePlaintext.isPresent()) {
        channel = managedChannelUsePlaintext.get();
        return verifySimpleCaptchaFromGrpc(simpleCaptchaVerifyGrpcCmd);
      } else {
        LOGGER.error(GRPC_SERVICE_NOT_FOUND.getResultMsg());
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
      }).orElse(null);
    }
  }

  private @Nullable SimpleCaptchaGeneratedGrpcCo generateSimpleCaptchaFromGrpc(
      SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd)
      throws ExecutionException, InterruptedException, TimeoutException {
    CaptchaServiceFutureStub captchaServiceFutureStub = CaptchaServiceGrpc.newFutureStub(
        channel);
    ListenableFuture<SimpleCaptchaGeneratedGrpcCo> simpleCaptchaGeneratedGrpcCoListenableFuture = captchaServiceFutureStub.generateSimpleCaptcha(
        simpleCaptchaGeneratedGrpcCmd);
    return simpleCaptchaGeneratedGrpcCoListenableFuture.get(3, TimeUnit.SECONDS);
  }

  private @NotNull ListenableFuture<SimpleCaptchaGeneratedGrpcCo> syncGenerateSimpleCaptchaFromGrpc(
      SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd) {
    CaptchaServiceFutureStub captchaServiceFutureStub = CaptchaServiceGrpc.newFutureStub(
        channel);
    return captchaServiceFutureStub.generateSimpleCaptcha(
        simpleCaptchaGeneratedGrpcCmd);
  }

  private @Nullable SimpleCaptchaVerifyGrpcResult verifySimpleCaptchaFromGrpc(
      SimpleCaptchaVerifyGrpcCmd simpleCaptchaVerifyGrpcCmd)
      throws ExecutionException, InterruptedException, TimeoutException {
    CaptchaServiceFutureStub captchaServiceFutureStub = CaptchaServiceGrpc.newFutureStub(
        channel);
    ListenableFuture<SimpleCaptchaVerifyGrpcResult> simpleCaptchaVerifyGrpcResultListenableFuture = captchaServiceFutureStub.verifySimpleCaptcha(
        simpleCaptchaVerifyGrpcCmd);
    return simpleCaptchaVerifyGrpcResultListenableFuture.get(3, TimeUnit.SECONDS);
  }

  private @NotNull ListenableFuture<SimpleCaptchaVerifyGrpcResult> syncVerifySimpleCaptchaFromGrpc(
      SimpleCaptchaVerifyGrpcCmd simpleCaptchaVerifyGrpcCmd) {
    CaptchaServiceFutureStub captchaServiceFutureStub = CaptchaServiceGrpc.newFutureStub(
        channel);
    return captchaServiceFutureStub.verifySimpleCaptcha(
        simpleCaptchaVerifyGrpcCmd);
  }
}
