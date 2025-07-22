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

import baby.mumu.unique.client.api.VerifyCodeGrpcService;
import baby.mumu.unique.client.api.grpc.VerifyCodeGeneratedGrpcCmd;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
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
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class VerifyCodeGrpcServiceTest {

  private final VerifyCodeGrpcService verifyCodeGrpcService;
  private static final Logger log = LoggerFactory.getLogger(VerifyCodeGrpcServiceTest.class);

  @Autowired
  public VerifyCodeGrpcServiceTest(VerifyCodeGrpcService verifyCodeGrpcService) {
    this.verifyCodeGrpcService = verifyCodeGrpcService;
  }

  @Test
  public void generate() {
    VerifyCodeGeneratedGrpcCmd verifyCodeGeneratedGrpcCmd = VerifyCodeGeneratedGrpcCmd.newBuilder()
      .setLength(4)
      .setTtl(500).build();
    Int64Value generate = verifyCodeGrpcService.generate(
      verifyCodeGeneratedGrpcCmd);
    VerifyCodeGrpcServiceTest.log.info("ID : {}",
      generate.getValue());
    Assertions.assertNotNull(generate);
  }

  @Test
  public void syncGenerate() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    VerifyCodeGeneratedGrpcCmd verifyCodeGeneratedGrpcCmd = VerifyCodeGeneratedGrpcCmd.newBuilder()
      .setLength(4)
      .setTtl(500).build();
    ListenableFuture<Int64Value> valueListenableFuture = verifyCodeGrpcService.syncGenerate(
      verifyCodeGeneratedGrpcCmd);
    valueListenableFuture.addListener(() -> {
      try {
        Int64Value int64Value = valueListenableFuture.get();
        VerifyCodeGrpcServiceTest.log.info("Sync ID : {}",
          int64Value.getValue());
        Assertions.assertNotNull(int64Value);
        countDownLatch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean complete = countDownLatch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(complete);
  }
}
