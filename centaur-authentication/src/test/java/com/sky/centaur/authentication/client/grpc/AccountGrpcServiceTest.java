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
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import com.sky.centaur.authentication.AuthenticationRequired;
import com.sky.centaur.authentication.client.api.AccountGrpcService;
import com.sky.centaur.authentication.client.api.grpc.AccountDisableGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AccountDisableGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AccountRegisterGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AccountRegisterGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AccountUpdateByIdGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AccountUpdateByIdGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.AccountUpdateRoleGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AccountUpdateRoleGrpcCo;
import com.sky.centaur.authentication.client.api.grpc.SexEnum;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.unique.client.api.CaptchaGrpcService;
import com.sky.centaur.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCmd;
import com.sky.centaur.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCo;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lognet.springboot.grpc.security.AuthCallCredentials;
import org.lognet.springboot.grpc.security.AuthHeader;
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
  private final CaptchaGrpcService captchaGrpcService;

  @Autowired
  public AccountGrpcServiceTest(AccountGrpcService accountGrpcService, MockMvc mockMvc,
      CaptchaGrpcService captchaGrpcService) {
    this.accountGrpcService = accountGrpcService;
    this.mockMvc = mockMvc;
    this.captchaGrpcService = captchaGrpcService;
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void register() throws ExecutionException, InterruptedException, TimeoutException {
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
        .setSimpleCaptchaGeneratedGrpcCo(
            SimpleCaptchaGeneratedGrpcCo.newBuilder().setLength(Int32Value.of(4))
                .setTtl(Int64Value.of(500))).build();
    SimpleCaptchaGeneratedGrpcCo simpleCaptchaGeneratedGrpcCo = captchaGrpcService.generateSimpleCaptcha(
        simpleCaptchaGeneratedGrpcCmd);
    AccountRegisterGrpcCmd accountRegisterGrpcCmd = AccountRegisterGrpcCmd.newBuilder()
        .setAccountRegisterCo(
            AccountRegisterGrpcCo.newBuilder().setId(Int64Value.of(926369451)).setUsername(
                    StringValue.of("test1"))
                .setPassword(StringValue.of("test1")).setRoleCode(StringValue.of("admin"))
                .setSex(SexEnum.SEXLESS)
                .build()).setCaptchaId(simpleCaptchaGeneratedGrpcCo.getId())
        .setCaptcha(simpleCaptchaGeneratedGrpcCo.getTarget())
        .build();
    Empty empty = accountGrpcService.register(
        accountRegisterGrpcCmd);
    Assertions.assertNotNull(empty);
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void syncRegister() throws InterruptedException, ExecutionException, TimeoutException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
        .setSimpleCaptchaGeneratedGrpcCo(
            SimpleCaptchaGeneratedGrpcCo.newBuilder().setLength(Int32Value.of(4))
                .setTtl(Int64Value.of(500))).build();
    SimpleCaptchaGeneratedGrpcCo simpleCaptchaGeneratedGrpcCo = captchaGrpcService.generateSimpleCaptcha(
        simpleCaptchaGeneratedGrpcCmd);
    AccountRegisterGrpcCmd accountRegisterGrpcCmd = AccountRegisterGrpcCmd.newBuilder()
        .setAccountRegisterCo(
            AccountRegisterGrpcCo.newBuilder().setId(Int64Value.of(926369451)).setUsername(
                    StringValue.of("test1"))
                .setPassword(StringValue.of("test1")).setRoleCode(StringValue.of("admin"))
                .setSex(SexEnum.SEXLESS)
                .build()).setCaptchaId(simpleCaptchaGeneratedGrpcCo.getId())
        .setCaptcha(simpleCaptchaGeneratedGrpcCo.getTarget())
        .build();
    ListenableFuture<Empty> accountRegisterGrpcCoListenableFuture = accountGrpcService.syncRegister(
        accountRegisterGrpcCmd);
    accountRegisterGrpcCoListenableFuture.addListener(() -> {
      try {
        Empty empty = accountRegisterGrpcCoListenableFuture.get();
        Assertions.assertNotNull(empty);
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
    Empty empty = accountGrpcService.updateById(
        accountUpdateByIdGrpcCmd, callCredentials);
    Assertions.assertNotNull(empty);
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
    ListenableFuture<Empty> accountUpdateByIdGrpcCoListenableFuture = accountGrpcService.syncUpdateById(
        accountUpdateByIdGrpcCmd, callCredentials);
    accountUpdateByIdGrpcCoListenableFuture.addListener(() -> {
      try {
        Empty empty = accountUpdateByIdGrpcCoListenableFuture.get();
        Assertions.assertNotNull(empty);
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
    Empty empty = accountGrpcService.updateRoleById(
        accountUpdateRoleGrpcCmd, callCredentials);
    Assertions.assertNotNull(empty);
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
    ListenableFuture<Empty> accountUpdateRoleGrpcCoListenableFuture = accountGrpcService.syncUpdateRoleById(
        accountUpdateRoleGrpcCmd, callCredentials);
    accountUpdateRoleGrpcCoListenableFuture.addListener(() -> {
      try {
        Empty empty = accountUpdateRoleGrpcCoListenableFuture.get();
        Assertions.assertNotNull(empty);
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
  public void disable() throws ExecutionException, InterruptedException, TimeoutException {
    AccountDisableGrpcCmd accountDisableGrpcCmd = AccountDisableGrpcCmd.newBuilder()
        .setAccountDisableGrpcCo(
            AccountDisableGrpcCo.newBuilder().setId(Int64Value.of(2))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    Empty empty = accountGrpcService.disable(
        accountDisableGrpcCmd, callCredentials);
    Assertions.assertNotNull(empty);
  }

  @Test
  @Transactional(rollbackFor = Exception.class)
  public void syncDisable() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    AccountDisableGrpcCmd accountDisableGrpcCmd = AccountDisableGrpcCmd.newBuilder()
        .setAccountDisableGrpcCo(
            AccountDisableGrpcCo.newBuilder().setId(Int64Value.of(2))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new CentaurException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<Empty> emptyListenableFuture = accountGrpcService.syncDisable(
        accountDisableGrpcCmd, callCredentials);
    emptyListenableFuture.addListener(() -> {
      try {
        Empty empty = emptyListenableFuture.get();
        Assertions.assertNotNull(empty);
        countDownLatch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = countDownLatch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
  }
}
