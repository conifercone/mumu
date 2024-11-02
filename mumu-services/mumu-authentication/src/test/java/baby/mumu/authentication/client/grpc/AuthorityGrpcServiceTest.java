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
import baby.mumu.authentication.client.api.grpc.AuthorityFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.AuthorityFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.AuthorityFindByIdGrpcCo;
import baby.mumu.authentication.client.api.grpc.PageOfAuthorityFindAllGrpcCo;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
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
  private static final Logger logger = LoggerFactory.getLogger(AuthorityGrpcServiceTest.class);

  @Autowired
  public AuthorityGrpcServiceTest(AuthorityGrpcService authorityGrpcService, MockMvc mockMvc) {
    this.authorityGrpcService = authorityGrpcService;
    this.mockMvc = mockMvc;
  }

  @Test
  public void findAll() {
    AuthorityFindAllGrpcCmd authorityFindAllGrpcCmd = AuthorityFindAllGrpcCmd.newBuilder()
      .setName(StringValue.of("数据"))
      .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
      AuthHeader.builder().bearer().tokenSupplier(
        () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
          () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    PageOfAuthorityFindAllGrpcCo pageOfAuthorityFindAllGrpcCo = authorityGrpcService.findAll(
      authorityFindAllGrpcCmd,
      callCredentials);
    logger.info("PageOfAuthorityFindAllGrpcCo: {}", pageOfAuthorityFindAllGrpcCo);
    pageOfAuthorityFindAllGrpcCo.getContentList().stream().map(AuthorityFindAllGrpcCo::getName)
      .map(StringValue::getValue).forEach(logger::info);
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
          () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<PageOfAuthorityFindAllGrpcCo> pageOfAuthorityFindAllGrpcCoListenableFuture = authorityGrpcService.syncFindAll(
      authorityFindAllGrpcCmd,
      callCredentials);
    pageOfAuthorityFindAllGrpcCoListenableFuture.addListener(() -> {
      try {
        PageOfAuthorityFindAllGrpcCo pageOfAuthorityFindAllGrpcCo = pageOfAuthorityFindAllGrpcCoListenableFuture.get();
        logger.info("Sync PageOfAuthorityFindAllGrpcCo: {}", pageOfAuthorityFindAllGrpcCo);
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

  @Test
  public void findById() {
    AuthCallCredentials callCredentials = new AuthCallCredentials(
      AuthHeader.builder().bearer().tokenSupplier(
        () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
          () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    AuthorityFindByIdGrpcCo authorityFindByIdGrpcCo = authorityGrpcService.findById(
      Int64Value.of(1),
      callCredentials);
    logger.info("AuthorityFindByIdGrpcCo: {}", authorityFindByIdGrpcCo);
    Assertions.assertNotNull(authorityFindByIdGrpcCo);
  }

  @Test
  public void syncFindById() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    AuthCallCredentials callCredentials = new AuthCallCredentials(
      AuthHeader.builder().bearer().tokenSupplier(
        () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
          () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<AuthorityFindByIdGrpcCo> authorityFindByIdGrpcCoListenableFuture = authorityGrpcService.syncFindById(
      Int64Value.of(1),
      callCredentials);
    authorityFindByIdGrpcCoListenableFuture.addListener(() -> {
      try {
        AuthorityFindByIdGrpcCo authorityFindByIdGrpcCo = authorityFindByIdGrpcCoListenableFuture.get();
        logger.info("Sync AuthorityFindByIdGrpcCo: {}", authorityFindByIdGrpcCo);
        Assertions.assertNotNull(authorityFindByIdGrpcCo);
        latch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = latch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
  }
}
