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
package com.sky.centaur.authentication.client.grpc;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.sky.centaur.authentication.AuthenticationRequired;
import com.sky.centaur.authentication.client.api.AuthorityGrpcService;
import com.sky.centaur.authentication.client.api.grpc.AuthorityAddGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AuthorityAddGrpcCo;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lognet.springboot.grpc.security.AuthCallCredentials;
import org.lognet.springboot.grpc.security.AuthHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthorityGrpcService单元测试
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class AuthorityGrpcServiceTest extends AuthenticationRequired {

  private final AuthorityGrpcService authorityGrpcService;
  private final MockMvc mockMvc;
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityGrpcServiceTest.class);

  @Autowired
  public AuthorityGrpcServiceTest(AuthorityGrpcService authorityGrpcService, MockMvc mockMvc) {
    this.authorityGrpcService = authorityGrpcService;
    this.mockMvc = mockMvc;
  }

  @Test
  @Transactional
  public void add() throws ExecutionException, InterruptedException, TimeoutException {
    AuthorityAddGrpcCmd authorityAddGrpcCmd = AuthorityAddGrpcCmd.newBuilder()
        .setAuthorityAddCo(
            AuthorityAddGrpcCo.newBuilder().setId(926369451).setCode("test").setName("test")
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    AuthorityAddGrpcCo authorityAddGrpcCo = authorityGrpcService.add(authorityAddGrpcCmd,
        callCredentials);
    LOGGER.info("AuthorityAddGrpcCo: {}", authorityAddGrpcCo);
    Assertions.assertNotNull(authorityAddGrpcCo);
    Assertions.assertEquals("test", authorityAddGrpcCo.getCode());
  }

  @Test
  @Transactional
  public void syncAdd() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    AuthorityAddGrpcCmd authorityAddGrpcCmd = AuthorityAddGrpcCmd.newBuilder()
        .setAuthorityAddCo(
            AuthorityAddGrpcCo.newBuilder().setId(926369451).setCode("test").setName("test")
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<AuthorityAddGrpcCo> authorityAddGrpcCoFuture = authorityGrpcService.syncAdd(
        authorityAddGrpcCmd,
        callCredentials);
    authorityAddGrpcCoFuture.addListener(() -> {
      try {
        AuthorityAddGrpcCo syncAuthorityAddGrpcCo = authorityAddGrpcCoFuture.get();
        LOGGER.info("Sync AuthorityAddGrpcCo: {}", syncAuthorityAddGrpcCo);
        Assertions.assertNotNull(syncAuthorityAddGrpcCo);
        Assertions.assertEquals("test", syncAuthorityAddGrpcCo.getCode());
        latch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = latch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
  }
}
