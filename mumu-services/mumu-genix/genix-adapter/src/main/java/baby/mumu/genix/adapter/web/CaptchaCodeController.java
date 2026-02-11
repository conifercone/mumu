/*
 * Copyright (c) 2024-2026, the original author or authors.
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

package baby.mumu.genix.adapter.web;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.response.ResponseWrapper;
import baby.mumu.genix.client.api.CaptchaCodeService;
import baby.mumu.genix.client.cmds.CaptchaCodeGeneratedCmd;
import baby.mumu.genix.client.cmds.CaptchaCodeVerifyCmd;
import baby.mumu.genix.client.dto.CaptchaCodeGeneratedDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 验证码相关接口
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@RestController
@Validated
@RequestMapping("/captchaCode")
@Tag(name = "验证码管理")
public class CaptchaCodeController {

    private final CaptchaCodeService captchaCodeService;

    @Autowired
    public CaptchaCodeController(CaptchaCodeService captchaCodeService) {
        this.captchaCodeService = captchaCodeService;
    }

    @Operation(summary = "获取简单验证码")
    @GetMapping("/generate")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.1")
    public CaptchaCodeGeneratedDTO generate(
        @ParameterObject @ModelAttribute @Validated CaptchaCodeGeneratedCmd captchaCodeGeneratedCmd) {
        return captchaCodeService.generate(captchaCodeGeneratedCmd);
    }

    @Operation(summary = "验证简单验证码",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
            description = "验证码校验命令对象",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = CaptchaCodeVerifyCmd.class))))
    @PostMapping("/verify")
    @RateLimiter
    @API(status = Status.STABLE, since = "1.0.1")
    public ResponseWrapper<Boolean> verify(
        @RequestBody CaptchaCodeVerifyCmd captchaCodeVerifyCmd) {
        return ResponseWrapper.success(captchaCodeService.verify(captchaCodeVerifyCmd));
    }
}
