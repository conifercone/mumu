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
package com.sky.centaur.extension.translation.deepl;

import com.deepl.api.Translator;
import com.sky.centaur.extension.translation.SimpleTextTranslation;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jetbrains.annotations.NotNull;

/**
 * deepl简单文本翻译
 *
 * @author kaiyu.shan
 * @since 1.0.3
 */
public class DeeplSimpleTextTranslation implements SimpleTextTranslation {

  private final Translator deeplTranslator;

  public DeeplSimpleTextTranslation(Translator deeplTranslator) {
    this.deeplTranslator = deeplTranslator;
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.3")
  public String translate(String text, @NotNull String targetLanguage) throws Exception {
    return deeplTranslator.translateText(text, null, targetLanguage).getText();
  }
}
