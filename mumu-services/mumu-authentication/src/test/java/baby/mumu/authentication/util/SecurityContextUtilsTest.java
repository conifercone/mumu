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
package baby.mumu.authentication.util;

import baby.mumu.basis.enums.LanguageEnum;
import baby.mumu.basis.kotlin.tools.SecurityContextUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

/**
 * SecurityContextUtil单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@WithUserDetails(value = "admin", userDetailsServiceBeanName = "userDetailsService")
public class SecurityContextUtilsTest {

  private static final Logger logger = LoggerFactory.getLogger(
    SecurityContextUtilsTest.class);

  @Test
  public void loginAccountId() {
    Long accountId = SecurityContextUtils.getLoginAccountId().orElse(null);
    Assertions.assertNotNull(accountId);
    logger.info("loginAccountId: {}", accountId);
  }

  @Test
  public void loginAccountName() {
    String accountName = SecurityContextUtils.getLoginAccountName().orElse(null);
    Assertions.assertNotNull(accountName);
    logger.info("loginAccountName: {}", accountName);
  }

  @Test
  public void loginAccountLanguage() {
    LanguageEnum languageEnum = SecurityContextUtils.getLoginAccountLanguage().orElse(null);
    Assertions.assertNotNull(languageEnum);
    logger.info("languageEnum: {}", languageEnum);
  }

}
