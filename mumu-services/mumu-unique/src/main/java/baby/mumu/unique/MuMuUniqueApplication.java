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

package baby.mumu.unique;

import baby.mumu.basis.annotations.Metamodel;
import baby.mumu.basis.constants.SpringBootConstants;
import com.github.guang19.leaf.spring.autoconfig.LeafAutoConfiguration;
import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * 分布式主键生成服务
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @since 1.0.0
 */
@SpringBootApplication
@Import(LeafAutoConfiguration.class)
@EnableRedisDocumentRepositories(basePackages = "baby.mumu.unique.infra.**.cache.**")
@Metamodel(projectName = true, projectVersion = true, formattedProjectVersion = true)
public class MuMuUniqueApplication {

  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(
      MuMuUniqueApplication.class);
    Map<String, Object> defaultProperties = new HashMap<>();
    defaultProperties.put(SpringBootConstants.APPLICATION_TITLE,
      MuMuUniqueApplicationMetamodel.PROJECT_NAME);
    defaultProperties.put(SpringBootConstants.APPLICATION_FORMATTED_VERSION,
      MuMuUniqueApplicationMetamodel.FORMATTED_PROJECT_VERSION);
    springApplication.setDefaultProperties(defaultProperties);
    springApplication.run(args);
  }
}
