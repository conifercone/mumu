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

import baby.mumu.extension.ocr.tess4j.Tess4jLanguageEnum;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * ocr单元测试
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
@SpringJUnitConfig(OcrConfiguration.class)
@TestPropertySource(properties = {
    "mumu.extension.ocr.tess4j.enabled=true"
})
public class Tess4jOcrTest {

  private final OcrProcessor ocrProcessor;
  private final ResourceLoader resourceLoader;

  @Autowired
  public Tess4jOcrTest(OcrProcessor ocrProcessor, ResourceLoader resourceLoader) {
    this.ocrProcessor = ocrProcessor;
    this.resourceLoader = resourceLoader;
  }

  @Test
  void ocrEngTest() throws IOException {
    File fileFromResource = getFileFromResource("ocr.png");
    Ocr ocr = new Ocr();
    ocr.setSourceFile(fileFromResource);
    ocr.setTargetLanguage(Tess4jLanguageEnum.ENG.getValue());
    String string = ocrProcessor.doOcr(ocr);
    System.out.println(string);
  }

  @Test
  void ocrNumberTest() throws IOException {
    File fileFromResource = getFileFromResource("ocr_number.jpg");
    Ocr ocr = new Ocr();
    ocr.setSourceFile(fileFromResource);
    ocr.setTargetLanguage(Tess4jLanguageEnum.ENG.getValue());
    String string = ocrProcessor.doOcr(ocr);
    System.out.println(string);
  }

  @Test
  void ocrBarCodeTest() throws IOException {
    File fileFromResource = getFileFromResource("ocr_bar_code.png");
    Ocr ocr = new Ocr();
    ocr.setSourceFile(fileFromResource);
    ocr.setTargetLanguage(Tess4jLanguageEnum.ENG.getValue());
    String string = ocrProcessor.doOcr(ocr);
    System.out.println(string);
  }


  @Test
  void ocrIdNumberTest() throws IOException {
    File fileFromResource = getFileFromResource("ocr_id_number.png");
    Ocr ocr = new Ocr();
    ocr.setSourceFile(fileFromResource);
    ocr.setTargetLanguage(Tess4jLanguageEnum.CHI_SIM.getValue());
    String string = ocrProcessor.doOcr(ocr);
    System.out.println(string);
  }

  @Test
  void ocrCnTest() throws IOException {
    File fileFromResource = getFileFromResource("ocr_cn.png");
    Ocr ocr = new Ocr();
    ocr.setSourceFile(fileFromResource);
    ocr.setTargetLanguage(Tess4jLanguageEnum.CHI_SIM.getValue());
    String string = ocrProcessor.doOcr(ocr);
    System.out.println(string);
  }

  @Test
  void ocrJpTest() throws IOException {
    File fileFromResource = getFileFromResource("ocr_jp.png");
    Ocr ocr = new Ocr();
    ocr.setSourceFile(fileFromResource);
    ocr.setTargetLanguage(Tess4jLanguageEnum.JPN.getValue());
    String string = ocrProcessor.doOcr(ocr);
    System.out.println(string);
  }


  @Test
  void ocrChatInterfaceTest() throws IOException {
    File fileFromResource = getFileFromResource("ocr_chat_interface.png");
    Ocr ocr = new Ocr();
    ocr.setSourceFile(fileFromResource);
    ocr.setTargetLanguage(Tess4jLanguageEnum.CHI_SIM.getValue());
    String string = ocrProcessor.doOcr(ocr);
    System.out.println(string);
  }

  private @NotNull File getFileFromResource(String fileName)
      throws IOException {
    Resource resource = resourceLoader.getResource("classpath:" + fileName);
    File tempFile = File.createTempFile("temp", "." + FilenameUtils.getExtension(fileName));
    try (var inputStream = resource.getInputStream()) {
      Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    tempFile.deleteOnExit();
    return tempFile;
  }
}
