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

package baby.mumu.genix.client.api;

import baby.mumu.genix.client.cmds.CaptchaCodeGeneratedCmd;
import baby.mumu.genix.client.cmds.CaptchaCodeVerifyCmd;
import baby.mumu.genix.client.dto.CaptchaCodeGeneratedDTO;

/**
 * 验证码service
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
public interface CaptchaCodeService {

  /**
   * 验证码生成
   *
   * @param captchaCodeGeneratedCmd 验证码生成指令
   * @return 验证码
   */
  CaptchaCodeGeneratedDTO generate(
    CaptchaCodeGeneratedCmd captchaCodeGeneratedCmd);

  /**
   * 验证码校验
   *
   * @param captchaCodeVerifyCmd 验证码校验指令
   * @return 校验结果
   */
  boolean verify(CaptchaCodeVerifyCmd captchaCodeVerifyCmd);

  /**
   * 根据ID删除验证码
   *
   * @param captchaCodeId 验证码ID
   */
  void delete(Long captchaCodeId);
}
