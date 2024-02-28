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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 鉴权服务
 *
 * @author 单开宇
 * @since 2024-01-09
 */
@SpringBootApplication
@EnableConfigurationProperties
@EnableJpaAuditing(auditorAwareRef = "centaurJpaAuditorAware")
public class CentaurAuthenticationApplication {

  public static void main(String[] args) {
    SpringApplication.run(CentaurAuthenticationApplication.class, args);
  }
}
