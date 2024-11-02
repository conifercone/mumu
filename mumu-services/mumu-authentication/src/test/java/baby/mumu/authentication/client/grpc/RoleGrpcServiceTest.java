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
import baby.mumu.authentication.client.api.RoleGrpcService;
import baby.mumu.authentication.client.api.grpc.PageOfRoleFindAllGrpcCo;
import baby.mumu.authentication.client.api.grpc.RoleFindAllGrpcCmd;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
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
 * RoleGrpcService单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class RoleGrpcServiceTest extends AuthenticationRequired {

  private final RoleGrpcService roleGrpcService;
  private final MockMvc mockMvc;
  private static final Logger logger = LoggerFactory.getLogger(RoleGrpcServiceTest.class);

  @Autowired
  public RoleGrpcServiceTest(RoleGrpcService roleGrpcService, MockMvc mockMvc) {
    this.roleGrpcService = roleGrpcService;
    this.mockMvc = mockMvc;
  }

  @Test
  public void findAll() {
    RoleFindAllGrpcCmd roleFindAllGrpcCmd = RoleFindAllGrpcCmd.newBuilder()
      .setName(StringValue.of("管理员"))
      .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
      AuthHeader.builder().bearer().tokenSupplier(
        () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
          () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    PageOfRoleFindAllGrpcCo pageOfRoleFindAllGrpcCo = roleGrpcService.findAll(
      roleFindAllGrpcCmd,
      callCredentials);
    logger.info("PageOfRoleFindAllGrpcCo: {}", pageOfRoleFindAllGrpcCo);
    Assertions.assertNotNull(pageOfRoleFindAllGrpcCo);
    Assertions.assertFalse(pageOfRoleFindAllGrpcCo.getContentList().isEmpty());
  }

  @Test
  public void syncFindAll() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    RoleFindAllGrpcCmd roleFindAllGrpcCmd = RoleFindAllGrpcCmd.newBuilder()
      .setName(StringValue.of("管理员"))
      .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
      AuthHeader.builder().bearer().tokenSupplier(
        () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
          () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<PageOfRoleFindAllGrpcCo> pageOfRoleFindAllGrpcCoListenableFuture = roleGrpcService.syncFindAll(
      roleFindAllGrpcCmd,
      callCredentials);
    pageOfRoleFindAllGrpcCoListenableFuture.addListener(() -> {
      try {
        PageOfRoleFindAllGrpcCo pageOfRoleFindAllGrpcCo = pageOfRoleFindAllGrpcCoListenableFuture.get();
        logger.info("Sync PageOfRoleFindAllGrpcCo: {}", pageOfRoleFindAllGrpcCo);
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
