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
package baby.mumu.mail.application.service;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import baby.mumu.mail.application.template.executor.TemplateMailSendCmdExe;
import baby.mumu.mail.client.api.TemplateMailService;
import baby.mumu.mail.client.api.grpc.TemplateMailSendGrpcCmd;
import baby.mumu.mail.client.api.grpc.TemplateMailServiceGrpc.TemplateMailServiceImplBase;
import baby.mumu.mail.client.cmds.TemplateMailSendCmd;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import java.util.Map;
import net.devh.boot.grpc.server.service.GrpcService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 模板邮件service实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Service
@GrpcService(interceptors = {ObservationGrpcServerInterceptor.class, ClientIpInterceptor.class})
@Observed(name = "TemplateMailServiceImpl")
public class TemplateMailServiceImpl extends TemplateMailServiceImplBase implements
  TemplateMailService {

  private final TemplateMailSendCmdExe templateMailSendCmdExe;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  public TemplateMailServiceImpl(TemplateMailSendCmdExe templateMailSendCmdExe) {
    this.templateMailSendCmdExe = templateMailSendCmdExe;
  }

  @Override
  public void sendMail(TemplateMailSendCmd templateMailSendCmd) {
    templateMailSendCmdExe.execute(templateMailSendCmd);
  }

  private @NotNull TemplateMailSendCmd getTemplateMailSendCmd(
    @NotNull TemplateMailSendGrpcCmd request) {
    TemplateMailSendCmd templateMailSendCmd = new TemplateMailSendCmd();
    templateMailSendCmd.setTo(request.getTo());
    templateMailSendCmd.setName(request.getName());
    templateMailSendCmd.setFrom(request.getFrom());
    templateMailSendCmd.setAddress(request.getAddress());
    templateMailSendCmd.setSubject(request.getSubject());
    try {
      // noinspection unchecked
      templateMailSendCmd.setData((Map<String, Object>) objectMapper.readValue(
        request.getData(),
        Map.class));
    } catch (JsonProcessingException e) {
      throw new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR);
    }
    return templateMailSendCmd;
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void sendMail(TemplateMailSendGrpcCmd request,
    @NotNull StreamObserver<Empty> responseObserver) {
    templateMailSendCmdExe.execute(getTemplateMailSendCmd(
      request));
    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }
}
