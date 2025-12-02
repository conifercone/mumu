/*
 * Copyright (c) 2024-2025, the original author or authors.
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

package baby.mumu.iam.client.grpc;

import baby.mumu.basis.constants.SpringBootConstants;
import baby.mumu.iam.AuthenticationRequired;
import baby.mumu.iam.IAMApplicationMetamodel;
import baby.mumu.iam.client.api.RoleGrpcService;
import baby.mumu.iam.client.api.grpc.PageOfRoleFindAllGrpcDTO;
import baby.mumu.iam.client.api.grpc.RoleFindAllGrpcCmd;
import baby.mumu.iam.client.api.grpc.RoleFindByIdGrpcDTO;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * RoleGrpcService单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@Import(GrpcSecurityTestConfiguration.class)
@TestPropertySource(properties = {
  SpringBootConstants.APPLICATION_TITLE + "=" + IAMApplicationMetamodel.PROJECT_NAME,
  SpringBootConstants.APPLICATION_FORMATTED_VERSION + "="
    + IAMApplicationMetamodel.FORMATTED_PROJECT_VERSION,
})
public class RoleGrpcServiceTest extends AuthenticationRequired {

  private final RoleGrpcService roleGrpcService;
  private static final Logger log = LoggerFactory.getLogger(RoleGrpcServiceTest.class);

  @Autowired
  public RoleGrpcServiceTest(RoleGrpcService roleGrpcService) {
    this.roleGrpcService = roleGrpcService;
  }

  @Test
  public void findAll() {
    RoleFindAllGrpcCmd roleFindAllGrpcCmd = RoleFindAllGrpcCmd.newBuilder()
      .setName(StringValue.of("管理员"))
      .setCurrent(Int32Value.of(1))
      .setPageSize(Int32Value.of(10))
      .build();

    PageOfRoleFindAllGrpcDTO pageOfRoleFindAllGrpcDTO = roleGrpcService.findAll(
      roleFindAllGrpcCmd
    );
    RoleGrpcServiceTest.log.info("PageOfRoleFindAllGrpcDTO: {}", pageOfRoleFindAllGrpcDTO);
    Assertions.assertNotNull(pageOfRoleFindAllGrpcDTO);
    Assertions.assertFalse(pageOfRoleFindAllGrpcDTO.getContentList().isEmpty());
  }

  @Test
  public void syncFindAll() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    RoleFindAllGrpcCmd roleFindAllGrpcCmd = RoleFindAllGrpcCmd.newBuilder()
      .setName(StringValue.of("管理员"))
      .setCurrent(Int32Value.of(1))
      .setPageSize(Int32Value.of(10))
      .build();

    ListenableFuture<PageOfRoleFindAllGrpcDTO> pageOfRoleFindAllGrpcDTOListenableFuture = roleGrpcService.syncFindAll(
      roleFindAllGrpcCmd
    );
    pageOfRoleFindAllGrpcDTOListenableFuture.addListener(() -> {
      try {
        PageOfRoleFindAllGrpcDTO pageOfRoleFindAllGrpcDTO = pageOfRoleFindAllGrpcDTOListenableFuture.get();
        RoleGrpcServiceTest.log.info("Sync PageOfRoleFindAllGrpcDTO: {}", pageOfRoleFindAllGrpcDTO);
        Assertions.assertNotNull(pageOfRoleFindAllGrpcDTO);
        Assertions.assertFalse(pageOfRoleFindAllGrpcDTO.getContentList().isEmpty());
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

    RoleFindByIdGrpcDTO roleFindByIdGrpcDTO = roleGrpcService.findById(
      Int64Value.of(0L)
    );
    RoleGrpcServiceTest.log.info("RoleFindByIdGrpcDTO: {}", roleFindByIdGrpcDTO);
    Assertions.assertNotNull(roleFindByIdGrpcDTO);
  }

  @Test
  public void syncFindById() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);

    ListenableFuture<RoleFindByIdGrpcDTO> roleFindByIdGrpcDTOListenableFuture = roleGrpcService.syncFindById(
      Int64Value.of(0L)
    );
    roleFindByIdGrpcDTOListenableFuture.addListener(() -> {
      try {
        RoleFindByIdGrpcDTO roleFindByIdGrpcDTO = roleFindByIdGrpcDTOListenableFuture.get();
        RoleGrpcServiceTest.log.info("Sync RoleFindByIdGrpcDTO: {}", roleFindByIdGrpcDTO);
        Assertions.assertNotNull(roleFindByIdGrpcDTO);
        latch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = latch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
  }
}
