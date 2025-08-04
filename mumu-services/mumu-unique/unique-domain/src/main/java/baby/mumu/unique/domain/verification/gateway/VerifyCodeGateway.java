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

package baby.mumu.unique.domain.verification.gateway;

import baby.mumu.unique.domain.verification.VerifyCode;

/**
 * 验证码领域网关
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
public interface VerifyCodeGateway {

  /**
   * 生成指定长度的简单验证码
   *
   * @param verifyCode 简单验证码
   * @return 验证码ID
   */
  Long generate(VerifyCode verifyCode);


  /**
   * 验证简单验证码
   *
   * @param verifyCode 简单验证码
   * @return 验证结果
   */
  boolean verify(VerifyCode verifyCode);

}
