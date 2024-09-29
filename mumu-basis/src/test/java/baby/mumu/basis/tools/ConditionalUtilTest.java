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

import org.junit.jupiter.api.Test;

/**
 * ConditionalUtil单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.1.0
 */
public class ConditionalUtilTest {

  @Test
  public void test() {
    ConditionalUtil.execute(true, (res) -> System.out.println(res), () -> "生成的消息");
  }

  @Test
  public void test2() {
    ConditionalUtil.execute(false, (res) -> System.out.println(res), () -> "生成的消息");
  }

  @Test
  public void test3() {
    System.out.println(ConditionalUtil.execute(true, () -> {
          System.out.println("代码被执行，生成的消息。");
          return "这是执行的消息。"; // 返回消息
        },
        () -> "这是备用消息。"));
  }

  @Test
  public void test4() {
    System.out.println(ConditionalUtil.execute(false, () -> {
          System.out.println("代码被执行，生成的消息。");
          return "这是执行的消息。"; // 返回消息
        },
        () -> "这是备用消息。"));
  }
}
