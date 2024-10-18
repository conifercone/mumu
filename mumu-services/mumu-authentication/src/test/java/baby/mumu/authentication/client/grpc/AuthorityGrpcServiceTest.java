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
import baby.mumu.authentication.client.api.AuthorityGrpcService;
import baby.mumu.authentication.client.api.grpc.AuthorityAddGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AuthorityAddGrpcCo;
import baby.mumu.authentication.client.api.grpc.AuthorityDeleteByIdGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AuthorityFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AuthorityFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.AuthorityUpdateGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AuthorityUpdateGrpcCo;
import baby.mumu.authentication.client.api.grpc.PageOfAuthorityFindAllGrpcCo;
import baby.mumu.authentication.infrastructure.authority.gatewayimpl.database.AuthorityRepository;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResultCode;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
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

/**
 * AuthorityGrpcService单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class AuthorityGrpcServiceTest extends AuthenticationRequired {

  private final AuthorityGrpcService authorityGrpcService;
  private final MockMvc mockMvc;
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityGrpcServiceTest.class);
  private final AuthorityRepository authorityRepository;

  @Autowired
  public AuthorityGrpcServiceTest(AuthorityGrpcService authorityGrpcService, MockMvc mockMvc,
      AuthorityRepository authorityRepository) {
    this.authorityGrpcService = authorityGrpcService;
    this.mockMvc = mockMvc;
    this.authorityRepository = authorityRepository;
  }

  @Test
  public void add() {
    AuthorityAddGrpcCmd authorityAddGrpcCmd = AuthorityAddGrpcCmd.newBuilder()
        .setAuthorityAddCo(
            AuthorityAddGrpcCo.newBuilder().setId(Int64Value.of(778223))
                .setCode(StringValue.of("theft"))
                .setName(StringValue.of("theft"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    Empty empty = authorityGrpcService.add(authorityAddGrpcCmd,
        callCredentials);
    Assertions.assertNotNull(empty);
    authorityRepository.deleteById(778223L);
  }

  @Test
  public void syncAdd() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    AuthorityAddGrpcCmd authorityAddGrpcCmd = AuthorityAddGrpcCmd.newBuilder()
        .setAuthorityAddCo(
            AuthorityAddGrpcCo.newBuilder().setId(Int64Value.of(465175550))
                .setCode(StringValue.of("rally"))
                .setName(StringValue.of("rally"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<Empty> authorityAddGrpcCoFuture = authorityGrpcService.syncAdd(
        authorityAddGrpcCmd,
        callCredentials);
    authorityAddGrpcCoFuture.addListener(() -> {
      try {
        Empty empty = authorityAddGrpcCoFuture.get();
        Assertions.assertNotNull(empty);
        latch.countDown();
        authorityRepository.deleteById(465175550L);
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = latch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
  }

  @Test
  public void deleteById() {
    AuthorityAddGrpcCmd authorityAddGrpcCmd = AuthorityAddGrpcCmd.newBuilder()
        .setAuthorityAddCo(
            AuthorityAddGrpcCo.newBuilder().setId(Int64Value.of(796816315))
                .setCode(StringValue.of("madness"))
                .setName(StringValue.of("madness"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    authorityGrpcService.add(authorityAddGrpcCmd, callCredentials);
    AuthorityDeleteByIdGrpcCmd authorityDeleteByIdGrpcCmd = AuthorityDeleteByIdGrpcCmd.newBuilder()
        .setId(Int64Value.of(796816315))
        .build();
    Empty empty = authorityGrpcService.deleteById(
        authorityDeleteByIdGrpcCmd,
        callCredentials);
    Assertions.assertNotNull(empty);
  }

  @Test
  public void syncDeleteById() throws InterruptedException {
    AuthorityAddGrpcCmd authorityAddGrpcCmd = AuthorityAddGrpcCmd.newBuilder()
        .setAuthorityAddCo(
            AuthorityAddGrpcCo.newBuilder().setId(Int64Value.of(1800851703))
                .setCode(StringValue.of("somewhat"))
                .setName(StringValue.of("somewhat"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    authorityGrpcService.add(authorityAddGrpcCmd, callCredentials);
    CountDownLatch latch = new CountDownLatch(1);
    AuthorityDeleteByIdGrpcCmd authorityDeleteByIdGrpcCmd = AuthorityDeleteByIdGrpcCmd.newBuilder()
        .setId(Int64Value.of(1800851703))
        .build();
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
  public void updateById() {
    AuthorityAddGrpcCmd authorityAddGrpcCmd = AuthorityAddGrpcCmd.newBuilder()
        .setAuthorityAddCo(
            AuthorityAddGrpcCo.newBuilder().setId(Int64Value.of(1801517971))
                .setCode(StringValue.of("focuses"))
                .setName(StringValue.of("focuses"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    authorityGrpcService.add(authorityAddGrpcCmd, callCredentials);
    AuthorityUpdateGrpcCmd authorityUpdateGrpcCmd = AuthorityUpdateGrpcCmd.newBuilder()
        .setAuthorityUpdateCo(
            AuthorityUpdateGrpcCo.newBuilder().setId(Int64Value.of(1801517971)).setName(
                    StringValue.of("conclusions"))
                .build())
        .build();
    Empty empty = authorityGrpcService.updateById(
        authorityUpdateGrpcCmd,
        callCredentials);
    Assertions.assertNotNull(empty);
    authorityRepository.deleteById(1801517971L);
  }

  @Test
  public void syncUpdateById() throws InterruptedException {
    AuthorityAddGrpcCmd authorityAddGrpcCmd = AuthorityAddGrpcCmd.newBuilder()
        .setAuthorityAddCo(
            AuthorityAddGrpcCo.newBuilder().setId(Int64Value.of(1562763044))
                .setCode(StringValue.of("pieces"))
                .setName(StringValue.of("pieces"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    authorityGrpcService.add(authorityAddGrpcCmd, callCredentials);
    CountDownLatch latch = new CountDownLatch(1);
    AuthorityUpdateGrpcCmd authorityUpdateGrpcCmd = AuthorityUpdateGrpcCmd.newBuilder()
        .setAuthorityUpdateCo(
            AuthorityUpdateGrpcCo.newBuilder().setId(Int64Value.of(1562763044)).setName(
                    StringValue.of("cents"))
                .build())
        .build();
    ListenableFuture<Empty> authorityUpdateGrpcCoListenableFuture = authorityGrpcService.syncUpdateById(
        authorityUpdateGrpcCmd,
        callCredentials);
    authorityUpdateGrpcCoListenableFuture.addListener(() -> {
      try {
        Empty empty = authorityUpdateGrpcCoListenableFuture.get();
        Assertions.assertNotNull(empty);
        latch.countDown();
        authorityRepository.deleteById(1562763044L);
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
        .setName(StringValue.of("数据"))
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    PageOfAuthorityFindAllGrpcCo pageOfAuthorityFindAllGrpcCo = authorityGrpcService.findAll(
        authorityFindAllGrpcCmd,
        callCredentials);
    LOGGER.info("PageOfAuthorityFindAllGrpcCo: {}", pageOfAuthorityFindAllGrpcCo);
    pageOfAuthorityFindAllGrpcCo.getContentList().stream().map(AuthorityFindAllGrpcCo::getName)
        .map(StringValue::getValue).forEach(LOGGER::info);
    Assertions.assertNotNull(pageOfAuthorityFindAllGrpcCo);
    Assertions.assertFalse(pageOfAuthorityFindAllGrpcCo.getContentList().isEmpty());
  }

  @Test
  public void syncFindAll() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    AuthorityFindAllGrpcCmd authorityFindAllGrpcCmd = AuthorityFindAllGrpcCmd.newBuilder()
        .setName(StringValue.of("数据"))
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
                () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
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
