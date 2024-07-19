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
package com.sky.centaur.mail.client.api;

import static com.sky.centaur.basis.response.ResultCode.GRPC_SERVICE_NOT_FOUND;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.mail.client.api.grpc.TemplateMailSendGrpcCmd;
import com.sky.centaur.mail.client.api.grpc.TemplateMailServiceGrpc;
import com.sky.centaur.mail.client.api.grpc.TemplateMailServiceGrpc.TemplateMailServiceBlockingStub;
import com.sky.centaur.mail.client.api.grpc.TemplateMailServiceGrpc.TemplateMailServiceFutureStub;
import io.grpc.ManagedChannel;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor;
import java.util.Optional;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.NotNull;
import org.lognet.springboot.grpc.security.AuthCallCredentials;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * 模板邮件对外提供grpc调用实例
 *
 * @author kaiyu.shan
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
      AuthCallCredentials callCredentials) {
    if (channel != null) {
      return sendMailFromGrpc(templateMailSendGrpcCmd, callCredentials);
    } else {
      Optional<ManagedChannel> managedChannelUsePlaintext = getManagedChannelUsePlaintext();
      if (managedChannelUsePlaintext.isPresent()) {
        channel = managedChannelUsePlaintext.get();
        return sendMailFromGrpc(templateMailSendGrpcCmd, callCredentials);
      } else {
        throw new CentaurException(GRPC_SERVICE_NOT_FOUND);
      }
    }
  }

  @API(status = Status.STABLE, since = "1.0.1")
  public ListenableFuture<Empty> syncSendMail(
      TemplateMailSendGrpcCmd templateMailSendGrpcCmd,
      AuthCallCredentials callCredentials) {
    if (channel != null) {
      return syncSendMailFromGrpc(templateMailSendGrpcCmd, callCredentials);
    } else {
      return getManagedChannelUsePlaintext().map(managedChannel -> {
        channel = managedChannel;
        return syncSendMailFromGrpc(templateMailSendGrpcCmd, callCredentials);
      }).orElse(null);
    }
  }


  private Empty sendMailFromGrpc(
      TemplateMailSendGrpcCmd templateMailSendGrpcCmd,
      AuthCallCredentials callCredentials) {
    TemplateMailServiceBlockingStub templateMailServiceBlockingStub = TemplateMailServiceGrpc.newBlockingStub(
        channel);
    return templateMailServiceBlockingStub.withCallCredentials(callCredentials)
        .sendMail(templateMailSendGrpcCmd);
  }

  private @NotNull ListenableFuture<Empty> syncSendMailFromGrpc(
      TemplateMailSendGrpcCmd templateMailSendGrpcCmd,
      AuthCallCredentials callCredentials) {
    TemplateMailServiceFutureStub templateMailServiceFutureStub = TemplateMailServiceGrpc.newFutureStub(
        channel);
    return templateMailServiceFutureStub.withCallCredentials(callCredentials)
        .sendMail(templateMailSendGrpcCmd);
  }

}
