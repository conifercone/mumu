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
package baby.mumu.file.client.grpc;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResultCode;
import baby.mumu.file.AuthenticationRequired;
import baby.mumu.file.client.api.StreamFileGrpcService;
import baby.mumu.file.client.api.grpc.StreamFileDownloadGrpcCmd;
import baby.mumu.file.client.api.grpc.StreamFileDownloadGrpcCo;
import baby.mumu.file.client.api.grpc.StreamFileDownloadGrpcResult;
import baby.mumu.file.client.api.grpc.StreamFileRemoveGrpcCmd;
import baby.mumu.file.client.api.grpc.StreamFileRemoveGrpcCo;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lognet.springboot.grpc.security.AuthCallCredentials;
import org.lognet.springboot.grpc.security.AuthHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * StreamFileGrpcService单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class StreamFileGrpcServiceTest extends AuthenticationRequired {

  private final StreamFileGrpcService streamFileGrpcService;
  private static final Logger LOGGER = LoggerFactory.getLogger(StreamFileGrpcServiceTest.class);

  @Autowired
  public StreamFileGrpcServiceTest(StreamFileGrpcService streamFileGrpcService) {
    this.streamFileGrpcService = streamFileGrpcService;
  }

  @Test
  public void download() {
    StreamFileDownloadGrpcCmd streamFileDownloadGrpcCmd = StreamFileDownloadGrpcCmd.newBuilder()
        .setStreamFileDownloadGrpcCo(
            StreamFileDownloadGrpcCo.newBuilder().setName(StringValue.of("test2.log"))
                .setStorageAddress(StringValue.of("test"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken().orElseThrow(
                () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    StreamFileDownloadGrpcResult download = streamFileGrpcService.download(
        streamFileDownloadGrpcCmd,
        callCredentials);
    Assertions.assertNotNull(download);
    String fileContent = download.getFileContent().getValue().toStringUtf8();
    Assertions.assertTrue(StringUtils.isNotBlank(fileContent));
    LOGGER.info("Download result: {}", fileContent);
  }

  @Test
  public void syncDownload() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    StreamFileDownloadGrpcCmd streamFileDownloadGrpcCmd = StreamFileDownloadGrpcCmd.newBuilder()
        .setStreamFileDownloadGrpcCo(
            StreamFileDownloadGrpcCo.newBuilder().setName(StringValue.of("test2.log"))
                .setStorageAddress(StringValue.of("test"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken().orElseThrow(
                () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<StreamFileDownloadGrpcResult> streamFileDownloadGrpcResultListenableFuture = streamFileGrpcService.syncDownload(
        streamFileDownloadGrpcCmd,
        callCredentials);
    streamFileDownloadGrpcResultListenableFuture.addListener(() -> {
      try {
        StreamFileDownloadGrpcResult streamFileDownloadGrpcResult = streamFileDownloadGrpcResultListenableFuture.get();
        Assertions.assertNotNull(streamFileDownloadGrpcResult);
        String fileContent = streamFileDownloadGrpcResult.getFileContent().getValue()
            .toStringUtf8();
        Assertions.assertTrue(StringUtils.isNotBlank(fileContent));
        LOGGER.info("SyncDownload result: {}", fileContent);
        latch.countDown();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }, MoreExecutors.directExecutor());
    boolean completed = latch.await(3, TimeUnit.SECONDS);
    Assertions.assertTrue(completed);
  }

  @Test
  public void removeFile() {
    StreamFileRemoveGrpcCmd streamFileRemoveGrpcCmd = StreamFileRemoveGrpcCmd.newBuilder()
        .setStreamFileRemoveGrpcCo(
            StreamFileRemoveGrpcCo.newBuilder().setName(StringValue.of("test2.log"))
                .setStorageAddress(StringValue.of("test"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken().orElseThrow(
                () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    Empty empty = streamFileGrpcService.removeFile(
        streamFileRemoveGrpcCmd,
        callCredentials);
    Assertions.assertNotNull(empty);
  }

  @Test
  public void syncRemoveFile() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    StreamFileRemoveGrpcCmd streamFileRemoveGrpcCmd = StreamFileRemoveGrpcCmd.newBuilder()
        .setStreamFileRemoveGrpcCo(
            StreamFileRemoveGrpcCo.newBuilder().setName(StringValue.of("test2.log"))
                .setStorageAddress(StringValue.of("test"))
                .build())
        .build();
    AuthCallCredentials callCredentials = new AuthCallCredentials(
        AuthHeader.builder().bearer().tokenSupplier(
            () -> ByteBuffer.wrap(getToken().orElseThrow(
                () -> new MuMuException(ResultCode.INTERNAL_SERVER_ERROR)).getBytes()))
    );
    ListenableFuture<Empty> emptyListenableFuture = streamFileGrpcService.syncRemoveFile(
        streamFileRemoveGrpcCmd,
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
