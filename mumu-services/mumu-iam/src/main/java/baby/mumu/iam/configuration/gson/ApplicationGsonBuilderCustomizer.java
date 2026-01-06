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

import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.redis.om.spring.serialization.gson.GsonBuilderCustomizer;
import org.javamoney.moneta.Money;
import org.jspecify.annotations.NonNull;

/**
 * gson 构建器定制器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.4.0
 */
public class ApplicationGsonBuilderCustomizer implements GsonBuilderCustomizer {

    @Override
    public void customize(@NonNull GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeAdapter(Money.class, new MoneySerializer())
            .registerTypeAdapter(Money.class, new MoneyDeserializer());
        gsonBuilder.setLongSerializationPolicy(LongSerializationPolicy.STRING);
    }
}
