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
package baby.mumu.basis.tools;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 条件工具类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.1.0
 */
public class ConditionalUtil {

  /**
   * 根据条件判断是否执行给定的代码块（无返回值）。 如果条件成立，使用 supplier 的返回值作为参数执行 action。
   *
   * @param condition 布尔值，决定是否执行代码块
   * @param action    要执行的代码块
   * @param supplier  供应者，用于生成参数
   */
  public static <T> void execute(boolean condition, Consumer<T> action,
      Supplier<T> supplier) {
    if (condition) {
      action.accept(supplier.get());
    }
  }

  /**
   * 根据条件判断是否执行给定的代码块（有返回值）。
   *
   * @param condition    布尔值，决定是否执行代码块
   * @param action       要执行的生产者
   * @param defaultValue 备用值的生产者
   * @param <T>          返回值的类型
   * @return 执行结果
   */
  public static <T> T execute(boolean condition, Supplier<T> action, Supplier<T> defaultValue) {
    if (condition) {
      return action.get();
    } else {
      return defaultValue.get();
    }
  }
}
