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

package com.sky.centaur.basis;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import com.sky.centaur.basis.kotlin.tools.SpringContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 基础模块配置类
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@Configuration
public class BasisConfiguration {

  @Bean
  @Order(HIGHEST_PRECEDENCE)
  public SpringContextUtil springContextUtil() {
    return new SpringContextUtil();
  }
}
