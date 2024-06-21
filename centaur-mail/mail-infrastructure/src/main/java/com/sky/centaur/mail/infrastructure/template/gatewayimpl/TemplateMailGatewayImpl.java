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
package com.sky.centaur.mail.infrastructure.template.gatewayimpl;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.StringValue;
import com.sky.centaur.basis.exception.CentaurException;
import com.sky.centaur.basis.kotlin.tools.SecurityContextUtil;
import com.sky.centaur.basis.response.ResultCode;
import com.sky.centaur.file.client.api.StreamFileGrpcService;
import com.sky.centaur.file.client.api.grpc.StreamFileDownloadGrpcCmd;
import com.sky.centaur.file.client.api.grpc.StreamFileDownloadGrpcCo;
import com.sky.centaur.file.client.api.grpc.StreamFileDownloadGrpcResult;
import com.sky.centaur.mail.domain.template.TemplateMail;
import com.sky.centaur.mail.domain.template.gateway.TemplateMailGateway;
import com.sky.centaur.mail.infrastructure.template.convertor.TemplateMailConvertor;
import com.sky.centaur.mail.infrastructure.template.gatewayimpl.thymeleaf.ThymeleafTemplateMailRepository;
import com.sky.centaur.mail.infrastructure.template.gatewayimpl.thymeleaf.dataobject.TemplateMailThymeleafDo;
import io.micrometer.observation.annotation.Observed;
import jakarta.mail.internet.MimeMessage;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.lognet.springboot.grpc.security.AuthCallCredentials;
import org.lognet.springboot.grpc.security.AuthHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * 模板邮件领域网关实现类
 *
 * @author kaiyu.shan
 * @since 1.0.1
 */
@Component
@Observed(name = "TemplateMailGatewayImpl")
public class TemplateMailGatewayImpl implements TemplateMailGateway {

  private final ThymeleafTemplateMailRepository thymeleafTemplateMailRepository;
  private final JavaMailSender javaMailSender;
  private final StreamFileGrpcService streamFileGrpcService;
  private static final Logger LOGGER = LoggerFactory.getLogger(
      TemplateMailGatewayImpl.class);

  @Autowired
  public TemplateMailGatewayImpl(ThymeleafTemplateMailRepository thymeleafTemplateMailRepository,
      JavaMailSender javaMailSender, StreamFileGrpcService streamFileGrpcService) {
    this.thymeleafTemplateMailRepository = thymeleafTemplateMailRepository;
    this.javaMailSender = javaMailSender;
    this.streamFileGrpcService = streamFileGrpcService;
  }

  @Override
  public void sendMail(TemplateMail templateMail) {
    Optional.ofNullable(templateMail).ifPresent(templateMailDomain -> {
      StreamFileDownloadGrpcCmd streamFileDownloadGrpcCmd = StreamFileDownloadGrpcCmd.newBuilder()
          .setStreamFileDownloadGrpcCo(
              StreamFileDownloadGrpcCo.newBuilder()
                  .setName(StringValue.of(templateMailDomain.getName()))
                  .setStorageAddress(StringValue.of(templateMailDomain.getAddress()))
                  .build())
          .build();
      byte[] bytes = SecurityContextUtil.getTokenValue().orElseThrow(
          () -> new CentaurException(ResultCode.UNAUTHORIZED)).getBytes();
      AuthCallCredentials callCredentials = new AuthCallCredentials(
          AuthHeader.builder().bearer().tokenSupplier(
              () -> ByteBuffer.wrap(bytes))
      );
      ListenableFuture<StreamFileDownloadGrpcResult> streamFileDownloadGrpcResultListenableFuture = streamFileGrpcService.syncDownload(
          streamFileDownloadGrpcCmd,
          callCredentials);
      streamFileDownloadGrpcResultListenableFuture.addListener(() -> {
        try {
          StreamFileDownloadGrpcResult streamFileDownloadGrpcResult = streamFileDownloadGrpcResultListenableFuture.get();
          Optional<TemplateMailThymeleafDo> thymeleafDo = TemplateMailConvertor.toThymeleafDo(
              templateMailDomain);
          thymeleafDo.ifPresent(thDo -> {
            thDo.setContent(streamFileDownloadGrpcResult.getFileContent().getValue()
                .toStringUtf8());
            thymeleafTemplateMailRepository.processTemplate(thDo).ifPresent(mailContent -> {
              try {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true,
                    Charsets.UTF_8.name());
                mimeMessageHelper.setFrom(templateMailDomain.getFrom());
                mimeMessageHelper.setTo(templateMailDomain.getTo());
                mimeMessageHelper.setSubject(templateMailDomain.getSubject());
                mimeMessageHelper.setText(mailContent, true);
                javaMailSender.send(mimeMessage);
              } catch (Exception e) {
                LOGGER.error(ResultCode.EMAIL_SENDING_EXCEPTION.getResultMsg(), e);
                throw new CentaurException(ResultCode.EMAIL_SENDING_EXCEPTION);
              }
            });
          });
        } catch (InterruptedException | ExecutionException e) {
          LOGGER.error(ResultCode.FAILED_TO_OBTAIN_EMAIL_TEMPLATE.getResultMsg(), e);
          throw new CentaurException(ResultCode.FAILED_TO_OBTAIN_EMAIL_TEMPLATE);
        }
      }, MoreExecutors.directExecutor());
    });

  }
}
