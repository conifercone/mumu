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
package com.sky.centaur.mail.infrastructure.template.gatewayimpl.thymeleaf;

import com.sky.centaur.mail.infrastructure.template.gatewayimpl.thymeleaf.dataobject.TemplateMailThymeleafDo;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Thymeleaf模板邮件操作类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Component
public class ThymeleafTemplateMailRepository {

  private final TemplateEngine templateEngine;

  @Autowired
  public ThymeleafTemplateMailRepository(TemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  public Optional<String> processTemplate(TemplateMailThymeleafDo templateMailThymeleafDo) {
    return Optional.ofNullable(templateMailThymeleafDo).flatMap(thDo -> Optional.ofNullable(
        thDo.getData())).map(thDoData -> {
      Context context = new Context();
      thDoData.forEach(context::setVariable);
      TemplateSpec templateSpec = new TemplateSpec(templateMailThymeleafDo.getContent(),
          TemplateMode.HTML);
      return templateEngine.process(templateSpec, context);
    });
  }
}
