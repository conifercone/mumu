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
import com.sky.centaur.authentication.client.api.AccountGrpcService;
import com.sky.centaur.authentication.client.api.grpc.AccountRegisterGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AccountRegisterGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AccountUpdateByIdGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AccountUpdateByIdGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AccountUpdateRoleGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AccountUpdateRoleGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.SexEnum;
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
 * AccountGrpcService单元测试
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class AccountGrpcServiceTest extends AuthenticationRequired {

  private final AccountGrpcService accountGrpcService;
  private final MockMvc mockMvc;
  private static final Logger LOGGER = LoggerFactory.getLogger(AccountGrpcServiceTest.class);

  @Autowired
  public AccountGrpcServiceTest(AccountGrpcService accountGrpcService, MockMvc mockMvc) {
    this.accountGrpcService = accountGrpcService;
    this.mockMvc = mockMvc;
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void register() throws ExecutionException, InterruptedException, TimeoutException {
    AccountRegisterGrpcCmd accountRegisterGrpcCmd = AccountRegisterGrpcCmd.newBuilder()
        .setAccountRegisterCo(
            AccountRegisterGrpcCo.newBuilder().setId(926369451).setUsername("test")
                .setPassword("test").setRoleCode("admin").setSex(SexEnum.SEXLESS)
                .build())
        .build();
    AccountRegisterGrpcCo accountRegisterGrpcCo = accountGrpcService.register(
        accountRegisterGrpcCmd);
    LOGGER.info("AccountRegisterGrpcCo: {}", accountRegisterGrpcCo);
    Assertions.assertNotNull(accountRegisterGrpcCo);
    Assertions.assertEquals("test", accountRegisterGrpcCo.getUsername());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void syncRegister() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    AccountRegisterGrpcCmd accountRegisterGrpcCmd = AccountRegisterGrpcCmd.newBuilder()
        .setAccountRegisterCo(
            AccountRegisterGrpcCo.newBuilder().setId(926369451).setUsername("test")
                .setPassword("test").setRoleCode("admin").setSex(SexEnum.SEXLESS)
                .build())
        .build();
    ListenableFuture<AccountRegisterGrpcCo> accountRegisterGrpcCoListenableFuture = accountGrpcService.syncRegister(
        accountRegisterGrpcCmd);
    accountRegisterGrpcCoListenableFuture.addListener(() -> {
      try {
        AccountRegisterGrpcCo syncAccountRegisterGrpcCo = accountRegisterGrpcCoListenableFuture.get();
        LOGGER.info("Sync AccountRegisterGrpcCo: {}", syncAccountRegisterGrpcCo);
        Assertions.assertNotNull(syncAccountRegisterGrpcCo);
        Assertions.assertEquals("test", syncAccountRegisterGrpcCo.getUsername());
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
  public void updateById() throws ExecutionException, InterruptedException, TimeoutException {
    AccountUpdateByIdGrpcCmd accountUpdateByIdGrpcCmd = AccountUpdateByIdGrpcCmd.newBuilder()
        .setAccountUpdateByIdGrpcCo(
            AccountUpdateByIdGrpcCo.newBuilder().setId(Int64Value.of(1))
                .setSex(SexEnum.SEXLESS)
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    AccountUpdateByIdGrpcCo accountUpdateByIdGrpcCo = accountGrpcService.updateById(
        accountUpdateByIdGrpcCmd, callCredentials);
    LOGGER.info("AccountUpdateByIdGrpcCo: {}", accountUpdateByIdGrpcCo);
    Assertions.assertNotNull(accountUpdateByIdGrpcCo);
    Assertions.assertEquals(SexEnum.SEXLESS, accountUpdateByIdGrpcCo.getSex());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void syncUpdateById() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    AccountUpdateByIdGrpcCmd accountUpdateByIdGrpcCmd = AccountUpdateByIdGrpcCmd.newBuilder()
        .setAccountUpdateByIdGrpcCo(
            AccountUpdateByIdGrpcCo.newBuilder().setId(Int64Value.of(1))
                .setSex(SexEnum.SEXLESS)
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<AccountUpdateByIdGrpcCo> accountUpdateByIdGrpcCoListenableFuture = accountGrpcService.syncUpdateById(
        accountUpdateByIdGrpcCmd, callCredentials);
    accountUpdateByIdGrpcCoListenableFuture.addListener(() -> {
      try {
        AccountUpdateByIdGrpcCo accountUpdateByIdGrpcCo = accountUpdateByIdGrpcCoListenableFuture.get();
        LOGGER.info("Sync AccountUpdateByIdGrpcCo: {}", accountUpdateByIdGrpcCo);
        Assertions.assertNotNull(accountUpdateByIdGrpcCo);
        Assertions.assertEquals(SexEnum.SEXLESS, accountUpdateByIdGrpcCo.getSex());
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
  public void updateRoleById() throws ExecutionException, InterruptedException, TimeoutException {
    AccountUpdateRoleGrpcCmd accountUpdateRoleGrpcCmd = AccountUpdateRoleGrpcCmd.newBuilder()
        .setAccountUpdateRoleGrpcCo(
            AccountUpdateRoleGrpcCo.newBuilder().setId(Int64Value.of(2))
                .setRoleCode(StringValue.of("test"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    AccountUpdateRoleGrpcCo accountUpdateRoleGrpcCo = accountGrpcService.updateRoleById(
        accountUpdateRoleGrpcCmd, callCredentials);
    LOGGER.info("AccountUpdateRoleGrpcCo: {}", accountUpdateRoleGrpcCo);
    Assertions.assertNotNull(accountUpdateRoleGrpcCo);
    Assertions.assertTrue(accountUpdateRoleGrpcCo.hasRoleCode());
    Assertions.assertEquals("test", accountUpdateRoleGrpcCo.getRoleCode().getValue());
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void syncUpdateRoleById() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    AccountUpdateRoleGrpcCmd accountUpdateRoleGrpcCmd = AccountUpdateRoleGrpcCmd.newBuilder()
        .setAccountUpdateRoleGrpcCo(
            AccountUpdateRoleGrpcCo.newBuilder().setId(Int64Value.of(2))
                .setRoleCode(StringValue.of("test"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<AccountUpdateRoleGrpcCo> accountUpdateRoleGrpcCoListenableFuture = accountGrpcService.syncUpdateRoleById(
        accountUpdateRoleGrpcCmd, callCredentials);
    accountUpdateRoleGrpcCoListenableFuture.addListener(() -> {
      try {
        AccountUpdateRoleGrpcCo accountUpdateRoleGrpcCo = accountUpdateRoleGrpcCoListenableFuture.get();
        LOGGER.info("Sync AccountUpdateRoleGrpcCo: {}", accountUpdateRoleGrpcCo);
        Assertions.assertNotNull(accountUpdateRoleGrpcCo);
        Assertions.assertTrue(accountUpdateRoleGrpcCo.hasRoleCode());
        Assertions.assertEquals("test", accountUpdateRoleGrpcCo.getRoleCode().getValue());
        countDownLatch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = countDownLatch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
  }
}
