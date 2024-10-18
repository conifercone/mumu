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
package baby.mumu.unique.client.grpc;

import baby.mumu.unique.client.api.CaptchaGrpcService;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCmd;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCo;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcCmd;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcCo;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcResult;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 验证码生成grpc接口单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class CaptchaGrpcServiceTest {

  private final CaptchaGrpcService captchaGrpcService;
  private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaGrpcServiceTest.class);

  @Autowired
  public CaptchaGrpcServiceTest(CaptchaGrpcService captchaGrpcService) {
    this.captchaGrpcService = captchaGrpcService;
  }

  @Test
  public void generateSimpleCaptcha() {
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
        .setLength(Int32Value.of(4))
        .setTtl(Int64Value.of(500)).build();
    SimpleCaptchaGeneratedGrpcCo simpleCaptchaGeneratedGrpcCo = captchaGrpcService.generateSimpleCaptcha(
        simpleCaptchaGeneratedGrpcCmd);
    LOGGER.info("simpleCaptchaGeneratedGrpcCo : {}", simpleCaptchaGeneratedGrpcCo);
    Assertions.assertNotNull(simpleCaptchaGeneratedGrpcCo);
  }

  @Test
  public void syncGenerateSimpleCaptcha() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
        .setLength(Int32Value.of(4))
        .setTtl(Int64Value.of(500)).build();
    ListenableFuture<SimpleCaptchaGeneratedGrpcCo> simpleCaptchaGeneratedGrpcCoListenableFuture = captchaGrpcService.syncGenerateSimpleCaptcha(
        simpleCaptchaGeneratedGrpcCmd);
    simpleCaptchaGeneratedGrpcCoListenableFuture.addListener(() -> {
      try {
        SimpleCaptchaGeneratedGrpcCo simpleCaptchaGeneratedGrpcCo = simpleCaptchaGeneratedGrpcCoListenableFuture.get();
        LOGGER.info("Sync SimpleCaptchaGeneratedGrpcCo : {}", simpleCaptchaGeneratedGrpcCo);
        Assertions.assertNotNull(simpleCaptchaGeneratedGrpcCo);
        countDownLatch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean complete = countDownLatch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(complete);
  }

  @Test
  public void verifySimpleCaptcha() {
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
        .setLength(Int32Value.of(4))
        .setTtl(Int64Value.of(500)).build();
    SimpleCaptchaGeneratedGrpcCo simpleCaptchaGeneratedGrpcCo = captchaGrpcService.generateSimpleCaptcha(
        simpleCaptchaGeneratedGrpcCmd);
    SimpleCaptchaVerifyGrpcCmd simpleCaptchaVerifyGrpcCmd = SimpleCaptchaVerifyGrpcCmd.newBuilder()
        .setSimpleCaptchaVerifyGrpcCo(
            SimpleCaptchaVerifyGrpcCo.newBuilder()
                .setSource(simpleCaptchaGeneratedGrpcCo.getTarget())
                .setId(simpleCaptchaGeneratedGrpcCo.getId())).build();
    SimpleCaptchaVerifyGrpcResult simpleCaptchaVerifyGrpcResult = captchaGrpcService.verifySimpleCaptcha(
        simpleCaptchaVerifyGrpcCmd);
    LOGGER.info("simpleCaptchaVerifyGrpcResult : {}", simpleCaptchaVerifyGrpcResult);
    Assertions.assertNotNull(simpleCaptchaVerifyGrpcResult);
    Assertions.assertTrue(simpleCaptchaVerifyGrpcResult.getResult());
  }

  @Test
  public void syncVerifySimpleCaptcha()
      throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
        .setLength(Int32Value.of(4))
        .setTtl(Int64Value.of(500)).build();
    SimpleCaptchaGeneratedGrpcCo simpleCaptchaGeneratedGrpcCo = captchaGrpcService.generateSimpleCaptcha(
        simpleCaptchaGeneratedGrpcCmd);
    SimpleCaptchaVerifyGrpcCmd simpleCaptchaVerifyGrpcCmd = SimpleCaptchaVerifyGrpcCmd.newBuilder()
        .setSimpleCaptchaVerifyGrpcCo(
            SimpleCaptchaVerifyGrpcCo.newBuilder()
                .setSource(simpleCaptchaGeneratedGrpcCo.getTarget())
                .setId(simpleCaptchaGeneratedGrpcCo.getId())).build();
    ListenableFuture<SimpleCaptchaVerifyGrpcResult> simpleCaptchaVerifyGrpcResultListenableFuture = captchaGrpcService.syncVerifySimpleCaptcha(
        simpleCaptchaVerifyGrpcCmd);
    simpleCaptchaVerifyGrpcResultListenableFuture.addListener(() -> {
      try {
        SimpleCaptchaVerifyGrpcResult simpleCaptchaVerifyGrpcResult = simpleCaptchaVerifyGrpcResultListenableFuture.get();
        LOGGER.info("Sync SimpleCaptchaVerifyGrpcResult : {}", simpleCaptchaVerifyGrpcResult);
        Assertions.assertNotNull(simpleCaptchaVerifyGrpcResult);
        Assertions.assertTrue(simpleCaptchaVerifyGrpcResult.getResult());
        countDownLatch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean complete = countDownLatch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(complete);
  }
}
