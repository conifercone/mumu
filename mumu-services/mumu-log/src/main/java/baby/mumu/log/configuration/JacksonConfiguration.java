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

package baby.mumu.log.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.jackson.datatype.money.MoneyModule;

/**
 * jackson配置
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@Configuration
public class JacksonConfiguration {

  @Bean
  public ObjectMapper objectMapper(StringSanitizerModule stringSanitizerModule) {
    return JsonMapper.builder()
      // 注册自定义模块
      .addModule(stringSanitizerModule)
      // 时间模块
      .addModule(new JavaTimeModule())
      // Money 模块（带 quoted decimal numbers）
      .addModule(new MoneyModule().withQuotedDecimalNumbers())
      // Long -> String 序列化（避免 JS 精度丢失）
      .addModule(new SimpleModule()
        .addSerializer(Long.class, ToStringSerializer.instance)
        .addSerializer(Long.TYPE, ToStringSerializer.instance))
      // 关闭时间戳写日期
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .build();
  }
}
