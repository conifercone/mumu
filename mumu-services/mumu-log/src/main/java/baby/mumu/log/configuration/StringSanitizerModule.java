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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Strings;
import java.io.IOException;
import java.io.Serial;
import org.springframework.stereotype.Component;

/**
 * jackson空字符串转NULL模块
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.0
 */
@Component
public class StringSanitizerModule extends SimpleModule {

  @Serial
  private static final long serialVersionUID = -8746350422998765852L;

  public StringSanitizerModule() {
    addDeserializer(String.class, new StdScalarDeserializer<>(String.class) {
      @Serial
      private static final long serialVersionUID = -3447488199252754578L;

      @Override
      public String deserialize(JsonParser jsonParser, DeserializationContext ctx)
        throws IOException {
        return Strings.emptyToNull(jsonParser.getValueAsString().trim());
      }
    });
  }
}
