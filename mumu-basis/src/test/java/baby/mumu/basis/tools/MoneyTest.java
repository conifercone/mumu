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

package baby.mumu.basis.tools;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import javax.money.MonetaryContext;
import javax.money.MonetaryContextBuilder;
import java.math.BigDecimal;

/**
 * Money 单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.4.0
 */
public class MoneyTest {

    @Test
    public void test() {
        MonetaryContext build = MonetaryContextBuilder.of().setMaxScale(2).build();
        Money money = Money.of(new BigDecimal("1000.00"), "USD", build);
        Money money2 = Money.of(new BigDecimal("2000.00"), "USD", build);
        System.out.println(money.add(money2)); // 加法
        System.out.println(money.subtract(money2)); // 减法
        System.out.println(money.multiply(2)); // 乘法
        System.out.println(money.divide(3)); // 除法
    }
}
