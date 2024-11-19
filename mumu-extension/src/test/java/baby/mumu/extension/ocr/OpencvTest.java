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
package baby.mumu.extension.ocr;

import baby.mumu.extension.fd.FaceDetection;
import baby.mumu.extension.fd.FaceDetectionConfiguration;
import baby.mumu.extension.fd.FaceDetectionProcessor;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * opencv单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
@SpringJUnitConfig(FaceDetectionConfiguration.class)
@TestPropertySource(properties = {
  "mumu.extension.fd.opencv.enabled=true"
})
public class OpencvTest {

  private final FaceDetectionProcessor faceDetectionProcessor;
  private final ResourceLoader resourceLoader;

  @Autowired
  public OpencvTest(FaceDetectionProcessor faceDetectionProcessor, ResourceLoader resourceLoader) {
    this.faceDetectionProcessor = faceDetectionProcessor;
    this.resourceLoader = resourceLoader;
  }

  @Test
  void numberOfFaces() throws IOException {
    FaceDetection faceDetection = new FaceDetection();
    faceDetection.setImageAbsolutePath(
      resourceLoader.getResource("classpath:faces.jpg").getFile().getAbsolutePath());
    Long l = faceDetectionProcessor.numberOfFaces(faceDetection);
    Assertions.assertEquals(4, l);
  }

  @Test
  void drawBorder() throws IOException {
    FaceDetection faceDetection = new FaceDetection();
    faceDetection.setImageAbsolutePath(
      resourceLoader.getResource("classpath:faces.jpg").getFile().getAbsolutePath());
    File faces = File.createTempFile("faces", ".jpg");
    faceDetection.setImageOutputAbsolutePath(faces.getAbsolutePath());
    faceDetectionProcessor.drawBorder(faceDetection);
  }
}
