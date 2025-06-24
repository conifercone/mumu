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

package baby.mumu.unique.client.grpc;

import baby.mumu.unique.client.api.PrimaryKeyGrpcService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 主键生成grpc接口单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class PrimaryKeyGrpcServiceTest {

  private final PrimaryKeyGrpcService primaryKeyGrpcService;
  private static final Logger log = LoggerFactory.getLogger(PrimaryKeyGrpcServiceTest.class);

  @Autowired
  public PrimaryKeyGrpcServiceTest(PrimaryKeyGrpcService primaryKeyGrpcService) {
    this.primaryKeyGrpcService = primaryKeyGrpcService;
  }

  @Test
  public void snowflake() {
    Long snowflake = primaryKeyGrpcService.snowflake();
    PrimaryKeyGrpcServiceTest.log.info("snowflake : {}", snowflake);
    Assertions.assertNotNull(snowflake);
  }
}
