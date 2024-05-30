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
import com.sky.centaur.authentication.client.api.RoleGrpcService;
import com.sky.centaur.authentication.client.api.grpc.PageOfRoleFindAllGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.RoleAddGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.RoleAddGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.RoleDeleteByIdGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.RoleFindAllGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.RoleFindAllGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.RoleUpdateGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.RoleUpdateGrpcCo;
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
 * RoleGrpcService单元测试
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class RoleGrpcServiceTest extends AuthenticationRequired {

  private final RoleGrpcService roleGrpcService;
  private final MockMvc mockMvc;
  private static final Logger LOGGER = LoggerFactory.getLogger(RoleGrpcServiceTest.class);

  @Autowired
  public RoleGrpcServiceTest(RoleGrpcService roleGrpcService, MockMvc mockMvc) {
    this.roleGrpcService = roleGrpcService;
    this.mockMvc = mockMvc;
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void add() throws ExecutionException, InterruptedException, TimeoutException {
    RoleAddGrpcCmd roleAddGrpcCmd = RoleAddGrpcCmd.newBuilder()
        .setRoleAddCo(
            RoleAddGrpcCo.newBuilder().setCode(StringValue.of("test")).setName(
                    StringValue.of("test"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    RoleAddGrpcCo roleAddGrpcCo = roleGrpcService.add(roleAddGrpcCmd,
        callCredentials);
    LOGGER.info("RoleAddGrpcCo: {}", roleAddGrpcCo);
    Assertions.assertNotNull(roleAddGrpcCo);
    Assertions.assertEquals("test", roleAddGrpcCo.getCode().getValue());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void syncAdd() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    RoleAddGrpcCmd roleAddGrpcCmd = RoleAddGrpcCmd.newBuilder()
        .setRoleAddCo(
            RoleAddGrpcCo.newBuilder().setCode(StringValue.of("test")).setName(
                    StringValue.of("test"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<RoleAddGrpcCo> roleAddGrpcCoListenableFuture = roleGrpcService.syncAdd(
        roleAddGrpcCmd,
        callCredentials);
    roleAddGrpcCoListenableFuture.addListener(() -> {
      try {
        RoleAddGrpcCo syncRoleAddGrpcCo = roleAddGrpcCoListenableFuture.get();
        LOGGER.info("Sync RoleAddGrpcCo: {}", syncRoleAddGrpcCo);
        Assertions.assertNotNull(syncRoleAddGrpcCo);
        Assertions.assertEquals("test", syncRoleAddGrpcCo.getCode().getValue());
        countDownLatch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = countDownLatch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void deleteById() throws ExecutionException, InterruptedException, TimeoutException {
    RoleDeleteByIdGrpcCmd roleDeleteByIdGrpcCmd = RoleDeleteByIdGrpcCmd.newBuilder()
        .setId(Int64Value.of(1L))
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    Empty empty = roleGrpcService.deleteById(
        roleDeleteByIdGrpcCmd,
        callCredentials);
    Assertions.assertNotNull(empty);
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void syncDeleteById() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    RoleDeleteByIdGrpcCmd roleDeleteByIdGrpcCmd = RoleDeleteByIdGrpcCmd.newBuilder()
        .setId(Int64Value.of(1L))
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<Empty> emptyListenableFuture = roleGrpcService.syncDeleteById(
        roleDeleteByIdGrpcCmd,
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
  public void updateById() throws ExecutionException, InterruptedException, TimeoutException {
    RoleUpdateGrpcCmd roleUpdateGrpcCmd = RoleUpdateGrpcCmd.newBuilder()
        .setRoleUpdateCo(
            RoleUpdateGrpcCo.newBuilder().setId(Int64Value.of(1)).setName(StringValue.of("test"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    RoleUpdateGrpcCo roleUpdateGrpcCo = roleGrpcService.updateById(
        roleUpdateGrpcCmd,
        callCredentials);
    LOGGER.info("RoleUpdateGrpcCo: {}", roleUpdateGrpcCo);
    Assertions.assertNotNull(roleUpdateGrpcCo);
    Assertions.assertEquals("test", roleUpdateGrpcCo.getName().getValue());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void syncUpdateById() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    RoleUpdateGrpcCmd roleUpdateGrpcCmd = RoleUpdateGrpcCmd.newBuilder()
        .setRoleUpdateCo(
            RoleUpdateGrpcCo.newBuilder().setId(Int64Value.of(1)).setName(StringValue.of("test"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<RoleUpdateGrpcCo> roleUpdateGrpcCoListenableFuture = roleGrpcService.syncUpdateById(
        roleUpdateGrpcCmd,
        callCredentials);
    roleUpdateGrpcCoListenableFuture.addListener(() -> {
      try {
        RoleUpdateGrpcCo roleUpdateGrpcCo = roleUpdateGrpcCoListenableFuture.get();
        LOGGER.info("Sync RoleUpdateGrpcCo: {}", roleUpdateGrpcCo);
        Assertions.assertNotNull(roleUpdateGrpcCo);
        Assertions.assertEquals("test", roleUpdateGrpcCo.getName().getValue());
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
    RoleFindAllGrpcCmd roleFindAllGrpcCmd = RoleFindAllGrpcCmd.newBuilder()
        .setRoleFindAllCo(
            RoleFindAllGrpcCo.newBuilder().setName(StringValue.of("管理员"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    PageOfRoleFindAllGrpcCo pageOfRoleFindAllGrpcCo = roleGrpcService.findAll(
        roleFindAllGrpcCmd,
        callCredentials);
    LOGGER.info("PageOfRoleFindAllGrpcCo: {}", pageOfRoleFindAllGrpcCo);
    Assertions.assertNotNull(pageOfRoleFindAllGrpcCo);
    Assertions.assertFalse(pageOfRoleFindAllGrpcCo.getContentList().isEmpty());
  }

  @Test
  public void syncFindAll() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    RoleFindAllGrpcCmd roleFindAllGrpcCmd = RoleFindAllGrpcCmd.newBuilder()
        .setRoleFindAllCo(
            RoleFindAllGrpcCo.newBuilder().setName(StringValue.of("管理员"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<PageOfRoleFindAllGrpcCo> pageOfRoleFindAllGrpcCoListenableFuture = roleGrpcService.syncFindAll(
        roleFindAllGrpcCmd,
        callCredentials);
    pageOfRoleFindAllGrpcCoListenableFuture.addListener(() -> {
      try {
        PageOfRoleFindAllGrpcCo pageOfRoleFindAllGrpcCo = pageOfRoleFindAllGrpcCoListenableFuture.get();
        LOGGER.info("Sync PageOfRoleFindAllGrpcCo: {}", pageOfRoleFindAllGrpcCo);
        Assertions.assertNotNull(pageOfRoleFindAllGrpcCo);
        Assertions.assertFalse(pageOfRoleFindAllGrpcCo.getContentList().isEmpty());
        latch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = latch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
  }
}
