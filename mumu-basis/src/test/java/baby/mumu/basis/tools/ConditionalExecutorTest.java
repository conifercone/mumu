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

import baby.mumu.basis.condition.ConditionalExecutor;
import org.junit.jupiter.api.Test;

/**
 * ConditionalExecutor单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.1.0
 */
public class ConditionalExecutorTest {

  @Test
  public void test() {
    ConditionalExecutor.of(true).ifTrue(System.out::println, () -> "生成的消息").condition(true)
        .ifTrue(() -> System.out.println("测试消息"))
        .condition(false).ifFalse(() -> System.out.println("测试消息（条件不成立）"))
        .ifTrue(() -> System.out.println("测试消息（条件不成立）"));
  }

  @Test
  public void test2() {
    ConditionalExecutor.of(false).ifTrue(System.out::println, () -> "生成的消息");
  }

  @Test
  public void test3() {
    System.out.println(ConditionalExecutor.of(true).orElse(() -> {
          System.out.println("代码被执行，生成的消息。");
          return "这是执行的消息。"; // 返回消息
        },
        () -> "这是备用消息。"));
  }

  @Test
  public void test4() {
    System.out.println(ConditionalExecutor.of(false).orElse(() -> {
          System.out.println("代码被执行，生成的消息。");
          return "这是执行的消息。"; // 返回消息
        },
        () -> "这是备用消息。"));
  }

  @Test
  public void test5() {
    String test = "测试消息";
    ConditionalExecutor.of(true).ifTrue(() -> System.out.println(test));
  }

  @Test
  public void test6() {
    String test = "测试消息";
    ConditionalExecutor.of(false).ifTrue(() -> System.out.println(test));
  }

  @Test
  public void test7() {
    String successTest = "条件成立测试消息";
    String failTest = "条件不成立测试消息";
    Runnable successAction = () -> System.out.println(successTest);
    Runnable failAction = () -> System.out.println(failTest);
    ConditionalExecutor.of(true).ifTrueElse(successAction, failAction).condition(false)
        .ifTrueElse(successAction, failAction);
  }

  @Test
  public void test8() {
    String test = "测试消息";
    ConditionalExecutor.of(this::booleanSupplier).ifTrue(() -> System.out.println(test));
  }

  public boolean booleanSupplier() {
    return true;
  }
}
