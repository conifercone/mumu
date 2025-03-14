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

import baby.mumu.basis.kotlin.tools.EmailUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 邮箱工具类测试
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 2.7.0
 */
public class EmailUtilsTest {

  @Test
  public void isValidEmailFormat() {
    boolean validEmailFormat = EmailUtils.isValidEmailFormat("kaiyu.shan@mumu.baby");
    Assertions.assertTrue(validEmailFormat);
    boolean validEmailFormat1 = EmailUtils.isValidEmailFormat("123456abc");
    Assertions.assertFalse(validEmailFormat1);
    boolean validEmailFormat2 = EmailUtils.isValidEmailFormat("kaiyu.shan.yz@gmail.com");
    Assertions.assertTrue(validEmailFormat2);
  }
}
