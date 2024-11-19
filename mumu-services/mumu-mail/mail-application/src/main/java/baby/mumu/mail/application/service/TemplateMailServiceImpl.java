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
package baby.mumu.mail.application.service;

import baby.mumu.basis.annotations.RateLimiter;
import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.grpc.interceptors.ClientIpInterceptor;
import baby.mumu.extension.provider.RateLimitingGrpcIpKeyProviderImpl;
import baby.mumu.mail.application.template.executor.TemplateMailSendCmdExe;
import baby.mumu.mail.client.api.TemplateMailService;
import baby.mumu.mail.client.api.grpc.TemplateMailSendGrpcCmd;
import baby.mumu.mail.client.api.grpc.TemplateMailSendGrpcCo;
import baby.mumu.mail.client.api.grpc.TemplateMailServiceGrpc.TemplateMailServiceImplBase;
import baby.mumu.mail.client.dto.TemplateMailSendCmd;
import baby.mumu.mail.client.dto.co.TemplateMailSendCo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerInterceptor;
import io.micrometer.observation.annotation.Observed;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.GRpcService;
import org.lognet.springboot.grpc.recovery.GRpcRuntimeExceptionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 模板邮件service实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@Service
@GRpcService(interceptors = {ObservationGrpcServerInterceptor.class, ClientIpInterceptor.class})
@Observed(name = "TemplateMailServiceImpl")
public class TemplateMailServiceImpl extends TemplateMailServiceImplBase implements
  TemplateMailService {

  private final TemplateMailSendCmdExe templateMailSendCmdExe;
  private final ObjectMapper objectMapper = new ObjectMapper();
  @Value("${spring.mail.username}")
  private String username;

  @Autowired
  public TemplateMailServiceImpl(TemplateMailSendCmdExe templateMailSendCmdExe) {
    this.templateMailSendCmdExe = templateMailSendCmdExe;
  }

  @Override
  public void sendMail(TemplateMailSendCmd templateMailSendCmd) {
    templateMailSendCmdExe.execute(templateMailSendCmd);
  }

  private @NotNull TemplateMailSendCo getTemplateMailSendCo(
    @NotNull TemplateMailSendGrpcCmd request) {
    TemplateMailSendCo templateMailSendCo = new TemplateMailSendCo();
    TemplateMailSendGrpcCo templateMailSendGrpcCo = request.getTemplateMailSendGrpcCo();
    templateMailSendCo.setTo(
      templateMailSendGrpcCo.hasTo() ? templateMailSendGrpcCo.getTo().getValue() : null);
    templateMailSendCo.setName(
      templateMailSendGrpcCo.hasName() ? templateMailSendGrpcCo.getName().getValue() : null);
    templateMailSendCo.setFrom(
      templateMailSendGrpcCo.hasFrom() ? templateMailSendGrpcCo.getFrom().getValue() : username);
    templateMailSendCo.setAddress(
      templateMailSendGrpcCo.hasAddress() ? templateMailSendGrpcCo.getAddress().getValue()
        : null);
    templateMailSendCo.setSubject(
      templateMailSendGrpcCo.hasSubject() ? templateMailSendGrpcCo.getSubject().getValue()
        : null);
    try {
      //noinspection unchecked
      templateMailSendCo.setData(
        templateMailSendGrpcCo.hasData() ? (Map<String, Object>) objectMapper.readValue(
          templateMailSendGrpcCo.getData().getValue(),
          Map.class) : null);
    } catch (JsonProcessingException e) {
      throw new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR);
    }
    return templateMailSendCo;
  }

  @Override
  @RateLimiter(keyProvider = RateLimitingGrpcIpKeyProviderImpl.class)
  public void sendMail(TemplateMailSendGrpcCmd request, StreamObserver<Empty> responseObserver) {
    TemplateMailSendCmd templateMailSendCmd = new TemplateMailSendCmd();
    TemplateMailSendCo templateMailSendCo = getTemplateMailSendCo(
      request);
    templateMailSendCmd.setTemplateMailSendCo(templateMailSendCo);
    try {
      templateMailSendCmdExe.execute(templateMailSendCmd);
    } catch (Exception e) {
      throw new GRpcRuntimeExceptionWrapper(e);
    }
    responseObserver.onNext(Empty.newBuilder().build());
    responseObserver.onCompleted();
  }
}
