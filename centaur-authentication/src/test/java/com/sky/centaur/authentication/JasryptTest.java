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
package com.sky.centaur.authentication;

import jakarta.annotation.Resource;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 配置文件敏感信息加密测试
 *
 * @author 单开宇
 * @since 2024-01-12
 */
@SpringBootTest
public class JasryptTest {

  Logger LOGGER = LoggerFactory.getLogger(JasryptTest.class);

  @Resource
  private StringEncryptor stringEncryptor;

  @Test
  void contextLoads() {
    String username = stringEncryptor.encrypt("admin");
    String password = stringEncryptor.encrypt("admin");
    LOGGER.info("username encrypt is {}", username);
    LOGGER.info("password encrypt is {}", password);
    LOGGER.info("username decrypt is {}", stringEncryptor.decrypt(username));
    LOGGER.info("password decrypt is {}", stringEncryptor.decrypt(password));
  }
}
