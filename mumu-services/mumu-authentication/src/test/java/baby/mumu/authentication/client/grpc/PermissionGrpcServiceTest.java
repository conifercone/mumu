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
import baby.mumu.authentication.client.api.PermissionGrpcService;
import baby.mumu.authentication.client.api.grpc.PageOfPermissionFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.PermissionFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.PermissionFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.PermissionFindByIdGrpcCo;
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
 * PermissionGrpcService单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class PermissionGrpcServiceTest extends AuthenticationRequired {

  private final PermissionGrpcService permissionGrpcService;
  private final MockMvc mockMvc;
  private static final Logger logger = LoggerFactory.getLogger(PermissionGrpcServiceTest.class);

  @Autowired
  public PermissionGrpcServiceTest(PermissionGrpcService permissionGrpcService, MockMvc mockMvc) {
    this.permissionGrpcService = permissionGrpcService;
    this.mockMvc = mockMvc;
  }

  @Test
  public void findAll() {
    PermissionFindAllGrpcCmd permissionFindAllGrpcCmd = PermissionFindAllGrpcCmd.newBuilder()
      .setName(StringValue.of("数据"))
      .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
      AuthHeader.builder().bearer().tokenSupplier(
        () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
          () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    PageOfPermissionFindAllGrpcCo pageOfPermissionFindAllGrpcCo = permissionGrpcService.findAll(
      permissionFindAllGrpcCmd,
      callCredentials);
    logger.info("PageOfPermissionFindAllGrpcCo: {}", pageOfPermissionFindAllGrpcCo);
    pageOfPermissionFindAllGrpcCo.getContentList().stream().map(PermissionFindAllGrpcCo::getName)
      .map(StringValue::getValue).forEach(logger::info);
    Assertions.assertNotNull(pageOfPermissionFindAllGrpcCo);
    Assertions.assertFalse(pageOfPermissionFindAllGrpcCo.getContentList().isEmpty());
  }

  @Test
  public void syncFindAll() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    PermissionFindAllGrpcCmd permissionFindAllGrpcCmd = PermissionFindAllGrpcCmd.newBuilder()
      .setName(StringValue.of("数据"))
      .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
      AuthHeader.builder().bearer().tokenSupplier(
        () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
          () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<PageOfPermissionFindAllGrpcCo> pageOfPermissionFindAllGrpcCoListenableFuture = permissionGrpcService.syncFindAll(
      permissionFindAllGrpcCmd,
      callCredentials);
    pageOfPermissionFindAllGrpcCoListenableFuture.addListener(() -> {
      try {
        PageOfPermissionFindAllGrpcCo pageOfPermissionFindAllGrpcCo = pageOfPermissionFindAllGrpcCoListenableFuture.get();
        logger.info("Sync PageOfPermissionFindAllGrpcCo: {}", pageOfPermissionFindAllGrpcCo);
        Assertions.assertNotNull(pageOfPermissionFindAllGrpcCo);
        Assertions.assertFalse(pageOfPermissionFindAllGrpcCo.getContentList().isEmpty());
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
    PermissionFindByIdGrpcCo permissionFindByIdGrpcCo = permissionGrpcService.findById(
      Int64Value.of(1),
      callCredentials);
    logger.info("PermissionFindByIdGrpcCo: {}", permissionFindByIdGrpcCo);
    Assertions.assertNotNull(permissionFindByIdGrpcCo);
  }

  @Test
  public void syncFindById() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    AuthCallCredentials callCredentials = new AuthCallCredentials(
      AuthHeader.builder().bearer().tokenSupplier(
        () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
          () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<PermissionFindByIdGrpcCo> permissionFindByIdGrpcCoListenableFuture = permissionGrpcService.syncFindById(
      Int64Value.of(1),
      callCredentials);
    permissionFindByIdGrpcCoListenableFuture.addListener(() -> {
      try {
        PermissionFindByIdGrpcCo permissionFindByIdGrpcCo = permissionFindByIdGrpcCoListenableFuture.get();
        logger.info("Sync PermissionFindByIdGrpcCo: {}", permissionFindByIdGrpcCo);
        Assertions.assertNotNull(permissionFindByIdGrpcCo);
        latch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = latch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
  }
}
