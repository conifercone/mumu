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

package com.sky.centaur.log.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Strings;
import java.io.IOException;
import org.springframework.stereotype.Component;

/**
 * jackson空字符串转NULL模块
 *
 * @author kaiyu.shan
 * @since 2024-02-02
 */
@Component
public class StringSanitizerModule extends SimpleModule {

  public StringSanitizerModule() {
    addDeserializer(String.class, new StdScalarDeserializer<>(String.class) {
      @Override
      public String deserialize(JsonParser jsonParser, DeserializationContext ctx)
          throws IOException {
        return Strings.emptyToNull(jsonParser.getValueAsString().trim());
      }
    });
  }
}
