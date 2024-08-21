/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
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
package com.sky.centaur.authentication.util;

import com.sky.centaur.basis.enums.LanguageEnum;
import com.sky.centaur.basis.kotlin.tools.SecurityContextUtil;
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
public class SecurityContextUtilTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      SecurityContextUtilTest.class);

  @Test
  public void loginAccountId() {
    Long accountId = SecurityContextUtil.getLoginAccountId().orElse(null);
    Assertions.assertNotNull(accountId);
    LOGGER.info("loginAccountId: {}", accountId);
  }

  @Test
  public void loginAccountLanguage() {
    LanguageEnum languageEnum = SecurityContextUtil.getLoginAccountLanguage().orElse(null);
    Assertions.assertNotNull(languageEnum);
    LOGGER.info("languageEnum: {}", languageEnum);
  }

}
