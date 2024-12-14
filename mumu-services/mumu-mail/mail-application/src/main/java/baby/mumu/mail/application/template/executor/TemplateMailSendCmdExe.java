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

import baby.mumu.mail.client.cmds.TemplateMailSendCmd;
import baby.mumu.mail.domain.template.gateway.TemplateMailGateway;
import baby.mumu.mail.infrastructure.template.convertor.TemplateMailConvertor;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    Optional.ofNullable(templateMailSendCmd).ifPresent(templateMailSendCmdNotNull -> {
      if (StringUtils.isBlank(templateMailSendCmd.getFrom())) {
        templateMailSendCmd.setFrom(username);
      }
      templateMailConvertor.toEntity(templateMailSendCmd)
        .ifPresent(templateMailGateway::sendMail);
    });
  }
}
