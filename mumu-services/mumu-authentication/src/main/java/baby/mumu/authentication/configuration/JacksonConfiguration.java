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
package baby.mumu.authentication.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.jackson.datatype.money.MoneyModule;

/**
 * jackson配置
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
@Configuration
public class JacksonConfiguration {

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.registerModule(new MoneyModule().withQuotedDecimalNumbers());
    SimpleModule simpleModule = new SimpleModule();
    simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
    objectMapper.registerModule(simpleModule);
    return objectMapper;
  }
}
