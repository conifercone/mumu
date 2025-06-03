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

package baby.mumu.extension.ocr.tess4j;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.ocr.Ocr;
import baby.mumu.extension.ocr.OcrProcessor;
import java.util.Optional;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;

/**
 * tess4j ocr处理器实现
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.0.0
 */
public class Tess4jOcrProcessor implements OcrProcessor {

  private final Tesseract tess4j;

  public Tess4jOcrProcessor(Tesseract tess4j) {
    this.tess4j = tess4j;
  }

  @Override
  public String doOcr(Ocr ocr) {
    return Optional.ofNullable(ocr).filter(
      ocrNonNull -> ocrNonNull.getSourceFile() != null && StringUtils.isNotBlank(
        ocrNonNull.getTargetLanguage())).map(ocrNonNull -> {
      tess4j.setLanguage(ocrNonNull.getTargetLanguage());
      try {
        return tess4j.doOCR(ocrNonNull.getSourceFile());
      } catch (TesseractException e) {
        throw new MuMuException(ResponseCode.OCR_RECOGNITION_FAILED);
      }
    }).orElse(StringUtils.EMPTY);
  }
}
