/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
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
package com.sky.centaur.extension.translation.aliyun;

import com.aliyun.alimt20181012.Client;
import com.aliyun.alimt20181012.models.GetDetectLanguageRequest;
import com.aliyun.alimt20181012.models.GetDetectLanguageResponse;
import com.aliyun.alimt20181012.models.TranslateGeneralRequest;
import com.aliyun.alimt20181012.models.TranslateGeneralResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import com.sky.centaur.basis.kotlin.tools.SecurityContextUtil;
import com.sky.centaur.extension.translation.SimpleTextTranslation;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

/**
 * 阿里云简单文本翻译
 *
 * @author kaiyu.shan
 * @since 1.0.3
 */
public class AliyunSimpleTextTranslation implements SimpleTextTranslation {

  private final Client client;

  public AliyunSimpleTextTranslation(Client client) {
    this.client = client;
  }

  @Override
  public String translate(String text, @NotNull String targetLanguage) throws Exception {
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
  }

  @Override
  public Optional<String> translateToAccountLanguageIfPossible(String text) {
    return Optional.ofNullable(text).filter(StringUtils::hasText)
        .flatMap(res -> SecurityContextUtil.getLoginAccountLanguage()).map(languageEnum -> {
          try {
            return this.translate(text, languageEnum.name().toLowerCase());
          } catch (Exception e) {
            // ignore
          }
          return null;
        });
  }
}
