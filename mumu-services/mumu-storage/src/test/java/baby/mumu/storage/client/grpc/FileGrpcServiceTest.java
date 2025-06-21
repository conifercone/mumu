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

package baby.mumu.storage.client.grpc;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.storage.client.api.FileGrpcService;
import baby.mumu.storage.client.api.grpc.StreamFileDownloadGrpcCmd;
import baby.mumu.storage.client.api.grpc.StreamFileDownloadGrpcResult;
import baby.mumu.storage.client.api.grpc.StreamFileRemoveGrpcCmd;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Empty;
import io.grpc.CallCredentials;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * FileGrpcService单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.1
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class FileGrpcServiceTest extends AuthenticationRequired {

  private final FileGrpcService fileGrpcService;
  private static final Logger log = LoggerFactory.getLogger(FileGrpcServiceTest.class);

  @Autowired
  public FileGrpcServiceTest(FileGrpcService fileGrpcService) {
    this.fileGrpcService = fileGrpcService;
  }

  @Test
  public void download() {
    StreamFileDownloadGrpcCmd streamFileDownloadGrpcCmd = StreamFileDownloadGrpcCmd.newBuilder()
      .setName("test2.log")
      .setStorageAddress("test")
      .build();
    CallCredentials callCredentials = CallCredentialsHelper.bearerAuth(
      () -> getToken().orElseThrow(
        () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)));

    StreamFileDownloadGrpcResult download = fileGrpcService.download(
      streamFileDownloadGrpcCmd,
      callCredentials);
    Assertions.assertNotNull(download);
    String fileContent = download.getFileContent().getValue().toStringUtf8();
    Assertions.assertTrue(StringUtils.isNotBlank(fileContent));
    FileGrpcServiceTest.log.info("Download result: {}", fileContent);
  }

  @Test
  public void syncDownload() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    StreamFileDownloadGrpcCmd streamFileDownloadGrpcCmd = StreamFileDownloadGrpcCmd.newBuilder()
      .setName("test2.log")
      .setStorageAddress("test")
      .build();
    CallCredentials callCredentials = CallCredentialsHelper.bearerAuth(
      () -> getToken().orElseThrow(
        () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)));
    ListenableFuture<StreamFileDownloadGrpcResult> streamFileDownloadGrpcResultListenableFuture = fileGrpcService.syncDownload(
      streamFileDownloadGrpcCmd,
      callCredentials);
    streamFileDownloadGrpcResultListenableFuture.addListener(() -> {
      try {
        StreamFileDownloadGrpcResult streamFileDownloadGrpcResult = streamFileDownloadGrpcResultListenableFuture.get();
        Assertions.assertNotNull(streamFileDownloadGrpcResult);
        String fileContent = streamFileDownloadGrpcResult.getFileContent().getValue()
          .toStringUtf8();
        Assertions.assertTrue(StringUtils.isNotBlank(fileContent));
        FileGrpcServiceTest.log.info("SyncDownload result: {}", fileContent);
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
      .setName("test2.log")
      .setStorageAddress("test")
      .build();
    CallCredentials callCredentials = CallCredentialsHelper.bearerAuth(
      () -> getToken().orElseThrow(
        () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)));
    Empty empty = fileGrpcService.removeFile(
      streamFileRemoveGrpcCmd,
      callCredentials);
    Assertions.assertNotNull(empty);
  }

  @Test
  public void syncRemoveFile() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    StreamFileRemoveGrpcCmd streamFileRemoveGrpcCmd = StreamFileRemoveGrpcCmd.newBuilder()
      .setName("test2.log")
      .setStorageAddress("test")
      .build();
    CallCredentials callCredentials = CallCredentialsHelper.bearerAuth(
      () -> getToken().orElseThrow(
        () -> new MuMuException(ResponseCode.INTERNAL_SERVER_ERROR)));
    ListenableFuture<Empty> emptyListenableFuture = fileGrpcService.syncRemoveFile(
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
