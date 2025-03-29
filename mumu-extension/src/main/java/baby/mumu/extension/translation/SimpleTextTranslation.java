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
package baby.mumu.extension.translation;

import baby.mumu.basis.exception.MuMuException;
import baby.mumu.basis.kotlin.tools.SecurityContextUtils;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

/**
 * 简单文本翻译接口
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.3
 */
public interface SimpleTextTranslation {

  @API(status = Status.STABLE, since = "1.0.3")
  String translate(String text, String targetLanguage) throws MuMuException;

  @API(status = Status.STABLE, since = "1.0.3")
  default Optional<String> translateToAccountLanguageIfPossible(String text) {
    return Optional.ofNullable(text).filter(StringUtils::isNotBlank)
      .flatMap(_ -> SecurityContextUtils.getLoginAccountLanguage()).map(languageEnum -> {
        try {
          return this.translate(text, languageEnum.getCode());
        } catch (Exception e) {
          return text;
        }
      });
  }
}
