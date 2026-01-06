/*
 * Copyright (c) 2024-2026, the original author or authors.
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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.javamoney.moneta.Money;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Money 序列化
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.4.0
 */
public class MoneySerializer implements JsonSerializer<Money> {

    @Override
    public JsonElement serialize(Money money, Type typeOfSrc, JsonSerializationContext context) {
        return Optional.ofNullable(money).map(moneyNotNull -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("currency", moneyNotNull.getCurrency().getCurrencyCode());  // 币种
            jsonObject.addProperty("amount", moneyNotNull.getNumberStripped().toPlainString());  // 金额
            return jsonObject;
        }).orElse(null);
    }
}
