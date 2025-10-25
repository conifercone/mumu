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

package baby.mumu.genix.application.captcha.executor;

import baby.mumu.genix.domain.captcha.gateway.CaptchaCodeGateway;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 简单验证码删除指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.15.0
 */
@Component
public class CaptchaCodeDeleteCmdExe {

  private final CaptchaCodeGateway captchaCodeGateway;

  @Autowired
  public CaptchaCodeDeleteCmdExe(
    CaptchaCodeGateway captchaCodeGateway) {
    this.captchaCodeGateway = captchaCodeGateway;
  }

  public void execute(Long captchaCodeId) {
    Optional.ofNullable(captchaCodeId).ifPresent(captchaCodeGateway::delete);
  }
}
