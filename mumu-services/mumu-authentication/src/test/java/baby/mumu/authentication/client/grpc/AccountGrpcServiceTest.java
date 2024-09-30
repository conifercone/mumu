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
package baby.mumu.authentication.client.grpc;

import baby.mumu.authentication.AuthenticationRequired;
import baby.mumu.authentication.client.api.AccountGrpcService;
import baby.mumu.authentication.client.api.grpc.AccountDisableGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AccountDisableGrpcCo;
import baby.mumu.authentication.client.api.grpc.AccountRegisterGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AccountRegisterGrpcCo;
import baby.mumu.authentication.client.api.grpc.AccountUpdateByIdGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AccountUpdateByIdGrpcCo;
import baby.mumu.authentication.client.api.grpc.AccountUpdateRoleGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AccountUpdateRoleGrpcCo;
import baby.mumu.authentication.client.api.grpc.LocalDate;
import baby.mumu.authentication.client.api.grpc.SexEnum;
import baby.mumu.authentication.infrastructure.account.gatewayimpl.database.AccountRepository;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResultCode;
import baby.mumu.unique.client.api.CaptchaGrpcService;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCmd;
import baby.mumu.unique.client.api.grpc.SimpleCaptchaGeneratedGrpcCo;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lognet.springboot.grpc.security.AuthCallCredentials;
import org.lognet.springboot.grpc.security.AuthHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * AccountGrpcService单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class AccountGrpcServiceTest extends AuthenticationRequired {

  private final AccountGrpcService accountGrpcService;
  private final MockMvc mockMvc;
  private final CaptchaGrpcService captchaGrpcService;
  private final AccountRepository accountRepository;

  @Autowired
  public AccountGrpcServiceTest(AccountGrpcService accountGrpcService, MockMvc mockMvc,
      CaptchaGrpcService captchaGrpcService, AccountRepository accountRepository) {
    this.accountGrpcService = accountGrpcService;
    this.mockMvc = mockMvc;
    this.captchaGrpcService = captchaGrpcService;
    this.accountRepository = accountRepository;
  }

  @Test
  public void register() {
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
        .setSimpleCaptchaGeneratedGrpcCo(
            SimpleCaptchaGeneratedGrpcCo.newBuilder().setLength(Int32Value.of(4))
                .setTtl(Int64Value.of(500))).build();
    SimpleCaptchaGeneratedGrpcCo simpleCaptchaGeneratedGrpcCo = null;
    try {
      simpleCaptchaGeneratedGrpcCo = captchaGrpcService.generateSimpleCaptcha(
          simpleCaptchaGeneratedGrpcCmd);
    } catch (Exception ignore) {
    }
    Optional.ofNullable(simpleCaptchaGeneratedGrpcCo)
        .ifPresent(simpleCaptchaGeneratedGrpcCoNonNull -> {
          AccountRegisterGrpcCmd accountRegisterGrpcCmd = AccountRegisterGrpcCmd.newBuilder()
              .setAccountRegisterCo(
                  AccountRegisterGrpcCo.newBuilder().setId(Int64Value.of(926369451)).setUsername(
                          StringValue.of("test1"))
                      .setPassword(StringValue.of("test1")).addRoleCode(StringValue.of("admin"))
                      .setSex(SexEnum.SEXLESS)
                      .setEmail(StringValue.of("547913250@qq.com"))
                      .setBirthday(LocalDate.newBuilder().setYear(Int32Value.of(1995))
                          .setMonth(Int32Value.of(8)).setDay(Int32Value.of(2)).build())
                      .build()).setCaptchaId(simpleCaptchaGeneratedGrpcCoNonNull.getId())
              .setCaptcha(simpleCaptchaGeneratedGrpcCoNonNull.getTarget())
              .build();
          Empty empty = accountGrpcService.register(
              accountRegisterGrpcCmd);
          Assertions.assertNotNull(empty);
          accountRepository.deleteById(926369451L);
        });
  }

  @Test
  public void syncRegister() {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
        .setSimpleCaptchaGeneratedGrpcCo(
            SimpleCaptchaGeneratedGrpcCo.newBuilder().setLength(Int32Value.of(4))
                .setTtl(Int64Value.of(500))).build();
    SimpleCaptchaGeneratedGrpcCo simpleCaptchaGeneratedGrpcCo = null;
    try {
      simpleCaptchaGeneratedGrpcCo = captchaGrpcService.generateSimpleCaptcha(
          simpleCaptchaGeneratedGrpcCmd);
    } catch (Exception ignore) {
    }
    Optional.ofNullable(simpleCaptchaGeneratedGrpcCo)
        .ifPresent(simpleCaptchaGeneratedGrpcCoNonNull -> {
          AccountRegisterGrpcCmd accountRegisterGrpcCmd = AccountRegisterGrpcCmd.newBuilder()
              .setAccountRegisterCo(
                  AccountRegisterGrpcCo.newBuilder().setId(Int64Value.of(998125644)).setUsername(
                          StringValue.of("Summer"))
                      .setPassword(StringValue.of("Summer")).addRoleCode(StringValue.of("admin"))
                      .setSex(SexEnum.SEXLESS)
                      .setEmail(StringValue.of("nadja_oharebsho@actress.gp"))
                      .setBirthday(LocalDate.newBuilder().setYear(Int32Value.of(1995))
                          .setMonth(Int32Value.of(8)).setDay(Int32Value.of(2)).build())
                      .build()).setCaptchaId(simpleCaptchaGeneratedGrpcCoNonNull.getId())
              .setCaptcha(simpleCaptchaGeneratedGrpcCoNonNull.getTarget())
              .build();
          ListenableFuture<Empty> accountRegisterGrpcCoListenableFuture = accountGrpcService.syncRegister(
              accountRegisterGrpcCmd);
          accountRegisterGrpcCoListenableFuture.addListener(() -> {
            try {
              Empty empty = accountRegisterGrpcCoListenableFuture.get();
              Assertions.assertNotNull(empty);
              countDownLatch.countDown();
              accountRepository.deleteById(998125644L);
            } catch (InterruptedException | ExecutionException e) {
              throw new RuntimeException(e);
            }
          }, MoreExecutors.directExecutor());
          boolean completed;
          try {
            completed = countDownLatch.await(3, TimeUnit.SECONDS);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          Assertions.assertTrue(completed);
        });
  }

  @Test
  public void updateById() {
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
        .setSimpleCaptchaGeneratedGrpcCo(
            SimpleCaptchaGeneratedGrpcCo.newBuilder().setLength(Int32Value.of(4))
                .setTtl(Int64Value.of(500))).build();
    SimpleCaptchaGeneratedGrpcCo simpleCaptchaGeneratedGrpcCo = null;
    try {
      simpleCaptchaGeneratedGrpcCo = captchaGrpcService.generateSimpleCaptcha(
          simpleCaptchaGeneratedGrpcCmd);
    } catch (Exception ignore) {
    }
    Optional.ofNullable(simpleCaptchaGeneratedGrpcCo)
        .ifPresent(simpleCaptchaGeneratedGrpcCoNonNull -> {
          AccountRegisterGrpcCmd accountRegisterGrpcCmd = AccountRegisterGrpcCmd.newBuilder()
              .setAccountRegisterCo(
                  AccountRegisterGrpcCo.newBuilder().setId(Int64Value.of(1998071806)).setUsername(
                          StringValue.of("Tavares"))
                      .setPassword(StringValue.of("Tavares")).addRoleCode(StringValue.of("admin"))
                      .setSex(SexEnum.SEXLESS)
                      .setEmail(StringValue.of("marylouise_mcgahancxi@memories.kn"))
                      .setBirthday(LocalDate.newBuilder().setYear(Int32Value.of(1995))
                          .setMonth(Int32Value.of(8)).setDay(Int32Value.of(2)).build())
                      .build()).setCaptchaId(simpleCaptchaGeneratedGrpcCoNonNull.getId())
              .setCaptcha(simpleCaptchaGeneratedGrpcCoNonNull.getTarget())
              .build();
          accountGrpcService.register(
              accountRegisterGrpcCmd);
          AccountUpdateByIdGrpcCmd accountUpdateByIdGrpcCmd = AccountUpdateByIdGrpcCmd.newBuilder()
              .setAccountUpdateByIdGrpcCo(
                  AccountUpdateByIdGrpcCo.newBuilder().setId(Int64Value.of(1998071806))
                      .setSex(SexEnum.FEMALE)
                      .build())
              .build();
          AuthCallCredentials callCredentials = new AuthCallCredentials(
              AuthHeader.builder().bearer().tokenSupplier(
                  () -> ByteBuffer.wrap(getToken(mockMvc, "Tavares", "Tavares").orElseThrow(
                      () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
          );
          Empty empty = accountGrpcService.updateById(
              accountUpdateByIdGrpcCmd, callCredentials);
          Assertions.assertNotNull(empty);
          accountRepository.deleteById(1998071806L);
        });
  }

  @Test
  public void syncUpdateById() {
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
        .setSimpleCaptchaGeneratedGrpcCo(
            SimpleCaptchaGeneratedGrpcCo.newBuilder().setLength(Int32Value.of(4))
                .setTtl(Int64Value.of(500))).build();
    SimpleCaptchaGeneratedGrpcCo simpleCaptchaGeneratedGrpcCo = null;
    try {
      simpleCaptchaGeneratedGrpcCo = captchaGrpcService.generateSimpleCaptcha(
          simpleCaptchaGeneratedGrpcCmd);
    } catch (Exception ignore) {
    }
    Optional.ofNullable(simpleCaptchaGeneratedGrpcCo)
        .ifPresent(simpleCaptchaGeneratedGrpcCoNonNull -> {
          AccountRegisterGrpcCmd accountRegisterGrpcCmd = AccountRegisterGrpcCmd.newBuilder()
              .setAccountRegisterCo(
                  AccountRegisterGrpcCo.newBuilder().setId(Int64Value.of(1202398040)).setUsername(
                          StringValue.of("Thierry"))
                      .setPassword(StringValue.of("Thierry")).addRoleCode(StringValue.of("admin"))
                      .setSex(SexEnum.SEXLESS)
                      .setEmail(StringValue.of("ronna_huggardcx4t@exactly.ird"))
                      .setBirthday(LocalDate.newBuilder().setYear(Int32Value.of(1995))
                          .setMonth(Int32Value.of(8)).setDay(Int32Value.of(2)).build())
                      .build()).setCaptchaId(simpleCaptchaGeneratedGrpcCoNonNull.getId())
              .setCaptcha(simpleCaptchaGeneratedGrpcCoNonNull.getTarget())
              .build();
          accountGrpcService.register(
              accountRegisterGrpcCmd);
          CountDownLatch countDownLatch = new CountDownLatch(1);
          AccountUpdateByIdGrpcCmd accountUpdateByIdGrpcCmd = AccountUpdateByIdGrpcCmd.newBuilder()
              .setAccountUpdateByIdGrpcCo(
                  AccountUpdateByIdGrpcCo.newBuilder().setId(Int64Value.of(1202398040))
                      .setSex(SexEnum.FEMALE)
                      .build())
              .build();
          AuthCallCredentials callCredentials = new AuthCallCredentials(
              AuthHeader.builder().bearer().tokenSupplier(
                  () -> ByteBuffer.wrap(getToken(mockMvc, "Thierry", "Thierry").orElseThrow(
                      () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
          );
          ListenableFuture<Empty> accountUpdateByIdGrpcCoListenableFuture = accountGrpcService.syncUpdateById(
              accountUpdateByIdGrpcCmd, callCredentials);
          accountUpdateByIdGrpcCoListenableFuture.addListener(() -> {
            try {
              Empty empty = accountUpdateByIdGrpcCoListenableFuture.get();
              Assertions.assertNotNull(empty);
              countDownLatch.countDown();
              accountRepository.deleteById(1202398040L);
            } catch (InterruptedException | ExecutionException e) {
              throw new RuntimeException(e);
            }
          }, MoreExecutors.directExecutor());
          boolean completed;
          try {
            completed = countDownLatch.await(3, TimeUnit.SECONDS);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          Assertions.assertTrue(completed);
        });
  }


  @Test
  public void updateRoleById() {
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
        .setSimpleCaptchaGeneratedGrpcCo(
            SimpleCaptchaGeneratedGrpcCo.newBuilder().setLength(Int32Value.of(4))
                .setTtl(Int64Value.of(500))).build();
    SimpleCaptchaGeneratedGrpcCo simpleCaptchaGeneratedGrpcCo = null;
    try {
      simpleCaptchaGeneratedGrpcCo = captchaGrpcService.generateSimpleCaptcha(
          simpleCaptchaGeneratedGrpcCmd);
    } catch (Exception ignore) {
    }
    Optional.ofNullable(simpleCaptchaGeneratedGrpcCo)
        .ifPresent(simpleCaptchaGeneratedGrpcCoNonNull -> {
          AccountRegisterGrpcCmd accountRegisterGrpcCmd = AccountRegisterGrpcCmd.newBuilder()
              .setAccountRegisterCo(
                  AccountRegisterGrpcCo.newBuilder().setId(Int64Value.of(1172156340)).setUsername(
                          StringValue.of("Safia"))
                      .setPassword(StringValue.of("Safia")).addRoleCode(StringValue.of("admin"))
                      .setSex(SexEnum.SEXLESS)
                      .setEmail(StringValue.of("kei_millnerwflt@ext.nfh"))
                      .setBirthday(LocalDate.newBuilder().setYear(Int32Value.of(1995))
                          .setMonth(Int32Value.of(8)).setDay(Int32Value.of(2)).build())
                      .build()).setCaptchaId(simpleCaptchaGeneratedGrpcCoNonNull.getId())
              .setCaptcha(simpleCaptchaGeneratedGrpcCoNonNull.getTarget())
              .build();
          accountGrpcService.register(
              accountRegisterGrpcCmd);
          AccountUpdateRoleGrpcCmd accountUpdateRoleGrpcCmd = AccountUpdateRoleGrpcCmd.newBuilder()
              .setAccountUpdateRoleGrpcCo(
                  AccountUpdateRoleGrpcCo.newBuilder().setId(Int64Value.of(1172156340))
                      .addRoleCode(StringValue.of("test"))
                      .build())
              .build();
          AuthCallCredentials callCredentials = new AuthCallCredentials(
              AuthHeader.builder().bearer().tokenSupplier(
                  () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                      () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
          );
          Empty empty = accountGrpcService.updateRoleById(
              accountUpdateRoleGrpcCmd, callCredentials);
          Assertions.assertNotNull(empty);
          accountRepository.deleteById(1172156340L);
        });
  }

  @Test
  public void syncUpdateRoleById() {
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
        .setSimpleCaptchaGeneratedGrpcCo(
            SimpleCaptchaGeneratedGrpcCo.newBuilder().setLength(Int32Value.of(4))
                .setTtl(Int64Value.of(500))).build();
    SimpleCaptchaGeneratedGrpcCo simpleCaptchaGeneratedGrpcCo = null;
    try {
      simpleCaptchaGeneratedGrpcCo = captchaGrpcService.generateSimpleCaptcha(
          simpleCaptchaGeneratedGrpcCmd);
    } catch (Exception ignore) {
    }
    Optional.ofNullable(simpleCaptchaGeneratedGrpcCo)
        .ifPresent(simpleCaptchaGeneratedGrpcCoNonNull -> {
          AccountRegisterGrpcCmd accountRegisterGrpcCmd = AccountRegisterGrpcCmd.newBuilder()
              .setAccountRegisterCo(
                  AccountRegisterGrpcCo.newBuilder().setId(Int64Value.of(2129661173)).setUsername(
                          StringValue.of("Megen"))
                      .setPassword(StringValue.of("Megen")).addRoleCode(StringValue.of("admin"))
                      .setSex(SexEnum.SEXLESS)
                      .setEmail(StringValue.of("cathi_beaversse@supposed.ad"))
                      .setBirthday(LocalDate.newBuilder().setYear(Int32Value.of(1995))
                          .setMonth(Int32Value.of(8)).setDay(Int32Value.of(2)).build())
                      .build()).setCaptchaId(simpleCaptchaGeneratedGrpcCoNonNull.getId())
              .setCaptcha(simpleCaptchaGeneratedGrpcCoNonNull.getTarget())
              .build();
          accountGrpcService.register(
              accountRegisterGrpcCmd);
          CountDownLatch countDownLatch = new CountDownLatch(1);
          AccountUpdateRoleGrpcCmd accountUpdateRoleGrpcCmd = AccountUpdateRoleGrpcCmd.newBuilder()
              .setAccountUpdateRoleGrpcCo(
                  AccountUpdateRoleGrpcCo.newBuilder().setId(Int64Value.of(2129661173))
                      .addRoleCode(StringValue.of("test"))
                      .build())
              .build();
          AuthCallCredentials callCredentials = new AuthCallCredentials(
              AuthHeader.builder().bearer().tokenSupplier(
                  () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                      () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
          );
          ListenableFuture<Empty> accountUpdateRoleGrpcCoListenableFuture = accountGrpcService.syncUpdateRoleById(
              accountUpdateRoleGrpcCmd, callCredentials);
          accountUpdateRoleGrpcCoListenableFuture.addListener(() -> {
            try {
              Empty empty = accountUpdateRoleGrpcCoListenableFuture.get();
              Assertions.assertNotNull(empty);
              countDownLatch.countDown();
              accountRepository.deleteById(2129661173L);
            } catch (InterruptedException | ExecutionException e) {
              throw new RuntimeException(e);
            }
          }, MoreExecutors.directExecutor());
          boolean completed;
          try {
            completed = countDownLatch.await(3, TimeUnit.SECONDS);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          Assertions.assertTrue(completed);
        });
  }

  @Test
  public void disable() {
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
        .setSimpleCaptchaGeneratedGrpcCo(
            SimpleCaptchaGeneratedGrpcCo.newBuilder().setLength(Int32Value.of(4))
                .setTtl(Int64Value.of(500))).build();
    SimpleCaptchaGeneratedGrpcCo simpleCaptchaGeneratedGrpcCo = null;
    try {
      simpleCaptchaGeneratedGrpcCo = captchaGrpcService.generateSimpleCaptcha(
          simpleCaptchaGeneratedGrpcCmd);
    } catch (Exception ignore) {
    }
    Optional.ofNullable(simpleCaptchaGeneratedGrpcCo)
        .ifPresent(simpleCaptchaGeneratedGrpcCoNonNull -> {
          AccountRegisterGrpcCmd accountRegisterGrpcCmd = AccountRegisterGrpcCmd.newBuilder()
              .setAccountRegisterCo(
                  AccountRegisterGrpcCo.newBuilder().setId(Int64Value.of(480432403)).setUsername(
                          StringValue.of("Sang"))
                      .setPassword(StringValue.of("Sang")).addRoleCode(StringValue.of("admin"))
                      .setSex(SexEnum.SEXLESS)
                      .setEmail(StringValue.of("bertha_sizemoreo@affairs.lcj"))
                      .setBirthday(LocalDate.newBuilder().setYear(Int32Value.of(1995))
                          .setMonth(Int32Value.of(8)).setDay(Int32Value.of(2)).build())
                      .build()).setCaptchaId(simpleCaptchaGeneratedGrpcCoNonNull.getId())
              .setCaptcha(simpleCaptchaGeneratedGrpcCoNonNull.getTarget())
              .build();
          accountGrpcService.register(
              accountRegisterGrpcCmd);
          AccountDisableGrpcCmd accountDisableGrpcCmd = AccountDisableGrpcCmd.newBuilder()
              .setAccountDisableGrpcCo(
                  AccountDisableGrpcCo.newBuilder().setId(Int64Value.of(480432403))
                      .build())
              .build();
          AuthCallCredentials callCredentials = new AuthCallCredentials(
              AuthHeader.builder().bearer().tokenSupplier(
                  () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                      () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
          );
          Empty empty = accountGrpcService.disable(
              accountDisableGrpcCmd, callCredentials);
          Assertions.assertNotNull(empty);
          accountRepository.deleteById(480432403L);
        });
  }

  @Test
  public void syncDisable() {
    SimpleCaptchaGeneratedGrpcCmd simpleCaptchaGeneratedGrpcCmd = SimpleCaptchaGeneratedGrpcCmd.newBuilder()
        .setSimpleCaptchaGeneratedGrpcCo(
            SimpleCaptchaGeneratedGrpcCo.newBuilder().setLength(Int32Value.of(4))
                .setTtl(Int64Value.of(500))).build();
    SimpleCaptchaGeneratedGrpcCo simpleCaptchaGeneratedGrpcCo = null;
    try {
      simpleCaptchaGeneratedGrpcCo = captchaGrpcService.generateSimpleCaptcha(
          simpleCaptchaGeneratedGrpcCmd);
    } catch (Exception ignore) {
    }
    Optional.ofNullable(simpleCaptchaGeneratedGrpcCo)
        .ifPresent(simpleCaptchaGeneratedGrpcCoNonNull -> {
          AccountRegisterGrpcCmd accountRegisterGrpcCmd = AccountRegisterGrpcCmd.newBuilder()
              .setAccountRegisterCo(
                  AccountRegisterGrpcCo.newBuilder().setId(Int64Value.of(1740311010)).setUsername(
                          StringValue.of("ignore"))
                      .setPassword(StringValue.of("ignore")).addRoleCode(StringValue.of("admin"))
                      .setSex(SexEnum.SEXLESS)
                      .setEmail(StringValue.of("estefany_simkinstbuz@hollow.rz"))
                      .setBirthday(LocalDate.newBuilder().setYear(Int32Value.of(1995))
                          .setMonth(Int32Value.of(8)).setDay(Int32Value.of(2)).build())
                      .build()).setCaptchaId(simpleCaptchaGeneratedGrpcCoNonNull.getId())
              .setCaptcha(simpleCaptchaGeneratedGrpcCoNonNull.getTarget())
              .build();
          accountGrpcService.register(
              accountRegisterGrpcCmd);
          CountDownLatch countDownLatch = new CountDownLatch(1);
          AccountDisableGrpcCmd accountDisableGrpcCmd = AccountDisableGrpcCmd.newBuilder()
              .setAccountDisableGrpcCo(
                  AccountDisableGrpcCo.newBuilder().setId(Int64Value.of(1740311010))
                      .build())
              .build();
          AuthCallCredentials callCredentials = new AuthCallCredentials(
              AuthHeader.builder().bearer().tokenSupplier(
                  () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                      () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
          );
          ListenableFuture<Empty> emptyListenableFuture = accountGrpcService.syncDisable(
              accountDisableGrpcCmd, callCredentials);
          emptyListenableFuture.addListener(() -> {
            try {
              Empty empty = emptyListenableFuture.get();
              Assertions.assertNotNull(empty);
              countDownLatch.countDown();
              accountRepository.deleteById(1740311010L);
            } catch (InterruptedException | ExecutionException e) {
              throw new RuntimeException(e);
            }
          }, MoreExecutors.directExecutor());
          boolean completed;
          try {
            completed = countDownLatch.await(3, TimeUnit.SECONDS);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          Assertions.assertTrue(completed);
        });
  }
}
