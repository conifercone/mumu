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
package com.sky.centaur.mail.infrastructure.template.convertor;

import com.sky.centaur.mail.client.dto.co.TemplateMailSendCo;
import com.sky.centaur.mail.domain.template.TemplateMail;
import com.sky.centaur.mail.infrastructure.template.gatewayimpl.thymeleaf.dataobject.TemplateMailThymeleafDo;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.Contract;

/**
 * 模板邮件转换器
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
public final class TemplateMailConvertor {

  private TemplateMailConvertor() {
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public static Optional<TemplateMailThymeleafDo> toThymeleafDo(TemplateMail templateMail) {
    return Optional.ofNullable(templateMail)
        .map(TemplateMailMapper.INSTANCE::toThymeleafDo);
  }

  @Contract("_ -> new")
  @API(status = Status.STABLE, since = "1.0.1")
  public static Optional<TemplateMail> toEntity(TemplateMailSendCo templateMailSendCo) {
    return Optional.ofNullable(templateMailSendCo)
        .map(TemplateMailMapper.INSTANCE::toEntity);
  }
}
