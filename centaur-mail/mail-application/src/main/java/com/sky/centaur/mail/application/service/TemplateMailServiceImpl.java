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
package com.sky.centaur.mail.application.service;

import com.sky.centaur.mail.application.template.executor.TemplateMailSendCmdExe;
import com.sky.centaur.mail.client.api.TemplateMailService;
import com.sky.centaur.mail.client.dto.TemplateMailSendCmd;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 模板邮件service实现类
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class})
@Observed(name = "TemplateMailServiceImpl")
public class TemplateMailServiceImpl implements TemplateMailService {

  private final TemplateMailSendCmdExe templateMailSendCmdExe;

  @Autowired
  public TemplateMailServiceImpl(TemplateMailSendCmdExe templateMailSendCmdExe) {
    this.templateMailSendCmdExe = templateMailSendCmdExe;
  }

  @Override
  public void sendMail(TemplateMailSendCmd templateMailSendCmd) {
    templateMailSendCmdExe.execute(templateMailSendCmd);
  }
}
