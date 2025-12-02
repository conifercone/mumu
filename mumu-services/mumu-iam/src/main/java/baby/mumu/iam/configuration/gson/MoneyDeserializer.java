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

package baby.mumu.iam.configuration.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Optional;
import org.javamoney.moneta.Money;

/**
 * Money 反序列化
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.4.0
 */
public class MoneyDeserializer implements JsonDeserializer<Money> {

  @Override
  public Money deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
    throws JsonParseException {
    return Optional.ofNullable(json).map(_ -> {
      JsonObject jsonObject = json.getAsJsonObject();
      String currency = jsonObject.get("currency").getAsString();
      String amountString = jsonObject.get("amount").getAsString();
      return Money.of(new BigDecimal(amountString), currency);  // 使用币种和金额创建 Money 对象
    }).orElse(null);
  }
}
