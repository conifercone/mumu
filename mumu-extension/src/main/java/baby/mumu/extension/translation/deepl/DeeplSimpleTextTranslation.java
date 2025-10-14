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

package baby.mumu.extension.translation.deepl;

import baby.mumu.basis.exception.ApplicationException;
import baby.mumu.basis.response.ResponseCode;
import baby.mumu.extension.translation.SimpleTextTranslation;
import com.deepl.api.DeepLException;
import com.deepl.api.Translator;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.jspecify.annotations.NonNull;

/**
 * deepl简单文本翻译
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.3
 */
public class DeeplSimpleTextTranslation implements SimpleTextTranslation {

  private final Translator deeplTranslator;

  public DeeplSimpleTextTranslation(Translator deeplTranslator) {
    this.deeplTranslator = deeplTranslator;
  }

  @Override
  @API(status = Status.STABLE, since = "1.0.3")
  public String translate(String text, @NonNull String targetLanguage) throws ApplicationException {
    try {
      return deeplTranslator.translateText(text, null, targetLanguage).getText();
    } catch (DeepLException | InterruptedException e) {
      throw new ApplicationException(ResponseCode.TRANSLATION_FAILED);
    }
  }
}
