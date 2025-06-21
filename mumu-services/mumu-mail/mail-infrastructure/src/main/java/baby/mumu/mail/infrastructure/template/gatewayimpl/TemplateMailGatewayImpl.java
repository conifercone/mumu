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

package baby.mumu.mail.infrastructure.template.gatewayimpl;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.kotlin.tools.SecurityContextUtils;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.mail.domain.template.TemplateMail;
import baby.mumu.mail.domain.template.gateway.TemplateMailGateway;
import baby.mumu.mail.infrastructure.template.convertor.TemplateMailConvertor;
import baby.mumu.mail.infrastructure.template.gatewayimpl.thymeleaf.ThymeleafTemplateMailRepository;
import baby.mumu.mail.infrastructure.template.gatewayimpl.thymeleaf.po.TemplateMailThymeleafPO;
import baby.mumu.storage.client.api.FileGrpcService;
import baby.mumu.storage.client.api.grpc.StreamFileDownloadGrpcCmd;
import baby.mumu.storage.client.api.grpc.StreamFileDownloadGrpcResult;
import io.grpc.CallCredentials;
import io.micrometer.observation.annotation.Observed;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * 模板邮件领域网关实现类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@Component
@Observed(name = "TemplateMailGatewayImpl")
public class TemplateMailGatewayImpl implements TemplateMailGateway {

  private final ThymeleafTemplateMailRepository thymeleafTemplateMailRepository;
  private final JavaMailSender javaMailSender;
  private final FileGrpcService fileGrpcService;
  private final TemplateMailConvertor templateMailConvertor;

  @Autowired
  public TemplateMailGatewayImpl(ThymeleafTemplateMailRepository thymeleafTemplateMailRepository,
    JavaMailSender javaMailSender, FileGrpcService fileGrpcService,
    TemplateMailConvertor templateMailConvertor) {
    this.thymeleafTemplateMailRepository = thymeleafTemplateMailRepository;
    this.javaMailSender = javaMailSender;
    this.fileGrpcService = fileGrpcService;
    this.templateMailConvertor = templateMailConvertor;
  }

  @Override
  public void sendMail(TemplateMail templateMail) {
    Optional.ofNullable(templateMail).ifPresent(templateMailDomain -> {
      StreamFileDownloadGrpcCmd streamFileDownloadGrpcCmd = StreamFileDownloadGrpcCmd.newBuilder()
        .setName(templateMailDomain.getName())
        .setStorageAddress(templateMailDomain.getAddress())
        .build();
      CallCredentials callCredentials = CallCredentialsHelper.bearerAuth(
        () -> SecurityContextUtils.getTokenValue().orElseThrow(
          () -> new MuMuException(ResponseCode.UNAUTHORIZED)));
      try {
        StreamFileDownloadGrpcResult streamFileDownloadGrpcResult = fileGrpcService.download(
          streamFileDownloadGrpcCmd,
          callCredentials);
        Optional<TemplateMailThymeleafPO> thymeleafDo = templateMailConvertor.toThymeleafPO(
          templateMailDomain);
        thymeleafDo.ifPresent(thDo -> {
          thDo.setContent(streamFileDownloadGrpcResult.getFileContent().getValue()
            .toStringUtf8());
          thymeleafTemplateMailRepository.processTemplate(thDo).ifPresent(mailContent -> {
            try {
              MimeMessage mimeMessage = javaMailSender.createMimeMessage();
              MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true,
                StandardCharsets.UTF_8.name());
              mimeMessageHelper.setFrom(templateMailDomain.getFrom());
              mimeMessageHelper.setTo(templateMailDomain.getTo());
              mimeMessageHelper.setSubject(templateMailDomain.getSubject());
              mimeMessageHelper.setText(mailContent, true);
              javaMailSender.send(mimeMessage);
            } catch (Exception e) {
              throw new MuMuException(ResponseCode.EMAIL_SENDING_EXCEPTION);
            }
          });
        });
      } catch (Exception e) {
        throw new MuMuException(ResponseCode.FAILED_TO_OBTAIN_EMAIL_TEMPLATE);
      }
    });
  }
}
