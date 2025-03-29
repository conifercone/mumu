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
package baby.mumu.message;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.constants.BeanNameConstants;
import baby.mumu.basis.constants.SpringBootConstants;
import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 消息服务
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.2
 */
@SpringBootApplication
@EnableConfigurationProperties
@EnableJpaAuditing(auditorAwareRef = BeanNameConstants.MUMU_JPA_AUDITOR_AWARE)
@EnableRedisDocumentRepositories(basePackages = "baby.mumu.message.infrastructure.**.cache.**")
@EnableMethodSecurity
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@EnableTransactionManagement
@Metamodel(projectName = true, projectVersion = true, formattedProjectVersion = true)
public class MuMuMessageApplication {

  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(
      MuMuMessageApplication.class);
    Map<String, Object> defaultProperties = new HashMap<>();
    defaultProperties.put(SpringBootConstants.APPLICATION_TITLE,
      MuMuMessageApplicationMetamodel.projectName);
    defaultProperties.put(SpringBootConstants.APPLICATION_FORMATTED_VERSION,
      MuMuMessageApplicationMetamodel.formattedProjectVersion);
    springApplication.setDefaultProperties(defaultProperties);
    springApplication.run(args);
  }
}
