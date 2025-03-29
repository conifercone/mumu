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

import baby.mumu.basis.kotlin.tools.PhoneUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 手机号工具类测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.7.0
 */
public class PhoneUtilsTest {

  @Test
  public void isValidPhoneNumber() {
    boolean validPhoneNumber = PhoneUtils.isValidPhoneNumber("13031723736", "+86");
    Assertions.assertTrue(validPhoneNumber);
    boolean validPhoneNumber2 = PhoneUtils.isValidPhoneNumber("5486560", "+86");
    Assertions.assertFalse(validPhoneNumber2);
    boolean validPhoneNumber3 = PhoneUtils.isValidPhoneNumber("2132901064", "+1");
    Assertions.assertTrue(validPhoneNumber3);
  }
}
