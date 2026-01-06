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

package baby.mumu.extension.translation.aliyun;

import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.translation.SimpleTextTranslation;
import com.aliyun.alimt20181012.Client;
import com.aliyun.alimt20181012.models.GetDetectLanguageRequest;
import com.aliyun.alimt20181012.models.GetDetectLanguageResponse;
import com.aliyun.alimt20181012.models.TranslateGeneralRequest;
import com.aliyun.alimt20181012.models.TranslateGeneralResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jspecify.annotations.NonNull;

/**
 * 阿里云简单文本翻译
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.3
 */
public class AliyunSimpleTextTranslation implements SimpleTextTranslation {

    private final Client client;

    public AliyunSimpleTextTranslation(Client client) {
        this.client = client;
    }

    @Override
    @API(status = Status.STABLE, since = "1.0.3")
    public String translate(String text, @NonNull String targetLanguage) throws ApplicationException {
        try {
            RuntimeOptions runtime = new RuntimeOptions();
            GetDetectLanguageRequest getDetectLanguageRequest = new GetDetectLanguageRequest()
                .setSourceText(text);
            GetDetectLanguageResponse detectLanguageWithOptions = client.getDetectLanguageWithOptions(
                getDetectLanguageRequest, runtime);
            String detectedLanguage = detectLanguageWithOptions.getBody().getDetectedLanguage();
            if (targetLanguage.equals(detectedLanguage)) {
                return text;
            }
            TranslateGeneralRequest translateGeneralRequest = new TranslateGeneralRequest().setFormatType(
                    "text")
                .setSourceLanguage(detectedLanguage)
                .setTargetLanguage(targetLanguage)
                .setSourceText(text)
                .setScene("general");

            TranslateGeneralResponse translateGeneralResponse = client.translateGeneralWithOptions(
                translateGeneralRequest, runtime);
            return translateGeneralResponse.getBody().getData().getTranslated();
        } catch (Exception e) {
            throw new ApplicationException(ResponseCode.TRANSLATION_FAILED);
        }
    }
}
