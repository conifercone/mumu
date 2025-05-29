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
package baby.mumu.mail.adapter.web;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.mail.client.api.TemplateMailService;
import baby.mumu.mail.client.cmds.TemplateMailSendCmd;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模板邮件管理
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@RestController
@Validated
@RequestMapping("/template/")
@Tag(name = "模板邮件管理")
public class TemplateMailController {

  private final TemplateMailService templateMailService;

  @Autowired
  public TemplateMailController(TemplateMailService templateMailService) {
    this.templateMailService = templateMailService;
  }

  @Operation(summary = "发送模板邮件")
  @PostMapping("/send")
  @ResponseBody
  @RateLimiter
  @API(status = Status.STABLE, since = "1.0.1")
  public void sendMail(@RequestBody TemplateMailSendCmd templateMailSendCmd) {
    templateMailService.sendMail(templateMailSendCmd);
  }
}
