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
package baby.mumu.mail.application.template.executor;

import baby.mumu.mail.client.dto.TemplateMailSendCmd;
import baby.mumu.mail.domain.template.gateway.TemplateMailGateway;
import baby.mumu.mail.infrastructure.template.convertor.TemplateMailConvertor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 模板邮件发送指令执行器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Component
public class TemplateMailSendCmdExe {

  private final TemplateMailGateway templateMailGateway;
  private final TemplateMailConvertor templateMailConvertor;

  @Value("${spring.mail.username}")
  private String username;

  @Autowired
  public TemplateMailSendCmdExe(TemplateMailGateway templateMailGateway,
    TemplateMailConvertor templateMailConvertor) {
    this.templateMailGateway = templateMailGateway;
    this.templateMailConvertor = templateMailConvertor;
  }

  public void execute(TemplateMailSendCmd templateMailSendCmd) {
    Assert.notNull(templateMailSendCmd, "TemplateMailSendCmd cannot be null");
    if (StringUtils.isBlank(templateMailSendCmd.getTemplateMailSendCo().getFrom())) {
      templateMailSendCmd.getTemplateMailSendCo().setFrom(username);
    }
    templateMailConvertor.toEntity(templateMailSendCmd.getTemplateMailSendCo())
      .ifPresent(templateMailGateway::sendMail);
  }
}
