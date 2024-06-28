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
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import com.sky.centaur.authentication.AuthenticationRequired;
import com.sky.centaur.authentication.client.api.AuthorityGrpcService;
import com.sky.centaur.authentication.client.api.grpc.AuthorityAddGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AuthorityAddGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AuthorityDeleteByIdGrpcCmd;
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
  public void add() {
    AuthorityAddGrpcCmd authorityAddGrpcCmd = AuthorityAddGrpcCmd.newBuilder()
        .setAuthorityAddCo(
            AuthorityAddGrpcCo.newBuilder().setCode(StringValue.of("test_code"))
                .setName(StringValue.of("test_name"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    Empty empty = authorityGrpcService.add(authorityAddGrpcCmd,
        callCredentials);
    Assertions.assertNotNull(empty);
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
    ListenableFuture<Empty> authorityAddGrpcCoFuture = authorityGrpcService.syncAdd(
        authorityAddGrpcCmd,
        callCredentials);
    authorityAddGrpcCoFuture.addListener(() -> {
      try {
        Empty empty = authorityAddGrpcCoFuture.get();
        Assertions.assertNotNull(empty);
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
  public void deleteById() {
    AuthorityDeleteByIdGrpcCmd authorityDeleteByIdGrpcCmd = AuthorityDeleteByIdGrpcCmd.newBuilder()
        .setId(Int64Value.of(3L))
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    Empty empty = authorityGrpcService.deleteById(
        authorityDeleteByIdGrpcCmd,
        callCredentials);
    Assertions.assertNotNull(empty);
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void syncDeleteById() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    AuthorityDeleteByIdGrpcCmd authorityDeleteByIdGrpcCmd = AuthorityDeleteByIdGrpcCmd.newBuilder()
        .setId(Int64Value.of(3L))
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<Empty> emptyListenableFuture = authorityGrpcService.syncDeleteById(
        authorityDeleteByIdGrpcCmd,
        callCredentials);
    emptyListenableFuture.addListener(() -> {
      try {
        Empty empty = emptyListenableFuture.get();
        Assertions.assertNotNull(empty);
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
  public void updateById() {
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
    Empty empty = authorityGrpcService.updateById(
        authorityUpdateGrpcCmd,
        callCredentials);
    Assertions.assertNotNull(empty);
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
    ListenableFuture<Empty> authorityUpdateGrpcCoListenableFuture = authorityGrpcService.syncUpdateById(
        authorityUpdateGrpcCmd,
        callCredentials);
    authorityUpdateGrpcCoListenableFuture.addListener(() -> {
      try {
        Empty empty = authorityUpdateGrpcCoListenableFuture.get();
        Assertions.assertNotNull(empty);
        latch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = latch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
  }

  @Test
  public void findAll() {
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
