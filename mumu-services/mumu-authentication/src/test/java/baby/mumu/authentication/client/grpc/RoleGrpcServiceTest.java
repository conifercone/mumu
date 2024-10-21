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
import baby.mumu.authentication.client.api.grpc.RoleAddGrpcCmd;
import baby.mumu.authentication.client.api.grpc.RoleAddGrpcCo;
import baby.mumu.authentication.client.api.grpc.RoleDeleteByIdGrpcCmd;
import baby.mumu.authentication.client.api.grpc.RoleFindAllGrpcCmd;
import baby.mumu.authentication.client.api.grpc.RoleUpdateGrpcCmd;
import baby.mumu.authentication.client.api.grpc.RoleUpdateGrpcCo;
import baby.mumu.authentication.infrastructure.role.gatewayimpl.database.RoleRepository;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
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
  private static final Logger LOGGER = LoggerFactory.getLogger(RoleGrpcServiceTest.class);
  private final RoleRepository roleRepository;

  @Autowired
  public RoleGrpcServiceTest(RoleGrpcService roleGrpcService, MockMvc mockMvc,
      RoleRepository roleRepository) {
    this.roleGrpcService = roleGrpcService;
    this.mockMvc = mockMvc;
    this.roleRepository = roleRepository;
  }

  @Test
  public void add() {
    RoleAddGrpcCmd roleAddGrpcCmd = RoleAddGrpcCmd.newBuilder()
        .setRoleAddCo(
            RoleAddGrpcCo.newBuilder().setId(Int64Value.of(342504981))
                .setCode(StringValue.of("handling")).setName(
                    StringValue.of("handling"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
              () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    Empty empty = roleGrpcService.add(roleAddGrpcCmd,
        callCredentials);
    Assertions.assertNotNull(empty);
    roleRepository.deleteById(342504981L);
  }

  @Test
  public void syncAdd() throws InterruptedException {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    RoleAddGrpcCmd roleAddGrpcCmd = RoleAddGrpcCmd.newBuilder()
        .setRoleAddCo(
            RoleAddGrpcCo.newBuilder().setId(Int64Value.of(834315577))
                .setCode(StringValue.of("make")).setName(
                    StringValue.of("make"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
              () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<Empty> roleAddGrpcCoListenableFuture = roleGrpcService.syncAdd(
        roleAddGrpcCmd,
        callCredentials);
    roleAddGrpcCoListenableFuture.addListener(() -> {
      try {
        Empty empty = roleAddGrpcCoListenableFuture.get();
        Assertions.assertNotNull(empty);
        countDownLatch.countDown();
        roleRepository.deleteById(834315577L);
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = countDownLatch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
  }

  @Test
  public void deleteById() {
    RoleAddGrpcCmd roleAddGrpcCmd = RoleAddGrpcCmd.newBuilder()
        .setRoleAddCo(
            RoleAddGrpcCo.newBuilder().setId(Int64Value.of(1563908921))
                .setCode(StringValue.of("scsi")).setName(
                    StringValue.of("scsi"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
              () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    roleGrpcService.add(roleAddGrpcCmd,
        callCredentials);
    RoleDeleteByIdGrpcCmd roleDeleteByIdGrpcCmd = RoleDeleteByIdGrpcCmd.newBuilder()
        .setId(Int64Value.of(1563908921))
        .build();
    Empty empty = roleGrpcService.deleteById(
        roleDeleteByIdGrpcCmd,
        callCredentials);
    Assertions.assertNotNull(empty);
  }

  @Test
  public void syncDeleteById() throws InterruptedException {
    RoleAddGrpcCmd roleAddGrpcCmd = RoleAddGrpcCmd.newBuilder()
        .setRoleAddCo(
            RoleAddGrpcCo.newBuilder().setId(Int64Value.of(1669344979))
                .setCode(StringValue.of("phrases")).setName(
                    StringValue.of("phrases"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
              () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    roleGrpcService.add(roleAddGrpcCmd,
        callCredentials);
    CountDownLatch latch = new CountDownLatch(1);
    RoleDeleteByIdGrpcCmd roleDeleteByIdGrpcCmd = RoleDeleteByIdGrpcCmd.newBuilder()
        .setId(Int64Value.of(1669344979))
        .build();
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
  public void updateById() {
    RoleAddGrpcCmd roleAddGrpcCmd = RoleAddGrpcCmd.newBuilder()
        .setRoleAddCo(
            RoleAddGrpcCo.newBuilder().setId(Int64Value.of(982837132))
                .setCode(StringValue.of("spent")).setName(
                    StringValue.of("spent"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
              () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    roleGrpcService.add(roleAddGrpcCmd,
        callCredentials);
    RoleUpdateGrpcCmd roleUpdateGrpcCmd = RoleUpdateGrpcCmd.newBuilder()
        .setRoleUpdateCo(
            RoleUpdateGrpcCo.newBuilder().setId(Int64Value.of(982837132))
                .setName(StringValue.of("balanced"))
                .build())
        .build();
    Empty empty = roleGrpcService.updateById(
        roleUpdateGrpcCmd,
        callCredentials);
    Assertions.assertNotNull(empty);
    roleRepository.deleteById(982837132L);
  }

  @Test
  public void syncUpdateById() throws InterruptedException {
    RoleAddGrpcCmd roleAddGrpcCmd = RoleAddGrpcCmd.newBuilder()
        .setRoleAddCo(
            RoleAddGrpcCo.newBuilder().setId(Int64Value.of(243376658))
                .setCode(StringValue.of("blowing")).setName(
                    StringValue.of("blowing"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken(mockMvc).orElseThrow(
              () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    roleGrpcService.add(roleAddGrpcCmd,
        callCredentials);
    CountDownLatch latch = new CountDownLatch(1);
    RoleUpdateGrpcCmd roleUpdateGrpcCmd = RoleUpdateGrpcCmd.newBuilder()
        .setRoleUpdateCo(
            RoleUpdateGrpcCo.newBuilder().setId(Int64Value.of(243376658))
                .setName(StringValue.of("skills"))
                .build())
        .build();
    ListenableFuture<Empty> roleUpdateGrpcCoListenableFuture = roleGrpcService.syncUpdateById(
        roleUpdateGrpcCmd,
        callCredentials);
    roleUpdateGrpcCoListenableFuture.addListener(() -> {
      try {
        Empty empty = roleUpdateGrpcCoListenableFuture.get();
        Assertions.assertNotNull(empty);
        latch.countDown();
        roleRepository.deleteById(243376658L);
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = latch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
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
    LOGGER.info("PageOfRoleFindAllGrpcCo: {}", pageOfRoleFindAllGrpcCo);
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
