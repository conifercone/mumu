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
package baby.mumu.basis.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.intellij.lang.annotations.Language;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.zalando.jackson.datatype.money.MoneyModule;

/**
 * jackson test
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.4.0
 */
public class JacksonTest {

  @Test
  public void test() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new MoneyModule().withQuotedDecimalNumbers());
    @Language("JSON") String json = """
      {
         "amount": "0.00",
         "currency": "USD"
      }
      """;
    Money money = objectMapper.readValue(json, Money.class);
    String written = objectMapper.writeValueAsString(money);
    System.out.println(written);
    System.out.println(money);
  }
}
