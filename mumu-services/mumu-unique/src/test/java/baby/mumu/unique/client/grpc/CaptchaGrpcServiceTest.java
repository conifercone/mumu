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

package baby.mumu.unique.client.grpc;

import baby.mumu.unique.client.api.CaptchaGrpcService;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCmd;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcDTO;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcCmd;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaVerifyGrpcResult;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class CaptchaGrpcServiceTest {

  private final CaptchaGrpcService captchaGrpcService;
  private static final Logger log = LoggerFactory.getLogger(CaptchaGrpcServiceTest.class);

  @Autowired
  public CaptchaGrpcServiceTest(CaptchaGrpcService captchaGrpcService) {
    this.captchaGrpcService = captchaGrpcService;
  }

  @Test
  public void generateSimpleCaptcha() {
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
      .setLength(4)
      .setTtl(500).build();
    SimpleCaptchaGeneratedGrpcDTO simpleCaptchaGeneratedGrpcDTO = captchaGrpcService.generateSimpleCaptcha(
      simpleCaptchaGeneratedGrpcCmd);
    log.info("simpleCaptchaGeneratedGrpcDTO : {}", simpleCaptchaGeneratedGrpcDTO);
    Assertions.assertNotNull(simpleCaptchaGeneratedGrpcDTO);
  }

  @Test
  public void syncGenerateSimpleCaptcha() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
      .setLength(4)
      .setTtl(500).build();
    ListenableFuture<SimpleCaptchaGeneratedGrpcDTO> simpleCaptchaGeneratedGrpcDTOListenableFuture = captchaGrpcService.syncGenerateSimpleCaptcha(
      simpleCaptchaGeneratedGrpcCmd);
    simpleCaptchaGeneratedGrpcDTOListenableFuture.addListener(() -> {
      try {
        SimpleCaptchaGeneratedGrpcDTO simpleCaptchaGeneratedGrpcDTO = simpleCaptchaGeneratedGrpcDTOListenableFuture.get();
        log.info("Sync SimpleCaptchaGeneratedGrpcDTO : {}", simpleCaptchaGeneratedGrpcDTO);
        Assertions.assertNotNull(simpleCaptchaGeneratedGrpcDTO);
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
      .setLength(4)
      .setTtl(500).build();
    SimpleCaptchaGeneratedGrpcDTO simpleCaptchaGeneratedGrpcDTO = captchaGrpcService.generateSimpleCaptcha(
      simpleCaptchaGeneratedGrpcCmd);
    SimpleCaptchaVerifyGrpcCmd simpleCaptchaVerifyGrpcCmd = SimpleCaptchaVerifyGrpcCmd.newBuilder()
      .setSource(simpleCaptchaGeneratedGrpcDTO.getTarget())
      .setId(simpleCaptchaGeneratedGrpcDTO.getId()).build();
    SimpleCaptchaVerifyGrpcResult simpleCaptchaVerifyGrpcResult = captchaGrpcService.verifySimpleCaptcha(
      simpleCaptchaVerifyGrpcCmd);
    log.info("simpleCaptchaVerifyGrpcResult : {}", simpleCaptchaVerifyGrpcResult);
    Assertions.assertNotNull(simpleCaptchaVerifyGrpcResult);
    Assertions.assertTrue(simpleCaptchaVerifyGrpcResult.getResult());
  }

  @Test
  public void syncVerifySimpleCaptcha()
    throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
      .setLength(4)
      .setTtl(500).build();
    SimpleCaptchaGeneratedGrpcDTO simpleCaptchaGeneratedGrpcDTO = captchaGrpcService.generateSimpleCaptcha(
      simpleCaptchaGeneratedGrpcCmd);
    SimpleCaptchaVerifyGrpcCmd simpleCaptchaVerifyGrpcCmd = SimpleCaptchaVerifyGrpcCmd.newBuilder()
      .setSource(simpleCaptchaGeneratedGrpcDTO.getTarget())
      .setId(simpleCaptchaGeneratedGrpcDTO.getId()).build();
    ListenableFuture<SimpleCaptchaVerifyGrpcResult> simpleCaptchaVerifyGrpcResultListenableFuture = captchaGrpcService.syncVerifySimpleCaptcha(
      simpleCaptchaVerifyGrpcCmd);
    simpleCaptchaVerifyGrpcResultListenableFuture.addListener(() -> {
      try {
        SimpleCaptchaVerifyGrpcResult simpleCaptchaVerifyGrpcResult = simpleCaptchaVerifyGrpcResultListenableFuture.get();
        log.info("Sync SimpleCaptchaVerifyGrpcResult : {}", simpleCaptchaVerifyGrpcResult);
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
