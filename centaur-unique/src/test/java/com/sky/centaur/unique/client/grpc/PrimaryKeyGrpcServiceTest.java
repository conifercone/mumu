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
package com.sky.centaur.unique.client.grpc;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.sky.centaur.unique.client.api.PrimaryKeyGrpcService;
import com.sky.centaur.unique.client.api.grpc.SnowflakeResult;
import java.util.Optional;
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
 * 主键生成grpc接口单元测试
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class PrimaryKeyGrpcServiceTest {

  private final PrimaryKeyGrpcService primaryKeyGrpcService;
  private static final Logger LOGGER = LoggerFactory.getLogger(PrimaryKeyGrpcServiceTest.class);

  @Autowired
  public PrimaryKeyGrpcServiceTest(PrimaryKeyGrpcService primaryKeyGrpcService) {
    this.primaryKeyGrpcService = primaryKeyGrpcService;
  }

  @Test
  public void snowflake() {
    Long snowflake = primaryKeyGrpcService.snowflake();
    LOGGER.info("snowflake : {}", snowflake);
    Assertions.assertNotNull(snowflake);
  }

  @Test
  public void syncSnowflake() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    Optional<ListenableFuture<SnowflakeResult>> optionalSnowflakeResultListenableFuture = primaryKeyGrpcService.syncSnowflake();
    optionalSnowflakeResultListenableFuture.ifPresent(
        snowflakeResultListenableFuture -> snowflakeResultListenableFuture.addListener(() -> {
          try {
            SnowflakeResult snowflakeResult = snowflakeResultListenableFuture.get();
            LOGGER.info("Sync SnowflakeResult : {}", snowflakeResult);
            Assertions.assertNotNull(snowflakeResult);
            countDownLatch.countDown();
          } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
          }
        }, MoreExecutors.directExecutor()));
    boolean complete = countDownLatch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(complete);
  }
}
