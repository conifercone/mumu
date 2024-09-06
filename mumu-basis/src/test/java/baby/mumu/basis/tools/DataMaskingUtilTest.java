/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
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

import baby.mumu.basis.kotlin.tools.DataMaskingUtil;
import org.junit.jupiter.api.Test;

/**
 * DataMaskingUtil单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
public class DataMaskingUtilTest {

  @Test
  public void maskPhoneNumber() {
    String phone = "13812345678";
    System.out.println(DataMaskingUtil.maskPhoneNumber(phone));
  }

  @Test
  public void maskIdCard() {
    String idCard = "123456789012345678";
    System.out.println(DataMaskingUtil.maskIdCard(idCard));
  }

  @Test
  public void maskEmail() {
    String email = "test@example.com";
    System.out.println(DataMaskingUtil.maskEmail(email));
  }

  @Test
  public void maskName() {
    String name = "张三丰";
    System.out.println(DataMaskingUtil.maskName(name));
  }

  @Test
  public void maskBankCard() {
    String bankCard = "6222021234567890";
    System.out.println(DataMaskingUtil.maskBankCard(bankCard));
  }

  @Test
  public void maskData() {
    System.out.println(DataMaskingUtil.maskData("12345678901234567890", 4, 4));
  }
}
