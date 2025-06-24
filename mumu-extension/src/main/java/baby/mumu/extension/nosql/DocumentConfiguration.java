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

package baby.mumu.extension.nosql;

import static baby.mumu.basis.constants.BeanNameConstants.MUMU_JPA_DOCUMENT_AUDITOR_AWARE;

import baby.mumu.basis.po.jpa.MuMuJpaDocumentAuditorAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * document相关配置类
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 2.2.0
 */
@Configuration
public class DocumentConfiguration {

  @Bean(name = MUMU_JPA_DOCUMENT_AUDITOR_AWARE)
  public MuMuJpaDocumentAuditorAware mumuJpaDocumentAuditorAware() {
    return new MuMuJpaDocumentAuditorAware();
  }
}
