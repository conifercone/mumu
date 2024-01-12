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
