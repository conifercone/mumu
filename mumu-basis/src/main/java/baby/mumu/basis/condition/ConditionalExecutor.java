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

package baby.mumu.basis.condition;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

/**
 * 条件执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.1.0
 */
public class ConditionalExecutor {

  private boolean condition;

  private ConditionalExecutor(boolean condition) {
    this.condition = condition;
  }

  @Contract(value = "_ -> new", pure = true)
  public static @NonNull ConditionalExecutor of(boolean condition) {
    return new ConditionalExecutor(condition);
  }

  @Contract("_, _ -> new")
  public static <T> @NonNull ConditionalExecutor of(@NonNull Predicate<T> predicate,
    @NonNull Supplier<T> supplier) {
    return new ConditionalExecutor(predicate.test(supplier.get()));
  }

  @Contract(value = "_ -> new", pure = true)
  public static @NonNull ConditionalExecutor of(@NonNull BooleanSupplier booleanSupplier) {
    return new ConditionalExecutor(booleanSupplier.getAsBoolean());
  }

  public ConditionalExecutor condition(boolean condition) {
    this.condition = condition;
    return this;
  }

  public ConditionalExecutor condition(@NonNull BooleanSupplier booleanSupplier) {
    this.condition = booleanSupplier.getAsBoolean();
    return this;
  }

  public <T> ConditionalExecutor condition(@NonNull Predicate<T> predicate,
    @NonNull Supplier<T> supplier) {
    this.condition = predicate.test(supplier.get());
    return this;
  }

  /**
   * 根据条件判断是否执行给定的代码块（无返回值）。 如果条件成立，使用 supplier 的返回值作为参数执行 action。
   *
   * @param action   要执行的代码块
   * @param supplier 供应者，用于生成参数
   */
  public <T> ConditionalExecutor ifTrue(Consumer<T> action, Supplier<T> supplier) {
    if (condition) {
      action.accept(supplier.get());
    }
    return this;
  }

  /**
   * 根据条件判断是否执行给定的代码块（无返回值, 条件成立）.
   *
   * @param action 要执行的代码块
   */
  public ConditionalExecutor ifTrue(Runnable action) {
    if (condition) {
      action.run();
    }
    return this;
  }

  /**
   * 根据条件判断是否执行给定的代码块（无返回值, 条件不成立）.
   *
   * @param action 要执行的代码块
   */
  public ConditionalExecutor ifFalse(Runnable action) {
    if (!condition) {
      action.run();
    }
    return this;
  }

  /**
   * 根据条件判断是否执行给定的代码块（有返回值）。
   *
   * @param action       要执行的生产者
   * @param defaultValue 备用值的生产者
   * @param <T>          返回值的类型
   * @return 执行结果
   */
  public <T> T orElseGet(Supplier<T> action, Supplier<T> defaultValue) {
    return condition ? action.get() : defaultValue.get();
  }

  /**
   * 根据条件判断需要返回的值。
   *
   * @param successValue 条件判断成功返回值
   * @param defaultValue 条件判断失败返回备用值
   * @param <T>          返回值的类型
   * @return 执行结果
   */
  public <T> T orElse(T successValue, T defaultValue) {
    return condition ? successValue : defaultValue;
  }

  /**
   * 根据条件判断是否执行给定的代码块（无返回值）.
   *
   * @param successAction 条件成立要执行的代码块
   * @param failAction    条件不成立要执行的代码块
   */
  public ConditionalExecutor ifTrueElse(Runnable successAction, Runnable failAction) {
    if (condition) {
      successAction.run();
    } else {
      failAction.run();
    }
    return this;
  }
}
