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
package baby.mumu.file;

import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.constants.SpringBootConstants;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 文件服务
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 1.0.1
 */
@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties
@Metamodel(projectName = true, projectVersion = true)
public class MuMuFileApplication {

  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(
      MuMuFileApplication.class);
    Map<String, Object> defaultProperties = new HashMap<>();
    defaultProperties.put(SpringBootConstants.APPLICATION_TITLE,
      MuMuFileApplicationMetamodel.projectName);
    defaultProperties.put(SpringBootConstants.APPLICATION_FORMATTED_VERSION,
      String.format(" (v%s)", MuMuFileApplicationMetamodel.projectVersion));
    springApplication.setDefaultProperties(defaultProperties);
    springApplication.run(args);
  }
}
