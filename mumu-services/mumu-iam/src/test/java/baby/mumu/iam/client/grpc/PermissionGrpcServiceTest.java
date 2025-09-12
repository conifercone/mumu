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
import baby.mumu.iam.MuMuIAMApplicationMetamodel;
import baby.mumu.iam.client.api.PermissionGrpcService;
import baby.mumu.iam.client.api.grpc.PageOfPermissionFindAllGrpcDTO;
import baby.mumu.iam.client.api.grpc.PermissionFindAllGrpcCmd;
import baby.mumu.iam.client.api.grpc.PermissionFindByIdGrpcDTO;
import baby.mumu.iam.client.api.grpc.PermissionGrpcDTO;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * PermissionGrpcService单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@Import(GrpcSecurityTestConfiguration.class)
@TestPropertySource(properties = {
  SpringBootConstants.APPLICATION_TITLE + "=" + MuMuIAMApplicationMetamodel.PROJECT_NAME,
  SpringBootConstants.APPLICATION_FORMATTED_VERSION + "="
    + MuMuIAMApplicationMetamodel.FORMATTED_PROJECT_VERSION,
})
public class PermissionGrpcServiceTest extends AuthenticationRequired {

  private final PermissionGrpcService permissionGrpcService;
  private static final Logger log = LoggerFactory.getLogger(PermissionGrpcServiceTest.class);

  @Autowired
  public PermissionGrpcServiceTest(PermissionGrpcService permissionGrpcService) {
    this.permissionGrpcService = permissionGrpcService;
  }

  @Test
  public void findAll() {
    PermissionFindAllGrpcCmd permissionFindAllGrpcCmd = PermissionFindAllGrpcCmd.newBuilder()
      .setName(StringValue.of("数据"))
      .setCurrent(Int32Value.of(1))
      .setPageSize(Int32Value.of(10))
      .build();
    PageOfPermissionFindAllGrpcDTO pageOfPermissionGrpcDTO = permissionGrpcService.findAll(
      permissionFindAllGrpcCmd
    );
    PermissionGrpcServiceTest.log.info("PageOfPermissionFindAllGrpcDTO: {}",
      pageOfPermissionGrpcDTO);
    pageOfPermissionGrpcDTO.getContentList().stream().map(PermissionGrpcDTO::getName)
      .forEach(PermissionGrpcServiceTest.log::info);
    Assertions.assertNotNull(pageOfPermissionGrpcDTO);
  }

  @Test
  public void syncFindAll() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    PermissionFindAllGrpcCmd permissionFindAllGrpcCmd = PermissionFindAllGrpcCmd.newBuilder()
      .setName(StringValue.of("数据"))
      .setCurrent(Int32Value.of(1))
      .setPageSize(Int32Value.of(10))
      .build();
    ListenableFuture<PageOfPermissionFindAllGrpcDTO> pageOfPermissionFindAllGrpcDTOListenableFuture = permissionGrpcService.syncFindAll(
      permissionFindAllGrpcCmd
    );
    pageOfPermissionFindAllGrpcDTOListenableFuture.addListener(() -> {
      try {
        PageOfPermissionFindAllGrpcDTO pageOfPermissionFindAllGrpcDTO = pageOfPermissionFindAllGrpcDTOListenableFuture.get();
        PermissionGrpcServiceTest.log.info("Sync PageOfPermissionFindAllGrpcDTO: {}",
          pageOfPermissionFindAllGrpcDTO);
        Assertions.assertNotNull(pageOfPermissionFindAllGrpcDTO);
        Assertions.assertFalse(pageOfPermissionFindAllGrpcDTO.getContentList().isEmpty());
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
    PermissionFindByIdGrpcDTO permissionFindByIdGrpcDTO = permissionGrpcService.findById(
      Int64Value.of(1)
    );
    PermissionGrpcServiceTest.log.info("PermissionFindByIdGrpcDTO: {}", permissionFindByIdGrpcDTO);
    Assertions.assertNotNull(permissionFindByIdGrpcDTO);
  }

  @Test
  public void syncFindById() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    ListenableFuture<PermissionFindByIdGrpcDTO> permissionFindByIdGrpcDTOListenableFuture = permissionGrpcService.syncFindById(
      Int64Value.of(1)
    );
    permissionFindByIdGrpcDTOListenableFuture.addListener(() -> {
      try {
        PermissionFindByIdGrpcDTO permissionFindByIdGrpcDTO = permissionFindByIdGrpcDTOListenableFuture.get();
        PermissionGrpcServiceTest.log.info("Sync PermissionFindByIdGrpcDTO: {}",
          permissionFindByIdGrpcDTO);
        Assertions.assertNotNull(permissionFindByIdGrpcDTO);
        latch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = latch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
  }
}
