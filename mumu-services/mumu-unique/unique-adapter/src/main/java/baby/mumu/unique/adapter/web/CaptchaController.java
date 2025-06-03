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

package baby.mumu.unique.adapter.web;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.response.ResponseWrapper;
import baby.mumu.unique.client.api.CaptchaService;
import baby.mumu.unique.client.cmds.SimpleCaptchaGeneratedCmd;
import baby.mumu.unique.client.cmds.SimpleCaptchaVerifyCmd;
import baby.mumu.unique.client.dto.SimpleCaptchaGeneratedDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码相关接口
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@RestController
@Validated
@RequestMapping("/captcha")
@Tag(name = "验证码管理")
public class CaptchaController {

  private final CaptchaService captchaService;

  @Autowired
  public CaptchaController(CaptchaService captchaService) {
    this.captchaService = captchaService;
  }

  @Operation(summary = "获取简单验证码")
  @GetMapping("/simple")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.1")
  public SimpleCaptchaGeneratedDTO simple(
    @ModelAttribute @Validated SimpleCaptchaGeneratedCmd simpleCaptchaGeneratedCmd) {
    return captchaService.generateSimpleCaptcha(simpleCaptchaGeneratedCmd);
  }

  @Operation(summary = "验证简单验证码")
  @PostMapping("/simple/verify")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.1")
  public ResponseWrapper<Boolean> verifySimple(
    @RequestBody SimpleCaptchaVerifyCmd simpleCaptchaVerifyCmd) {
    return ResponseWrapper.success(captchaService.verifySimpleCaptcha(simpleCaptchaVerifyCmd));
  }
}
