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
package baby.mumu.extension.ocr.aliyun;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.ocr.Ocr;
import baby.mumu.extension.ocr.OcrProcessor;
import com.aliyun.ocr_api20210707.Client;
import com.aliyun.ocr_api20210707.models.RecognizeBasicRequest;
import com.aliyun.ocr_api20210707.models.RecognizeBasicResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import java.io.FileInputStream;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

/**
 * 阿里云ocr处理器
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
public class AliyunOcrProcessor implements OcrProcessor {

  private final Client client;

  public AliyunOcrProcessor(Client client) {
    this.client = client;
  }

  @Override
  public String doOcr(Ocr ocr) {
    return Optional.ofNullable(ocr).filter(ocrNotNull -> ocrNotNull.getSourceFile() != null)
      .map(ocrNotNull -> {
        try {
          RecognizeBasicRequest recognizeBasicRequest = new RecognizeBasicRequest()
            .setBody(new FileInputStream(ocrNotNull.getSourceFile()))
            .setNeedRotate(false);
          RecognizeBasicResponse recognizeBasicResponse = client.recognizeBasicWithOptions(
            recognizeBasicRequest, new RuntimeOptions());
          return recognizeBasicResponse.getBody().getData();
        } catch (Exception e) {
          throw new MuMuException(ResponseCode.OCR_RECOGNITION_FAILED);
        }
      }).orElse(StringUtils.EMPTY);
  }
}
