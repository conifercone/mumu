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
package baby.mumu.mail.client.api;

import static baby.mumu.basis.response.ResponseCode.GRPC_SERVICE_NOT_FOUND;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.mail.client.api.grpc.TemplateMailSendGrpcCmd;
import baby.mumu.mail.client.api.grpc.TemplateMailServiceGrpc;
import baby.mumu.mail.client.api.grpc.TemplateMailServiceGrpc.TemplateMailServiceBlockingStub;
import baby.mumu.mail.client.api.grpc.TemplateMailServiceGrpc.TemplateMailServiceFutureStub;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * 模板邮件对外提供grpc调用实例
 *
 * @author <a href="mailto:kaiyu.shan@mumu.baby">kaiyu.shan</a>
 * @since 1.0.1
 */
public class TemplateMailGrpcService extends MailGrpcService implements
  DisposableBean {

  private ManagedChannel channel;

  public TemplateMailGrpcService(
    DiscoveryClient discoveryClient,
    ObjectProvider<ObservationGrpcClientInterceptor> grpcClientInterceptorObjectProvider) {
    super(discoveryClient, grpcClientInterceptorObjectProvider);
  }

  @Override
  public void destroy() {
    Optional.ofNullable(channel).ifPresent(ManagedChannel::shutdown);
  }

  @API(status = Status.STABLE, since = "1.0.1")
  public Empty sendMail(TemplateMailSendGrpcCmd templateMailSendGrpcCmd,
    CallCredentials callCredentials) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return sendMailFromGrpc(templateMailSendGrpcCmd, callCredentials);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }

  @API(status = Status.STABLE, since = "1.0.1")
  public ListenableFuture<Empty> syncSendMail(
    TemplateMailSendGrpcCmd templateMailSendGrpcCmd,
    CallCredentials callCredentials) {
    return Optional.ofNullable(channel)
      .or(this::getManagedChannelUsePlaintext)
      .map(ch -> {
        channel = ch;
        return syncSendMailFromGrpc(templateMailSendGrpcCmd, callCredentials);
      })
      .orElseThrow(() -> new MuMuException(GRPC_SERVICE_NOT_FOUND));
  }


  private Empty sendMailFromGrpc(
    TemplateMailSendGrpcCmd templateMailSendGrpcCmd,
    CallCredentials callCredentials) {
    TemplateMailServiceBlockingStub templateMailServiceBlockingStub = TemplateMailServiceGrpc.newBlockingStub(
      channel);
    return templateMailServiceBlockingStub.withCallCredentials(callCredentials)
      .sendMail(templateMailSendGrpcCmd);
  }

  private @NotNull ListenableFuture<Empty> syncSendMailFromGrpc(
    TemplateMailSendGrpcCmd templateMailSendGrpcCmd,
    CallCredentials callCredentials) {
    TemplateMailServiceFutureStub templateMailServiceFutureStub = TemplateMailServiceGrpc.newFutureStub(
      channel);
    return templateMailServiceFutureStub.withCallCredentials(callCredentials)
      .sendMail(templateMailSendGrpcCmd);
  }

}
