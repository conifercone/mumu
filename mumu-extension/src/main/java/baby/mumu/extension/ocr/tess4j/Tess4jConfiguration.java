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
package baby.mumu.extension.ocr.tess4j;

import baby.mumu.extension.ExtensionProperties;
import baby.mumu.extension.ocr.OcrProcessor;
import java.util.Optional;
import net.sourceforge.tess4j.Tesseract;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * tess4j配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
@Configuration
@EnableConfigurationProperties(ExtensionProperties.class)
@ConditionalOnProperty(prefix = "mumu.extension.ocr.tess4j", value = "enabled", havingValue = "true")
@ConditionalOnClass(Tesseract.class)
public class Tess4jConfiguration {

  @Bean
  @ConditionalOnMissingBean(Tesseract.class)
  public Tesseract tesseract(ExtensionProperties extensionProperties) {
    Tesseract tesseract = new Tesseract();
    Optional.ofNullable(extensionProperties.getOcr().getTess4j().getDataPath())
      .filter(StringUtils::isNotBlank)
      .ifPresentOrElse(tesseract::setDatapath,
        () -> tesseract.setDatapath(System.getenv("TESS4J_DATA_PATH")));
    return tesseract;
  }

  @Bean
  @ConditionalOnBean(Tesseract.class)
  @ConditionalOnMissingBean(OcrProcessor.class)
  public OcrProcessor ocrProcessor(Tesseract tesseract) {
    return new Tess4jOcrProcessor(tesseract);
  }
}
