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
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import com.sky.centaur.authentication.AuthenticationRequired;
import com.sky.centaur.authentication.client.api.AuthorityGrpcService;
import com.sky.centaur.authentication.client.api.grpc.AuthorityAddGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AuthorityAddGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AuthorityDeleteGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AuthorityDeleteGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AuthorityFindAllGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AuthorityFindAllGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AuthorityUpdateGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AuthorityUpdateGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.PageOfAuthorityFindAllGrpcCo;
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
  @Transactional(rollbackFor = Exception.class)
  public void add() throws ExecutionException, InterruptedException, TimeoutException {
    AuthorityAddGrpcCmd authorityAddGrpcCmd = AuthorityAddGrpcCmd.newBuilder()
        .setAuthorityAddCo(
            AuthorityAddGrpcCo.newBuilder().setCode(StringValue.of("test"))
                .setName(StringValue.of("test"))
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
    Assertions.assertEquals("test", authorityAddGrpcCo.getCode().getValue());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void syncAdd() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    AuthorityAddGrpcCmd authorityAddGrpcCmd = AuthorityAddGrpcCmd.newBuilder()
        .setAuthorityAddCo(
            AuthorityAddGrpcCo.newBuilder().setCode(StringValue.of("test"))
                .setName(StringValue.of("test"))
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
        Assertions.assertEquals("test", syncAuthorityAddGrpcCo.getCode().getValue());
        latch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = latch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void delete() throws ExecutionException, InterruptedException, TimeoutException {
    AuthorityDeleteGrpcCmd authorityDeleteGrpcCmd = AuthorityDeleteGrpcCmd.newBuilder()
        .setAuthorityDeleteCo(
            AuthorityDeleteGrpcCo.newBuilder().setId(Int64Value.of(1))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    AuthorityDeleteGrpcCo authorityDeleteGrpcCo = authorityGrpcService.delete(
        authorityDeleteGrpcCmd,
        callCredentials);
    LOGGER.info("AuthorityDeleteGrpcCo: {}", authorityDeleteGrpcCo);
    Assertions.assertNotNull(authorityDeleteGrpcCo);
    Assertions.assertEquals(1, authorityDeleteGrpcCo.getId().getValue());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void syncDelete() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    AuthorityDeleteGrpcCmd authorityDeleteGrpcCmd = AuthorityDeleteGrpcCmd.newBuilder()
        .setAuthorityDeleteCo(
            AuthorityDeleteGrpcCo.newBuilder().setId(Int64Value.of(1))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<AuthorityDeleteGrpcCo> authorityDeleteGrpcCoListenableFuture = authorityGrpcService.syncDelete(
        authorityDeleteGrpcCmd,
        callCredentials);
    authorityDeleteGrpcCoListenableFuture.addListener(() -> {
      try {
        AuthorityDeleteGrpcCo authorityDeleteGrpcCo = authorityDeleteGrpcCoListenableFuture.get();
        LOGGER.info("Sync AuthorityDeleteGrpcCo: {}", authorityDeleteGrpcCo);
        Assertions.assertNotNull(authorityDeleteGrpcCo);
        Assertions.assertEquals(1, authorityDeleteGrpcCo.getId().getValue());
        latch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = latch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void updateById() throws ExecutionException, InterruptedException, TimeoutException {
    AuthorityUpdateGrpcCmd authorityUpdateGrpcCmd = AuthorityUpdateGrpcCmd.newBuilder()
        .setAuthorityUpdateCo(
            AuthorityUpdateGrpcCo.newBuilder().setId(Int64Value.of(1)).setName(
                    StringValue.of("test"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    AuthorityUpdateGrpcCo authorityUpdateGrpcCo = authorityGrpcService.updateById(
        authorityUpdateGrpcCmd,
        callCredentials);
    LOGGER.info("AuthorityUpdateGrpcCo: {}", authorityUpdateGrpcCo);
    Assertions.assertNotNull(authorityUpdateGrpcCo);
    Assertions.assertEquals("test", authorityUpdateGrpcCo.getName().getValue());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void syncUpdateById() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    AuthorityUpdateGrpcCmd authorityUpdateGrpcCmd = AuthorityUpdateGrpcCmd.newBuilder()
        .setAuthorityUpdateCo(
            AuthorityUpdateGrpcCo.newBuilder().setId(Int64Value.of(1)).setName(
                    StringValue.of("test"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<AuthorityUpdateGrpcCo> authorityUpdateGrpcCoListenableFuture = authorityGrpcService.syncUpdateById(
        authorityUpdateGrpcCmd,
        callCredentials);
    authorityUpdateGrpcCoListenableFuture.addListener(() -> {
      try {
        AuthorityUpdateGrpcCo authorityUpdateGrpcCo = authorityUpdateGrpcCoListenableFuture.get();
        LOGGER.info("Sync AuthorityUpdateGrpcCo: {}", authorityUpdateGrpcCo);
        Assertions.assertNotNull(authorityUpdateGrpcCo);
        Assertions.assertEquals("test", authorityUpdateGrpcCo.getName().getValue());
        latch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = latch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
  }

  @Test
  public void findAll() throws ExecutionException, InterruptedException, TimeoutException {
    AuthorityFindAllGrpcCmd authorityFindAllGrpcCmd = AuthorityFindAllGrpcCmd.newBuilder()
        .setAuthorityFindAllCo(
            AuthorityFindAllGrpcCo.newBuilder().setName(StringValue.of("数据"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    PageOfAuthorityFindAllGrpcCo pageOfAuthorityFindAllGrpcCo = authorityGrpcService.findAll(
        authorityFindAllGrpcCmd,
        callCredentials);
    LOGGER.info("PageOfAuthorityFindAllGrpcCo: {}", pageOfAuthorityFindAllGrpcCo);
    Assertions.assertNotNull(pageOfAuthorityFindAllGrpcCo);
    Assertions.assertFalse(pageOfAuthorityFindAllGrpcCo.getContentList().isEmpty());
  }

  @Test
  public void syncFindAll() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    AuthorityFindAllGrpcCmd authorityFindAllGrpcCmd = AuthorityFindAllGrpcCmd.newBuilder()
        .setAuthorityFindAllCo(
            AuthorityFindAllGrpcCo.newBuilder().setName(StringValue.of("数据"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<PageOfAuthorityFindAllGrpcCo> pageOfAuthorityFindAllGrpcCoListenableFuture = authorityGrpcService.syncFindAll(
        authorityFindAllGrpcCmd,
        callCredentials);
    pageOfAuthorityFindAllGrpcCoListenableFuture.addListener(() -> {
      try {
        PageOfAuthorityFindAllGrpcCo pageOfAuthorityFindAllGrpcCo = pageOfAuthorityFindAllGrpcCoListenableFuture.get();
        LOGGER.info("Sync PageOfAuthorityFindAllGrpcCo: {}", pageOfAuthorityFindAllGrpcCo);
        Assertions.assertNotNull(pageOfAuthorityFindAllGrpcCo);
        Assertions.assertFalse(pageOfAuthorityFindAllGrpcCo.getContentList().isEmpty());
        latch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = latch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
  }
}
