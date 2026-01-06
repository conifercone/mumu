/*
 * Copyright (c) 2024-2026, the original author or authors.
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

package baby.mumu.genix.client.grpc;

import baby.mumu.genix.client.api.CaptchaCodeGrpcService;
import baby.mumu.genix.client.api.grpc.CaptchaCodeGeneratedGrpcCmd;
import baby.mumu.genix.client.api.grpc.CaptchaCodeGeneratedGrpcDTO;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 验证码生成grpc接口单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class CaptchaCodeGrpcServiceTest {

    private final CaptchaCodeGrpcService captchaCodeGrpcService;
    private static final Logger log = LoggerFactory.getLogger(CaptchaCodeGrpcServiceTest.class);

    @Autowired
    public CaptchaCodeGrpcServiceTest(CaptchaCodeGrpcService captchaCodeGrpcService) {
        this.captchaCodeGrpcService = captchaCodeGrpcService;
    }

    @Test
    public void generate() {
        CaptchaCodeGeneratedGrpcCmd captchaCodeGeneratedGrpcCmd = CaptchaCodeGeneratedGrpcCmd.newBuilder()
            .setLength(4)
            .setTtl(500).build();
        CaptchaCodeGeneratedGrpcDTO generate = captchaCodeGrpcService.generate(
            captchaCodeGeneratedGrpcCmd);
        CaptchaCodeGrpcServiceTest.log.info("ID : {}",
            generate.getId());
        Assertions.assertNotNull(generate);
    }

    @Test
    public void syncGenerate() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CaptchaCodeGeneratedGrpcCmd captchaCodeGeneratedGrpcCmd = CaptchaCodeGeneratedGrpcCmd.newBuilder()
            .setLength(4)
            .setTtl(500).build();
        ListenableFuture<CaptchaCodeGeneratedGrpcDTO> valueListenableFuture = captchaCodeGrpcService.syncGenerate(
            captchaCodeGeneratedGrpcCmd);
        valueListenableFuture.addListener(() -> {
            try {
                CaptchaCodeGeneratedGrpcDTO captchaCodeGeneratedGrpcDTO = valueListenableFuture.get();
                CaptchaCodeGrpcServiceTest.log.info("Sync ID : {}",
                    Objects.requireNonNull(captchaCodeGeneratedGrpcDTO).getId());
                Assertions.assertNotNull(captchaCodeGeneratedGrpcDTO);
                countDownLatch.countDown();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }, MoreExecutors.directExecutor());
        boolean complete = countDownLatch.await(3, TimeUnit.SECONDS);
        Assertions.assertTrue(complete);
    }
}
