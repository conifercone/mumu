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
import baby.mumu.iam.client.api.AccountGrpcService;
import baby.mumu.iam.client.api.grpc.AccountCurrentLoginGrpcDTO;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * AccountGrpcService单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@Import(GrpcSecurityTestConfiguration.class)
@AutoConfigureMockMvc
@WithUserDetails(value = "admin", userDetailsServiceBeanName = "userDetailsService")
@TestPropertySource(properties = {
  SpringBootConstants.APPLICATION_TITLE + "=" + IAMApplicationMetamodel.PROJECT_NAME,
  SpringBootConstants.APPLICATION_FORMATTED_VERSION + "="
    + IAMApplicationMetamodel.FORMATTED_PROJECT_VERSION,
})
public class AccountGrpcServiceTest extends AuthenticationRequired {

  private final AccountGrpcService accountGrpcService;
  private static final Logger log = LoggerFactory.getLogger(AccountGrpcServiceTest.class);

  @Autowired
  public AccountGrpcServiceTest(AccountGrpcService accountGrpcService) {
    this.accountGrpcService = accountGrpcService;
  }

  @Test
  public void queryCurrentLoginAccount() {
    AccountCurrentLoginGrpcDTO accountCurrentLoginGrpcDTO = accountGrpcService.queryCurrentLoginAccount();
    Assertions.assertNotNull(accountCurrentLoginGrpcDTO);
    AccountGrpcServiceTest.log.info("AccountCurrentLoginGrpcDTO:{}", accountCurrentLoginGrpcDTO);
  }

  @Test
  public void syncQueryCurrentLoginAccount() {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    ListenableFuture<AccountCurrentLoginGrpcDTO> accountCurrentLoginGrpcDTOListenableFuture = accountGrpcService.syncQueryCurrentLoginAccount(
    );
    accountCurrentLoginGrpcDTOListenableFuture.addListener(() -> {
      try {
        AccountCurrentLoginGrpcDTO accountCurrentLoginGrpcDTO = accountCurrentLoginGrpcDTOListenableFuture.get();
        Assertions.assertNotNull(accountCurrentLoginGrpcDTO);
        AccountGrpcServiceTest.log.info("Sync AccountCurrentLoginGrpcDTO:{}",
          accountCurrentLoginGrpcDTO);
        countDownLatch.countDown();
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
  }

}
