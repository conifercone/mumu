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
package com.sky.centaur.message;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import com.sky.centaur.basis.constants.BeanNameConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 消息服务
 *
 * @author kaiyu.shan
 * @since 1.0.2
 */
@SpringBootApplication
@EnableConfigurationProperties
@EnableJpaAuditing(auditorAwareRef = BeanNameConstants.CENTAUR_JPA_AUDITOR_AWARE)
@EnableRedisDocumentRepositories(basePackages = "com.sky.centaur.message.infrastructure.**")
@EnableMethodSecurity
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@EnableTransactionManagement
public class CentaurMessageApplication {

  public static void main(String[] args) {
    SpringApplication.run(CentaurMessageApplication.class, args);
  }
}
