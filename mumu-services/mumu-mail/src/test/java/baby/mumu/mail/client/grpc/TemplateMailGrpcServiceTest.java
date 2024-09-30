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
package baby.mumu.mail.client.grpc;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResultCode;
import baby.mumu.mail.AuthenticationRequired;
import baby.mumu.mail.client.api.TemplateMailGrpcService;
import baby.mumu.mail.client.api.grpc.TemplateMailSendGrpcCmd;
import baby.mumu.mail.client.api.grpc.TemplateMailSendGrpcCo;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lognet.springboot.grpc.security.AuthCallCredentials;
import org.lognet.springboot.grpc.security.AuthHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * TemplateMailGrpcService单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class TemplateMailGrpcServiceTest extends AuthenticationRequired {

  private final TemplateMailGrpcService templateMailGrpcService;

  @Autowired
  public TemplateMailGrpcServiceTest(TemplateMailGrpcService templateMailGrpcService) {
    this.templateMailGrpcService = templateMailGrpcService;
  }

  @Test
  public void sendMail() {
    @Language("JSON") String data = """
        {
                    "verifyCode": [
                        5,
                        2,
                        8,
                        4
                    ]
                }""";
    TemplateMailSendGrpcCmd templateMailSendGrpcCmd = TemplateMailSendGrpcCmd.newBuilder()
        .setTemplateMailSendGrpcCo(
            TemplateMailSendGrpcCo.newBuilder()
                .setName(StringValue.of("template/verification_code.html"))
                .setAddress(StringValue.of("mail"))
                .setFrom(StringValue.of("conifercone@163.com"))
                .setTo(StringValue.of("kaiyu.shan@outlook.com"))
                .setSubject(StringValue.of("验证码"))
                .setData(StringValue.of(data))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken().orElseThrow(
                () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    Empty empty = templateMailGrpcService.sendMail(
        templateMailSendGrpcCmd,
        callCredentials);
    Assertions.assertNotNull(empty);
  }

  @Test
  public void syncSendMail() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    @Language("JSON") String data = """
        {
                    "verifyCode": [
                        5,
                        2,
                        8,
                        4
                    ]
                }""";
    TemplateMailSendGrpcCmd templateMailSendGrpcCmd = TemplateMailSendGrpcCmd.newBuilder()
        .setTemplateMailSendGrpcCo(
            TemplateMailSendGrpcCo.newBuilder()
                .setName(StringValue.of("template/verification_code.html"))
                .setAddress(StringValue.of("mail"))
                .setFrom(StringValue.of("conifercone@163.com"))
                .setTo(StringValue.of("kaiyu.shan@outlook.com"))
                .setSubject(StringValue.of("验证码"))
                .setData(StringValue.of(data))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken().orElseThrow(
                () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<Empty> emptyListenableFuture = templateMailGrpcService.syncSendMail(
        templateMailSendGrpcCmd,
        callCredentials);
    emptyListenableFuture.addListener(() -> {
      try {
        Empty empty = emptyListenableFuture.get();
        Assertions.assertNotNull(empty);
        latch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = latch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
  }
}
