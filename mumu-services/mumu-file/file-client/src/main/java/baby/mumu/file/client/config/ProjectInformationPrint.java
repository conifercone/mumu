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
package baby.mumu.file.client.config;

import baby.mumu.basis.annotations.Metamodel;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 项目信息打印
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.2.0
 */
@Metamodel(projectVersion = true, projectName = true)
public class ProjectInformationPrint {

  private final Logger log = LoggerFactory.getLogger(ProjectInformationPrint.class);

  @PostConstruct
  public void run() {
    log.info(":: {} :: {}", ProjectInformationPrintMetamodel.projectName,
      ProjectInformationPrintMetamodel.projectVersion);
  }
}
